package com.floweytech.agrotrack.reports.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * ReportPeriod Value Object that encapsulates the starDate and endDate for a report.
 * @param startDate
 * @param endDate
 * @author Diego Vilca
 */
public record ReportPeriod(LocalDate startDate, LocalDate endDate) {

    public ReportPeriod {
        if(startDate == null || endDate == null){
            throw new IllegalStateException("startDate and endDate cannot be null");
        }

        if(!startDate.isBefore(endDate)){
            throw new IllegalStateException("startDate must be before endDate");
        }

    }
}
