package com.tg.bankofsuccess.models;

public class CurrentAccount extends Account {
	
	
    // Additional attributes specific to current accounts
    String companyName;
    String userWebsite;
    String registrationNumber;

    // Getter and Setters
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setUserWebsite(String userWebsite) {
        this.userWebsite = userWebsite;
    }

    public String getUserWebsite() {
        return this.userWebsite;
    }

    public void setRegistrationNumber(Long registrationNumber) {
        // Convert Long to String and store
        this.registrationNumber = registrationNumber.toString();
    }

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }
}
