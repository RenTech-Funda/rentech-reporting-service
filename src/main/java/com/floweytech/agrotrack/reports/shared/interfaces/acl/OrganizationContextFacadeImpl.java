package com.floweytech.agrotrack.reports.shared.interfaces.acl;

import org.springframework.stereotype.Service;

@Service
public class OrganizationContextFacadeImpl implements OrganizationContextFacade {

    @Override
    public boolean existsPlotById(Long plotId) {
        return true;
    }
}