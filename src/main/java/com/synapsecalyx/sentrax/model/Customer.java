package com.synapsecalyx.sentrax.model;

import java.util.Objects;

/** A bank customer whose transactions are monitored for fraud. */
public class Customer {
    private long id;
    private String fullName;
    private String email;
    private String homeLocation;
    private String registeredDeviceId;

    public Customer() {
    }

    public Customer(long id, String fullName, String email, String homeLocation, String registeredDeviceId) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.homeLocation = homeLocation;
        this.registeredDeviceId = registeredDeviceId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHomeLocation() { return homeLocation; }
    public void setHomeLocation(String homeLocation) { this.homeLocation = homeLocation; }

    public String getRegisteredDeviceId() { return registeredDeviceId; }
    public void setRegisteredDeviceId(String registeredDeviceId) { this.registeredDeviceId = registeredDeviceId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return id == customer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", fullName='" + fullName + "'}";
    }
}
