package com.mebelkart.api.order.v1.core;


/**
 * Created by vinitpayal on 20/04/16.
 */
public class Customer {
    private Integer idCustomer;
    private Integer idGender;
    private Integer idDefaultGroup;
    private String firstName;
    private String lastName;
    private String email;
    private String birthDay;
    private Boolean newsletter;
    private Boolean isGuest;
    private Boolean deleted;
    private String dateAdd;
    private String dateUpd;

    public Customer(Integer idCustomer, Integer idGender, Integer idDefaultGroup, String firstName
            , String lastName, String email, String birthDay, Boolean newsletter, Boolean isGuest, Boolean deleted
            , String dateAdd, String dateUpd) {
        this.idCustomer = idCustomer;
        this.idGender = idGender;
        this.idDefaultGroup = idDefaultGroup;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDay = birthDay;
        this.newsletter = newsletter;
        this.isGuest = isGuest;
        this.deleted = deleted;
        this.dateAdd = dateAdd;
        this.dateUpd = dateUpd;
    }

    public Integer getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(Integer idCustomer) {
        this.idCustomer = idCustomer;
    }

    public Integer getIdGender() {
        return idGender;
    }

    public void setIdGender(Integer idGender) {
        this.idGender = idGender;
    }

    public Integer getIdDefaultGroup() {
        return idDefaultGroup;
    }

    public void setIdDefaultGroup(Integer idDefaultGroup) {
        this.idDefaultGroup = idDefaultGroup;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public Boolean getNewsletter() {
        return newsletter;
    }

    public void setNewsletter(Boolean newsletter) {
        this.newsletter = newsletter;
    }

    public Boolean getGuest() {
        return isGuest;
    }

    public void setGuest(Boolean guest) {
        isGuest = guest;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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
}
