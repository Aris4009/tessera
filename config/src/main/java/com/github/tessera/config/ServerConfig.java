package com.github.tessera.config;

import com.github.tessera.config.constraints.ValidSsl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(factoryMethod = "create")
public class ServerConfig {

    @NotNull
    @XmlElement(required = false, defaultValue = "0.0.0.0")
    private final String hostName;

    @NotNull
    @XmlElement
    private final Integer port;

    @Valid
    @XmlElement(required = false)
    @ValidSsl
    private final SslConfig sslConfig;

    public ServerConfig(String hostName, Integer port, SslConfig sslConfig) {
        this.hostName = Optional.ofNullable(hostName).orElse("0.0.0.0");
        this.port = port;
        this.sslConfig = sslConfig;
    }

    private static ServerConfig create() {
        return new ServerConfig(null, null, null);
    }

    public String getHostName() {
        return hostName;
    }

    public Integer getPort() {
        return port;
    }

    public SslConfig getSslConfig() {
        return sslConfig;
    }

    public URI getServerUri() {
        try {
            return new URI(hostName + ":" + port);
        } catch (URISyntaxException ex) {
            throw new ConfigException(ex);
        }
    }

    public boolean isSsl() {
        return Objects.nonNull(sslConfig) && sslConfig.getTls() == SslAuthenticationMode.STRICT;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.hostName);
        hash = 53 * hash + Objects.hashCode(this.port);
        hash = 53 * hash + Objects.hashCode(this.sslConfig);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServerConfig other = (ServerConfig) obj;
        if (!Objects.equals(this.hostName, other.hostName)) {
            return false;
        }
        if (!Objects.equals(this.port, other.port)) {
            return false;
        }
        if (!Objects.equals(this.sslConfig, other.sslConfig)) {
            return false;
        }
        return true;
    }

}
