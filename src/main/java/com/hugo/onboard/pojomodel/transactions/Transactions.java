package com.hugo.onboard.pojomodel.transactions;

import java.util.Date;

public class Transactions {
    private Long id;
    private String username;
    private Date datePurchased;
    private Double grams;
    private Double price;
    private String status;

    public Transactions() {

    }

    public Transactions(String username, Date datePurchased, Double grams, Double price, String status) {
        this.username = username;
        this.datePurchased = datePurchased;
        this.grams = grams;
        this.price = price;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getGrams() {
        return grams;
    }

    public void setGrams(Double grams) {
        this.grams = grams;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDatePurchased() {
        return datePurchased;
    }

    public void setDatePurchased(Date datePurchased) {
        this.datePurchased = datePurchased;
    }

}
