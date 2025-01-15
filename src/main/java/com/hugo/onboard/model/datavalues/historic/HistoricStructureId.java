package com.hugo.onboard.model.datavalues.historic;

import jakarta.persistence.Embeddable;

@Embeddable
public class HistoricStructureId {
    private String metalName;
    private String currency;
    private String weightUnit;

    private HistoricEmbedded historyEmbedded;

    public HistoricStructureId(HistoricEmbedded historyEmbedded) {
        this.metalName = "XAG";
        this.currency = "INR";
        this.weightUnit = "g";
        this.historyEmbedded = historyEmbedded;
    }

    public HistoricStructureId(String metalName, String currency, String weightUnit, HistoricEmbedded historyEmbedded) {
        this.metalName = metalName;
        this.currency = currency;
        this.weightUnit = weightUnit;
        this.historyEmbedded = historyEmbedded;
    }

    public HistoricStructureId() {

    }

    public String getMetalName() {
        return metalName;
    }

    public void setMetalName(String metalName) {
        this.metalName = metalName;
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

    public HistoricEmbedded getHistoryEmbedded() {
        return historyEmbedded;
    }

    public void setHistoryEmbedded(HistoricEmbedded historyEmbedded) {
        this.historyEmbedded = historyEmbedded;
    }

    @Override
    public String toString() {
        return "HistoricStructureId{"
                + "metalName='" + metalName + '\''
                + ", currency='" + currency + '\''
                + ", weightUnit='" + weightUnit + '\''
                + ", historyEmbedded=" + historyEmbedded
                + '}';
    }
}
