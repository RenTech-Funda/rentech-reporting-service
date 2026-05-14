package com.floweytech.agrotrack.reports.shared.interfaces.acl;

import com.floweytech.agrotrack.reports.shared.interfaces.rest.resources.ReadingResource;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Monitoring Context Facade Interface
 * @summary
 * Defines the contract for the Anti-Corruption Layer (ACL) of the Monitoring & Control Context.
 * It exposes specific monitoring data and capabilities to other Bounded Contexts (like Reports)
 * ensuring loose coupling by returning Resources (DTOs) instead of Domain Entities.
 *
 * @author FloweyTech developer team
 */
public interface MonitoringContextFacade {
    List<ReadingResource> fetchReadingsByPlotAndTypeAndPeriod(
            Long plotId,
            String readingType,
            LocalDateTime start,
            LocalDateTime end
    );
}