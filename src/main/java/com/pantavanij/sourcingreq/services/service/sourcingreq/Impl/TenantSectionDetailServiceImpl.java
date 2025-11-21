package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.*;
import com.pantavanij.sourcingreq.services.domain.mapper.DataSourceMapper;
import com.pantavanij.sourcingreq.services.domain.mapper.TenantSectionDetailMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class TenantSectionDetailServiceImpl implements TenantSectionDetailService {
    private final TenantSectionService tenantSectionService;
    private final TenantSectionRepository tenantSectionRepository;
    private final TenantSectionDetailRepository tenantSectionDetailRepository;
    private final TenantSectionDetailDataSourceRepository tenantSectionDetailDataSourceRepository;
    private final TenantSectionDetailDependencyRepository tenantSectionDetailDependencyRepository;
    private final TenantSectionDetailValidatorRepository tenantSectionDetailValidatorRepository;
    private final TenantSectionDetailWatchRepository tenantSectionDetailWatchRepository;
    private final TenantSectionDetailDescriptionRepository tenantSectionDetailDescriptionRepository;
    private final TenantService tenantService;
    private final DataSourceRepository dataSourceRepository;
    private final TenantOrganizationTemplateRepository tenantOrganizationTemplateRepository;

    @Override
    public List<TenantSectionDetailDto> getExportRequestRpt(Integer tenantId, Integer requestReportId, Integer organizationId) {
        List<TenantSectionDetail> tenantSectionDetailList;

        if (organizationId != null) {
            String tenantCode = AppUtil.getTenantId();
            Integer templateId = tenantOrganizationTemplateRepository
                    .findTemplateIdByTenantCodeAndOrganizationId(tenantCode, organizationId);
            if (templateId == null) {
                throw new BusinessException(
                        String.format("Template not found for tenantId=%s, organizationId=%s", tenantId, organizationId)
                );
            }
            tenantSectionDetailList =
                    tenantSectionDetailRepository.getTenantSectionDetailByTenantAndTemplateAndExportRequestRpt(
                            tenantId, templateId, requestReportId);
        } else {
            tenantSectionDetailList =
                    tenantSectionDetailRepository.getTenantSectionDetailByTenantAndExportRequestRpt(
                            tenantId, requestReportId);
        }

        return TenantSectionDetailMapper.INSTANCE.toTenantSectionDetailDto(tenantSectionDetailList);
    }


    @Override
    public List<TenantSectionDetailDto> getExportRequestItemRpt(Integer tenantId, Integer requestItemReportId, Integer organizationId) {
        List<TenantSectionDetail> tenantSectionDetailList;

        if (organizationId != null) {
            String tenantCode = AppUtil.getTenantId();
            Integer templateId = tenantOrganizationTemplateRepository
                    .findTemplateIdByTenantCodeAndOrganizationId(tenantCode, organizationId);
            if (templateId == null) {
                throw new BusinessException(
                        String.format("Template not found for tenantId=%s, organizationId=%s", tenantId, organizationId)
                );
            }
            tenantSectionDetailList =
                    tenantSectionDetailRepository.getTenantSectionDetailByTenantAndTemplateAndExportRequestItemRpt(
                            tenantId, templateId, requestItemReportId);
        } else {
            tenantSectionDetailList =
                    tenantSectionDetailRepository.getTenantSectionDetailByTenantAndExportRequestItemRpt(
                            tenantId, requestItemReportId);
        }

        return TenantSectionDetailMapper.INSTANCE.toTenantSectionDetailDto(tenantSectionDetailList);
    }



    @Override
    public boolean isRequiredField(Integer tenantId, String fieldName, Integer validatorId, String sectionType) {
        boolean result = false;
        TenantSectionDetail tenantSectionDetail = tenantSectionDetailRepository.getTenantSectionDetailByTenantAndFieldName(tenantId, fieldName, validatorId, sectionType);
        if(tenantSectionDetail != null) {
            result = true;
        }
        return result;
    }

    public List<TenantSectionDetailFieldNameDto> findTenantSectionDetailFieldName(
            List<String> types, String labelFormat, Integer organizationId) {

        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        List<TenantSectionDetailFieldNameDto> fieldNameDtoList = new ArrayList<>();
        List<Long> idList = new ArrayList<>();

        // ✅ 1) หา tenantTemplate จาก organizationId
        TenantTemplate tenantTemplate = null;

        if (organizationId != null) {
            Optional<TenantOrganizationTemplate> tenantOrgTemplateOpt =
                    tenantOrganizationTemplateRepository.findTop1ByIdTenantIdAndIdOrganizationId(
                            tenant.getRecId().intValue(), organizationId
                    );

            if (tenantOrgTemplateOpt.isPresent()) {
                tenantTemplate = tenantOrgTemplateOpt.get().getTenantTemplate();
            }
        }

        // ✅ 2) หา tenantSectionList โดยกรองตาม tenant + template (ถ้ามี)
        List<TenantSection> tenantSectionList;
        if (tenantTemplate != null) {
            tenantSectionList = tenantSectionRepository.findByTenantAndTemplateAndTypeIn(
                    tenant, tenantTemplate, types);
        } else {
            // fallback case (ไม่มี org หรือ mapping)
            tenantSectionList = tenantSectionRepository.findByTenantAndTypeIn(tenant, types);
        }

        List<Long> tenantSectionIds = tenantSectionList.stream()
                .map(TenantSection::getId)
                .collect(Collectors.toList());

        List<TenantSectionDetail> tenantSectionDetailList =
                tenantSectionDetailRepository.findByTenantAndTenantSectionIdIn(tenant, tenantSectionIds);

        for (TenantSectionDetail detail : tenantSectionDetailList) {
            if (!idList.contains(detail.getId())) {
                String copyFieldName = detail.getFieldName();
                String label = detail.getLabel();
                String reportLabel = detail.getReportLabel();
                if ("fieldNameLabel".equals(labelFormat)) {
                    label = copyFieldName.substring(0, 1).toUpperCase() + copyFieldName.substring(1);
                }

                TenantSectionDetailFieldNameDto dto = new TenantSectionDetailFieldNameDto();

                Optional<TenantSectionDetailDataSource> tenantSectionDetailDataSource =
                        tenantSectionDetailDataSourceRepository.findTop1ByTenantSectionDetailId(detail.getId());
                tenantSectionDetailDataSource.ifPresent(dataSrc -> {
                    Optional<DataSource> dataSource =
                            dataSourceRepository.findByRecId(dataSrc.getDataSource().getRecId());
                    dataSource.ifPresent(source ->
                            dto.setDataSource(DataSourceMapper.INSTANCE.toDataSourceObjDto(source)));
                });

                dto.setId(detail.getId());
                dto.setName(detail.getFieldName());
                dto.setFieldName(detail.getFieldName());
                dto.setValue(detail.getId());
                dto.setLabel(label);
                dto.setReportLabel(reportLabel);
                dto.setType(detail.getType());

                fieldNameDtoList.add(dto);
            }
            idList.add(detail.getId());
        }
        return fieldNameDtoList;
    }



    @Override
    public List<TenantSectionDetailFieldNameDto> getTenantSectionDetailFieldName(Integer tenantSectionId, Integer organizationId) {
        List<String> types = new ArrayList<>(List.of("REQ"));
        Optional<TenantSection> tenantSectionOpt = tenantSectionRepository.findByRecId(tenantSectionId);
        if (tenantSectionOpt.isPresent()) {
            TenantSection tenantSection = tenantSectionOpt.get();
            if (!types.contains(tenantSection.getType())) {
                types.add(tenantSection.getType());
            }
        }
        return findTenantSectionDetailFieldName(types,"fieldNameLabel", organizationId);
    }

    @Override
    public List<TenantSectionDetailFieldNameDto> getTenantSectionDetailFieldNameByTypes(String tenantSectionTypes, Integer organizationId) {
        List<String> types = Arrays.asList(tenantSectionTypes.split(","));
        return findTenantSectionDetailFieldName(types, "label", organizationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TenantSectionDetailDto createTenantSectionDetail(TenantSectionDetailRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            TenantSectionDto tenantSectionDto = tenantSectionService.getByRecId(request.getTenantSectionId());
            if (tenantSectionDto == null) {
                throw new DataNotFoundException(String.format(ApiMessage.E7096.description(), "Tenant section id: " + request.getTenantSectionId()));
            }

            // Handler sequence.
            if (!request.isVisible()) {
                request.setSequence(0);
            } else {
                Optional<TenantSectionDetail> detailOptional = tenantSectionDetailRepository.findFirstByTenantRecIdAndTenantSectionIdOrderBySequenceDesc(tenant.getRecId(), request.getTenantSectionId().longValue());
                if (detailOptional.isPresent() && request.getSequence() == 0) {
                    request.setSequence(detailOptional.get().getSequence() + 1);
                } else if (detailOptional.isEmpty() && request.getSequence() == 0) {
                    request.setSequence(1);
                } else {
                    detailOptional.ifPresent(detail -> tenantSectionDetailRepository.reOrderOtherSequence(tenant.getRecId(), request.getTenantSectionId(), detail.getSequence() + 1, request.getSequence()));
                }
            }

            // Handler item export sequence.
//            if (!request.isExportItemRpt()) {
//                request.setExportItemRptSequence(0);
//            } else {
//                Optional<TenantSectionDetail> detailOptional = tenantSectionDetailRepository.findFirstByTenantRecIdAndTenantSectionIdOrderByExportItemRptSequenceDesc(tenant.getRecId(), request.getTenantSectionId().longValue());
//                if (detailOptional.isPresent() && request.getExportItemRptSequence() == 0) {
//                    request.setExportItemRptSequence(detailOptional.get().getExportItemRptSequence() + 1);
//                } else if (detailOptional.isEmpty() && request.getExportItemRptSequence() == 0) {
//                    request.setExportItemRptSequence(1);
//                } else {
//                    detailOptional.ifPresent(detail -> tenantSectionDetailRepository.reOrderOtherExportItemRptSequence(tenant.getRecId(), request.getTenantSectionId(), detail.getExportItemRptSequence() + 1, request.getExportItemRptSequence()));
//                }
//            }

            // Handler request export sequence.
//            if (!request.isExportRequestRpt()) {
//                request.setExportRequestRptSequence(0);
//            } else {
//                Optional<TenantSectionDetail> detailOptional = tenantSectionDetailRepository.findFirstByTenantRecIdAndTenantSectionIdOrderByExportRequestRptSequenceDesc(tenant.getRecId(), request.getTenantSectionId().longValue());
//                if (detailOptional.isPresent() && request.getExportRequestRptSequence() == 0) {
//                    request.setExportRequestRptSequence(detailOptional.get().getExportRequestRptSequence() + 1);
//                } else if (detailOptional.isEmpty() && request.getExportRequestRptSequence() == 0) {
//                    request.setExportRequestRptSequence(1);
//                } else {
//                    detailOptional.ifPresent(detail -> tenantSectionDetailRepository.reOrderOtherExportRequestSequence(tenant.getRecId(), request.getTenantSectionId(),detail.getExportRequestRptSequence() + 1, request.getExportRequestRptSequence()));
//                }
//            }

            // Handler header sequence.
            if (!request.isHeader()) {
                request.setHeaderSequence(0);
            } else {
                Optional<TenantSectionDetail> detailOptional = tenantSectionDetailRepository.findFirstByTenantRecIdAndTenantSectionIdOrderByHeaderSequenceDesc(tenant.getRecId(), request.getTenantSectionId().longValue());
                if (detailOptional.isPresent() && request.getHeaderSequence() == 0) {
                    request.setHeaderSequence(detailOptional.get().getHeaderSequence() + 1);
                } else if (detailOptional.isEmpty() && request.getHeaderSequence() == 0) {
                    request.setHeaderSequence(1);
                } else {
                    detailOptional.ifPresent(detail -> tenantSectionDetailRepository.reOrderOtherHeaderSequence(tenant.getRecId(), request.getTenantSectionId(), detail.getHeaderSequence() + 1, request.getHeaderSequence()));
                }
            }

            // Handler mode view sequence.
            if (!request.isModeView()) {
                request.setModeViewSequence(0);
            } else {
                Optional<TenantSectionDetail> detailOptional = tenantSectionDetailRepository.findFirstByTenantRecIdAndTenantSectionIdOrderByModeViewSequenceDesc(tenant.getRecId(), request.getTenantSectionId().longValue());
                if (detailOptional.isPresent() && request.getModeViewSequence() == 0) {
                    request.setModeViewSequence(detailOptional.get().getModeViewSequence() + 1);
                } else if (detailOptional.isEmpty() && request.getModeViewSequence() == 0) {
                    request.setModeViewSequence(1);
                } else {
                    detailOptional.ifPresent(detail -> tenantSectionDetailRepository.reOrderOtherModeViewSequence(tenant.getRecId(), request.getTenantSectionId(), detail.getModeViewSequence() + 1, request.getModeViewSequence()));
                }
            }

            // Handler mode view sequence SQN
            if (!request.isModeViewSQN()) {
                request.setModeViewSequenceSQN(0);
            } else {
                Optional<TenantSectionDetail> detailOptional = tenantSectionDetailRepository.findFirstByTenantRecIdAndTenantSectionIdOrderByModeViewSequenceSQNDesc(tenant.getRecId(), request.getTenantSectionId().longValue());
                if (detailOptional.isPresent() && request.getModeViewSequenceSQN() == 0) {
                    request.setModeViewSequenceSQN(detailOptional.get().getModeViewSequenceSQN() + 1);
                } else if (detailOptional.isEmpty() && request.getModeViewSequenceSQN() == 0) {
                    request.setModeViewSequenceSQN(1);
                } else {
                    detailOptional.ifPresent(detail -> tenantSectionDetailRepository.reOrderOtherModeViewSequenceSQN(tenant.getRecId(), request.getTenantSectionId(), detail.getModeViewSequenceSQN() + 1, request.getModeViewSequenceSQN()));
                }
            }

            // Handler mode view sequence SQV
            if (!request.isModeViewSQV()) {
                request.setModeViewSequenceSQV(0);
            } else {
                Optional<TenantSectionDetail> detailOptional = tenantSectionDetailRepository.findFirstByTenantRecIdAndTenantSectionIdOrderByModeViewSequenceSQVDesc(tenant.getRecId(), request.getTenantSectionId().longValue());
                if (detailOptional.isPresent() && request.getModeViewSequenceSQV() == 0) {
                    request.setModeViewSequenceSQV(detailOptional.get().getModeViewSequenceSQV() + 1);
                } else if (detailOptional.isEmpty() && request.getModeViewSequenceSQV() == 0) {
                    request.setModeViewSequenceSQV(1);
                } else {
                    detailOptional.ifPresent(detail -> tenantSectionDetailRepository.reOrderOtherModeViewSequenceSQV(tenant.getRecId(), request.getTenantSectionId(), detail.getModeViewSequenceSQV() + 1, request.getModeViewSequenceSQV()));
                }
            }

            // Handler mode view sequence SQP
            if (!request.isModeViewSQP()) {
                request.setModeViewSequenceSQP(0);
            } else {
                Optional<TenantSectionDetail> detailOptional = tenantSectionDetailRepository.findFirstByTenantRecIdAndTenantSectionIdOrderByModeViewSequenceSQPDesc(tenant.getRecId(), request.getTenantSectionId().longValue());
                if (detailOptional.isPresent() && request.getModeViewSequenceSQP() == 0) {
                    request.setModeViewSequenceSQP(detailOptional.get().getModeViewSequenceSQP() + 1);
                } else if (detailOptional.isEmpty() && request.getModeViewSequenceSQP() == 0) {
                    request.setModeViewSequenceSQP(1);
                } else {
                    detailOptional.ifPresent(detail -> tenantSectionDetailRepository.reOrderOtherModeViewSequenceSQP(tenant.getRecId(), request.getTenantSectionId(), detail.getModeViewSequenceSQP() + 1, request.getModeViewSequenceSQP()));
                }
            }

            // Handler mode view sequence SQA
            if (!request.isModeViewSQA()) {
                request.setModeViewSequenceSQA(0);
            } else {
                Optional<TenantSectionDetail> detailOptional = tenantSectionDetailRepository.findFirstByTenantRecIdAndTenantSectionIdOrderByModeViewSequenceSQADesc(tenant.getRecId(), request.getTenantSectionId().longValue());
                if (detailOptional.isPresent() && request.getModeViewSequenceSQA() == 0) {
                    request.setModeViewSequenceSQA(detailOptional.get().getModeViewSequenceSQA() + 1);
                } else if (detailOptional.isEmpty() && request.getModeViewSequenceSQA() == 0) {
                    request.setModeViewSequenceSQA(1);
                } else {
                    detailOptional.ifPresent(detail -> tenantSectionDetailRepository.reOrderOtherModeViewSequenceSQA(tenant.getRecId(), request.getTenantSectionId(), detail.getModeViewSequenceSQA() + 1, request.getModeViewSequenceSQA()));
                }
            }

            TenantSectionDetail saveDetail = tenantSectionDetailRepository.save(getTenantSectionDetailCreate(request, tenant));
            if (request.getTenantSectionDetailDataSourceRequest() != null) {
                TenantSectionDetailDataSourceRequest detailDataSourceRequest = request.getTenantSectionDetailDataSourceRequest();
                TenantSectionDetailDataSourceKey newKey = new TenantSectionDetailDataSourceKey(
                        saveDetail.getId(), detailDataSourceRequest.getDataSourceId()
                );

                TenantSectionDetailDataSource detailDataSourceEntity = TenantSectionDetailDataSource.builder()
                    .id(newKey)
                    .tenantSectionDetail(saveDetail)
                    .dataSource(DataSource.builder()
                        .recId(detailDataSourceRequest.getDataSourceId())
                        .build())
                    .build();

                tenantSectionDetailDataSourceRepository.save(detailDataSourceEntity);
            }
            if (request.getTenantSectionDetailDependencyRequestList() != null && !request.getTenantSectionDetailDependencyRequestList().isEmpty()) {
                List<TenantSectionDetailDependencyRequest> detailDependenciesRequestList = request.getTenantSectionDetailDependencyRequestList();
                detailDependenciesRequestList.forEach(detailDependency -> {
                    TenantSectionDetailDependencyKey newKey = new TenantSectionDetailDependencyKey(
                            saveDetail.getId(), detailDependency.getSequence()
                    );

                    TenantSectionDetailDependency detailDependencyEntity = TenantSectionDetailDependency.builder()
                        .id(newKey)
                        .tenantSectionDetail(saveDetail)
                        .name(detailDependency.getName())
                        .value(!detailDependency.getValues().isEmpty() ? String.join(",", detailDependency.getValues()) : null)
                        .groupName(detailDependency.getGroupName())
                        .action(detailDependency.getAction())
                        .build();

                    tenantSectionDetailDependencyRepository.save(detailDependencyEntity);
                });
            }
            if (request.getTenantSectionDetailValidatorRequestList() != null && !request.getTenantSectionDetailValidatorRequestList().isEmpty()) {
                List<TenantSectionDetailValidatorRequest> detailValidatorRequestList = request.getTenantSectionDetailValidatorRequestList();
                detailValidatorRequestList.forEach(detailValidator -> {
                    TenantSectionDetailValidatorKey newKey = new TenantSectionDetailValidatorKey(
                            saveDetail.getId(), detailValidator.getSequence()
                    );

                    TenantSectionDetailValidator detailValidatorEntity = TenantSectionDetailValidator.builder()
                        .id(newKey)
                        .tenantSectionDetail(saveDetail)
                        .validator(Validator.builder()
                            .recId(detailValidator.getValidatorId())
                            .build())
                        .sequence(detailValidator.getSequence())
                        .name(detailValidator.getName())
                        .value(detailValidator.getValue())
                        .build();

                    tenantSectionDetailValidatorRepository.save(detailValidatorEntity);
                });
            }
            if (request.getTenantSectionDetailWatchRequestList() != null && !request.getTenantSectionDetailWatchRequestList().isEmpty()) {
                List<TenantSectionDetailWatchRequest> detailWatchRequestList = request.getTenantSectionDetailWatchRequestList();
                detailWatchRequestList.forEach(detailWatch -> {
                    TenantSectionDetailWatchKey newKey = new TenantSectionDetailWatchKey(
                            saveDetail.getId(), detailWatch.getSequence()
                    );

                    TenantSectionDetailWatch detailWatchEntity = TenantSectionDetailWatch.builder()
                        .id(newKey)
                        .tenantSectionDetail(saveDetail)
                        .groupName(detailWatch.getGroupName())
                        .fieldName(detailWatch.getFieldName())
                        .originalFieldName(detailWatch.getOriginalFieldName())
                        .updatedFieldName(detailWatch.getUpdatedFieldName())
                        .values(!detailWatch.getValues().isEmpty() ? String.join(",", detailWatch.getValues()) : null)
                        .build();

                    tenantSectionDetailWatchRepository.save(detailWatchEntity);
                });
            }
            if (request.getTenantSectionDetailDescriptionRequest() != null) {
                TenantSectionDetailDescriptionRequest detailDescriptionRequest = request.getTenantSectionDetailDescriptionRequest();
                TenantSectionDetailDescription detailDescriptionEntity = TenantSectionDetailDescription.builder()
                    .tenantSectionDetail(saveDetail)
                    .description(detailDescriptionRequest.getDescription())
                    .descriptionAction(detailDescriptionRequest.getDescriptionAction())
                    .descriptionLabelAction(detailDescriptionRequest.getDescriptionLabelAction())
                    .disable(detailDescriptionRequest.getDisable())
                    .createdBy(AppUtil.getUserName())
                    .createdDate(DateTimeUtil.getTimestampUTC())
                    .updatedBy(AppUtil.getUserName())
                    .updatedDate(DateTimeUtil.getTimestampUTC())
                    .build();

                tenantSectionDetailDescriptionRepository.save(detailDescriptionEntity);
            }

            return TenantSectionDetailMapper.INSTANCE.toTenantSectionDetailDto(saveDetail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private TenantSectionDetail getTenantSectionDetailCreate(TenantSectionDetailRequest detailRequest, Tenant tenant) {
        TenantSectionDetail detail = new TenantSectionDetail();
        detail.setTenantSection(TenantSection.builder()
                .id(detailRequest.getTenantSectionId().longValue())
                .build());
        detail.setTenant(tenant);
        detail.setName(detailRequest.getName());
        detail.setFieldName(detailRequest.getFieldName());
        detail.setFieldGroupName(detailRequest.getFieldGroupName());
        detail.setFieldGroupLabel(detailRequest.getFieldGroupLabel());
        detail.setType(detailRequest.getType());
        detail.setTooltip(detailRequest.getTooltip());
        detail.setLabel(detailRequest.getLabel());
        detail.setReportLabel(detailRequest.getReportLabel());
        detail.setPreSelectName(detailRequest.getPreSelectName());
        detail.setPreSelectValues(detailRequest.getPreSelectValues());
        detail.setSpan(detailRequest.getSpan());
        detail.setPreSpan(detailRequest.getPreSpan());
        detail.setPostSpan(detailRequest.getPostSpan());
        detail.setPlaceholder(detailRequest.getPlaceholder());
        detail.setDependencyObjectName(detailRequest.getDependencyObjectName());
        detail.setDependencyObjectValues(detailRequest.getDependencyObjectValues() != null &&
                !detailRequest.getDependencyObjectValues().isEmpty() ? String.join(",", detailRequest.getDependencyObjectValues()): null);
        detail.setWatchResetValue(detailRequest.getWatchResetValue());
        detail.setWatchOriginalFieldName(detailRequest.getWatchOriginalFieldName());
        detail.setWatchUpdatedFieldName(detailRequest.getWatchUpdatedFieldName());
        detail.setDisableObjectName(detailRequest.getDisableObjectName());
        detail.setDisableObjectValues(detailRequest.getDisableObjectValues() != null &&
                !detailRequest.getDisableObjectValues().isEmpty() ? String.join(",", detailRequest.getDisableObjectValues()): null);
        detail.setDisabledStyles(detailRequest.getDisabledStyles());
        detail.setCssStyles(detailRequest.getCssStyles());
        detail.setMaxLength(detailRequest.getMaxLength());
        detail.setHeight(detailRequest.getHeight());
        detail.setWidth(detailRequest.getWidth());
        detail.setAlign(detailRequest.getAlign());
        detail.setFixed(detailRequest.getFixed());
        detail.setSequence(detailRequest.getSequence());
        detail.setReportFilter(detailRequest.isReportFilter());
        detail.setVisible(detailRequest.isVisible());
        detail.setForceVisible(detailRequest.isForceVisible());
        detail.setFullPreview(detailRequest.isFullPreview());
        detail.setPreview(detailRequest.isPreview());
        detail.setPreviewSequence(detailRequest.getPreviewSequence());
//        detail.setExportItemRpt(detailRequest.isExportItemRpt());
//        detail.setExportItemRptSequence(detailRequest.getExportItemRptSequence());
//        detail.setExportRequestRpt(detailRequest.isExportRequestRpt());
//        detail.setExportRequestRptSequence(detailRequest.getExportRequestRptSequence());
        detail.setHeader(detailRequest.isHeader());
        detail.setHeaderSequence(detailRequest.getHeaderSequence());
        detail.setCreatedBy(AppUtil.getUserName());
        detail.setCreatedDate(DateTimeUtil.getTimestampUTC());
        detail.setUpdatedBy(AppUtil.getUserName());
        detail.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        detail.setHeaderLabel(detailRequest.getHeaderLabel());
        detail.setDisabled(detailRequest.isDisabled());
        detail.setExcelItemTemplate(detailRequest.isExcelItemTemplate());
        detail.setModeView(detailRequest.isModeView());
        detail.setModeViewSequence(detailRequest.getModeViewSequence());
        detail.setModeViewSQN(detailRequest.isModeViewSQN());
        detail.setModeViewSequenceSQN(detailRequest.getModeViewSequenceSQN());
        detail.setModeViewSQV(detailRequest.isModeViewSQV());
        detail.setModeViewSequenceSQV(detailRequest.getModeViewSequenceSQV());
        detail.setModeViewSQP(detailRequest.isModeViewSQP());
        detail.setModeViewSequenceSQP(detailRequest.getModeViewSequenceSQP());
        detail.setModeViewSQA(detailRequest.isModeViewSQA());
        detail.setModeViewSequenceSQA(detailRequest.getModeViewSequenceSQA());
        detail.setMode(detailRequest.getMode());
        detail.setReportExampleData(detailRequest.getReportExampleData());
        detail.setRemark(detailRequest.getRemark());
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TenantSectionDetailDto updateTenantSectionDetail(TenantSectionDetailRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            TenantSectionDto tenantSectionDto = tenantSectionService.getByRecId(request.getTenantSectionId());
            if (tenantSectionDto == null) {
                throw new DataNotFoundException(String.format(ApiMessage.E7096.description(), "Tenant section id: " + request.getTenantSectionId()));
            }

            Optional<TenantSectionDetail> detailOptional = tenantSectionDetailRepository.findFirstByTenantAndId(tenant, request.getRecId().longValue());
            if (detailOptional.isPresent()) {
                // Handler sequence.
                if (!request.isVisible()) {
                    request.setSequence(0);
                    tenantSectionDetailRepository.updateVisibleAndSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getSequence());
                    tenantSectionDetailRepository.reOrderSequence(tenant.getRecId(), request.getTenantSectionId());
                }
//                else {
//                    tenantSectionDetailRepository.reOrderOtherSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getSequence(), request.getSequence());
//                }

                // Handler item export sequence.
//                if (!request.isExportItemRpt()) {
//                    request.setExportItemRptSequence(0);
//                    tenantSectionDetailRepository.updateExportItemRptAndSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getExportItemRptSequence());
//                    tenantSectionDetailRepository.reOrderExportItemRptSequence(tenant.getRecId(), request.getTenantSectionId());
//                }
////                else {
////                    tenantSectionDetailRepository.reOrderOtherExportItemRptSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getExportItemRptSequence() + 1, request.getExportItemRptSequence());
////                }

                // Handler request export sequence.
//                if (!request.isExportRequestRpt()) {
//                    request.setExportRequestRptSequence(0);
//                    tenantSectionDetailRepository.updateExportRequestRptAndSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getExportRequestRptSequence());
//                    tenantSectionDetailRepository.reOrderExportRequestRptSequence(tenant.getRecId(), request.getTenantSectionId());
//                }
////                else {
////                    tenantSectionDetailRepository.reOrderOtherExportRequestSequence(tenant.getRecId(), request.getTenantSectionId(),detailOptional.get().getExportRequestRptSequence() + 1, request.getExportRequestRptSequence());
////                }

                // Handler header sequence.
                if (!request.isHeader()) {
                    request.setHeaderSequence(0);
                    tenantSectionDetailRepository.updateHeaderAndSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getHeaderSequence());
                    tenantSectionDetailRepository.reOrderHeaderSequence(tenant.getRecId(), request.getTenantSectionId());
                }
//                else {
//                    tenantSectionDetailRepository.reOrderOtherHeaderSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getHeaderSequence() + 1, request.getHeaderSequence());
//                }

                // Handler mode view sequence.
                if (!request.isModeView()) {
                    request.setModeViewSequence(0);
                    tenantSectionDetailRepository.updateModeViewAndSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getModeViewSequence());
                    tenantSectionDetailRepository.reOrderModeViewSequence(tenant.getRecId(), request.getTenantSectionId());
                }
//                else {
//                    tenantSectionDetailRepository.reOrderOtherModeViewSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getModeViewSequence() + 1, request.getModeViewSequence());
//                }

                // Handler mode view sequence SQN
                if (!request.isModeViewSQN()) {
                    request.setModeViewSequenceSQN(0);
                    tenantSectionDetailRepository.updateModeViewSQNAndSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getModeViewSequenceSQN());
                    tenantSectionDetailRepository.reOrderModeViewSequenceSQN(tenant.getRecId(), request.getTenantSectionId());
                }
//                else {
//                    tenantSectionDetailRepository.reOrderOtherModeViewSequenceSQN(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getModeViewSequenceSQN() + 1, request.getModeViewSequenceSQN());
//                }

                // Handler mode view sequence SQV
                if (!request.isModeViewSQV()) {
                    request.setModeViewSequenceSQV(0);
                    tenantSectionDetailRepository.updateModeViewSQVAndSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getModeViewSequenceSQV());
                    tenantSectionDetailRepository.reOrderModeViewSequenceSQV(tenant.getRecId(), request.getTenantSectionId());
                }
//                else {
//                    tenantSectionDetailRepository.reOrderOtherModeViewSequenceSQV(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getModeViewSequenceSQV() + 1, request.getModeViewSequenceSQV());
//                }

                // Handler mode view sequence SQP
                if (!request.isModeViewSQP()) {
                    request.setModeViewSequenceSQP(0);
                    tenantSectionDetailRepository.updateModeViewSQPAndSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getModeViewSequenceSQP());
                    tenantSectionDetailRepository.reOrderModeViewSequenceSQP(tenant.getRecId(), request.getTenantSectionId());
                }
//                else {
//                    tenantSectionDetailRepository.reOrderOtherModeViewSequenceSQP(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getModeViewSequenceSQP() + 1, request.getModeViewSequenceSQP());
//                }

                // Handler mode view sequence SQA
                if (!request.isModeViewSQA()) {
                    request.setModeViewSequenceSQA(0);
                    tenantSectionDetailRepository.updateModeViewSQAAndSequence(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getModeViewSequenceSQA());
                    tenantSectionDetailRepository.reOrderModeViewSequenceSQA(tenant.getRecId(), request.getTenantSectionId());
                }
//                else {
//                    tenantSectionDetailRepository.reOrderOtherModeViewSequenceSQA(tenant.getRecId(), request.getTenantSectionId(), detailOptional.get().getModeViewSequenceSQA() + 1, request.getModeViewSequenceSQA());
//                }

                TenantSectionDetail tenantSectionDetailUpdate = tenantSectionDetailRepository.save(getTenantSectionDetailUpdate(detailOptional.get(), request));
                return TenantSectionDetailMapper.INSTANCE.toTenantSectionDetailDto(tenantSectionDetailUpdate);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private TenantSectionDetail getTenantSectionDetailUpdate(TenantSectionDetail detail, TenantSectionDetailRequest detailRequest) {
        detail.setName(detailRequest.getName());
        detail.setFieldName(detailRequest.getFieldName());
        detail.setFieldGroupName(detailRequest.getFieldGroupName());
        detail.setFieldGroupLabel(detailRequest.getFieldGroupLabel());
        detail.setType(detailRequest.getType());
        detail.setTooltip(detailRequest.getTooltip());
        detail.setLabel(detailRequest.getLabel());
        detail.setReportLabel(detailRequest.getReportLabel());
        detail.setPreSelectName(detailRequest.getPreSelectName());
        detail.setPreSelectValues(detailRequest.getPreSelectValues());
        detail.setSpan(detailRequest.getSpan());
        detail.setPreSpan(detailRequest.getPreSpan());
        detail.setPostSpan(detailRequest.getPostSpan());
        detail.setPlaceholder(detailRequest.getPlaceholder());
        detail.setDependencyObjectName(detailRequest.getDependencyObjectName());
        detail.setDependencyObjectValues(detailRequest.getDependencyObjectValues() != null &&
                !detailRequest.getDependencyObjectValues().isEmpty() ? String.join(",", detailRequest.getDependencyObjectValues()): null);
        detail.setWatchResetValue(detailRequest.getWatchResetValue());
        detail.setWatchOriginalFieldName(detailRequest.getWatchOriginalFieldName());
        detail.setWatchUpdatedFieldName(detailRequest.getWatchUpdatedFieldName());
        detail.setDisableObjectName(detailRequest.getDisableObjectName());
        detail.setDisableObjectValues(detailRequest.getDisableObjectValues() != null &&
                !detailRequest.getDisableObjectValues().isEmpty() ? String.join(",", detailRequest.getDisableObjectValues()): null);
        detail.setDisabledStyles(detailRequest.getDisabledStyles());
        detail.setCssStyles(detailRequest.getCssStyles());
        detail.setMaxLength(detailRequest.getMaxLength());
        detail.setHeight(detailRequest.getHeight());
        detail.setWidth(detailRequest.getWidth());
        detail.setAlign(detailRequest.getAlign());
        detail.setFixed(detailRequest.getFixed());
        detail.setSequence(detailRequest.getSequence());
        detail.setReportFilter(detailRequest.isReportFilter());
        detail.setVisible(detailRequest.isVisible());
        detail.setForceVisible(detailRequest.isForceVisible());
        detail.setFullPreview(detailRequest.isFullPreview());
        detail.setPreview(detailRequest.isPreview());
        detail.setPreviewSequence(detailRequest.getPreviewSequence());
//        detail.setExportItemRpt(detailRequest.isExportItemRpt());
//        detail.setExportItemRptSequence(detailRequest.getExportItemRptSequence());
//        detail.setExportRequestRpt(detailRequest.isExportRequestRpt());
//        detail.setExportRequestRptSequence(detailRequest.getExportRequestRptSequence());
        detail.setHeader(detailRequest.isHeader());
        detail.setHeaderSequence(detailRequest.getHeaderSequence());
        detail.setUpdatedBy(AppUtil.getUserName());
        detail.setUpdatedDate(DateTimeUtil.getTimestampUTC());
        detail.setHeaderLabel(detailRequest.getHeaderLabel());
        detail.setDisabled(detailRequest.isDisabled());
        detail.setExcelItemTemplate(detailRequest.isExcelItemTemplate());
        detail.setModeView(detailRequest.isModeView());
        detail.setModeViewSequence(detailRequest.getModeViewSequence());
        detail.setModeViewSQN(detailRequest.isModeViewSQN());
        detail.setModeViewSequenceSQN(detailRequest.getModeViewSequenceSQN());
        detail.setModeViewSQV(detailRequest.isModeViewSQV());
        detail.setModeViewSequenceSQV(detailRequest.getModeViewSequenceSQV());
        detail.setModeViewSQP(detailRequest.isModeViewSQP());
        detail.setModeViewSequenceSQP(detailRequest.getModeViewSequenceSQP());
        detail.setModeViewSQA(detailRequest.isModeViewSQA());
        detail.setModeViewSequenceSQA(detailRequest.getModeViewSequenceSQA());
        detail.setMode(detailRequest.getMode());
        detail.setReportExampleData(detailRequest.getReportExampleData());
        detail.setRemark(detailRequest.getRemark());

        if (detailRequest.getTenantSectionDetailDataSourceRequest() != null) {
            updateTenantSectionDetailDataSource(detail, detailRequest.getTenantSectionDetailDataSourceRequest());
        }

        if (detailRequest.getTenantSectionDetailDependencyRequestList() != null && !detailRequest.getTenantSectionDetailDependencyRequestList().isEmpty()) {
            updateTenantSectionDetailDependency(detail, detailRequest.getTenantSectionDetailDependencyRequestList());
        } else {
            List<TenantSectionDetailDependency> tenantSectionDetailDependencyList = detail.getTenantSectionDetailDependencyList();
            Integer idToRemove = detailRequest.getRecId();
            tenantSectionDetailDependencyList.removeIf(dependency -> dependency.getId().getTenantSectionDetailId().intValue() == idToRemove);
            detail.setTenantSectionDetailDependencyList(tenantSectionDetailDependencyList);
        }

        if (detailRequest.getTenantSectionDetailValidatorRequestList() != null && !detailRequest.getTenantSectionDetailValidatorRequestList().isEmpty()) {
            updateTenantSectionDetailValidator(detail, detailRequest.getTenantSectionDetailValidatorRequestList());
        } else {
            List<TenantSectionDetailValidator> tenantSectionDetailValidatorList = detail.getTenantSectionDetailValidatorList();
            Integer idToRemove = detailRequest.getRecId();
            tenantSectionDetailValidatorList.removeIf(validator -> validator.getId().getTenantSectionDetailId().intValue() == idToRemove);
            detail.setTenantSectionDetailValidatorList(tenantSectionDetailValidatorList);
        }

        if (detailRequest.getTenantSectionDetailWatchRequestList() != null && !detailRequest.getTenantSectionDetailWatchRequestList().isEmpty()) {
            updateTenantSectionDetailWatch(detail, detailRequest.getTenantSectionDetailWatchRequestList());
        } else {
            List<TenantSectionDetailWatch> tenantSectionDetailWatchList = detail.getTenantSectionDetailWatchList();
            Integer idToRemove = detailRequest.getRecId();
            tenantSectionDetailWatchList.removeIf(watch -> watch.getId().getTenantSectionDetailId().intValue() == idToRemove);
            detail.setTenantSectionDetailWatchList(tenantSectionDetailWatchList);
        }

        if (detailRequest.getTenantSectionDetailDescriptionRequest() != null) {
            updateTenantSectionDetailDescription(detail, detailRequest.getTenantSectionDetailDescriptionRequest());
        } else {
            detail.setTenantSectionDetailDescription(null);
        }

        return detail;
    }

    private void updateTenantSectionDetailDescription(TenantSectionDetail detail, TenantSectionDetailDescriptionRequest detailDescriptionRequest) {
        TenantSectionDetailDescription existingDescription = detail.getTenantSectionDetailDescription();
        if (existingDescription != null &&
                existingDescription.getTenantSectionDetail().getId().intValue() == detailDescriptionRequest.getTenantSectionDetailId()) {
            existingDescription.setDescription(detailDescriptionRequest.getDescription());
            existingDescription.setDescriptionLabelAction(detailDescriptionRequest.getDescriptionLabelAction());
            existingDescription.setDescriptionAction(detailDescriptionRequest.getDescriptionAction());
            existingDescription.setDisable(detailDescriptionRequest.getDisable());
            existingDescription.setUpdatedBy(AppUtil.getUserName());
            existingDescription.setUpdatedDate(DateTimeUtil.getTimestampUTC());
            detail.setTenantSectionDetailDescription(existingDescription);

        } else {
            TenantSectionDetailDescription newDescription = TenantSectionDetailDescription.builder()
                .tenantSectionDetail(TenantSectionDetail.builder()
                    .id(detailDescriptionRequest.getTenantSectionDetailId().longValue())
                    .build())
                .description(detailDescriptionRequest.getDescription())
                .descriptionLabelAction(detailDescriptionRequest.getDescriptionLabelAction())
                .descriptionAction(detailDescriptionRequest.getDescriptionAction())
                .disable(detailDescriptionRequest.getDisable())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();

            detail.setTenantSectionDetailDescription(newDescription);
        }
    }

    private void updateTenantSectionDetailWatch(TenantSectionDetail detail, List<TenantSectionDetailWatchRequest> detailWatchRequestList) {
        List<TenantSectionDetailWatch> existingWatchList = detail.getTenantSectionDetailWatchList();
        Map<TenantSectionDetailWatchKey, TenantSectionDetailWatch> existingWatchMap = existingWatchList
            .stream()
            .collect(Collectors.toMap(TenantSectionDetailWatch::getId, Function.identity()));

       for (TenantSectionDetailWatchRequest request : detailWatchRequestList) {
           TenantSectionDetailWatchKey newKay = new TenantSectionDetailWatchKey(
               request.getTenantSectionDetailId().longValue(), request.getSequence()
           );

           Optional<TenantSectionDetailWatch> existingWatchOpt = existingWatchMap.values()
               .stream()
               .filter(detailWatch -> detailWatch.getId().getTenantSectionDetailId() == request.getTenantSectionDetailId().intValue())
               .findFirst();

           if (existingWatchOpt.isPresent()) {
               TenantSectionDetailWatch updateWatch = TenantSectionDetailWatch.builder()
                    .id(newKay)
                    .tenantSectionDetail(detail)
                    .fieldName(request.getFieldName())
                    .originalFieldName(request.getOriginalFieldName())
                    .updatedFieldName(request.getUpdatedFieldName())
                    .groupName(request.getGroupName())
                    .values(request.getValues() != null && !request.getValues().isEmpty() ? String.join(",", request.getValues()) : "")
                    .build();
               existingWatchList.add(updateWatch);

           } else {
               TenantSectionDetailWatch newWatch = TenantSectionDetailWatch.builder()
                   .id(newKay)
                   .tenantSectionDetail(TenantSectionDetail.builder()
                       .id(request.getTenantSectionDetailId().longValue())
                       .build())
                   .fieldName(request.getFieldName())
                   .originalFieldName(request.getOriginalFieldName())
                   .updatedFieldName(request.getUpdatedFieldName())
                   .groupName(request.getGroupName())
                   .values(request.getValues() != null && !request.getValues().isEmpty() ? String.join(",", request.getValues()) : "")
                   .build();
               existingWatchList.add(newWatch);
           }
       }

        existingWatchMap.values().forEach(existingWatchList::remove);
        detail.setTenantSectionDetailWatchList(existingWatchList);
    }

    private void updateTenantSectionDetailValidator(TenantSectionDetail detail, List<TenantSectionDetailValidatorRequest> validatorRequestList) {
        List<TenantSectionDetailValidator> existingValidatorList = detail.getTenantSectionDetailValidatorList();
        Map<TenantSectionDetailValidatorKey, TenantSectionDetailValidator> existingValidatorMap = existingValidatorList
            .stream()
            .collect(Collectors.toMap(TenantSectionDetailValidator::getId, Function.identity()));

        for (TenantSectionDetailValidatorRequest request : validatorRequestList) {
            TenantSectionDetailValidatorKey newKey = new TenantSectionDetailValidatorKey(
                request.getTenantSectionDetailId().longValue(),
                request.getValidatorId()
            );

            Optional<TenantSectionDetailValidator> existingValidatorOpt = existingValidatorMap.values()
                .stream()
                .filter(detailValidator -> detailValidator.getId().getTenantSectionDetailId().intValue() == request.getTenantSectionDetailId())
                .findFirst();

            if (existingValidatorOpt.isPresent()) {
                TenantSectionDetailValidator updateValidator = TenantSectionDetailValidator.builder()
                    .id(newKey)
                    .tenantSectionDetail(detail)
                    .validator(Validator.builder()
                        .recId(request.getValidatorId())
                        .build())
                    .name(request.getName())
                    .value(request.getValue())
                    .sequence(request.getSequence())
                    .build();
                existingValidatorList.add(updateValidator);

            } else {
                TenantSectionDetailValidator newValidator = TenantSectionDetailValidator.builder()
                    .id(newKey)
                    .tenantSectionDetail(TenantSectionDetail.builder()
                        .id(request.getTenantSectionDetailId().longValue())
                        .build())
                    .validator(Validator.builder()
                        .recId(request.getValidatorId())
                        .build())
                    .name(request.getName())
                    .value(request.getValue())
                    .sequence(request.getSequence())
                    .build();
                existingValidatorList.add(newValidator);
            }
        }

        existingValidatorMap.values().forEach(existingValidatorList::remove);
        detail.setTenantSectionDetailValidatorList(existingValidatorList);
    }

    private void updateTenantSectionDetailDependency(TenantSectionDetail detail, List<TenantSectionDetailDependencyRequest> dependencyRequestList) {
        List<TenantSectionDetailDependency> existingDependencyList = detail.getTenantSectionDetailDependencyList();
        Map<TenantSectionDetailDependencyKey, TenantSectionDetailDependency> existingDependencyMap = existingDependencyList
                .stream()
                .collect(Collectors.toMap(TenantSectionDetailDependency::getId, Function.identity()));

        for (TenantSectionDetailDependencyRequest request : dependencyRequestList) {
            TenantSectionDetailDependencyKey newKey = new TenantSectionDetailDependencyKey(
                request.getTenantSectionDetailId().longValue(),
                request.getSequence()
            );

            Optional<TenantSectionDetailDependency> existingDependencyOpt = existingDependencyMap.values()
                .stream()
                .filter(detailDependency -> detailDependency.getId().getTenantSectionDetailId().intValue() == request.getTenantSectionDetailId())
                .findFirst();

            if (existingDependencyOpt.isPresent()) {
                TenantSectionDetailDependency updateDetailDependency = TenantSectionDetailDependency.builder()
                    .id(newKey)
                    .tenantSectionDetail(detail)
                    .name(request.getName())
                    .value(request.getValues() != null && !request.getValues().isEmpty() ? String.join(",", request.getValues()): "")
                    .groupName(request.getGroupName())
                    .action(request.getAction())
                    .build();
                existingDependencyList.add(updateDetailDependency);

            } else {
                TenantSectionDetailDependency newDetailDependency = TenantSectionDetailDependency.builder()
                    .id(newKey)
                    .tenantSectionDetail(TenantSectionDetail.builder()
                        .id(request.getTenantSectionDetailId().longValue())
                        .build())
                    .name(request.getName())
                    .value(request.getValues() != null && !request.getValues().isEmpty() ? String.join(",", request.getValues()): "")
                    .groupName(request.getGroupName())
                    .action(request.getAction())
                    .build();
                existingDependencyList.add(newDetailDependency);
            }
        }

        existingDependencyMap.values().forEach(existingDependencyList::remove);
        detail.setTenantSectionDetailDependencyList(existingDependencyList);
    }

    private void updateTenantSectionDetailDataSource(TenantSectionDetail detail, TenantSectionDetailDataSourceRequest detailDataSourceRequest) {
        if (detailDataSourceRequest != null) {
            TenantSectionDetailDataSourceKey newKey = new TenantSectionDetailDataSourceKey(
                    detail.getId(), detailDataSourceRequest.getDataSourceId()
            );

            List<TenantSectionDetailDataSource> detailDataSourceList = detail.getTenantSectionDetailDataSourceList();
            Optional<TenantSectionDetailDataSource> existingDataSourceOpt = detail.getTenantSectionDetailDataSourceList()
                .stream()
                .filter(ds -> ds.getId().getTenantSectionDetailId().equals(detail.getId()))
                .findFirst();

            if (existingDataSourceOpt.isPresent()) {
                TenantSectionDetailDataSource existingDataSource = existingDataSourceOpt.get();
                if (detailDataSourceRequest.getDataSourceId() == 0) {
                    detailDataSourceList.remove(existingDataSource);
                } else if (!existingDataSource.getId().getDataSourceId().equals(detailDataSourceRequest.getDataSourceId())) {
                    TenantSectionDetailDataSource updatedDataSource = TenantSectionDetailDataSource.builder()
                        .id(newKey)
                        .tenantSectionDetail(detail)
                        .dataSource(DataSource.builder()
                            .recId(detailDataSourceRequest.getDataSourceId())
                            .build())
                        .build();
                    detailDataSourceList.remove(existingDataSource);
                    detailDataSourceList.add(updatedDataSource);
                }
            } else {
                TenantSectionDetailDataSource newDataSource = TenantSectionDetailDataSource.builder()
                    .id(newKey)
                    .tenantSectionDetail(detail)
                    .dataSource(DataSource.builder()
                        .recId(detailDataSourceRequest.getDataSourceId())
                        .build())
                    .build();
                detailDataSourceList.add(newDataSource);
            }
            detail.setTenantSectionDetailDataSourceList(detailDataSourceList);
        }
    }
}
