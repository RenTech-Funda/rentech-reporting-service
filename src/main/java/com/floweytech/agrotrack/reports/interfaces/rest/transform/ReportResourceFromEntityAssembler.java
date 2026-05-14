package com.floweytech.agrotrack.reports.interfaces.rest.transform;

import com.floweytech.agrotrack.reports.domain.model.aggregates.Report;
import com.floweytech.agrotrack.reports.interfaces.rest.resources.ReportResource;

public class ReportResourceFromEntityAssembler {

    public static ReportResource toResourceFromEntity(Report entity) {
        return new ReportResource(
                entity.getId(),
                entity.getStatus().name(),
                entity.getPlotId().value(),
                entity.getOrganizationId().value(),
                entity.getType().name(),
                entity.getMetricType().name(),
                entity.getMetrics(),
                entity.getReportPeriod().startDate(),
                entity.getReportPeriod().endDate(),
                entity.getGeneratedAt()
        );
    }
}