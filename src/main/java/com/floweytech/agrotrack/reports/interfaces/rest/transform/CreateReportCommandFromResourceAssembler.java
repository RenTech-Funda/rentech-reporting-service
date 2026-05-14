package com.floweytech.agrotrack.reports.interfaces.rest.transform;

import com.floweytech.agrotrack.reports.domain.model.commands.CreateReportCommand;
import com.floweytech.agrotrack.reports.domain.model.valueobjects.MetricType;
import com.floweytech.agrotrack.reports.domain.model.valueobjects.ReportType;
import com.floweytech.agrotrack.reports.interfaces.rest.resources.CreateReportResource;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.OrganizationId;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.ProfileId;

public class CreateReportCommandFromResourceAssembler {

    public static CreateReportCommand toCommandFromResource(
            CreateReportResource resource,
            Long organizationId,
            Long plotId,
            Long profileId) {
        return new CreateReportCommand(
                new ProfileId(profileId),
                new PlotId(plotId),
                new OrganizationId(organizationId),
                ReportType.valueOf(resource.type()),
                MetricType.valueOf(resource.metricType()),
                resource.periodStart(),
                resource.periodEnd()
        );
    }
}