package com.hugo.metalbroker.pojomodel.datavalues.spot;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotItems {
    private Date date;
    private Double ask;
    private Double mid;
    private Double value;
    private Double bid;
    private Double performance;
    @JsonProperty("weight_unit")
    private String weightUnit;

    public SpotItems(Date date, Double ask, Double mid, Double value, Double bid, Double performance) {
        this.date = date;
        this.ask = ask;
        this.mid = mid;
        this.value = value;
        this.bid = bid;
        this.performance = performance;
        this.weightUnit = "g";
    }

    public SpotItems(Date date, Double ask, Double mid, Double value, Double bid, Double performance, String weightUnit) {
        this.date = date;
        this.ask = ask;
        this.mid = mid;
        this.value = value;
        this.bid = bid;
        this.performance = performance;
        this.weightUnit = weightUnit;
    }

    public SpotItems() {

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getAsk() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

    public Double getMid() {
        return mid;
    }

    public void setMid(Double mid) {
        this.mid = mid;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getPerformance() {
        return performance;
    }

    public void setPerformance(Double performance) {
        this.performance = performance;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    @Override
    public String toString() {
        return "date=" + "SpotItems{" + date + ", ask=" + ask + ", mid=" + mid + ", value=" + value + ", bid=" + bid + ", performance=" + performance + ", weightUnit='" + weightUnit + '\'' + '}';
    }
}
