package com.hugo.onboard.model.datavalues.spot;

import java.util.List;

import com.hugo.onboard.model.datavalues.historic.HistoricItems;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

@Embeddable
public class SpotEmbedded {
    @OneToMany
    private List<SpotItems> spotItems;
    private int marketsClosed;
    private HistoricItems historicItems;

    public SpotEmbedded(List<SpotItems> spotItems, int marketsClosed, HistoricItems historicItems) {
        this.spotItems = spotItems;
        this.marketsClosed = marketsClosed;
        this.historicItems = historicItems;
    }

    public SpotEmbedded() {

    }

    public List<SpotItems> getSpotItems() {
        return spotItems;
    }

    public void setSpotItems(List<SpotItems> spotItems) {
        this.spotItems = spotItems;
    }

    public int getMarketsClosed() {
        return marketsClosed;
    }

    public void setMarketsClosed(int marketsClosed) {
        this.marketsClosed = marketsClosed;
    }

    public HistoricItems getHistoricData() {
        return historicItems;
    }

    public void setHistoricData(HistoricItems historicItems) {
        this.historicItems = historicItems;
    }

    @Override
    public String toString() {
        return "SpotEmbedded{"
                + "spotItems=" + spotItems
                + ", marketsClosed=" + marketsClosed
                + ", historicData=" + historicItems
                + '}';
    }
}
