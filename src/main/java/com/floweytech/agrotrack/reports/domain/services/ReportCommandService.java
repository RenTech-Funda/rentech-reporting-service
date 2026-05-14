package com.floweytech.agrotrack.reports.domain.services;

import com.floweytech.agrotrack.reports.domain.model.commands.CreateReportCommand;

/**
 * ReportCommandService
 * Service that handles report commands
 */
public interface ReportCommandService {

    /**
     * Handle a create report command
     * @param command
     * @return
     */
    Long handle (CreateReportCommand command);
}
