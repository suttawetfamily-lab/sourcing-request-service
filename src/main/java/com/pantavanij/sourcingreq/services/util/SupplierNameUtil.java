package com.pantavanij.sourcingreq.services.util;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Supplier;
public class SupplierNameUtil {

    public static String resolveSupplierName(Supplier supplier, boolean isShowLocalLanguage, String supplierFieldName) {
        if (supplier == null || supplierFieldName == null) return "";

        if (isShowLocalLanguage) {
            if ("fullCompanyNameLocal".equalsIgnoreCase(supplierFieldName)) {
                return supplier.getFullCompanyNameLocal();
            } else if ("companyNameLocal".equalsIgnoreCase(supplierFieldName)) {
                return supplier.getCompanyNameLocal();
            }
        } else {
            if ("fullCompanyNameInter".equalsIgnoreCase(supplierFieldName)) {
                return supplier.getFullCompanyNameEN();
            } else if ("companyNameInter".equalsIgnoreCase(supplierFieldName)) {
                return supplier.getCompanyNameEN();
            }
        }
        return "";
    }
}

