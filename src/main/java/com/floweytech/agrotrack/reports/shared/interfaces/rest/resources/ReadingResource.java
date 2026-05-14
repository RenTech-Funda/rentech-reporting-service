package com.floweytech.agrotrack.reports.shared.interfaces.rest.resources;

import java.time.LocalDateTime;

/**
 * Reading Resource
 * @summary
 * Represents a simplified data transfer object for a single environmental reading.
 * It encapsulates the essential data of a measurement (value, unit, and timestamp)
 * and is used to share monitoring data with other contexts or layers (like Reports)
 * without exposing the full internal domain entity.
 *
 * @param value The numerical value of the reading (e.g., 25.5).
 * @param unit The unit of measurement for the value (e.g., "C", "%", "pH").
 * @param measuredAt The exact date and time when the reading was recorded.
 *
 * @author FloweyTech developer team
 */
public record ReadingResource(Double value,
                              String unit,
                              LocalDateTime measuredAt) {
}