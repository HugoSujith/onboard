package com.hugo.onboard.model.datavalues.historic;

import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.List;

@Embeddable
public class HistoricEmbedded {
    @OneToMany
    private List<HistoricItems> historicItems;
    private HistoricPerformance historicPerformance;

    public HistoricEmbedded(List<HistoricItems> historicItems, HistoricPerformance historicPerformance) {
        this.historicItems = historicItems;
        this.historicPerformance = historicPerformance;
    }

    public HistoricEmbedded() {

    }

    public List<HistoricItems> getHistoricItems() {
        return historicItems;
    }

    public void setHistoricItems(List<HistoricItems> historicItems) {
        this.historicItems = historicItems;
    }

    public HistoricPerformance getHistoricPerformance() {
        return historicPerformance;
    }

    public void setHistoricPerformance(HistoricPerformance historicPerformance) {
        this.historicPerformance = historicPerformance;
    }

    @Override
    public String toString() {
        return "HistoryEmbedded{"
                + "historicItems=" + historicItems
                + ", historicPerformance=" + historicPerformance
                + '}';
    }
}
