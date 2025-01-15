package com.hugo.onboard.repository;

import com.hugo.onboard.model.transactions.TransactionId;
import com.hugo.onboard.model.transactions.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, TransactionId> {
}
