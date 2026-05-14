package com.floweytech.agrotrack.reports.domain.services;

import com.floweytech.agrotrack.reports.domain.model.aggregates.Report;
import com.floweytech.agrotrack.reports.domain.model.queries.GetAllReportsByProfileIdQuery;
import com.floweytech.agrotrack.reports.domain.model.queries.GetReportByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * ReportQueryService
 */
public interface ReportQueryService {
    /**
     * Handle a get report by id query
     * @param query The get report by id query containing the report id
     * @return The report
     * @see GetReportByIdQuery
     *
     */
    Optional<Report> handle(GetReportByIdQuery query);

    /**
     * Handle get all reports by profile id query
     * @summary
     * Processes the query to retrieve all reports belonging to a specific user profile.
     *
     * @param query The GetAllReportsByProfileIdQuery containing the profile ID.
     * @return A list of Report entities.
     */
    List<Report> handle(GetAllReportsByProfileIdQuery query);

}
