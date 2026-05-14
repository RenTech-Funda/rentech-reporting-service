package com.floweytech.agrotrack.reports.application.internal.outboundservices.acl;

import com.floweytech.agrotrack.reports.shared.domain.model.valueobjects.PlotId;
import com.floweytech.agrotrack.reports.shared.interfaces.acl.OrganizationContextFacade;
import org.springframework.stereotype.Service;

/**
 * External Organization Service
 * @summary
 * An outbound service acting as an Anti-Corruption Layer (ACL) adapter for the Organization Context.
 * It isolates the Reports domain from the external Organization context, handling the translation
 * of local Value Objects (like PlotId) into primitive types required by the external facade.
 *
 * @author FloweyTech developer team
 */
@Service
public class ExternalOrganizationService {

    private final OrganizationContextFacade organizationContextFacade;

    /**
     * Constructor
     * @summary
     * Initializes the service injecting the Organization Context Facade.
     *
     * @param organizationContextFacade The facade interface for the Organization context.
     */
    public ExternalOrganizationService(OrganizationContextFacade organizationContextFacade) {
        this.organizationContextFacade = organizationContextFacade;
    }

    /**
     * Verify Plot Existence
     * @summary
     * Checks if a specific plot exists in the external Organization context.
     * It unwraps the domain-specific PlotId value object to communicate with the external interface.
     *
     * @param plotId The PlotId value object to verify.
     * @return true if the plot exists externally, false otherwise.
     */
    public boolean fetchPlotExists(PlotId plotId) {
        return organizationContextFacade.existsPlotById(plotId.value());
    }
}
