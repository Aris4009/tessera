package com.quorum.tessera.config.adapters;

import com.quorum.tessera.config.KeyData;
import com.quorum.tessera.config.KeyDataConfig;
import com.quorum.tessera.config.keypairs.*;
import com.quorum.tessera.config.keys.KeyEncryptorHolder;
import com.quorum.tessera.config.keys.KeyEncryptor;
import com.quorum.tessera.config.util.JaxbUtil;
import com.quorum.tessera.io.FilesDelegate;
import static java.nio.charset.StandardCharsets.UTF_8;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Objects;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class KeyDataAdapter extends XmlAdapter<KeyData, ConfigKeyPair> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyDataAdapter.class);

    public static final String NACL_FAILURE_TOKEN = "NACL_FAILURE";

    private KeyEncryptorHolder keyEncryptorHolder = KeyEncryptorHolder.INSTANCE;

    private final FilesDelegate filesDelegate;

    public KeyDataAdapter() {
        this(FilesDelegate.create());
    }

    protected KeyDataAdapter(FilesDelegate filesDelegate) {
        this.filesDelegate = Objects.requireNonNull(filesDelegate);
    }

    @Override
    public ConfigKeyPair unmarshal(final KeyData keyData) {

        if (!keyEncryptorHolder.getKeyEncryptor().isPresent()) {
            LOGGER.debug("Ignoring  unmarshal as we pending keyEncryptor initialisation");
            return null;
        }

        // case 1, the keys are provided inline
        if (Objects.nonNull(keyData.getPrivateKey()) && Objects.nonNull(keyData.getPublicKey())) {
            return new DirectKeyPair(keyData.getPublicKey(), keyData.getPrivateKey());
        }

        // case 2, the config is provided inline
        if (keyData.getPublicKey() != null && keyData.getConfig() != null) {
            return new InlineKeypair(
                    keyData.getPublicKey(), keyData.getConfig(), keyEncryptorHolder.getKeyEncryptor().get());
        }

        // case 3, the Azure Key Vault data is provided
        if (keyData.getAzureVaultPublicKeyId() != null && keyData.getAzureVaultPrivateKeyId() != null) {
            return new AzureVaultKeyPair(
                    keyData.getAzureVaultPublicKeyId(),
                    keyData.getAzureVaultPrivateKeyId(),
                    keyData.getAzureVaultPublicKeyVersion(),
                    keyData.getAzureVaultPrivateKeyVersion());
        }

        // case 4, the Hashicorp Vault data is provided
        if (keyData.getHashicorpVaultPublicKeyId() != null
                && keyData.getHashicorpVaultPrivateKeyId() != null
                && keyData.getHashicorpVaultSecretEngineName() != null
                && keyData.getHashicorpVaultSecretName() != null) {
            return new HashicorpVaultKeyPair(
                    keyData.getHashicorpVaultPublicKeyId(),
                    keyData.getHashicorpVaultPrivateKeyId(),
                    keyData.getHashicorpVaultSecretEngineName(),
                    keyData.getHashicorpVaultSecretName(),
                    keyData.getHashicorpVaultSecretVersion());
        }

        // case 5, the keys are provided inside a file
        if (keyData.getPublicKeyPath() != null && keyData.getPrivateKeyPath() != null) {

            if (filesDelegate.exists(keyData.getPublicKeyPath()) && filesDelegate.exists(keyData.getPrivateKeyPath())) {
                byte[] publicKeyData = filesDelegate.readAllBytes(keyData.getPublicKeyPath());
                final String publicKey = new String(publicKeyData, UTF_8);

                KeyDataConfig keyDataConfig =
                        JaxbUtil.unmarshal(
                                filesDelegate.newInputStream(keyData.getPrivateKeyPath()), KeyDataConfig.class);

                KeyEncryptor keyEncryptor = keyEncryptorHolder.getKeyEncryptor().get();

                InlineKeypair inlineKeypair = new InlineKeypair(publicKey, keyDataConfig, keyEncryptor);
                return new FilesystemKeyPair(keyData.getPublicKeyPath(), keyData.getPrivateKeyPath(), inlineKeypair);
            }
        }

        // case 6, the key config specified is invalid
        return new UnsupportedKeyPair(
                keyData.getConfig(),
                keyData.getPrivateKey(),
                keyData.getPublicKey(),
                keyData.getPrivateKeyPath(),
                keyData.getPublicKeyPath(),
                keyData.getAzureVaultPublicKeyId(),
                keyData.getAzureVaultPrivateKeyId(),
                keyData.getAzureVaultPublicKeyVersion(),
                keyData.getAzureVaultPrivateKeyVersion(),
                keyData.getHashicorpVaultPublicKeyId(),
                keyData.getHashicorpVaultPrivateKeyId(),
                keyData.getHashicorpVaultSecretEngineName(),
                keyData.getHashicorpVaultSecretName(),
                keyData.getHashicorpVaultSecretVersion());
    }

    @Override
    public KeyData marshal(final ConfigKeyPair keyPair) {

        if (!keyEncryptorHolder.getKeyEncryptor().isPresent()) {
            return null;
        }

        KeyData keyData = new KeyData();

        if (keyPair instanceof DirectKeyPair) {
            DirectKeyPair kp = (DirectKeyPair) keyPair;

            keyData.setPublicKey(kp.getPublicKey());
            keyData.setPrivateKey(kp.getPrivateKey());
            return keyData;
        }

        if (keyPair instanceof InlineKeypair) {
            InlineKeypair kp = (InlineKeypair) keyPair;

            keyData.setPublicKey(kp.getPublicKey());
            keyData.setConfig(kp.getPrivateKeyConfig());
            return keyData;
        }

        if (keyPair instanceof AzureVaultKeyPair) {
            AzureVaultKeyPair kp = (AzureVaultKeyPair) keyPair;

            keyData.setAzureVaultPublicKeyId(kp.getPublicKeyId());
            keyData.setAzureVaultPrivateKeyId(kp.getPrivateKeyId());
            keyData.setAzureVaultPublicKeyVersion(kp.getPublicKeyVersion());
            keyData.setAzureVaultPrivateKeyVersion(kp.getPrivateKeyVersion());
            return keyData;
        }

        if (keyPair instanceof HashicorpVaultKeyPair) {
            HashicorpVaultKeyPair kp = (HashicorpVaultKeyPair) keyPair;

            keyData.setHashicorpVaultPublicKeyId(kp.getPublicKeyId());
            keyData.setHashicorpVaultPrivateKeyId(kp.getPrivateKeyId());
            keyData.setHashicorpVaultSecretEngineName(kp.getSecretEngineName());
            keyData.setHashicorpVaultSecretName(kp.getSecretName());
            return keyData;
        }

        if (keyPair instanceof FilesystemKeyPair) {
            FilesystemKeyPair kp = (FilesystemKeyPair) keyPair;

            keyData.setPublicKeyPath(kp.getPublicKeyPath());
            keyData.setPrivateKeyPath(kp.getPrivateKeyPath());
            return keyData;
        }

        if (keyPair instanceof UnsupportedKeyPair) {
            UnsupportedKeyPair kp = (UnsupportedKeyPair) keyPair;
            return new KeyData(
                    kp.getConfig(),
                    kp.getPrivateKey(),
                    kp.getPublicKey(),
                    kp.getPrivateKeyPath(),
                    kp.getPublicKeyPath(),
                    kp.getAzureVaultPrivateKeyId(),
                    kp.getAzureVaultPublicKeyId(),
                    kp.getAzureVaultPublicKeyVersion(),
                    kp.getAzureVaultPrivateKeyVersion(),
                    kp.getHashicorpVaultPrivateKeyId(),
                    kp.getHashicorpVaultPublicKeyId(),
                    kp.getHashicorpVaultSecretEngineName(),
                    kp.getHashicorpVaultSecretName(),
                    kp.getHashicorpVaultSecretVersion());
        }

        throw new UnsupportedOperationException("The keypair type " + keyPair.getClass() + " is not allowed");
    }
}
