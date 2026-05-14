package com.floweytech.agrotrack.reports.shared.interfaces.acl;

import com.floweytech.agrotrack.reports.shared.interfaces.rest.resources.ReadingResource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class MonitoringContextFacadeImpl implements MonitoringContextFacade {

    @Override
    public List<ReadingResource> fetchReadingsByPlotAndTypeAndPeriod(
            Long plotId,
            String readingType,
            LocalDateTime start,
            LocalDateTime end) {
        return Collections.emptyList();
    }
}