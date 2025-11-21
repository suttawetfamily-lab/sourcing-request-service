package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.google.gson.Gson;
import com.pantavanij.sourcingreq.services.client.DelegateClient;
import com.pantavanij.sourcingreq.services.client.EmailClient;
import com.pantavanij.sourcingreq.services.config.SupplierEmailNotifyConfig;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.approver.ForwardedApprover;
import com.pantavanij.sourcingreq.services.domain.dto.reviewer.RequestReportLineDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.DelegateeByDelegatorResponse;
import com.pantavanij.sourcingreq.services.domain.response.DelegationActiveResponse;
import com.pantavanij.sourcingreq.services.domain.response.EmailResponse;
import com.pantavanij.sourcingreq.services.domain.response.UserDetailResponse;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.enums.EmailActivity;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.*;
import static com.pantavanij.sourcingreq.services.enums.EmailActivity.*;
import static com.pantavanij.sourcingreq.services.util.CommonUtils.distinctByKey;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final EmailClient emailClient;
    private final DelegateClient delegateClient;
    private final UaaService uaaService;
    private final RequestReviewerService requestReviewerService;
    private final WorkflowInstanceApprovalService workflowInstanceApprovalService;
    private final TenantService tenantService;
    private final TenantConfigService tenantConfigService;
    private final TenantEmailActivityService tenantEmailActivityService;
    private final RequestPurchaserService requestPurchaserService;
    private final RequestReportLineService requestReportLineService;
    private final RequestForwarderService requestForwarderService;
    private final RequestDeptApproverService requestDeptApproverService;
    private final RequestRepository requestRepository;
    private final SupplierEmailNotifyConfig supplierEmailNotifyConfig;
    private final ExcSourcingDeptApproverService excSourcingDeptApproverService;
    private final ExcSourcingPurchaserService excSourcingPurchaserService;


    @Override
    public List<EmailResponse> sendEMailNotification(SendEMailRequest sendEMailRequest, RequestDto requestDto) {
        List<EmailResponse> responseList = new ArrayList<>();
        try {
            log.info("sendEMailNotification requestDto: {}", requestDto);
            Map<String, Object> mappingData = new HashMap<>();
            String requestNo = requestDto.getRequestNo();
            mappingData.put("requestNo", requestNo);
            mappingData.put("requestName", requestDto.getRequestName());

            String tenantCode = AppUtil.getTenantId();
            String idp = AppUtil.getIdp();
            Tenant tenant = tenantService.findByCode(tenantCode);
            Integer tenantId = tenant.getRecId();
            String username = AppUtil.getUserName();

            mappingData.put("organizationName", requestDto.getOrganizationObj().getLabel());

            String timezone = getTimeZoneUser();
            String expectedDateString = getDateString(requestDto.getExpectedDate(), timezone);
            mappingData.put("expectedDate", expectedDateString);

            String requestedDateString = getDateString(requestDto.getRequestDate(), timezone);
            mappingData.put("requestedDate", requestedDateString);

            log.info("sendEMailNotification getEmailRequest: {}", sendEMailRequest);
            EmailContractDetailDto emailContractDetailDto = retrieveEmailNotification(sendEMailRequest.getEmailActivity(), requestDto);

            // mock for test api using postman
            if (sendEMailRequest.getEmailContractDetailDto() != null) {
                emailContractDetailDto = sendEMailRequest.getEmailContractDetailDto();
            }

            ContractDetailClientDto requesterDetail = emailContractDetailDto.getRequester();

            StringBuilder requesterName = new StringBuilder();
            if (requesterDetail != null) {
                if (requesterDetail.getFirstName() != null) {
                    requesterName.append(requesterDetail.getFirstName());
                }

                if (requesterDetail.getFirstName() != null && requesterDetail.getLastName() != null) {
                    requesterName.append(" ");
                    requesterName.append(requesterDetail.getLastName());
                }

                mappingData.put("requesterName", requesterName.toString());
                mappingData.put("requesterEmail", requesterDetail.getEmail());
                mappingData.put("requesterPhone", requesterDetail.getPhone());
            }

            log.info("sendEMailNotification : {}", requesterDetail);
            String logoOpn = tenantConfigService.getEmailImageByName("EBO_LOGO", tenantId);

            String imageHost = tenantConfigService.getEmailImageUrl(tenantId);
            String imageOpnUrl = String.format(imageHost, logoOpn);
            mappingData.put("logoOpn", "<img src=\""+imageOpnUrl+"\" width=\"216\" />");

            String logoPhone = tenantConfigService.getEmailImageByName("EBO_PHONE", tenantId);
            String imagePhoneUrl = String.format(imageHost, logoPhone);
            mappingData.put("logoPhone", "<img src=\""+imagePhoneUrl+"\" align=\"top\" style=\"margin:0 5px 0 0;\" width=\"20\" height=\"20\" />");

            String loginEPUrl = tenantConfigService.getEmailLoginUrlEPByName("LOGIN_EP_URL", tenantId);
            mappingData.put("loginUrl", "<a href=\""+loginEPUrl+"\" style=\"color: #009EFB;\">"+loginEPUrl+"</a>");

            EMailConfigDto eMailConfigDto = new EMailConfigDto();
            String mailFrom = tenantConfigService.getEmailAddressDefault(tenant.getRecId());
            eMailConfigDto.setMailFrom(mailFrom);

            List<ContractDetailClientDto> contractDtoList = new ArrayList<>();
            Long templateId = 0L;

            switch (sendEMailRequest.getEmailActivity()) {
                case PLEASE_REVIEW_AND_APPROVE_SR:
                    templateId = tenantConfigService.getEmailTemplateIdByName(PLEASE_REVIEW_AND_APPROVE_SR.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Please review and approve the sourcing request (Request No. " + requestNo + ")");

                    if (sendEMailRequest.getActivity().equals(ACTIVITY_SUBMIT)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) PLEASE_REVIEW_AND_APPROVE_SR.id(), ACTIVITY_SUBMIT.id());
                        if (!tenantConfigService.isEnableDeptApprover(tenantId)) {
                            if (emailContractDetailDto.getPurchaserList() != null && sendMailToRoleDto.isPurchaser()) {
                                contractDtoList.addAll(emailContractDetailDto.getPurchaserList());
                            }
                        } else if (sendMailToRoleDto.isApprover()){
                            // Add current dept-approver into contractDtoList.
                            contractDtoList.addAll(this.getCurrentAwaitingDeptApprover(requestDto.getRecId(), tenant.getCode(), idp));
                        }

                    } else if (sendEMailRequest.getActivity().equals(ACTIVITY_APPROVED)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) PLEASE_REVIEW_AND_APPROVE_SR.id(), ACTIVITY_APPROVED.id());
                        if (!tenantConfigService.isEnableDeptApprover(tenantId)) {
                            if (emailContractDetailDto.getPurchaserList() != null && sendMailToRoleDto.isPurchaser()) {
                                contractDtoList.addAll(emailContractDetailDto.getPurchaserList());
                            }
                        } else {
                            if (!requestDeptApproverService.isLastDeptApprover(requestDto.getRecId()) && sendMailToRoleDto.isApprover()) {
                                // Add current dept-approver into contractDtoList.
                                contractDtoList.addAll(this.getCurrentAwaitingDeptApprover(requestDto.getRecId(), tenant.getCode(), idp));
                            } else {
                                if (emailContractDetailDto.getPurchaserList() != null && sendMailToRoleDto.isPurchaser()) {
                                    contractDtoList.addAll(emailContractDetailDto.getPurchaserList());
                                }
                            }
                        }
                    }
                    log.info("sendEMailNotification [REQUEST_DTO]: {}", requestDto);
                    log.info("sendEMailNotification [PLEASE_REVIEW_AND_APPROVE_SR]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendEMailNotification [SEND_MAIL: PLEASE_REVIEW_AND_APPROVE_SR]: {}", responseList);
                    break;

                case PLEASE_REVIEW_SR:
                    templateId = tenantConfigService.getEmailTemplateIdByName(PLEASE_REVIEW_SR.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Please review the sourcing request (Request No. " + requestNo + ")");

                    if (sendEMailRequest.getActivity().equals(ACTIVITY_SUBMIT)) {
                        handlerContact(PLEASE_REVIEW_SR.id(), ACTIVITY_SUBMIT.id(), contractDtoList, emailContractDetailDto);
                    }
                    log.info("sendEMailNotification [REQUEST_DTO]: {}", requestDto);
                    log.info("sendEMailNotification [PLEASE_REVIEW_SR]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendEMailNotification [SEND_MAIL: PLEASE_REVIEW_SR]: {}", responseList);
                    break;

                case SR_HAS_BEEN_REJECTED:
                    templateId = tenantConfigService.getEmailTemplateIdByName(SR_HAS_BEEN_REJECTED.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Sourcing Request has been Rejected (Request No. " + requestNo + ")");

                    if (sendEMailRequest.getActivity().equals(ACTIVITY_REJECT)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) SR_HAS_BEEN_REJECTED.id(), ACTIVITY_REJECT.id());
                        handlerContact(SR_HAS_BEEN_REJECTED.id(), ACTIVITY_REJECT.id(), contractDtoList, emailContractDetailDto);
                        if (sendMailToRoleDto.isApprover()) {
                            // Add current dept-approver into contractDtoList.
                            contractDtoList.addAll(this.getAllApprovedDeptApprove(requestDto.getRecId(), tenant.getCode(), idp));
                        }
                    } else if(sendEMailRequest.getActivity().equals(ACTIVITY_REJECTED)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) SR_HAS_BEEN_REJECTED.id(), ACTIVITY_REJECTED.id());
                        handlerContact(SR_HAS_BEEN_REJECTED.id(), ACTIVITY_REJECTED.id(), contractDtoList, emailContractDetailDto);
                        if (sendMailToRoleDto.isApprover()) {
                             // Add current dept-approver into contractDtoList.
                            contractDtoList.addAll(this.getAllApprovedDeptApprove(requestDto.getRecId(), tenant.getCode(), idp));
                        }
                    }
                    log.info("sendEMailNotification [REQUEST_DTO]: {}", requestDto);
                    log.info("sendEMailNotification [SR_HAS_BEEN_REJECTED]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendEMailNotification [SEND_MAIL: SR_HAS_BEEN_REJECTED]: {}", responseList);
                    break;

                case SR_HAS_BEEN_CANCELLED:
                    templateId = tenantConfigService.getEmailTemplateIdByName(SR_HAS_BEEN_CANCELLED.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Sourcing Request has been Cancelled (Request No. " + requestNo + ")");

                    if (sendEMailRequest.getActivity().equals(ACTIVITY_CANCEL)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) SR_HAS_BEEN_CANCELLED.id(), ACTIVITY_CANCEL.id());
                        handlerContact(SR_HAS_BEEN_CANCELLED.id(), ACTIVITY_CANCEL.id(), contractDtoList, emailContractDetailDto);
                        if (sendMailToRoleDto.isApprover()) {
                            // Add current dept-approver into contractDtoList.
                            contractDtoList.addAll(this.get1StCancelledDeptApprover(requestDto.getRecId(), tenant.getCode(), idp));
                        }
                    }
                    log.info("sendEMailNotification [REQUEST_DTO]: {}", requestDto);
                    log.info("sendEMailNotification [SR_HAS_BEEN_CANCELLED]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendEMailNotification [SEND_MAIL: SR_HAS_BEEN_CANCELLED]: {}", responseList);
                    break;

                case SR_HAS_BEEN_APPROVED:
                    templateId = tenantConfigService.getEmailTemplateIdByName(SR_HAS_BEEN_APPROVED.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Sourcing Request has been Approved (Request No. " + requestNo + ")");

                    if (sendEMailRequest.getActivity().equals(ACTIVITY_APPROVED)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) SR_HAS_BEEN_APPROVED.id(), ACTIVITY_APPROVED.id());
                        if (emailContractDetailDto.getRequester() != null && sendMailToRoleDto.isRequester()) {
                            contractDtoList.add(emailContractDetailDto.getRequester());
                        }
                        if (requestDeptApproverService.isLastDeptApprover(requestDto.getRecId())) {
                            if (emailContractDetailDto.getReviewerList() != null && sendMailToRoleDto.isReviewer()) {
                                contractDtoList.addAll(emailContractDetailDto.getReviewerList());
                            }
                        }
                    } else if(sendEMailRequest.getActivity().equals(ACTIVITY_CONFIRM)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) SR_HAS_BEEN_APPROVED.id(), ACTIVITY_CONFIRM.id());
                        handlerContact(SR_HAS_BEEN_APPROVED.id(), ACTIVITY_CONFIRM.id(), contractDtoList, emailContractDetailDto);
                        if (sendMailToRoleDto.isApprover()) {
                            // Add current dept-approver into contractDtoList.
                            contractDtoList.addAll(this.getAllApprovedDeptApprove(requestDto.getRecId(), tenant.getCode(), idp));
                        }
                    }
                    log.info("sendEMailNotification [REQUEST_DTO]: {}", requestDto);
                    log.info("sendEMailNotification [SR_HAS_BEEN_APPROVED]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendEMailNotification [SEND_MAIL: SR_HAS_BEEN_APPROVED]: {}", responseList);
                    break;

                case SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_FORWARD_REQUEST_REQUESTER:
                    templateId = tenantConfigService.getEmailTemplateIdByName(SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_FORWARD_REQUEST_REQUESTER.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Sourcing Request has been Forwarded (Request No. " + requestNo + ")");

                    if (sendEMailRequest.getActivity().equals(ACTIVITY_FORWARD)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_FORWARD_REQUEST_REQUESTER.id(), ACTIVITY_FORWARD.id());
                        if (emailContractDetailDto.getRequester() != null && sendMailToRoleDto.isRequester()) {
                            contractDtoList.add(emailContractDetailDto.getRequester());
                        }
                        if (sendMailToRoleDto.isPurchaser()) {
                            contractDtoList.addAll(getForwardedPurchaserEmail(requestDto, tenant, idp));
                        }
                    }

                    log.info("sendEMailNotification [REQUEST_DTO]: {}", requestDto);
                    log.info("sendEMailNotification [SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_FORWARD_REQUEST_REQUESTER]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendEMailNotification [SEND_MAIL: SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_FORWARD_REQUEST_REQUESTER]: {}", responseList);
                    break;

                case SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_RECEIVED_FORWARDED_REQUEST:
                    templateId = tenantConfigService.getEmailTemplateIdByName(SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_RECEIVED_FORWARDED_REQUEST.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Sourcing Request has been Forwarded (Request No. " + requestNo + ")");

                    if (sendEMailRequest.getActivity().equals(ACTIVITY_FORWARD)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_FORWARD_REQUEST_REQUESTER.id(), ACTIVITY_FORWARD.id());
                        if (sendMailToRoleDto.isPurchaser()) {
                            contractDtoList.add(getReceiveForwardedPurchaserEmail(requestDto, tenant, idp));
                        }
                    }

                    log.info("sendEMailNotification [REQUEST_DTO]: {}", requestDto);
                    log.info("sendEMailNotification [SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_RECEIVED_FORWARDED_REQUEST]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendEMailNotification [SEND_MAIL: SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_RECEIVED_FORWARDED_REQUEST]: {}", responseList);
                    break;

                case SR_CREATE_ORACLE_PR_FAILED:
                    templateId = tenantConfigService.getEmailTemplateIdByName(SR_CREATE_ORACLE_PR_FAILED.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Sourcing Request has been Rejected (Request No. " + requestNo + ")");

                    if(sendEMailRequest.getActivity().equals(ACTIVITY_COPY_TO_PR_FAILED)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) SR_CREATE_ORACLE_PR_FAILED.id(), ACTIVITY_COPY_TO_PR_FAILED.id());
                        handlerContact(SR_CREATE_ORACLE_PR_FAILED.id(), ACTIVITY_COPY_TO_PR_FAILED.id(), contractDtoList, emailContractDetailDto);
                        if (emailContractDetailDto.getPurchaserList() != null && sendMailToRoleDto.isPurchaser()) {
                            // Add current purchaser into contractDtoList.
                            contractDtoList.addAll(emailContractDetailDto.getPurchaserList());
                        }
                        if (emailContractDetailDto.getRequester() != null && sendMailToRoleDto.isRequester()) {
                            // Add current requester into contractDtoList.
                            contractDtoList.add(emailContractDetailDto.getRequester());
                        }
                    }
                    log.info("sendExcSourcingEMailNotification [REQUEST_DTO]: {}", requestDto);
                    log.info("sendExcSourcingEMailNotification [SR_CREATE_ORACLE_PR_FAILED]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendExcSourcingEMailNotification [SEND_MAIL: SR_CREATE_ORACLE_PR_FAILED]: {}", responseList);
                    break;

                case SR_CREATE_ORACLE_PR_SUCCESSFULLY:
                    templateId = tenantConfigService.getEmailTemplateIdByName(SR_CREATE_ORACLE_PR_SUCCESSFULLY.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Sourcing Request has been Rejected (Request No. " + requestNo + ")");

                    if(sendEMailRequest.getActivity().equals(ACTIVITY_COPY_TO_PR)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) SR_CREATE_ORACLE_PR_SUCCESSFULLY.id(), ACTIVITY_COPY_TO_PR.id());
                        handlerContact(SR_CREATE_ORACLE_PR_SUCCESSFULLY.id(), ACTIVITY_COPY_TO_PR.id(), contractDtoList, emailContractDetailDto);
                        if (emailContractDetailDto.getPurchaserList() != null && sendMailToRoleDto.isPurchaser()) {
                            // Add current purchaser into contractDtoList.
                            contractDtoList.addAll(emailContractDetailDto.getPurchaserList());
                        }
                        if (emailContractDetailDto.getRequester() != null && sendMailToRoleDto.isRequester()) {
                            // Add current requester into contractDtoList.
                            contractDtoList.add(emailContractDetailDto.getRequester());
                        }
                    }
                    log.info("sendExcSourcingEMailNotification [REQUEST_DTO]: {}", requestDto);
                    log.info("sendExcSourcingEMailNotification [SR_CREATE_ORACLE_PR_SUCCESSFULLY]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendExcSourcingEMailNotification [SEND_MAIL: SR_CREATE_ORACLE_PR_SUCCESSFULLY]: {}", responseList);
                    break;

//                case DELEGATION_HAS_BEEN_CREATED_FOR_WHO_DELEGATE:
//                    templateId = tenantConfigService.getEmailTemplateIdByName(DELEGATION_HAS_BEEN_CREATED_FOR_WHO_DELEGATE.toString(), tenantId);
//                    eMailConfigDto.setTemplateId(templateId);
//                    eMailConfigDto.setMailSubject("The delegation has been submitted.");
//
//                    if (emailContractDetailDto.getPurchaserList() != null) {
//                        contractDtos.addAll(emailContractDetailDto.getPurchaserList());
//                    }
//                    log.info("sendEMailNotification [REQUEST_DTO]: {}", requestDto);
//                    log.info("sendEMailNotification [DELEGATION_HAS_BEEN_CREATED_FOR_WHO_DELEGATE]: {}", contractDtos);
//                    responseList = sendEmail(contractDtos, mappingData, eMailConfigDto);
//                    log.info("sendEMailNotification [SEND_MAIL: DELEGATION_HAS_BEEN_CREATED_FOR_WHO_DELEGATE]: {}", responseList);
//                    break;
//
//                case DELEGATION_HAS_BEEN_CREATED_FOR_WHO_RECEIVED_DELEGATE:
//                    templateId = tenantConfigService.getEmailTemplateIdByName(DELEGATION_HAS_BEEN_CREATED_FOR_WHO_RECEIVED_DELEGATE.toString(), tenantId);
//                    eMailConfigDto.setTemplateId(templateId);
//                    String delegationPeriod = "01/02/2023 - 05/02/2023";
//                    eMailConfigDto.setMailSubject("You are delegated on "+ delegationPeriod +".");
//
//                    if (emailContractDetailDto.getPurchaserList() != null) {
//                        contractDtos.addAll(emailContractDetailDto.getPurchaserList());
//                    }
//                    log.info("sendEMailNotification [REQUEST_DTO]: {}", requestDto);
//                    log.info("sendEMailNotification [DELEGATION_HAS_BEEN_CREATED_FOR_WHO_RECEIVED_DELEGATE]: {}", contractDtos);
//                    responseList = sendEmail(contractDtos, mappingData, eMailConfigDto);
//                    log.info("sendEMailNotification [SEND_MAIL: DELEGATION_HAS_BEEN_CREATED_FOR_WHO_RECEIVED_DELEGATE]: {}", responseList);
//                    break;
//
//                case DELEGATION_HAS_BEEN_CANCELLED:
//                    templateId = tenantConfigService.getEmailTemplateIdByName(DELEGATION_HAS_BEEN_CANCELLED.toString(), tenantId);
//                    eMailConfigDto.setTemplateId(templateId);
//                    eMailConfigDto.setMailSubject("The delegation has been cancelled");
//
//                    if (emailContractDetailDto.getPurchaserList() != null) {
//                        contractDtos.addAll(emailContractDetailDto.getPurchaserList());
//                    }
//                    log.info("sendEMailNotification [REQUEST_DTO]: {}", requestDto);
//                    log.info("sendEMailNotification [DELEGATION_HAS_BEEN_CANCELLED]: {}", contractDtos);
//                    responseList = sendEmail(contractDtos, mappingData, eMailConfigDto);
//                    log.info("sendEMailNotification [SEND_MAIL: DELEGATION_HAS_BEEN_CANCELLED]: {}", responseList);
//                    break;

            }
            return responseList;
        } catch (Exception ex) {
            log.info("sendEMailNotification Failed: {}", ex.getMessage());
            EmailResponse emailResponse = new EmailResponse(true, ex.getMessage(), null);
            return List.of(emailResponse);
        }
    }

    @Override
    public List<EmailResponse> sendExcSourcingEMailNotification(SendExcSourcingEMailRequest sendExcSourcingEMailRequest, RequestDto requestDto, ExcSourcingDto excSourcingDto) {
        List<EmailResponse> responseList = new ArrayList<>();
        try {
            log.info("sendExcSourcingEMailNotification requestDto: {}", requestDto);
            Map<String, Object> mappingData = new HashMap<>();
            String requestNo = requestDto.getRequestNo();
            mappingData.put("requestNo", requestNo);
            mappingData.put("requestName", requestDto.getRequestName());
            mappingData.put("sourcingDocNo", excSourcingDto.getExcSourcingDocNo());
            mappingData.put("sourcingType", "Exceptional Sourcing");

            String tenantCode = AppUtil.getTenantId();
            String idp = AppUtil.getIdp();
            Tenant tenant = tenantService.findByCode(tenantCode);
            Integer tenantId = tenant.getRecId();
            String username = AppUtil.getUserName();

            mappingData.put("organizationName", requestDto.getOrganizationObj().getLabel());

            String timezone = getTimeZoneUser();
            String expectedDateString = getDateString(requestDto.getExpectedDate(), timezone);
            mappingData.put("expectedDate", expectedDateString);

            String requestedDateString = getDateString(requestDto.getRequestDate(), timezone);
            mappingData.put("requestedDate", requestedDateString);

            log.info("sendExcSourcingEMailNotification getEmailRequest: {}", sendExcSourcingEMailRequest);
            EmailExcSourcingContractDetailDto emailExcSourcingContractDetailDto = retrieveExcSourcingEmailNotification(sendExcSourcingEMailRequest.getEmailActivity(), requestDto, excSourcingDto);

            // mock for test api using postman
            if (sendExcSourcingEMailRequest.getEmailExcSourcingContractDetailDto() != null) {
                emailExcSourcingContractDetailDto = sendExcSourcingEMailRequest.getEmailExcSourcingContractDetailDto();
            }

            ContractDetailClientDto purchaserDetail = emailExcSourcingContractDetailDto.getPurchaser();

            StringBuilder purchaserName = new StringBuilder();
            if (purchaserDetail != null) {
                if (purchaserDetail.getFirstName() != null) {
                    purchaserName.append(purchaserDetail.getFirstName());
                }

                if (purchaserDetail.getFirstName() != null && purchaserDetail.getLastName() != null) {
                    purchaserName.append(" ");
                    purchaserName.append(purchaserDetail.getLastName());
                }

                mappingData.put("purchaserName", purchaserName.toString());
                mappingData.put("purchaserEmail", purchaserDetail.getEmail());
                mappingData.put("purchaserPhone", purchaserDetail.getPhone());
            }

            log.info("sendExcSourcingEMailNotification : {}", purchaserDetail);
            String logoOpn = tenantConfigService.getEmailImageByName("EBO_LOGO", tenantId);

            String imageHost = tenantConfigService.getEmailImageUrl(tenantId);
            String imageOpnUrl = String.format(imageHost, logoOpn);
            mappingData.put("logoOpn", "<img src=\""+imageOpnUrl+"\" width=\"216\" />");

            String logoPhone = tenantConfigService.getEmailImageByName("EBO_PHONE", tenantId);
            String imagePhoneUrl = String.format(imageHost, logoPhone);
            mappingData.put("logoPhone", "<img src=\""+imagePhoneUrl+"\" align=\"top\" style=\"margin:0 5px 0 0;\" width=\"20\" height=\"20\" />");

            String loginEPUrl = tenantConfigService.getEmailLoginUrlEPByName("LOGIN_EP_URL", tenantId);
            mappingData.put("loginUrl", "<a href=\""+loginEPUrl+"\" style=\"color: #009EFB;\">"+loginEPUrl+"</a>");

            EMailConfigDto eMailConfigDto = new EMailConfigDto();
            String mailFrom = tenantConfigService.getEmailAddressDefault(tenant.getRecId());
            eMailConfigDto.setMailFrom(mailFrom);

            List<ContractDetailClientDto> contractDtoList = new ArrayList<>();
            Long templateId = 0L;

            switch (sendExcSourcingEMailRequest.getEmailActivity()) {
                case EXC_SR_IS_WAITING_FOR_PURCHASING_APPROVER_TO_APPROVE:
                    templateId = tenantConfigService.getEmailTemplateIdByName(EXC_SR_IS_WAITING_FOR_PURCHASING_APPROVER_TO_APPROVE.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Please review and approve the sourcing request");

                    if (sendExcSourcingEMailRequest.getActivity().equals(ACTIVITY_EXC_SOURCING_SUBMIT)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) EXC_SR_IS_WAITING_FOR_PURCHASING_APPROVER_TO_APPROVE.id(), ACTIVITY_EXC_SOURCING_SUBMIT.id());
                        if (sendMailToRoleDto.isApprover()){
                            // Add current dept-approver into contractDtoList.
                            contractDtoList.addAll(this.getCurrentExcSourcingAwaitingDeptApprover(requestDto.getRecId(), tenant.getCode(), idp));
                        }

                    }
                    log.info("sendExcSourcingEMailNotification [EXCSOURCING_DTO]: {}", excSourcingDto);
                    log.info("sendExcSourcingEMailNotification [REQUEST_IS_WAITING_FOR_PURCHASING_APPROVER_TO_APPROVE]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendExcSourcingEMailNotification [SEND_MAIL: REQUEST_IS_WAITING_FOR_PURCHASING_APPROVER_TO_APPROVE]: {}", responseList);
                    break;

                case EXC_SR_HAS_BEEN_APPROVED_BY_DEPT_APPROVER:
                    templateId = tenantConfigService.getEmailTemplateIdByName(EXC_SR_HAS_BEEN_APPROVED_BY_DEPT_APPROVER.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Sourcing request has been approved");

                    if (sendExcSourcingEMailRequest.getActivity().equals(ACTIVITY_EXC_SOURCING_APPROVED)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) EXC_SR_HAS_BEEN_APPROVED_BY_DEPT_APPROVER.id(), ACTIVITY_EXC_SOURCING_APPROVED.id());
                        if (!excSourcingDeptApproverService.isLastDeptApprover(excSourcingDto.getRecId()) && sendMailToRoleDto.isExcSourcingApprover()) {
                            // Add current dept-approver into contractDtoList.
                            contractDtoList.addAll(this.getCurrentExcSourcingAwaitingDeptApprover(excSourcingDto.getRecId(), tenant.getCode(), idp));
                        } else {
                            // Add current purchasing approver into contractDtoList.
                            contractDtoList.addAll(this.getCurrentExcSourcingAwaitingPurchaser(excSourcingDto.getRecId(), tenant.getCode(), idp));
                        }
                    }
                    log.info("sendExcSourcingEMailNotification [EXCSOURCING_DTO]: {}", excSourcingDto);
                    log.info("sendExcSourcingEMailNotification [EXC_SR_HAS_BEEN_APPROVED_BY_DEPT_APPROVER]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendExcSourcingEMailNotification [SEND_MAIL: EXC_SR_HAS_BEEN_APPROVED_BY_DEPT_APPROVER]: {}", responseList);
                    break;

                case EXC_SR_HAS_BEEN_REJECTED_BY_DEPT_APPROVER:
                    templateId = tenantConfigService.getEmailTemplateIdByName(EXC_SR_HAS_BEEN_REJECTED_BY_DEPT_APPROVER.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Sourcing Request has been rejected");

                    if(sendExcSourcingEMailRequest.getActivity().equals(ACTIVITY_EXC_SOURCING_REJECTED)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) EXC_SR_HAS_BEEN_REJECTED_BY_DEPT_APPROVER.id(), ACTIVITY_EXC_SOURCING_REJECTED.id());
                        handlerContact(EXC_SR_HAS_BEEN_REJECTED_BY_DEPT_APPROVER.id(), ACTIVITY_REJECTED.id(), contractDtoList, emailExcSourcingContractDetailDto);
                        if (emailExcSourcingContractDetailDto.getPurchaser() != null && sendMailToRoleDto.isPurchaser()) {
                            // Add current dept-approver into contractDtoList.
                            contractDtoList.add(emailExcSourcingContractDetailDto.getPurchaser());
                        }
                    }
                    log.info("sendExcSourcingEMailNotification [EXCSOURCING_DTO]: {}", excSourcingDto);
                    log.info("sendExcSourcingEMailNotification [EXC_SR_HAS_BEEN_REJECTED_BY_DEPT_APPROVER]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendExcSourcingEMailNotification [SEND_MAIL: EXC_SR_HAS_BEEN_REJECTED_BY_DEPT_APPROVER]: {}", responseList);
                    break;

                case EXC_SR_HAS_BEEN_APPROVED_BY_PURCHASING_APPROVER:
                    templateId = tenantConfigService.getEmailTemplateIdByName(EXC_SR_HAS_BEEN_APPROVED_BY_PURCHASING_APPROVER.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Sourcing request has been approved");

                    if (sendExcSourcingEMailRequest.getActivity().equals(ACTIVITY_EXC_SOURCING_APPROVED)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) EXC_SR_HAS_BEEN_APPROVED_BY_PURCHASING_APPROVER.id(), ACTIVITY_EXC_SOURCING_APPROVED.id());
                        if (!excSourcingPurchaserService.isLastPurchaser(excSourcingDto.getRecId()) && sendMailToRoleDto.isExcSourcingPurchaser()) {
                            // Add current purchasing approver into contractDtoList.
                            contractDtoList.addAll(this.getCurrentExcSourcingAwaitingPurchaser(excSourcingDto.getRecId(), tenant.getCode(), idp));
                        } else {

                            if (emailExcSourcingContractDetailDto.getPurchaser() != null && sendMailToRoleDto.isExcSourcingPurchaser()) {
                                contractDtoList.add(emailExcSourcingContractDetailDto.getPurchaser());
                            }
                        }
                    }
                    log.info("sendExcSourcingEMailNotification [EXCSOURCING_DTO]: {}", excSourcingDto);
                    log.info("sendExcSourcingEMailNotification [SR_HAS_BEEN_APPROVED_BY_PURCHASING_APPROVER]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendExcSourcingEMailNotification [SEND_MAIL: SR_HAS_BEEN_APPROVED_BY_PURCHASING_APPROVER]: {}", responseList);
                    break;

                case EXC_SR_HAS_BEEN_REJECTED_BY_PURCHASING_APPROVER:
                    templateId = tenantConfigService.getEmailTemplateIdByName(EXC_SR_HAS_BEEN_REJECTED_BY_PURCHASING_APPROVER.toString(), tenantId);
                    eMailConfigDto.setTemplateId(templateId);
                    eMailConfigDto.setMailSubject("Sourcing Request has been rejected");

                    if(sendExcSourcingEMailRequest.getActivity().equals(ACTIVITY_EXC_SOURCING_REJECTED)) {
                        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) EXC_SR_HAS_BEEN_REJECTED_BY_PURCHASING_APPROVER.id(), ACTIVITY_EXC_SOURCING_REJECTED.id());
                        handlerContact(EXC_SR_HAS_BEEN_REJECTED_BY_PURCHASING_APPROVER.id(), ACTIVITY_REJECTED.id(), contractDtoList, emailExcSourcingContractDetailDto);
                        if (emailExcSourcingContractDetailDto.getPurchaser() != null && sendMailToRoleDto.isPurchaser()) {
                            // Add current purchasing approver into contractDtoList.
                            contractDtoList.add(emailExcSourcingContractDetailDto.getPurchaser());
                        }
                    }
                    log.info("sendExcSourcingEMailNotification [EXCSOURCING_DTO]: {}", excSourcingDto);
                    log.info("sendExcSourcingEMailNotification [SR_HAS_BEEN_REJECTED_BY_PURCHASING_APPROVER]: {}", contractDtoList);
                    responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
                    log.info("sendExcSourcingEMailNotification [SEND_MAIL: SR_HAS_BEEN_REJECTED_BY_PURCHASING_APPROVER]: {}", responseList);
                    break;

            }
            return responseList;
        } catch (Exception ex) {
            log.info("sendExcSourcingEMailNotification Failed: {}", ex.getMessage());
            EmailResponse emailResponse = new EmailResponse(true, ex.getMessage(), null);
            return List.of(emailResponse);
        }
    }

    private void handlerContact(
            Integer emailActivityId,
            Integer activityId,
            List<ContractDetailClientDto> contractDtoList,
            EmailContractDetailDto emailContractDetailDto
    ) {
        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) emailActivityId, activityId);
        if (emailContractDetailDto.getRequester() != null && sendMailToRoleDto.isRequester()) {
            contractDtoList.add(emailContractDetailDto.getRequester());
        }
        if (emailContractDetailDto.getReviewerList() != null && sendMailToRoleDto.isReviewer()) {
            contractDtoList.addAll(emailContractDetailDto.getReviewerList());
        }
        if (emailContractDetailDto.getPurchaserList() != null && sendMailToRoleDto.isPurchaser()) {
            contractDtoList.addAll(emailContractDetailDto.getPurchaserList());
        }
        if (emailContractDetailDto.getReportLineList() != null && sendMailToRoleDto.isReportLine()) {
            contractDtoList.addAll(emailContractDetailDto.getReportLineList());
        }
    }

    private void handlerContact(
            Integer emailActivityId,
            Integer activityId,
            List<ContractDetailClientDto> contractDtoList,
            EmailExcSourcingContractDetailDto emailExcSourcingContractDetailDto
    ) {
        SendMailToRoleDto sendMailToRoleDto = getSendMailToRole((long) emailActivityId, activityId);
        if (emailExcSourcingContractDetailDto.getPurchaser() != null && sendMailToRoleDto.isPurchaser()) {
            contractDtoList.add(emailExcSourcingContractDetailDto.getPurchaser());
        }
        if (emailExcSourcingContractDetailDto.getExcSourcingDeptApproverList() != null && sendMailToRoleDto.isExcSourcingApprover()) {
            contractDtoList.addAll(emailExcSourcingContractDetailDto.getExcSourcingDeptApproverList());
        }
        if (emailExcSourcingContractDetailDto.getExcSourcingPurchasingApproverList() != null && sendMailToRoleDto.isExcSourcingPurchaser()) {
            contractDtoList.addAll(emailExcSourcingContractDetailDto.getExcSourcingPurchasingApproverList());
        }
    }

    private SendMailToRoleDto getSendMailToRole(Long mailActivity, Integer activity) {
        return tenantEmailActivityService.getByTenantIdAndEmailActivityIdAndActivityId(mailActivity, activity);
    }

    @Override
    public List<EmailResponse> sendSupplierEMailNotification(SendEMailRequest sendEMailRequest, SupplierMailingLog supplierMailingLog) {
        List<EmailResponse> responseList = new ArrayList<>();
        try {
            log.info("sendSupplierEMailNotification supplierMailingLog: {}", supplierMailingLog);
            Map<String, Object> mappingData = new HashMap<>();

            Timestamp submitDate = DateTimeUtil.convertTimestampByUserTimeZone(supplierMailingLog.getSupplierMailingQueue().getSubmitDate(), "Asia/Bangkok");
            Date date = new Date();
            date.setTime(submitDate.getTime());
            String formattedSubmitDateTH = String.format("%s %s น.", DateTimeTHUtil.DateThaiFormat(submitDate), new SimpleDateFormat("HH:mm").format(date));
            String formattedSubmitDateEN = new SimpleDateFormat("d MMMMM yyyy hh:mm aaa").format(date);

            mappingData.put("requestName", supplierMailingLog.getSupplierMailingQueue().getRequestName());
            mappingData.put("submitDateTH", formattedSubmitDateTH);
            mappingData.put("submitDateEN", formattedSubmitDateEN);
            mappingData.put("companyName", supplierMailingLog.getSupplierMailingQueue().getOrgName());
            mappingData.put("categoryNameTH", supplierMailingLog.getSupplierCategoryValueLocal());
            mappingData.put("categoryNameEN", supplierMailingLog.getSupplierCategoryValueInter());

            log.info("sendEMailNotification getEmailRequest: {}", sendEMailRequest);
            EmailContractDetailDto emailContractDetailDto = new EmailContractDetailDto();// = retrieveEmailNotification(sendEMailRequest.getEmailActivity(), requestDto);

            // mock for test api using postman
            if (sendEMailRequest.getEmailContractDetailDto() != null) {
                emailContractDetailDto = sendEMailRequest.getEmailContractDetailDto();
            }

            String imagePtvnS360LogoUrl = supplierEmailNotifyConfig.getSupplierNotifyEmailLogoUrl();
            mappingData.put("logoPtvnS360", "<img src=\""+imagePtvnS360LogoUrl+"\" width=\"270.5px\" />");

            EMailConfigDto eMailConfigDto = new EMailConfigDto();
            String mailFrom = "noreply@pantavanij.com";
            eMailConfigDto.setMailFrom(mailFrom);

            List<ContractDetailClientDto> contractDtoList = new ArrayList<>();
            Long templateId = 0L;

            if (sendEMailRequest.getEmailActivity().equals(SUPPLIER_NOTIFY)) {
                templateId = Long.parseLong(supplierEmailNotifyConfig.getSupplierNotifyEmailTemplateId());
                eMailConfigDto.setTemplateId(templateId);
                eMailConfigDto.setMailSubject("Matching new buyer demand with your categories.");
                //Redefine mappingData to supplier Supplier Notification

                contractDtoList.add(emailContractDetailDto.getRequester());
                responseList = sendEmail(contractDtoList, mappingData, eMailConfigDto);
            }
            return responseList;
        } catch (Exception ex) {
            ex.printStackTrace();
            EmailResponse emailResponse = new EmailResponse(true, ex.getMessage(), null);
            return List.of(emailResponse);
        }
    }

    private List<ContractDetailClientDto> getCurrentAwaitingDeptApprover(Long requestId, String tenantId, String idp) {
        List<ContractDetailClientDto> result = new ArrayList<>();

        RequestDeptApproverDto requestDeptApproverDto = requestDeptApproverService.findCurrentAwaitingDeptApprover(requestId);

        if(requestDeptApproverDto != null) {
            result.add(uaaService.getContractDetail(tenantId, idp, requestDeptApproverDto.getApproverDto().getLoginId(), new HashMap<>()));
        }
        return result;
    }

    private List<ContractDetailClientDto> getCurrentExcSourcingAwaitingDeptApprover(Long excSourcingId, String tenantId, String idp) {
        List<ContractDetailClientDto> result = new ArrayList<>();

        ExcSourcingDeptApproverDto excSourcingDeptApproverDto = excSourcingDeptApproverService.findCurrentAwaitingDeptApprover(excSourcingId);

        if(excSourcingDeptApproverDto != null) {
            result.add(uaaService.getContractDetail(tenantId, idp, excSourcingDeptApproverDto.getApproverDto().getLoginId(), new HashMap<>()));
        }
        return result;
    }

    private List<ContractDetailClientDto> getCurrentExcSourcingAwaitingPurchaser(Long excSourcingId, String tenantId, String idp) {
        List<ContractDetailClientDto> result = new ArrayList<>();

        ExcSourcingPurchaserDto excSourcingPurchaserDto = excSourcingPurchaserService.findCurrentAwaitingPurchaser(excSourcingId);

        if(excSourcingPurchaserDto != null) {
            result.add(uaaService.getContractDetail(tenantId, idp, excSourcingPurchaserDto.getApproverDto().getLoginId(), new HashMap<>()));
        }
        return result;
    }

    private List<ContractDetailClientDto> get1StCancelledDeptApprover(Long requestId, String tenantId, String idp) {
        List<ContractDetailClientDto> result = new ArrayList<>();

        RequestDeptApproverDto requestDeptApproverDto = requestDeptApproverService.find1StCancelledDeptApprover(requestId);

        if(requestDeptApproverDto != null) {
            result.add(uaaService.getContractDetail(tenantId, idp, requestDeptApproverDto.getApproverDto().getLoginId(), new HashMap<>()));
        }
        return result;
    }

    private List<ContractDetailClientDto> getLatestApprovedDeptApprove(Long requestId, String tenantId, String idp) {
        List<ContractDetailClientDto> result = new ArrayList<>();

        RequestDeptApproverDto requestDeptApproverDto = requestDeptApproverService.findLatestApprovedDeptApprover(requestId);

        if(requestDeptApproverDto != null) {
            result.add(uaaService.getContractDetail(tenantId, idp, requestDeptApproverDto.getApproverDto().getLoginId(), new HashMap<>()));
        }
        return result;
    }

    private List<ContractDetailClientDto> getAllApprovedDeptApprove(Long requestId, String tenantId, String idp) {
        List<ContractDetailClientDto> result = new ArrayList<>();

        List<RequestDeptApproverDto> requestDeptApproverDtos = requestDeptApproverService.findAllApprovedDeptApprover(requestId);

        if(requestDeptApproverDtos != null && !requestDeptApproverDtos.isEmpty()) {
            for (RequestDeptApproverDto requestDeptApproverDto : requestDeptApproverDtos) {
                result.add(uaaService.getContractDetail(tenantId, idp, requestDeptApproverDto.getApproverDto().getLoginId(), new HashMap<>()));
            }
        }
        return result;
    }

    private List<ContractDetailClientDto> getForwardedPurchaserEmail(RequestDto requestDto, Tenant tenant, String idp) {
        List<ContractDetailClientDto> result = new ArrayList<>();

        InstanceApproverSectionDto instanceApproverSectionDto = new InstanceApproverSectionDto();
        Optional<InstanceApproverHeaderDto> instanceApproverHeaderDto = requestDto.getApproverHeaders().stream()
                .filter(ap -> ap.getHeaderName().equalsIgnoreCase("PURCHASER GROUP")).findFirst();
        if(instanceApproverHeaderDto.isPresent()) {
            instanceApproverSectionDto = instanceApproverHeaderDto.get().getApproverSections().get(0);
        }

        for (InstanceApproverDto approverDto : instanceApproverSectionDto.getApprovers()) {
            if ("FORWARED".equals(approverDto.getStatus())) {
                result.add(uaaService.getContractDetail(tenant.getCode(), idp, approverDto.getLoginId(), new HashMap<>()));
            }
        }
        return result;
    }

    private ContractDetailClientDto getReceiveForwardedPurchaserEmail(RequestDto requestDto, Tenant tenant, String idp) {
        InstanceApproverSectionDto instanceApproverSectionDto = new InstanceApproverSectionDto();
        Optional<InstanceApproverHeaderDto> instanceApproverHeaderDto = requestDto.getApproverHeaders().stream()
                .filter(ap -> ap.getHeaderName().equalsIgnoreCase("PURCHASER GROUP")).findFirst();
        if(instanceApproverHeaderDto.isPresent()) {
            instanceApproverSectionDto = instanceApproverHeaderDto.get().getApproverSections().get(0);
        }

        int total = instanceApproverSectionDto.getApprovers().size();
        InstanceApproverDto approverDto = instanceApproverSectionDto.getApprovers().get(total - 1);
        ContractDetailClientDto receiveForward = uaaService.getContractDetail(tenant.getCode(), idp, approverDto.getLoginId(), new HashMap<>());

        return receiveForward;
    }

    @Override
    public boolean isLastApprover(RequestDto requestDto) {
        boolean result = true;

        Page<WorkflowInstApproverDto> approvers = this.getApprovers(requestDto);
        List<String> refApproverIds = approvers.getContent().stream()
                .filter(a -> a.getIsRequired() && a.getStatus().equals("PENDING"))
                .map(WorkflowInstApproverDto::getReferApproverId)
                .collect(Collectors.toList());

        if (refApproverIds != null && refApproverIds.size() > 0) {
            result = false;
        }
        return result;
    }

    private List<EmailResponse> sendEmail(List<ContractDetailClientDto> contractDtos, Map<String, Object> mappingData, EMailConfigDto eMailConfigDto) {
        return contractDtos.stream()
                .filter(Objects::nonNull)
                .filter(distinctByKey(dto ->dto.getEmail()+""+dto.getFirstName()+""+dto.getLastName()))
                .map(contract -> {
                    EmailResponse emailResponse = null;
                    Gson gson = new Gson();

                    if (contract != null) {
                        eMailConfigDto.setMailTo(Arrays.asList(contract.getEmail()));
                        StringBuilder fullname = new StringBuilder();
                        if (contract.getFirstName() != null) {
                            fullname.append(contract.getFirstName());
                        }

                        if (contract.getFirstName() != null && contract.getLastName() != null) {
                            fullname.append(" ");
                            fullname.append(contract.getLastName());
                        }

                        mappingData.put("fullname", fullname.toString().trim());
                        eMailConfigDto.setMappingData(gson.toJson(mappingData));
                        log.info("Send mail : {} to {} with email {}", eMailConfigDto.getMailSubject(), fullname, contract.getEmail());
                        EMailDto response = emailClient.sendEmail(eMailConfigDto);
                        emailResponse = EmailResponse.builder().email(contract.getEmail()).isError(response.isError())
                                .message(StringUtils.isNotBlank(response.getErrorMsg()) ? response.getErrorMsg() : "success").build();
                    }
                    return emailResponse;
                }).collect(Collectors.toList());
    }

    private String getTimeZoneUser() {
        UserDto userDto = AppUtil.getUser();
        UserDetailResponse userDetail = uaaService.getUserDetailByTenantIdAndIdpAndUserName(userDto.getTenantId(), userDto.getIdp(), null, userDto.getUsername(), null);
        return userDetail.getTimeZone();
    }

    private String getDateString(Timestamp timestamp, String timezone) {
        return new java.text.SimpleDateFormat("dd/MM/yyyy")
                .format(DateTimeUtil.getTimestampByTimeZone(timestamp, timezone));
    }

    private Page<WorkflowInstApproverDto> getApprovers(RequestDto requestDto) {

        // Find instanceApproverId by ApproverName & DocumentId
        WorkflowInstanceApproverCriteria criteria = new WorkflowInstanceApproverCriteria();
        criteria.setPageNumber(1);
        criteria.setPageSize(100);

        List<String> refDocumentIds = new ArrayList<>();
        refDocumentIds.add(requestDto.getRecId().toString());
        criteria.setRefDocumentId(refDocumentIds);

        List<String> workFlowStatus = new ArrayList<>();
        workFlowStatus.addAll(Arrays.asList("PENDING", "AWAITING", "REJECTED", "APPROVED", "CANCELLED"));
        criteria.setStatus(workFlowStatus);

        // Optional Params
        List<Long> workflowInstanceIds = new ArrayList<>();
        workflowInstanceIds.add(requestDto.getWorkflowInstanceId());
        criteria.setWorkflowInstanceId(workflowInstanceIds);

        return workflowInstanceApprovalService.getByCriteria(criteria);
    }

    private List<RequestPurchaser> getRequestApprovers(RequestDto requestDto) {
        log.info("Get Request Approvers of Request : {}", requestDto.getRequestNo());
        Request request = requestRepository.findRequestsByRecId(requestDto.getRecId());
        List<RequestPurchaser> requestPurchaserList = requestPurchaserService.getRequestPurchaserByRequest(request);
        log.info("Get Request Approvers of Request : {}, size : {}", requestDto.getRequestNo(), requestPurchaserList.size());
        return requestPurchaserList;
    }

    private EmailContractDetailDto retrieveEmailNotification(EmailActivity emailActivity, RequestDto requestDto) {
        EmailContractDetailDto contracts = new EmailContractDetailDto();
        String tenantId = AppUtil.getTenantId();
        String idp = AppUtil.getIdp();

        //Sample Postman :
        // {{wf_host}}/api/projects/sourcingreq/workflow-instance-approver?
        // pageNumber=0&pageSize=100&referApproverId=Supansa&refDocumentId=373

        List<RequestPurchaser> requestPurchasers = this.getRequestApprovers(requestDto);
        Map<String,UserDetailResponse> userDetailMap = new HashMap<>();

        ContractDetailClientDto requester = uaaService.getContractDetail(tenantId, idp, requestDto.getCreatedBy(), userDetailMap);
        List<ContractDetailClientDto> reviewerList = this.getReviewer(tenantId, idp, requestDto.getRecId(), userDetailMap);
        List<ContractDetailClientDto> reportLineList = this.getReportLine(tenantId, idp, requestDto.getRecId(), userDetailMap);
        List<ContractDetailClientDto> deptApproverList = this.getDeptApprover(tenantId, idp, requestDto.getRecId(), userDetailMap);
        List<ContractDetailClientDto> purchaserList = this.getPurchaserEmail(tenantId, idp, requestDto, requestPurchasers, userDetailMap, emailActivity);

        contracts.setRequester(requester);
        contracts.setReviewerList(reviewerList);
        contracts.setReportLineList(reportLineList);
        contracts.setDeptApproverList(deptApproverList);
        contracts.setPurchaserList(purchaserList);

        return contracts;
    }

    private List<ContractDetailClientDto> getReviewer(String tenantId, String idp, Long requestId, Map<String,UserDetailResponse> userDetailMap) {
        List<RequestReviewerDto> requestReviewerList = requestReviewerService.findByRequest(requestId);
        List<ContractDetailClientDto> reviewer = new ArrayList<>();
        List<String> reviewerId = new ArrayList<>();
        for (RequestReviewerDto requestReviewerDto : requestReviewerList) {
            reviewerId.add(requestReviewerDto.getReviewer().getLoginId());
        }

        if (!reviewerId.isEmpty()) {
            for (String id : reviewerId) {
                reviewer.add(uaaService.getContractDetail(tenantId, idp, id, userDetailMap));
            }
        }

        return reviewer;
    }

    private List<ContractDetailClientDto> getReportLine(String tenantId, String idp, Long requestId, Map<String,UserDetailResponse> userDetailMap) {
        List<RequestReportLineDto> requestReportLineList = requestReportLineService.getRequestReportLineByRequest(requestId);
        List<ContractDetailClientDto> reportLine = new ArrayList<>();
        List<String> reportLineId = new ArrayList<>();
        for (RequestReportLineDto requestReportLineDto : requestReportLineList) {
            reportLineId.add(requestReportLineDto.getReportLine().getLoginId());
        }

        if (!reportLineId.isEmpty()) {
            for (String id : reportLineId) {
                reportLine.add(uaaService.getContractDetail(tenantId, idp, id, userDetailMap));
            }
        }

        return reportLine;
    }

    private List<ContractDetailClientDto> getDeptApprover(String tenantId, String idp, Long requestId, Map<String,UserDetailResponse> userDetailMap) {
        List<RequestDeptApproverDto> requestDeptApproverList = requestDeptApproverService.findByRequest(requestId);
        List<ContractDetailClientDto> deptApprover = new ArrayList<>();
        List<String> deptApproverId = new ArrayList<>();
        for (RequestDeptApproverDto requestDeptApproverDto : requestDeptApproverList) {
            deptApproverId.add(requestDeptApproverDto.getApproverDto().getLoginId());
        }

        if (!deptApproverId.isEmpty()) {
            for (String id : deptApproverId) {
                deptApprover.add(uaaService.getContractDetail(tenantId, idp, id, userDetailMap));
            }
        }

        return deptApprover;
    }

    private List<ContractDetailClientDto> getPurchaserEmail(String tenantId, String idp, RequestDto requestDto, List<RequestPurchaser> approvers, Map<String,UserDetailResponse> userDetailMap, EmailActivity emailActivity) {
        List<ContractDetailClientDto> purchaser = new ArrayList<>();

        ForwardedApprover forwardedApprover = null;
        boolean isForwardedApprover = requestForwarderService.isCurrentForwarder(requestDto.getRecId(), requestDto.getAssignedBy());
        if(isForwardedApprover) {
            forwardedApprover = requestForwarderService.getForwardedApprover(requestDto.getRecId());
            if(forwardedApprover != null) {
                purchaser.add(uaaService.getContractDetail(tenantId, idp, forwardedApprover.getForwardedApprover(), userDetailMap));
            }
        } else {
            List<String> refApproverIds = approvers.stream()
                    .map(RequestPurchaser::getPurchaser)
                    .map(Purchaser::getLoginId)
                    .collect(Collectors.toList());

            if (refApproverIds != null && refApproverIds.size() > 0) {
                for (String refApprover : refApproverIds) {
                    purchaser.add(uaaService.getContractDetail(tenantId, idp, refApprover, userDetailMap));
                }
            }
        }

        // Case Delegation
        if (emailActivity == PLEASE_REVIEW_AND_APPROVE_SR
                && AppUtil.getUserName().equalsIgnoreCase(requestDto.getCreatedBy())) {
            // Case : Requester Submit Request
            // TODO : Refactoring codes to supplort both BAY & Others
//            if(requestDto.getApproverHeaders() != null && requestDto.getApproverHeaders().size() > 0) {
//                InstanceApproverHeaderDto instanceApproverHeaderDto = requestDto.getApproverHeaders().get(0);
//                if(instanceApproverHeaderDto.getApproverSections() != null && instanceApproverHeaderDto.getApproverSections().size() > 0) {
//                    InstanceApproverSectionDto instanceApproverSectionDto = instanceApproverHeaderDto.getApproverSections().get(0);
//                    if(instanceApproverSectionDto.getApprovers() != null && instanceApproverSectionDto.getApprovers().size() > 0) {
//                        InstanceApproverDto InstanceApproverDto = instanceApproverSectionDto.getApprovers().get(0);
//
//                    }
//                }
//            }
            List<String> delegateeList = null;
            String authHeader = "Bearer " + AppUtil.getJwtToken();
            DelegateeByDelegatorRequest delegateeByDelegatorRequest = new DelegateeByDelegatorRequest();
            delegateeByDelegatorRequest.setDelegatorUserName(requestDto.getAssignedBy());
            delegateeByDelegatorRequest.setDelegationStatuses(Collections.singletonList(DelegationStatusEnum.DELEGATION_ACTIVE.id()));

            DelegateeByDelegatorResponse delegateeByDelegatorResponse = delegateClient.getDelegateeByDelegator(authHeader, delegateeByDelegatorRequest);
            if (delegateeByDelegatorResponse.getDelegateeUserNames() != null) {
                delegateeList = delegateeByDelegatorResponse.getDelegateeUserNames();
            }
            if (delegateeList != null && !delegateeList.isEmpty()) {
                for (String delegatee : delegateeList) {
                    purchaser.add(uaaService.getContractDetail(tenantId, idp, delegatee, userDetailMap));
                }
            }
        }
        // Case requester cancel request, then check the delegation of this request.
        // If status is active must send email to that delegatee, too.
        else if (emailActivity == SR_HAS_BEEN_CANCELLED) {
            String authHeader = "Bearer " + AppUtil.getJwtToken();
            DelegateeByDelegatorRequest delegateeByDelegatorRequest = new DelegateeByDelegatorRequest();
            delegateeByDelegatorRequest.setDelegatorUserName(requestDto.getAssignedBy());
            delegateeByDelegatorRequest.setDelegationStatuses(Collections.singletonList(DelegationStatusEnum.DELEGATION_ACTIVE.id()));

            List<String> delegateeList = null;
            DelegateeByDelegatorResponse delegateeByDelegatorResponse = delegateClient.getDelegateeByDelegator(authHeader, delegateeByDelegatorRequest);
            if (delegateeByDelegatorResponse != null && delegateeByDelegatorResponse.getDelegateeUserNames() != null) {
                delegateeList = delegateeByDelegatorResponse.getDelegateeUserNames();
            }
            if (delegateeList != null && !delegateeList.isEmpty()) {
                delegateeList.forEach(delegatee -> {
                    purchaser.add(uaaService.getContractDetail(tenantId, idp, delegatee, userDetailMap));
                });
            }
        }
        // Case : Delegatee reject and approved request.
        else if ((emailActivity == SR_HAS_BEEN_REJECTED || emailActivity == SR_HAS_BEEN_APPROVED)
                && !AppUtil.getUserName().equalsIgnoreCase(requestDto.getAssignedBy())) {
            String authHeader = "Bearer " + AppUtil.getJwtToken();
            DelegationActiveRequest delegationActiveRequest = new DelegationActiveRequest();
            delegationActiveRequest.setDelegateeBy(AppUtil.getUserName());
            delegationActiveRequest.setDelegatorBy(requestDto.getAssignedBy());

            DelegationActiveResponse delegationActiveResponse = delegateClient.getActiveDelegationByDeletatorAndDelegatee(authHeader, delegationActiveRequest);
            if (delegationActiveResponse.getData() != null) {
                DelegationDto delegationDto = delegationActiveResponse.getData();
                purchaser.add(uaaService.getContractDetail(tenantId, idp, delegationDto.getDelegateeBy(), userDetailMap));
            }

        }

        return purchaser;
    }

    private EmailExcSourcingContractDetailDto retrieveExcSourcingEmailNotification(EmailActivity emailActivity, RequestDto requestDto, ExcSourcingDto excSourcingDto) {
        EmailExcSourcingContractDetailDto contracts = new EmailExcSourcingContractDetailDto();
        String tenantId = AppUtil.getTenantId();
        String idp = AppUtil.getIdp();

        Map<String,UserDetailResponse> userDetailMap = new HashMap<>();

        ContractDetailClientDto purchaser = uaaService.getContractDetail(tenantId, idp, requestDto.getAssignedBy(), userDetailMap);
        List<ContractDetailClientDto> excSourcingdeptApproverList = this.getExcSourcingDeptApprover(tenantId, idp, excSourcingDto.getRecId(), userDetailMap);
        List<ContractDetailClientDto> excSourcingPurchasingApproverList = this.getExcSourcingPurchasingApprover(tenantId, idp, excSourcingDto.getRecId(), userDetailMap);

        contracts.setPurchaser(purchaser);
        contracts.setExcSourcingDeptApproverList(excSourcingdeptApproverList);
        contracts.setExcSourcingPurchasingApproverList(excSourcingPurchasingApproverList);

        return contracts;
    }

    private List<ContractDetailClientDto> getExcSourcingDeptApprover(String tenantId, String idp, Long excSourcingId, Map<String,UserDetailResponse> userDetailMap) {
        List<ExcSourcingDeptApproverDto> excSourcingDeptApproverList = excSourcingDeptApproverService.findByExcSourcing(excSourcingId);
        List<ContractDetailClientDto> deptApprover = new ArrayList<>();
        List<String> deptApproverId = new ArrayList<>();
        for (ExcSourcingDeptApproverDto excSourcingDeptApproverDto : excSourcingDeptApproverList) {
            deptApproverId.add(excSourcingDeptApproverDto.getApproverDto().getLoginId());
        }

        if (!deptApproverId.isEmpty()) {
            for (String id : deptApproverId) {
                deptApprover.add(uaaService.getContractDetail(tenantId, idp, id, userDetailMap));
            }
        }

        return deptApprover;
    }

    private List<ContractDetailClientDto> getExcSourcingPurchasingApprover(String tenantId, String idp, Long excSourcingId, Map<String,UserDetailResponse> userDetailMap) {
        List<ExcSourcingPurchaserDto> excSourcingPurchaserList = excSourcingPurchaserService.findByExcSourcing(excSourcingId);
        List<ContractDetailClientDto> deptApprover = new ArrayList<>();
        List<String> deptApproverId = new ArrayList<>();
        for (ExcSourcingPurchaserDto excSourcingPurchaserDto : excSourcingPurchaserList) {
            deptApproverId.add(excSourcingPurchaserDto.getApproverDto().getLoginId());
        }

        if (!deptApproverId.isEmpty()) {
            for (String id : deptApproverId) {
                deptApprover.add(uaaService.getContractDetail(tenantId, idp, id, userDetailMap));
            }
        }

        return deptApprover;
    }


//    @Override
//    public List<EmailResponse> sendEMailNotificationDelegate(SendEMailRequest sendEMailRequest, ContractDetailClientDto delegatorDetail, ContractDetailClientDto delegateeDetail, String delegationPeriod, String delegationCancelReason, String delegationCancelDateTime) {
//        List<EmailResponse> responseList = new ArrayList<>();
//        try {
//            log.info("sendEMailNotificationDelegate");
//            Map<String, Object> mappingData = new HashMap<>();
//
//            String tenantCode = AppUtil.getTenantId();
//            Tenant tenant = tenantService.findByCode(tenantCode);
//            Integer tenantId = tenant.getRecId();
//
//            String logoOpn = tenantConfigService.getEmailImageByName("EBO_LOGO", tenantId);
//
//            String imageHost = tenantConfigService.getEmailImageUrl(tenantId);
//            String imageOpnUrl = String.format(imageHost, logoOpn);
//            mappingData.put("logoOpn", "<img src=\""+imageOpnUrl+"\" width=\"216\" />");
//
//            String logoPhone = tenantConfigService.getEmailImageByName("EBO_PHONE", tenantId);
//            String imagePhoneUrl = String.format(imageHost, logoPhone);
//            mappingData.put("logoPhone", "<img src=\""+imagePhoneUrl+"\" align=\"top\" style=\"margin:0 5px 0 0;\" width=\"20\" height=\"20\" />");
//
//            String loginEPUrl = tenantConfigService.getEmailLoginUrlEPByName("LOGIN_EP_URL", tenantId);
//            mappingData.put("loginUrl", "<a href=\""+loginEPUrl+"\" style=\"color: #009EFB;\">"+loginEPUrl+"</a>");
//
//            EMailConfigDto eMailConfigDto = new EMailConfigDto();
//            String mailFrom = tenantConfigService.getEmailAddressDefault(tenant.getRecId());
//            eMailConfigDto.setMailFrom(mailFrom);
//
//            mappingData.put("delegationPeriod", delegationPeriod);
//
//            List<ContractDetailClientDto> contractDtos = new ArrayList<>();
//            StringBuilder delegationName;
//            StringBuilder delegatorName;
//            Long templateId = 0L;
//            switch (sendEMailRequest.getEmailActivity()) {
//                case DELEGATION_HAS_BEEN_CREATED_FOR_WHO_DELEGATE:
////                    templateId = 206L;
//                    templateId = tenantConfigService.getEmailTemplateIdByName(DELEGATION_HAS_BEEN_CREATED_FOR_WHO_DELEGATE.toString(), tenantId);
//                    eMailConfigDto.setTemplateId(templateId);
//                    eMailConfigDto.setMailSubject("The delegation has been submitted.");
//                    contractDtos.add(delegatorDetail);
//                    delegationName = new StringBuilder();
//                    if (delegateeDetail.getFirstName() != null) {
//                        delegationName.append(delegateeDetail.getFirstName());
//                    }
//                    if (delegateeDetail.getFirstName() != null && delegateeDetail.getLastName() != null) {
//                        delegationName.append(" ");
//                        delegationName.append(delegateeDetail.getLastName());
//                    }
//                    mappingData.put("delegationName", delegationName.toString().trim());
//                    responseList = sendEmail(contractDtos, mappingData, eMailConfigDto);
//                    break;
//
//                case DELEGATION_HAS_BEEN_CREATED_FOR_WHO_RECEIVED_DELEGATE:
////                    templateId = 207L;
//                    templateId = tenantConfigService.getEmailTemplateIdByName(DELEGATION_HAS_BEEN_CREATED_FOR_WHO_RECEIVED_DELEGATE.toString(), tenantId);
//                    eMailConfigDto.setTemplateId(templateId);
//                    eMailConfigDto.setMailSubject("You are delegated on "+ delegationPeriod +".");
//                    contractDtos.add(delegateeDetail);
//                    delegationName = new StringBuilder();
//                    if (delegatorDetail.getFirstName() != null) {
//                        delegationName.append(delegatorDetail.getFirstName());
//                    }
//                    if (delegatorDetail.getFirstName() != null && delegatorDetail.getLastName() != null) {
//                        delegationName.append(" ");
//                        delegationName.append(delegatorDetail.getLastName());
//                    }
//                    mappingData.put("delegationName", delegationName.toString().trim());
//                    responseList = sendEmail(contractDtos, mappingData, eMailConfigDto);
//                    break;
//
//                case DELEGATION_HAS_BEEN_CANCELLED:
////                    templateId = 208L;
//                    templateId = tenantConfigService.getEmailTemplateIdByName(DELEGATION_HAS_BEEN_CANCELLED.toString(), tenantId);
//                    eMailConfigDto.setTemplateId(templateId);
//                    eMailConfigDto.setMailSubject("The delegation has been cancelled");
//                    contractDtos.add(delegatorDetail);
//                    contractDtos.add(delegateeDetail);
//                    delegationName = new StringBuilder();
//                    if (delegateeDetail.getFirstName() != null) {
//                        delegationName.append(delegateeDetail.getFirstName());
//                    }
//                    if (delegateeDetail.getFirstName() != null && delegateeDetail.getLastName() != null) {
//                        delegationName.append(" ");
//                        delegationName.append(delegateeDetail.getLastName());
//                    }
//                    delegatorName = new StringBuilder();
//                    if (delegatorDetail.getFirstName() != null) {
//                        delegatorName.append(delegatorDetail.getFirstName());
//                    }
//                    if (delegatorDetail.getFirstName() != null && delegatorDetail.getLastName() != null) {
//                        delegatorName.append(" ");
//                        delegatorName.append(delegatorDetail.getLastName());
//                    }
//                    mappingData.put("delegationName", delegatorName.toString().trim());
//                    mappingData.put("delegatorName", delegatorName.toString().trim());
//                    mappingData.put("delegationCancelReason", delegationCancelReason);
//                    mappingData.put("delegationCancelDateTime", delegationCancelDateTime);
//                    responseList = sendEmail(contractDtos, mappingData, eMailConfigDto);
//                    break;
//
//            }
//            return responseList;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            EmailResponse emailResponse = new EmailResponse(true, ex.getMessage(), null);
//            return Arrays.asList(emailResponse);
//        }
//
//    }

}
