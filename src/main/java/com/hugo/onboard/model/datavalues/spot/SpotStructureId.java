package com.hugo.onboard.model.datavalues.spot;

import jakarta.persistence.Embeddable;

@Embeddable
public class SpotStructureId {
    private String metal;
    private String currency;
    private String weightUnit;

    private SpotEmbedded spotEmbedded;

    public SpotStructureId(SpotEmbedded spotEmbedded) {
        this.metal = "XAG";
        this.currency = "INR";
        this.weightUnit = "g";
        this.spotEmbedded = spotEmbedded;
    }

    public SpotStructureId(String metal, String currency, String weightUnit, SpotEmbedded spotEmbedded) {
        this.metal = metal;
        this.currency = currency;
        this.weightUnit = weightUnit;
        this.spotEmbedded = spotEmbedded;
    }

    public SpotStructureId() {

    }

    public String getMetal() {
        return metal;
    }

    public void setMetal(String metal) {
        this.metal = metal;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public SpotEmbedded getSpotEmbedded() {
        return spotEmbedded;
    }

    public void setSpotEmbedded(SpotEmbedded spotEmbedded) {
        this.spotEmbedded = spotEmbedded;
    }
}
