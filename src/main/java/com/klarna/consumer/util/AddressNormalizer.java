package com.klarna.consumer.util;

import com.klarna.consumer.api.Address;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

public final class AddressNormalizer {

    private AddressNormalizer() {}

    public static String normalizeAddressField(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String[] words = value.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(normalizeWord(word)).append(" ");
        }
        return sb.toString().trim();
    }

    public static Address normalize(Address address) {
        return address
                .withGivenName(normalizeAddressField(address.getGivenName()))
                .withSurname(normalizeAddressField(address.getSurname()))
                .withStreet(normalizeAddressField(address.getStreet()))
                .withStreetNo(normalizeAddressField(address.getStreetNo()))
                .withZipCode(normalizeAddressField(address.getZipCode()))
                .withCity(normalizeAddressField(address.getCity()))
                .withCountry(normalizeAddressField(address.getCountry()))
                .withCareOf(normalizeAddressField(address.getCareOf()));
    }

    private static String normalizeWord(String word) {
        return WordUtils.capitalizeFully(word);
    }
}
