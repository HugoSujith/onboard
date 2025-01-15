package com.hugo.onboard.repository;

import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<PulsarProperties.Transaction, Long> {
}
