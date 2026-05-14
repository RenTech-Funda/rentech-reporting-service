package com.floweytech.agrotrack.reports.shared.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;

@Embeddable
public record PlotId(Long plotId) {
    protected PlotId() {
        this(null);
    }

    @JsonCreator
    public PlotId(@JsonProperty("plotId") Long plotId) {
        if (plotId == null || plotId < 1)
            throw new IllegalArgumentException("plotId must be greater than zero");
        this.plotId = plotId;
    }
    public Long value() { return plotId; }
}