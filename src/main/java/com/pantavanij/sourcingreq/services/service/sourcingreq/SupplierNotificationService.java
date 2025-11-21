package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.request.NotifySupplierRequest;
import com.pantavanij.sourcingreq.services.domain.request.SupplierContactRequest;
import com.pantavanij.sourcingreq.services.domain.response.SupplierContactResponse;

public interface SupplierNotificationService {
    boolean notifySupplier(NotifySupplierRequest request, boolean validateRequired);
    SupplierContactResponse getSupplierContact(SupplierContactRequest request);
}
