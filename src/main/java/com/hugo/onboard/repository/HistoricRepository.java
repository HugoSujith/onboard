package com.hugo.onboard.repository;

import com.hugo.onboard.model.datavalues.historic.HistoricStructure;
import com.hugo.onboard.model.datavalues.historic.HistoricStructureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricRepository extends JpaRepository<HistoricStructure, HistoricStructureId> {
}
