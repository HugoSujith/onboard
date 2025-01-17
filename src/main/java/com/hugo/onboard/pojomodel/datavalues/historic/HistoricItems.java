package com.hugo.onboard.pojomodel.datavalues.historic;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricItems {
    private Date date;
    @JsonProperty("weight_unit")
    private String weightUnit;
    private Double open;
    private Double close;
    private Double high;
    private Double low;
    private Double MA50;
    private Double MA200;

    public HistoricItems(Date date, String weightUnit, Double open, Double close, Double high, Double low) {
        this.date = date;
        this.weightUnit = weightUnit != null ? weightUnit : "g";
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
    }

    public HistoricItems(Date date, Double open, Double close, Double high, Double low) {
        this(date, "g", open, close, high, low);
    }

    public HistoricItems(Date date, String weightUnit, Double open, Double close, Double high, Double low, Double MA50) {
        this(date, weightUnit, open, close, high, low);
        this.MA50 = MA50;
    }

    public HistoricItems(Date date, String weightUnit, Double open, Double close, Double high, Double low, Double MA50, Double MA200) {
        this(date, weightUnit, open, close, high, low, MA50);
        this.MA200 = MA200;
    }

    public HistoricItems() {

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getMA50() {
        return MA50;
    }

    public void setMA50(Double MA50) {
        this.MA50 = MA50;
    }

    public Double getMA200() {
        return MA200;
    }

    public void setMA200(Double MA200) {
        this.MA200 = MA200;
    }

    @Override
    public String toString() {
        return "HistoricItems{"
                + "date=" + date
                + ", weightUnit='" + weightUnit + '\''
                + ", open=" + open
                + ", close=" + close
                + ", high=" + high
                + ", low=" + low
                + ", MA50=" + MA50
                + ", MA200=" + MA200
                + '}';
    }
}
