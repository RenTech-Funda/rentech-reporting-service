package com.floweytech.agrotrack.reports.application.internal.outboundservices.acl;

import com.floweytech.agrotrack.reports.shared.interfaces.acl.MonitoringContextFacade;
import com.floweytech.agrotrack.reports.shared.interfaces.rest.resources.ReadingResource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * External Monitoring Service (ACL)
 * @summary
 * An outbound service acting as an Anti-Corruption Layer (ACL) between the Reports Context
 * and the Monitoring & Control Context. It is responsible for translating domain concepts
 * and fetching raw reading data needed for report generation.
 *
 * @author FloweyTech developer team
 */
@Service
public class ExternalMonitoringService {

    private final MonitoringContextFacade monitoringContextFacade;

    public ExternalMonitoringService(MonitoringContextFacade monitoringContextFacade) {
        this.monitoringContextFacade = monitoringContextFacade;
    }

    /**
     * Fetch readings for report generation
     * @summary
     * Retrieves a list of reading resources from the external monitoring context
     * based on the plot ID, metric type, and date range.
     *
     * @param plotId The ID of the plot.
     * @param readingTypeStr The type of reading (e.g., "TEMPERATURE").
     * @param start The start date/time of the period.
     * @param end The end date/time of the period.
     * @return A list of ReadingResource objects.
     */
    public List<ReadingResource> fetchReadingsForReport(Long plotId, String readingTypeStr, LocalDateTime start, LocalDateTime end) {
        try {
            return monitoringContextFacade.fetchReadingsByPlotAndTypeAndPeriod(
                    plotId,
                    readingTypeStr,
                    start,
                    end
            );
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
