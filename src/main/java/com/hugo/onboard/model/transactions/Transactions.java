package com.hugo.onboard.model.transactions;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class Transactions {

    @EmbeddedId
    private TransactionId id;

    private Double grams;
    private Double price;
    private String status;

    public TransactionId getId() {
        return id;
    }

    public void setId(TransactionId id) {
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
}
