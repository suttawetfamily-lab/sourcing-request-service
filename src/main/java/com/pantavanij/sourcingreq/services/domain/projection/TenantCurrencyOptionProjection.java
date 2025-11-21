package com.pantavanij.sourcingreq.services.domain.projection;

public interface TenantCurrencyOptionProjection {
    String getValue();
    String getCode();
    String getName();
    Integer getSequence();
    Boolean getActive();
    Boolean getIsDefault();
}
