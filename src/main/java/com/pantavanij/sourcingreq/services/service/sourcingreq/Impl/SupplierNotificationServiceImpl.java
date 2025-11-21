package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.SupplierDirectoryClient;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.SupplierMailingQueue;
import com.pantavanij.sourcingreq.services.domain.request.MailingConfigRequest;
import com.pantavanij.sourcingreq.services.domain.request.NotifySupplierRequest;
import com.pantavanij.sourcingreq.services.domain.request.SupplierContactRequest;
import com.pantavanij.sourcingreq.services.domain.response.MailingConfigResponse;
import com.pantavanij.sourcingreq.services.domain.response.SupplierContactResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BadRequestException;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SupplierMailingQueueRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SupplierNotificationService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.EPAuthService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.pantavanij.sourcingreq.services.enums.SupplierMailingQueueStatus.SUPPLIER_MAILING_QUEUE_AWAITING;

@Service
@RequiredArgsConstructor
public class SupplierNotificationServiceImpl implements SupplierNotificationService {

    private final EPAuthService epAuthService;

    private final SupplierDirectoryClient supplierDirectoryClient;

    private final SupplierMailingQueueRepository supplierMailingQueueRepository;

    @Override
    public boolean notifySupplier(NotifySupplierRequest request, boolean validateRequired) {

        boolean emailNotify = false;

        if(validateRequired) {
            if (request.getTenant().isEmpty()) {
                throw new BadRequestException(ApiMessage.E1002, "tenant is missing");
            } else if (request.getOrgName().isEmpty()) {
                throw new BadRequestException(ApiMessage.E1002, "orgName is missing");
            } else if (request.getSrName().isEmpty()) {
                throw new BadRequestException(ApiMessage.E1002, "srName is missing");
            } else if (request.getOpCat().isEmpty()) {
                throw new BadRequestException(ApiMessage.E1002, "opCat is missing");
            }
        }

        MailingConfigRequest mailingConfigRequest = new MailingConfigRequest();
        mailingConfigRequest.setTenant(request.getTenant());
        mailingConfigRequest.setOpCat(request.getOpCat());
        mailingConfigRequest.setOpSubCat(request.getOpSubCat());
        MailingConfigResponse mailingConfigResponse = epAuthService.getSRMailingConfig(mailingConfigRequest);

        if(mailingConfigResponse != null) {
            emailNotify = mailingConfigResponse.isSrNotifySupplier();
            if(emailNotify) {


                // Insert new record in Supplier Mailing Queue
                SupplierMailingQueue supplierMailingQueue = new SupplierMailingQueue();
                supplierMailingQueue.setTenant(request.getTenant());
                supplierMailingQueue.setOrgName(request.getOrgName());
                supplierMailingQueue.setCategory(request.getOpCat());
                supplierMailingQueue.setSubCategory(request.getOpSubCat());
                supplierMailingQueue.setRequestName(request.getSrName());

                try {
                    if(DateTimeUtil.isValidDateString(request.getSrSubmitDate(), "yyyy-MM-dd HH:mm:ss")) {
                        String strSubmitDate = request.getSrSubmitDate();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        Date submitDate = null;
                        submitDate = formatter.parse(strSubmitDate);
                        supplierMailingQueue.setSubmitDate(DateTimeUtil.getTimestampUTC(submitDate));
                    } else {
                        throw new BadRequestException(ApiMessage.E1002, "srSubmitDate is invalid format");
                    }

                } catch (ParseException e) {
                    throw new BadRequestException(ApiMessage.E1002, "srSubmitDate is invalid format");
                }

                supplierMailingQueue.setCatLevel1Id(Integer.parseInt(mailingConfigResponse.getScCatLevel1()));
                supplierMailingQueue.setCatLevel2Id(Integer.parseInt(mailingConfigResponse.getScCatLevel2()));
                supplierMailingQueue.setCatLevel3Id(Integer.parseInt(mailingConfigResponse.getScCatLevel3()));
                supplierMailingQueue.setNumberOfContact(0);
                supplierMailingQueue.setStatus(SUPPLIER_MAILING_QUEUE_AWAITING.code());
                supplierMailingQueue.setCreatedBy(AppUtil.getUserName());
                supplierMailingQueue.setCreatedDate(DateTimeUtil.getTimestampUTC());

                supplierMailingQueueRepository.save(supplierMailingQueue);
            }
        }

        return emailNotify;
    }

    @Override
    public SupplierContactResponse getSupplierContact(SupplierContactRequest request) {
        return  supplierDirectoryClient.searchSupplierContact(request.getPageNo(),
                request.getPageSize(),
                request.getCatLevel1Id(),
                request.getCatLevel2Id(),
                request.getCatLevel3Id(),
                request.getRuleName());
    }

}
