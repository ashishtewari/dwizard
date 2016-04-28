package com.mebelkart.api.order.v1.core;

/**
 * Created by vinitpayal on 20/04/16.
 */
public class Country {

    private Integer idCountry;
    private Integer idZone;
    private Integer idCurrency;
    private String isoCode;
    private Integer callPrefix;
    private Boolean active;
    private Boolean containsStates;
    private Boolean needIdentificationNumber;
    private Boolean needZipCode;
    private String zipCodeFormat;
    private Boolean displayTaxLabel;

    public Country(Integer idCountry, Integer idZone, Integer idCurrency, String isoCode, Integer callPrefix
            , Boolean active, Boolean containsStates, Boolean needIdentificationNumber, Boolean needZipCode
            , String zipCodeFormat, Boolean displayTaxLabel) {
        this.idCountry = idCountry;
        this.idZone = idZone;
        this.idCurrency = idCurrency;
        this.isoCode = isoCode;
        this.callPrefix = callPrefix;
        this.active = active;
        this.containsStates = containsStates;
        this.needIdentificationNumber = needIdentificationNumber;
        this.needZipCode = needZipCode;
        this.zipCodeFormat = zipCodeFormat;
        this.displayTaxLabel = displayTaxLabel;
    }

    public Integer getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(Integer idCountry) {
        this.idCountry = idCountry;
    }

    public Integer getIdZone() {
        return idZone;
    }

    public void setIdZone(Integer idZone) {
        this.idZone = idZone;
    }

    public Integer getIdCurrency() {
        return idCurrency;
    }

    public void setIdCurrency(Integer idCurrency) {
        this.idCurrency = idCurrency;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public Integer getCallPrefix() {
        return callPrefix;
    }

    public void setCallPrefix(Integer callPrefix) {
        this.callPrefix = callPrefix;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getContainsStates() {
        return containsStates;
    }

    public void setContainsStates(Boolean containsStates) {
        this.containsStates = containsStates;
    }

    public Boolean getNeedIdentificationNumber() {
        return needIdentificationNumber;
    }

    public void setNeedIdentificationNumber(Boolean needIdentificationNumber) {
        this.needIdentificationNumber = needIdentificationNumber;
    }

    public Boolean getNeedZipCode() {
        return needZipCode;
    }

    public void setNeedZipCode(Boolean needZipCode) {
        this.needZipCode = needZipCode;
    }

    public String getZipCodeFormat() {
        return zipCodeFormat;
    }

    public void setZipCodeFormat(String zipCodeFormat) {
        this.zipCodeFormat = zipCodeFormat;
    }

    public Boolean getDisplayTaxLabel() {
        return displayTaxLabel;
    }

    public void setDisplayTaxLabel(Boolean displayTaxLabel) {
        this.displayTaxLabel = displayTaxLabel;
    }
}
