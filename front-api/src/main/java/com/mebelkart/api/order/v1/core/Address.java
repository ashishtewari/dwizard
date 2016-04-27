package com.mebelkart.api.order.v1.core;

import org.joda.time.DateTime;

/**
 * Created by vinitpayal on 20/04/16.
 */
public class Address {
    private Integer idAddress;
    private Integer idCountry;
    private Integer idState;
    private Integer idManufacturer;
    private Integer idSupplier;
    private String alias;
    private String company;
    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String postCode;
    private String city;
    private String other;
    private String phone;
    private String phoneMobile;
    private String vatNumber;
    private String dni;
    private Boolean isGuestAddress;
    private String dateAdd;
    private String dateUpd;
    private Boolean active;
    private Country country;
    private State state;


    public Address(Integer idAddress, Integer idCountry, Integer idState, Integer idManufacturer
            , Integer idSupplier, String alias, String company, String firstName, String lastName
            , String address1, String address2, String postCode, String city, String other, String phone
            , String phoneMobile, String vatNumber, String dni, Boolean isGuestAddress, String dateAdd
            , String dateUpd, Boolean active, Country country, State state) {
        this.idAddress = idAddress;
        this.idCountry = idCountry;
        this.idState = idState;
        this.idManufacturer = idManufacturer;
        this.idSupplier = idSupplier;
        this.alias = alias;
        this.company = company;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address1 = address1;
        this.address2 = address2;
        this.postCode = postCode;
        this.city = city;
        this.other = other;
        this.phone = phone;
        this.phoneMobile = phoneMobile;
        this.vatNumber = vatNumber;
        this.dni = dni;
        this.isGuestAddress = isGuestAddress;
        this.dateAdd = dateAdd;
        this.dateUpd = dateUpd;
        this.active = active;
        this.country = country;
        this.state = state;
    }

    public Integer getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(Integer idAddress) {
        this.idAddress = idAddress;
    }

    public Integer getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(Integer idCountry) {
        this.idCountry = idCountry;
    }

    public Integer getIdState() {
        return idState;
    }

    public void setIdState(Integer idState) {
        this.idState = idState;
    }

    public Integer getIdManufacturer() {
        return idManufacturer;
    }

    public void setIdManufacturer(Integer idManufacturer) {
        this.idManufacturer = idManufacturer;
    }

    public Integer getIdSupplier() {
        return idSupplier;
    }

    public void setIdSupplier(Integer idSupplier) {
        this.idSupplier = idSupplier;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneMobile() {
        return phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Boolean getGuestAddress() {
        return isGuestAddress;
    }

    public void setGuestAddress(Boolean guestAddress) {
        isGuestAddress = guestAddress;
    }

    public String getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(String dateAdd) {
        this.dateAdd = dateAdd;
    }

    public String getDateUpd() {
        return dateUpd;
    }

    public void setDateUpd(String dateUpd) {
        this.dateUpd = dateUpd;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
