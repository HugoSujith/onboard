package com.hugo.onboard.repository;

import java.util.Date;

import com.hugo.onboard.model.datavalues.historic.HistoricItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricRepository extends JpaRepository<HistoricItems, Date> {
}
