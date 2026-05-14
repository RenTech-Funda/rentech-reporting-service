package com.floweytech.agrotrack.reports.domain.model.queries;

/**
 * Query to get a report by id
 * @param reportId
 */
public record GetReportByIdQuery(Long reportId) {
    /**
     * Constructor.
     * @param reportId Report id
     *                 Must be greater than 0.
     *                 Must not be null
     * @throws IllegalArgumentException if reportId is null or negative
     */

    public GetReportByIdQuery {
        if(reportId == null || reportId <= 0 )
            throw new IllegalArgumentException("Report id cannot be null or negative");
    }
}
