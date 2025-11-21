package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.BifrostClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.dto.pr.AttachmentDto;
import com.pantavanij.sourcingreq.services.domain.dto.pr.DFFTicketNumberDto;
import com.pantavanij.sourcingreq.services.domain.dto.pr.DFFTicketTypeDto;
import com.pantavanij.sourcingreq.services.domain.dto.pr.DistributionDto;
import com.pantavanij.sourcingreq.services.domain.dto.pr.LineDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.DepartmentMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.OptionDtoMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.RequestMapper;
import com.pantavanij.sourcingreq.services.domain.request.SendEMailRequest;
import com.pantavanij.sourcingreq.services.domain.request.pr.CreateOraclePrRequest;
import com.pantavanij.sourcingreq.services.domain.response.PurchaseRequisitionResponse;
import com.pantavanij.sourcingreq.services.domain.response.pr.CreateOraclePrResponse;
import com.pantavanij.sourcingreq.services.enums.EmailActivity;
import com.pantavanij.sourcingreq.services.exception.DataNotFoundException;
import com.pantavanij.sourcingreq.services.exception.ExternalServiceException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.Activity.*;
import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_QUALIFIED_SUPPLIER;
import static com.pantavanij.sourcingreq.services.util.CommonUtils.mapOptionalToOptionDto;

@RequiredArgsConstructor
@Service
public class PrServiceImpl implements PrService {
   private static final Logger logger = LoggerFactory.getLogger(PrServiceImpl.class);

    private final BifrostClient bifrostClient;
    private final RequestRepository requestRepository;
    private final RequestItemService requestItemService;
    private final RequestService requestService;
    private final TenantService tenantService;
    private final ExistingPriceItemService existingPriceItemService;
    private final RequestTypeRepository requestTypeRepository;
    private final UaaService uaaService;
    private final PrRepository prRepository;
    private final RequestItemPrService requestItemPrService;
    private final RequestHistoryService requestHistoryService;
    private final TenantConfigService tenantConfigService;
    private final ExistingPriceItemRepository existingPriceItemRepository;
    private final RequestDepartmentRepository requestDepartmentRepository;
    private final RequestCategoryRepository requestCategoryRepository;
    private final RequestLocationRepository requestLocationRepository;
    private final EmailService emailService;

    private final FileUtil fileUtil;
    private final String FOLDER_NAME = "attachments";

    public static final String PR_REF = "RefPR_";
    private final RequestItemRepository requestItemRepository;
    private final LocationRepository locationRepository;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseRequisitionResponse createPurchaseRequisition(String requestNo, List<Long> requestItemsId) {
        try {
            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            Integer tenantId = tenant.getRecId();
            RequestDto requestDto = requestService.getRequestByRequestNo(requestNo, tenantId);
            Long requestId = requestDto.getRecId();
            setRequestDto(requestNo, requestItemsId, tenantId, requestDto, requestId);
            requestDto.setTypeObj(mapOptionalToOptionDto(requestTypeRepository.findTop1ByRequestId(requestDto.getRecId()), OptionDtoMapper.INSTANCE::toTypeOptionDto));
            String uri = tenantConfigService.getCreateToPrURL(tenant.getRecId());
            PurchaseRequisitionResponse response = bifrostClient.createPR(uri, requestDto);
            Request request = requestRepository.findRequestByRecId(requestId);
            // Save Request History : 10 COPY_TO_PR	Copy request to PR successfully
            requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_COPY_TO_PR.id());
            return response;
        } catch (HttpClientErrorException | ExternalServiceException | DataNotFoundException ex) {
            return PurchaseRequisitionResponse.builder().data(null).header(new HeaderPRDto()).build();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateOraclePrResponse createERPPurchaseRequisition(Tenant tenant, String requestNo, List<Long> requestItemsId) {

        Integer tenantId = tenant.getRecId();
        Request request = requestRepository.findFirstByRequestNo(requestNo);
        CreateOraclePrRequest prRequest = new CreateOraclePrRequest();

        try {

            prRequest.setPreparerEmail("pinsri@deloitte.com"); // Temporary hardcoding to "pinsri@deloitte.com"
            prRequest.setExternallyManagedFlag(false);
            prRequest.setDescription(request.getRequestName());
            prRequest.setInterfaceSourceCode("PTVN");
            prRequest.setRequisitioningBU("CP Axtra BU");
            prRequest.setTaxationCountryCode("TH"); // Temporary hardcoding to "TH"
            prRequest.setTaxationCountry("Thailand"); // Temporary hardcoding to "Thailand"

            List<DFFTicketNumberDto> dffDtos = new ArrayList<>();
            DFFTicketNumberDto dffDto = new DFFTicketNumberDto();
            dffDto.setPtvnTicketNumber(requestNo);
            dffDtos.add(dffDto);
            prRequest.setDff(dffDtos);

            List<LineDto> lineDtos = new ArrayList<>();
            Integer lineNo = 1;
            for(Long requestItemId : requestItemsId) {

                RequestItem requestItem = requestItemRepository.findRequestItemByRecId(requestItemId);
                if(requestItem.getSourcingStatus().getRecId() == SOURCING_QUALIFIED_SUPPLIER.id()) {
                    ExistingPriceItem existingPriceItem = existingPriceItemRepository.findByRequestItemAndNullableSourcingDocNo(requestItem, requestItem.getSourcingDocNo());

                    for (ExistingPriceItemSupplier existingPriceItemSupplier : existingPriceItem.getExistingPriceItemSupplierList()) {
                        LineDto lineDto = new LineDto();
                        lineDto.setLineNumber(lineNo);
                        lineDto.setDestinationTypeCode("EXPENSE");

                        Optional<RequestLocation> requestLocation = requestLocationRepository.findTop1ByRequestId(request.getRecId());
                        if (requestLocation.isPresent()) {
                            Integer locationId = requestLocation.get().getLocation().getRecId();
                            String organizationCode = locationRepository.getOrganizationCodeByLocation(tenantId, locationId);
                            lineDto.setDeliverToLocationCode(requestLocation.get().getLocation().getName());
                            lineDto.setDestinationOrganizationCode(organizationCode);

                        } else {

                            lineDto.setDeliverToLocationCode("No Deliver To Location");
                            lineDto.setDestinationOrganizationCode("No Destination Organization Code");
                        }

                        lineDto.setRequesterEmail("pinsri@deloitte.com"); // Temporary hardcoding to "pinsri@deloitte.com"
                        lineDto.setQuantity(existingPriceItem.getQuantity());
                        lineDto.setUom(existingPriceItem.getUnit().getCode());
                        lineDto.setItemDescription(String.format("%s : %s", existingPriceItem.getItemName(), existingPriceItem.getItemDescription()));
                        OptionDto categoryOptionDto = mapOptionalToOptionDto(requestCategoryRepository.findTop1ByRequestId(request.getRecId()), OptionDtoMapper.INSTANCE::toCategoryOptionDto);
                        lineDto.setCategoryName(categoryOptionDto != null ? categoryOptionDto.getName() : "");
                        lineDto.setLineTypeCode("ORA_Rate Based Services"); //Fixed to "ORA_Rate Based Services"
                        lineDto.setCurrencyCode(request.getCurrency().getCode());
                        lineDto.setPrice(existingPriceItemSupplier.getUnitPrice());
                        lineDto.setSupplier(existingPriceItemSupplier.getSupplierFullName());
                        lineDto.setSupplierSite("NT 30");
                        lineDto.setNegotiatedByPreparerFlag(true);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        lineDto.setRequestedDeliveryDate(sdf.format(request.getRequestDate()));

                        // Set Attachment Objects
                        List<AttachmentDto> attachmentDtos = new ArrayList<>();
                        String customFolderName = String.format("%s/%s",FOLDER_NAME, tenant.getCode());

                        for(RequestItemAttachment requestItemAttachment : requestItem.getRequestItemAttachmentList()) {
                            AttachmentDto attachmentDto = new AttachmentDto();
                            attachmentDto.setDatatypeCode("FILE");
                            attachmentDto.setFileName(requestItemAttachment.getAttachment().getFileName());
                            attachmentDto.setFileContents(new String(Base64.getEncoder().encode(fileUtil.downloadFile(requestItemAttachment.getAttachment().getFileId(), customFolderName))));
                            attachmentDto.setTitle(requestItemAttachment.getNote());
                            attachmentDto.setDescription("");

                            attachmentDtos.add(attachmentDto);
                        }

                        for(ExistingPriceItemAttachment existingPriceItemAttachment : existingPriceItem.getExistingPriceItemAttachmentList()) {
                            AttachmentDto attachmentDto = new AttachmentDto();
                            attachmentDto.setDatatypeCode("FILE");
                            attachmentDto.setFileName(existingPriceItemAttachment.getAttachment().getFileName());
                            attachmentDto.setFileContents(new String(Base64.getEncoder().encode(fileUtil.downloadFile(existingPriceItemAttachment.getAttachment().getFileId(), customFolderName))));
                            attachmentDto.setTitle(existingPriceItemAttachment.getNote());
                            attachmentDto.setDescription("");

                            attachmentDtos.add(attachmentDto);
                        }

                        lineDto.setAttachments(attachmentDtos);

                        // Set DFF Type
                        List<DFFTicketTypeDto> dffTypeDtos = new ArrayList<>();
                        DFFTicketTypeDto dffTypeDto = new DFFTicketTypeDto();
                        dffTypeDto.setPtvnTicketType(requestItem.getSourcingDocNo());
                        dffTypeDtos.add(dffTypeDto);
                        lineDto.setDff(dffTypeDtos);

                        // Set Distribution Objects
                        List<DistributionDto> distributionDtos = new ArrayList<>();
                        DistributionDto distributionDto = new DistributionDto();
                        distributionDto.setDistributionNumber(1);
                        distributionDto.setQuantity(existingPriceItem.getQuantity());
                        distributionDto.setCurrencyAmount(existingPriceItemSupplier.getUnitPrice());
                        distributionDtos.add(distributionDto);

                        lineDto.setDistributions(distributionDtos);

                        lineDtos.add(lineDto);
                        lineNo++;
                    }
                }
            }

            prRequest.setLines(lineDtos);


            String uri = tenantConfigService.getCreateToPrURL(tenant.getRecId());
            CreateOraclePrResponse createOraclePrResponse = bifrostClient.createOraclePR(uri, prRequest);

            // Send email notification
            SendEMailRequest copyToPREmail = new SendEMailRequest();
            copyToPREmail.setRequestId(request.getRecId());
            copyToPREmail.setEmailActivity(EmailActivity.SR_CREATE_ORACLE_PR_SUCCESSFULLY);
            copyToPREmail.setActivity(ACTIVITY_COPY_TO_PR);
            RequestDto requestDtoForEmail = RequestMapper.INSTANCE.toRequestDto(request);
            emailService.sendEMailNotification(copyToPREmail, requestDtoForEmail);

            // Save Request History : 10 COPY_TO_PR	Copy request to PR successfully
            requestHistoryService.saveRequestHistoryByAction(request, ACTIVITY_COPY_TO_PR.id());

            return createOraclePrResponse;


        } catch (HttpClientErrorException | ExternalServiceException | DataNotFoundException ex)  {

            // Set CopyToPRFailed flag

            Request readRequest = requestRepository.findRequestsByRecId(request.getRecId());
            readRequest.setIsCopyToPRFail(true);
            requestRepository.save(readRequest);

            // Send email notification
            SendEMailRequest copyToPREmail = new SendEMailRequest();
            copyToPREmail.setRequestId(request.getRecId());
            copyToPREmail.setEmailActivity(EmailActivity.SR_CREATE_ORACLE_PR_FAILED);
            copyToPREmail.setActivity(ACTIVITY_COPY_TO_PR_FAILED);
            RequestDto requestDtoForEmail = RequestMapper.INSTANCE.toRequestDto(request);
            emailService.sendEMailNotification(copyToPREmail, requestDtoForEmail);

            // Save Request History : 21 COPY_TO_PR_FAILED	Copy request to PR failed
            requestHistoryService.saveRequestHistoryByAction(readRequest, ACTIVITY_COPY_TO_PR_FAILED.id(), ex.getMessage());

            return CreateOraclePrResponse.builder().requisition(null).message(ex.getMessage()).build();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pr savePrNumber(String prNumber) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Pr pr = Pr.builder()
                .prNumber(prNumber)
                .tenant(tenant)
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .build();
        return prRepository.save(pr);
    }

    @Override
    public void saveRequestItemPR(Integer prId, List<Long> requestItemsId) {
        requestItemsId.forEach(item -> {
            RequestItem requestItem = requestItemService.getRequestItemByRequestItemId(item);
            if (requestItem != null) {
                requestItemPrService.saveRequestItemPR(requestItem.getRecId(), prId);
            }
        });
    }

    private void setRequestDto(String requestNo, List<Long> requestItemsId, Integer tenantId, RequestDto requestDto, Long requestId) {
        List<RequestItemV2Dto> requestItemDtos = requestItemService.getRequestItemByRequestIdList(requestId, requestItemsId, tenantId);
        requestItemDtos = setExistingPriceItemDto(tenantId, requestId, requestItemDtos);

        Optional<RequestDepartment> requestDepartment = Optional.ofNullable(requestDepartmentRepository.findTop1ByRequestId(requestId).orElse(null));
        DepartmentDto departmentDto = DepartmentMapper.INSTANCE.toDepartmentDto(requestDepartment.isPresent() ? requestDepartment.get().getDepartment() : null);
        String timeZone = uaaService.getUserTimeZone(AppUtil.getUser(), null);
        requestDto.setDepartmentDto(DepartmentMapper.INSTANCE.convertTimeStampByTimeZone(departmentDto, timeZone));
        requestDto.setRequestItemList(requestItemDtos);
        requestDto.setRefPRNumber(generateRefPrNumber(requestId, requestNo, tenantId));
    }

    private List<RequestItemV2Dto> setExistingPriceItemDto(Integer tenantId, Long requestId, List<RequestItemV2Dto> requestItemDtos) {
        return requestItemDtos.stream()
                .map(reqItem -> {
                    ExistingPriceItemResponseDto existingPriceItemDto = existingPriceItemService.getExistingPriceItemByRequestIdAndRequestItemId(requestId, reqItem.getRecId(), tenantId, reqItem.getSourcingDocNo());
                    reqItem.setExistingPriceItemDto(existingPriceItemDto);
                    return reqItem;
                })
                .collect(Collectors.toList());
    }

    private String generateRefPrNumber(Long requestId, String requestNo, Integer tenantId) {
        StringBuilder refPRNumber = new StringBuilder();
        Optional<String> maxRefPRNumberOpt = prRepository.findMaxPRNumberByRequestId(requestId, tenantId);
        Integer runnigNumber = 0;
        if (maxRefPRNumberOpt.isPresent()) {
            runnigNumber = Integer.parseInt(maxRefPRNumberOpt.get());
        }
        String refPRRunnigNumber = String.format("%02d", runnigNumber + 1);
        refPRNumber.append(PR_REF);
        refPRNumber.append(requestNo);
        refPRNumber.append("-");
        refPRNumber.append(refPRRunnigNumber);
        return refPRNumber.toString();
    }

}
