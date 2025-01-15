package com.hugo.onboard.model.transactions;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class TransactionId implements Serializable {
    private String username;
    private Date datePurchased;

    public TransactionId() {}

    public TransactionId(String username, Date datePurchased) {
        this.username = username;
        this.datePurchased = datePurchased;
    }

    // Getters and setters

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

    // Override equals and hashCode for proper comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionId that = (TransactionId) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(datePurchased, that.datePurchased);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, datePurchased);
    }
}
