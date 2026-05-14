package com.floweytech.agrotrack.reports.application.internal.eventhandlers;

import com.floweytech.agrotrack.reports.domain.model.events.ReportCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Report Created Event Handler
 * @summary
 * This class is responsible for listening to and handling the ReportCreatedEvent.
 * Currently, it logs the key details of the created report (ID, Plot ID, and Organization ID)
 * for monitoring and auditing purposes.
 *
 * @author FloweyTech developer team
 */
@Service
public class ReportCreatedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportCreatedEventHandler.class);

    /**
     * Handle Report Created Event
     * @summary
     * Responds to the publication of a ReportCreatedEvent by logging its details.
     * This method is automatically triggered by the application event publisher
     * when a report is successfully persisted.
     *
     * @param event The ReportCreatedEvent instance containing the report's domain information.
     */
    @EventListener
    public void on(ReportCreatedEvent event) {
        LOGGER.info("Domain Event received: Report with ID {} created for Plot {} in Organization {}",
                event.getId(),
                event.getPlotId().value(),
                event.getOrganizationId().value());

    }
}
