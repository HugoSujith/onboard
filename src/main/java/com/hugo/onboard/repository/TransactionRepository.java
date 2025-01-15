package com.hugo.onboard.repository;

import com.hugo.onboard.model.transactions.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {
}
