package com.klarna.consumer.api;

public class Address extends ValueObject {

    private String givenName;
    private String surname;
    private String street;
    private String streetNo;
    private String careOf;
    private String zipCode;
    private String city;
    private String country;

    public Address() {

    }

    private Address(String givenName, String surname, String street, String streetNo, String careOf, String zipCode,
                    String city, String country) {
        this.givenName = givenName;
        this.surname = surname;
        this.street = street;
        this.streetNo = streetNo;
        this.careOf = careOf;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }

    public String getGivenName() {
        return givenName;
    }

    public Address withGivenName(String givenName) {
        return new Address(givenName, surname, street, streetNo, careOf, zipCode, city, country);
    }

    public String getSurname() {
        return surname;
    }

    public Address withSurname(String surname) {
        return new Address(givenName, surname, street, streetNo, careOf, zipCode, city, country);
    }

    public String getStreet() {
        return street;
    }

    public Address withStreet(String street) {
        return new Address(givenName, surname, street, streetNo, careOf, zipCode, city, country);
    }

    public String getStreetNo() {
        return streetNo;
    }

    public Address withStreetNo(String streetNo) {
        return new Address(givenName, surname, street, streetNo, careOf, zipCode, city, country);
    }

    public String getCareOf() {
        return careOf;
    }

    public Address withCareOf(String careOf) {
        return new Address(givenName, surname, street, streetNo, careOf, zipCode, city, country);
    }

    public String getZipCode() {
        return zipCode;
    }

    public Address withZipCode(String zipCode) {
        return new Address(givenName, surname, street, streetNo, careOf, zipCode, city, country);
    }

    public String getCity() {
        return city;
    }

    public Address withCity(String city) {
        return new Address(givenName, surname, street, streetNo, careOf, zipCode, city, country);
    }

    public String getCountry() {
        return country;
    }

    public Address withCountry(String country) {
        return new Address(givenName, surname, street, streetNo, careOf, zipCode, city, country);
    }

}
