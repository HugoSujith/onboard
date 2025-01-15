package com.hugo.onboard.model.datavalues.historic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class HistoricPerformance {
    @Id
    private Long id = 1L; // Fixed ID set to 1
    private Double fiveD;
    private Double oneM;
    private Double ytd;
    private Double oneY;
    private Double fiveY;
    private Double tenY;
    private Double max;

    public HistoricPerformance(Double fiveD, Double oneM, Double ytd, Double oneY, Double fiveY, Double tenY, Double max) {
        this.id = 1L; // Ensuring ID is always set to 1
        this.fiveD = fiveD;
        this.oneM = oneM;
        this.ytd = ytd;
        this.oneY = oneY;
        this.fiveY = fiveY;
        this.tenY = tenY;
        this.max = max;
    }

    public HistoricPerformance() {
        this.id = 1L; // Default constructor with fixed ID
    }

    public Long getId() {
        return id;
    }

    // Removed the setId method to prevent external modification of the ID
    public Double getFiveD() {
        return fiveD;
    }

    public void setFiveD(Double fiveD) {
        this.fiveD = fiveD;
    }

    public Double getOneM() {
        return oneM;
    }

    public void setOneM(Double oneM) {
        this.oneM = oneM;
    }

    public Double getYtd() {
        return ytd;
    }

    public void setYtd(Double ytd) {
        this.ytd = ytd;
    }

    public Double getOneY() {
        return oneY;
    }

    public void setOneY(Double oneY) {
        this.oneY = oneY;
    }

    public Double getFiveY() {
        return fiveY;
    }

    public void setFiveY(Double fiveY) {
        this.fiveY = fiveY;
    }

    public Double getTenY() {
        return tenY;
    }

    public void setTenY(Double tenY) {
        this.tenY = tenY;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }
}
