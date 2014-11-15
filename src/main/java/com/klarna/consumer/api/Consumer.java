package com.klarna.consumer.api;

public class Consumer extends ValueObject {

    private String consumerId;
    private String email;
    private String mobilePhone;
    private Address address;

    public Consumer() {

    }

    private Consumer(String consumerId, String email, String mobilePhone, Address address) {
        this.consumerId = consumerId;
        this.email = email;
        this.mobilePhone = mobilePhone;
        this.address = address;
    }

    public String getConsumerId() {
        return consumerId;
    }

	public Consumer withConsumerId(String consumerId) {
        return new Consumer(consumerId, email, mobilePhone, address);
    }

    public String getEmail() {
        return email;
    }

    public Consumer withEmail(String email) {
        return new Consumer(consumerId, email, mobilePhone, address);
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public Consumer withMobilePhone(String mobilePhone) {
        return new Consumer(consumerId, email, mobilePhone, address);
    }

    public Address getAddress() {
        return address;
    }

    public Consumer withAddress(Address address) {
        return new Consumer(consumerId, email, mobilePhone, address);
    }

}
