package com.floweytech.agrotrack.reports.domain.model.events;

import com.floweytech.agrotrack.reports.domain.model.valueobjects.ReportType;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.OrganizationId;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.PlotId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


/**
 * ReportCreatedEvent
 * Event fired when a report is successfully generated.
 */
@Getter
public class ReportCreatedEvent extends ApplicationEvent {

    private final Long id;
    private final PlotId plotId;
    private final OrganizationId organizationId;
    private final ReportType type;

    public ReportCreatedEvent(Object source, Long id, PlotId plotId, OrganizationId organizationId, ReportType type) {
        super(source);
        this.id = id;
        this.plotId = plotId;
        this.organizationId = organizationId;
        this.type = type;
    }
}
