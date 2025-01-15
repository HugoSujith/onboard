package com.hugo.onboard.model.datavalues.spot;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class SpotStructure {
    @EmbeddedId
    private SpotStructureId spotStructureId;

    public SpotStructure() {

    }

    public SpotStructure(SpotStructureId spotStructureId) {
        this.spotStructureId = spotStructureId;
    }

    public SpotStructureId getSpotStructureId() {
        return spotStructureId;
    }

    public void setSpotStructureId(SpotStructureId spotStructureId) {
        this.spotStructureId = spotStructureId;
    }
}
