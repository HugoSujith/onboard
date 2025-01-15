package com.hugo.onboard.model.datavalues.historic;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class HistoricStructure {

    @EmbeddedId
    private HistoricStructureId id;

    public HistoricStructure() {}

    public HistoricStructure(HistoricStructureId id) {
        this.id = id;
    }

    public HistoricStructureId getId() {
        return id;
    }

    public void setId(HistoricStructureId id) {
        this.id = id;
    }
}
