package com.klarna.consumer.util;

import com.klarna.consumer.api.Address;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddressNormalizerTest {

    @Test
    public void assertNormalizedEmptyStringIsNull() {
        String normalized = AddressNormalizer.normalizeAddressField("");
        assertEquals(null, normalized);
    }

    @Test
    public void assertNormalizedBlankStringIsNull() {
        String normalized = AddressNormalizer.normalizeAddressField("   ");
        assertEquals(null, normalized);
    }

    @Test
    public void assertNormalizedNullIsNull() {
        String normalized = AddressNormalizer.normalizeAddressField(null);
        assertEquals(null, normalized);
    }

    @Test
    public void assertNormalizedWordStartsWithUpperCase() {
        String normalized = AddressNormalizer.normalizeAddressField("hello");
        assertEquals("Hello", normalized);
    }

    @Test
    public void assertNormalizedWordStartsWithUpperCaseAndEndsWithLowerCase() {
        String normalized = AddressNormalizer.normalizeAddressField("hELLO");
        assertEquals("Hello", normalized);
    }

    @Test
    public void testNormalizeMultipleWords() {
        String normalized = AddressNormalizer.normalizeAddressField("hello world");
        assertEquals("Hello World", normalized);
    }

    @Test
    public void testNormalizeMultipleWordsWithTrailingWhitespaces() {
        String normalized = AddressNormalizer.normalizeAddressField("hello world   ");
        assertEquals("Hello World", normalized);
    }

    @Test
    public void testNormalizeMultipleWordsWithInitialWhitespaces() {
        String normalized = AddressNormalizer.normalizeAddressField("   hello world");
        assertEquals("Hello World", normalized);
    }

    @Test
    public void assertThatMultipleWhitespacesAreRemoved() {
        String normalized = AddressNormalizer.normalizeAddressField("   hello   bIG   WOrlD  ");
        assertEquals("Hello Big World", normalized);
    }

    @Test
    public void testNormalizeAddress() {
        Address address = new Address()
                .withGivenName("john")
                .withSurname("doe")
                .withStreet("some street")
                .withStreetNo("4  b")
                .withZipCode("123 45")
                .withCity("STockholm")
                .withCountry("Sweden ")
                .withCareOf("");

        Address normalizedAddress = AddressNormalizer.normalize(address);

        assertEquals("John", normalizedAddress.getGivenName());
        assertEquals("Doe", normalizedAddress.getSurname());
        assertEquals("Some Street", normalizedAddress.getStreet());
        assertEquals("4 B", normalizedAddress.getStreetNo());
        assertEquals("123 45", normalizedAddress.getZipCode());
        assertEquals("Stockholm", normalizedAddress.getCity());
        assertEquals("Sweden", normalizedAddress.getCountry());
        assertEquals(null, normalizedAddress.getCareOf());
    }

}
