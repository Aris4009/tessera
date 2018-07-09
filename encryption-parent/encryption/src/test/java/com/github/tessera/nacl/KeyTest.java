package com.github.tessera.nacl;

import org.junit.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyTest {

    @Test
    public void differentClassesAreNotEqual() {
        final Object key = new Key("bogus".getBytes());
        final boolean isEqual = Objects.equals(key, "test");

        assertThat(isEqual).isFalse();
    }

    @Test
    public void sameInstanceIsEqual() {
        final Key key = new Key("bogus".getBytes());

        assertThat(key).isEqualTo(key).isSameAs(key);
    }

    @Test
    public void getKeyBytes() {
        final byte[] data = "bogus".getBytes();
        final Key key = new Key("bogus".getBytes());

        assertThat(key.getKeyBytes()).isEqualTo(data).isNotSameAs(data);
    }
    
    @Test
    public void hashCodeTest() {
        final byte[] data = "bogus".getBytes();
        final Key key = new Key(data);

        assertThat(key).hasSameHashCodeAs(new Key(data));
    }
    
    @Test
    public void toStringTest() {
        final byte[] data = "bogus".getBytes();
        final Key key = new Key(data);
        
        assertThat(key.toString()).isNotBlank();
    }
    
}
