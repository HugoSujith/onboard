package com.hugo.onboard.repository;

import com.hugo.onboard.model.datavalues.spot.SpotStructure;
import com.hugo.onboard.model.datavalues.spot.SpotStructureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends JpaRepository<SpotStructure, SpotStructureId> {
}
