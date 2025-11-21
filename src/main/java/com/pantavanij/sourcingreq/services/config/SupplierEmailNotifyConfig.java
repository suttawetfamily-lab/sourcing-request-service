package com.pantavanij.sourcingreq.services.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SupplierEmailNotifyConfig {

    @Value("${supplier.notify.email.template.id}")
    private String supplierNotifyEmailTemplateId;

    @Value("${supplier.notify.email.logo.url}")
    private String supplierNotifyEmailLogoUrl;

}