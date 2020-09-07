package com.quorum.tessera.partyinfo.model;

import com.quorum.tessera.encryption.PublicKey;
import com.quorum.tessera.partyinfo.node.Recipient;
import org.junit.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class RecipientTest {

    private static final PublicKey TEST_KEY = PublicKey.from(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9});

    @Test
    public void differentClassesAreNotEqual() {
        final boolean isEqual = Objects.equals(Recipient.of(TEST_KEY, "url"), "test");

        assertThat(isEqual).isFalse();
    }

    @Test
    public void sameInstanceIsEqual() {
        final Recipient recipient = Recipient.of(TEST_KEY, "url");

        assertThat(recipient).isEqualTo(recipient).isSameAs(recipient);
    }

    @Test
    public void sameFieldsAreEqual() {
        final Recipient recipient = Recipient.of(TEST_KEY, "url");
        final Recipient duplicate = Recipient.of(PublicKey.from(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9}), "url");

        assertThat(recipient).isEqualTo(duplicate).isNotSameAs(duplicate);
    }

    @Test
    public void ifEitherFieldIsntEqualThenObjectIsntEqual() {
        final Recipient recipient = Recipient.of(TEST_KEY, "url");
        final Recipient duplicate2 = Recipient.of(PublicKey.from(new byte[] {2, 3, 4, 5, 6, 7, 8, 9}), "url");

        assertThat(recipient).isNotEqualTo(duplicate2);
    }

    @Test
    public void getters() {
        final Recipient recipient = Recipient.of(PublicKey.from(new byte[] {1, 2, 3}), "partyurl");

        assertThat(recipient.getUrl()).isEqualTo("partyurl").isSameAs("partyurl");
        assertThat(recipient.getKey()).isEqualTo(PublicKey.from(new byte[] {1, 2, 3}));
    }

    @Test
    public void hashCodeIsSame() {
        final Recipient recipient = Recipient.of(TEST_KEY, "url");
        final Recipient duplicate = Recipient.of(PublicKey.from(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9}), "url");

        assertThat(recipient).hasSameHashCodeAs(duplicate);
    }

    @Test
    public void toStringReturnsFormattedValue() {
        final Recipient recipient = Recipient.of(TEST_KEY, "url");

        final String recipientAsString = recipient.toString();
        assertThat(recipientAsString).isEqualTo("Recipient{key=PublicKey[AQIDBAUGBwgJ], url='url'}");
    }
}
