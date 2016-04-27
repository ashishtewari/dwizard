package com.mebelkart.api.order.v1.core;

/**
 * Created by vinitpayal on 21/04/16.
 */
public class State {
    private Integer idState;
    private Integer idCountry;
    private Integer idZone;
    private String name;
    private String isoCode;
    private Integer taxBehavior;
    private Boolean active;

    public State(Integer idState, Integer idCountry, Integer idZone, String name, String isoCode
            , Integer taxBehavior, Boolean active) {
        this.idState = idState;
        this.idCountry = idCountry;
        this.idZone = idZone;
        this.name = name;
        this.isoCode = isoCode;
        this.taxBehavior = taxBehavior;
        this.active = active;
    }

    public Integer getIdState() {
        return idState;
    }

    public void setIdState(Integer idState) {
        this.idState = idState;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public Integer getTaxBehavior() {
        return taxBehavior;
    }

    public void setTaxBehavior(Integer taxBehavior) {
        this.taxBehavior = taxBehavior;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
