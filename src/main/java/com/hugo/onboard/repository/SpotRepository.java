package com.hugo.onboard.repository;

import java.util.Date;

import com.hugo.onboard.model.datavalues.spot.SpotItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotRepository extends JpaRepository<SpotItems, Date> {
}
