package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthBasicAuthenClient;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.mapper.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.AppiniResponse;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestReportRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantConfigRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.*;

import static com.pantavanij.sourcingreq.services.util.Constant.REC_ID;
import static com.pantavanij.sourcingreq.services.util.Constant.TENANT;

@RequiredArgsConstructor
@Service
public class TenantConfigServiceImpl implements TenantConfigService {
    private final EpAuthBasicAuthenClient epAuthBasicAuthenClient;
    private final SourcingMenuService sourcingMenuService;
    private final TenantConfigRepository tenantConfigRepository;
    private final TenantService tenantService;
    private final RequestReportRepository requestReportRepository;

    @Override
    public TenantConfigDto createTenantConfig(TenantConfigRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<TenantConfig> tenantConfigOptional = tenantConfigRepository.findFirstByTenantRecIdOrderBySequenceDesc(tenant.getRecId());
            if (tenantConfigOptional.isPresent() && request.getSequence() == 0) {
                request.setSequence(tenantConfigOptional.get().getSequence() + 1);
            } else if (tenantConfigOptional.isEmpty() && request.getSequence() == 0) {
                request.setSequence(1);
            } else {
                tenantConfigOptional.ifPresent(tenantConfig -> tenantConfigRepository.reOrderOtherTenantConfigSequence(tenant.getRecId(), tenantConfig.getSequence() + 1, request.getSequence()));
            }
            TenantConfig tenantConfig = tenantConfigRepository.save(getTenantConfigSubmit(request, tenant));
            return TenantConfigMapper.INSTANCE.toTenantConfigDto(tenantConfig);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TenantConfigSearchDto searchTenantConfigByCondition(TenantConfigSearchRequest request, Pageable pageable, String timeZone) {
        Page<TenantConfig> tenantConfigPage = tenantConfigRepository.findAll(Specification.where(getTenantConfigSearchSpecification(request)), pageable);
        int totalPage = tenantConfigPage.getTotalPages();
        long total = tenantConfigPage.getTotalElements();
        List<TenantConfigDto> tenantConfigList = TenantConfigMapper.INSTANCE.toTenantConfigDtoList(tenantConfigPage.getContent(), timeZone);

        TenantConfigSearchDto tenantConfigSearchDto = new TenantConfigSearchDto();
        tenantConfigSearchDto.setTenantConfigList(tenantConfigList);
        tenantConfigSearchDto.setTotal(total);
        tenantConfigSearchDto.setTotalPage(totalPage);
        tenantConfigSearchDto.setPageSize(pageable.getPageSize());
        return tenantConfigSearchDto;
    }

    @Override
    public TenantConfigDto getByTenantConfigIdAndTenant(Integer tenantConfigId, String timeZone) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<TenantConfig> tenantConfigOptional = tenantConfigRepository.findByRecIdAndTenant(tenantConfigId, tenant);
        if (tenantConfigOptional.isPresent()) {
            TenantConfig tenantConfig = tenantConfigOptional.get();
            return TenantConfigMapper.INSTANCE.toTenantConfigDto(tenantConfig, timeZone);
        }
        return null;
    }

    @Override
    public TenantConfigDto updateTenantConfig(TenantConfigRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<TenantConfig> tenantConfigOptional = tenantConfigRepository.findByRecIdAndTenant(request.getRecId(), tenant);
            if (tenantConfigOptional.isPresent()) {
                tenantConfigRepository.reOrderOtherTenantConfigSequence(tenant.getRecId(), tenantConfigOptional.get().getSequence(), request.getSequence());
                TenantConfig tenantConfig = tenantConfigRepository.save(getTenantConfigUpdate(tenantConfigOptional.get(), request, tenant));
                return TenantConfigMapper.INSTANCE.toTenantConfigDto(tenantConfig);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    public boolean deleteTenantConfig(Integer tenantConfigId) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        try {
            Optional<TenantConfig> tenantConfigOptional = tenantConfigRepository.findByRecIdAndTenant(tenantConfigId, tenant);
            if (tenantConfigOptional.isPresent()) {
                tenantConfigRepository.deleteByRecIdAndTenant(tenantConfigId, tenant);
                tenantConfigRepository.reOrderSequenceByTenantRecId(tenant.getRecId());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TenantConfigDto updateTenantConfigSequence(SequenceRequest request) {
        Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
        Optional<TenantConfig> tenantConfigOptional = tenantConfigRepository.findByRecIdAndTenant(request.getRecId(), tenant);
        if (tenantConfigOptional.isPresent()) {
            tenantConfigRepository.reOrderOtherTenantConfigSequence(tenant.getRecId(), tenantConfigOptional.get().getSequence(), request.getSequence());
            TenantConfig tenantConfigUpdateSequence = tenantConfigRepository.save(getTenantConfigSequenceUpdate(tenantConfigOptional.get(), request, tenant));
            return TenantConfigMapper.INSTANCE.toTenantConfigDto(tenantConfigUpdateSequence);
        }
        return null;
    }

    public TenantConfig getTenantConfigSequenceUpdate(TenantConfig tenantConfig, SequenceRequest request, Tenant tenant) {
        return TenantConfig.builder()
                .recId(request.getRecId())
                .tenant(tenant)
                .topic(tenantConfig.getTopic())
                .section(tenantConfig.getSection())
                .name(tenantConfig.getName())
                .value(tenantConfig.getValue())
                .sequence(request.getSequence())
                .description(tenantConfig.getDescription())
                .createdBy(tenantConfig.getCreatedBy())
                .createdDate(tenantConfig.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public TenantConfig getTenantConfigSubmit(TenantConfigRequest request, Tenant tenant) {
        return TenantConfig.builder()
                .recId(request.getRecId())
                .tenant(tenant)
                .topic(request.getTopic())
                .section(request.getSection())
                .name(request.getName())
                .value(request.getValue())
                .sequence(request.getSequence())
                .description(request.getDescription())
                .createdBy(AppUtil.getUserName())
                .createdDate(DateTimeUtil.getTimestampUTC())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    public TenantConfig getTenantConfigUpdate(TenantConfig tenantConfig, TenantConfigRequest request, Tenant tenant) {
        return TenantConfig.builder()
                .recId(request.getRecId())
                .tenant(tenant)
                .topic(request.getTopic())
                .section(request.getSection())
                .name(request.getName())
                .value(request.getValue())
                .sequence(request.getSequence())
                .description(request.getDescription())
                .createdBy(tenantConfig.getCreatedBy())
                .createdDate(tenantConfig.getCreatedDate())
                .updatedBy(AppUtil.getUserName())
                .updatedDate(DateTimeUtil.getTimestampUTC())
                .build();
    }

    private Specification<TenantConfig> getTenantConfigSearchSpecification(TenantConfigSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Tenant tenant = tenantService.findByCode(AppUtil.getTenantId());
            if (tenant != null) {
                predicates.add(criteriaBuilder.equal(root.get(TENANT).get(REC_ID), tenant.getRecId()));
            }

            List<ConditionSearchRequest> conditionSearchRequests = request.getConditionSearchList();
            if (conditionSearchRequests != null && !conditionSearchRequests.isEmpty()) {
                List<Predicate> orPredicates = new ArrayList<>();
                for (ConditionSearchRequest conditionSearchRequest : conditionSearchRequests) {
                    String searchField = conditionSearchRequest.getSearchField();
                    String searchValue = conditionSearchRequest.getSearchValue();

                    Predicate predicate = null;
                    if (StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchValue)) {
                        if (Constant.TOPIC.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (Constant.SECTION.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (Constant.NAME.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (Constant.VALUE.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else if (Constant.CREATED_BY.equalsIgnoreCase(searchField)) {
                            predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                        } else {
                            if (CommonUtils.isNumeric(searchValue)) {
                                predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue + "%");
                            } else {
                                predicate = criteriaBuilder.like(root.get(searchField), "%" + searchValue.toLowerCase() + "%");
                            }
                        }
                        if (predicate != null) {
                            orPredicates.add(predicate);
                        }
                    }
                }
                if (!orPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
                }
            }

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public String getWorkflowTemplateId(Integer tenantId) {
        String topic = "Workflow";
        String section = "TemplateId";
        String name = "NA";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getWorkflowTemplateIdForERFX(Integer tenantId) {
        String topic = "Workflow";
        String section = "TemplateId";
        String name = "eRFX";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getEPAuthPrivilegeCode(Integer tenantId, String section) {
        String topic = "UserInfo";
        String name = "PrivilegeCode";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getProjectListURL(Integer tenanId) {
        String topic = "API";
        String section = "ProjectList";
        String name = "URL";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenanId, topic, section, name);
    }
    @Override
    public String getCreateToPrURL(Integer tenanId) {
        String topic = "API";
        String section = "CreateToPr";
        String name = "URL";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenanId, topic, section, name);
    }

    @Override
    public String getCreateSupplier(Integer tenanId) {
        String topic = "API";
        String section = "CreateSupplier";
        String name = "URL";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenanId, topic, section, name);
    }

    @Override
    public String getGetSupplier(Integer tenanId) {
        String topic = "API";
        String section = "GetSupplier";
        String name = "URL";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenanId, topic, section, name);
    }

    @Override
    public String getWorkflowApprovalType(Integer tenantId) {
        String topic = "Workflow";
        String section = "ApprovalType";
        String name = "NA";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public Long getEmailTemplateIdByName(String name, Integer tenantId) {
        String topic = "Email";
        String section = "TemplateId";
        String templateId = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Long.valueOf(templateId);
    }

    @Override
    public String getEmailAddressDefault(Integer tenantId) {
        String topic = "Email";
        String section = "EmailAddress";
        String name = "Noreply";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public Long getMaxSessionTimeout(Integer tenantId) {
        String minutes = "10";

        if(this.getEPTimeoutConfig(tenantId)) {
            AppiniResponse response = epAuthBasicAuthenClient.getSessionTimeout(AppUtil.getTenantId(),"Enterprise Buyer","Timeout","Session");
            if(response != null)
                minutes = response.getValue();
        } else {
            String topic = "Timeout";
            String section = "Minutes";
            String name = "Session";
            minutes = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        }

        return Long.valueOf(minutes);

    }

    @Override
    public String getEmailImageByName(String name, Integer tenantId) {
        String topic = "Email";
        String section = "Image";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getEmailLoginUrlEPByName(String name, Integer tenantId) {
        String topic = "Email";
        String section = "LoginUrl";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getLimitSearchSupplierWebwork(Integer tenantId) {
        String topic = "Supplier Web Work";
        String section = "Search Supplier";
        String name = "Limit";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getRequestItemTemplate(Integer tenantId) {
        String topic = "Template";
        String section = "RequestItem";
        String name = "NA";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getRequestItemReportName(Integer tenantId) {
        String topic = "Configuration";
        String section = "RequestItem";
        String name = "ReportName";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public boolean getBorgAllConfig(Integer tenantId) {
        String topic = "Configuration";
        String section = "EPAuth";
        String name = "CanViewBorgAll";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getEPTimeoutConfig(Integer tenantId) {
        String topic = "Configuration";
        String section = "EPAuth";
        String name = "SessionTimeout";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getApprovalAllTapConfig(Integer tenantId) {
        String topic = "Configuration";
        String section = "Approval";
        String name = "isShowAllApprovalTap";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getApprovalSourcingTapConfig(Integer tenantId) {
        String topic = "Configuration";
        String section = "SourcingApproval";
        String name = "isShowSourcingApprovalTap";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public String getApproveSourcingPrivilegeCodes(Integer tenantId) {
        String topic = "Configuration";
        String section = "SourcingApproval";
        String name = "approveSourcingPrivilegeCodes";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getExcludeOrganizations(Integer tenantId) {
        String topic = "Configuration";
        String section = "Organization";
        String name = "excludeOrganizations";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getCopyToPrPrivilegeCodes(Integer tenantId) {
        String topic = "Configuration";
        String section = "CopyToPR";
        String name = "copyToPrPrivilegeCodes";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getCopyToPrOrganizations(Integer tenantId) {
        String topic = "Configuration";
        String section = "CopyToPR";
        String name = "copyToPrOrganizations";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }


    @Override
    public boolean getForwardApprovalWorkflow(Integer tenantId) {
        String topic = "Configuration";
        String section = "Show/Hide";
        String name = "ForwardApprovalWorkflow";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getExistingPriceOptionConfig(Integer tenantId) {
        String topic = "Configuration";
        String section = "ExistingPriceItem";
        String name = "isShowExistingPriceCreateSourcing";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public String getEmailImageUrl(Integer tenantId) {
        String topic = "Email";
        String section = "Image";
        String name = "EMAIL_IMAGE_URL";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getSRLogoImageFileId(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "srLogoImageFileId";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setSRLogoImageFileId(Integer tenantId, String fileId) {
        String topic = "Logo";
        String section = "Image";
        String name = "srLogoImageFileId";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(fileId);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getSRLogoImageStyles(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "srLogoImageStyles";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setSRLogoImageStyles(Integer tenantId, String logoImageStyles) {
        String topic = "Logo";
        String section = "Image";
        String name = "srLogoImageStyles";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(logoImageStyles);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getEPLogoImageFileId(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "epLogoImageFileId";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setEPLogoImageFileId(Integer tenantId, String fileId) {
        String topic = "Logo";
        String section = "Image";
        String name = "epLogoImageFileId";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(fileId);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getEPLogoImageStyles(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "epLogoImageStyles";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setEPLogoImageStyles(Integer tenantId, String logoImageStyles) {
        String topic = "Logo";
        String section = "Image";
        String name = "epLogoImageStyles";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(logoImageStyles);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getSELogoImageFileId(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "seLogoImageFileId";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setSELogoImageFileId(Integer tenantId, String fileId) {
        String topic = "Logo";
        String section = "Image";
        String name = "seLogoImageFileId";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(fileId);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getSELogoImageStyles(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "seLogoImageStyles";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setSELogoImageStyles(Integer tenantId, String logoImageStyles) {
        String topic = "Logo";
        String section = "Image";
        String name = "seLogoImageStyles";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(logoImageStyles);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getERFXLogoImageFileId(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "erfxLogoImageFileId";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setERFXLogoImageFileId(Integer tenantId, String fileId) {
        String topic = "Logo";
        String section = "Image";
        String name = "erfxLogoImageFileId";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(fileId);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getERFXLogoImageStyles(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "erfxLogoImageStyles";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setERFXLogoImageStyles(Integer tenantId, String logoImageStyles) {
        String topic = "Logo";
        String section = "Image";
        String name = "erfxLogoImageStyles";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(logoImageStyles);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getUAMAdminLogoImageFileId(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "uamAdminLogoImageFileId";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setUAMAdminLogoImageFileId(Integer tenantId, String fileId) {
        String topic = "Logo";
        String section = "Image";
        String name = "uamAdminLogoImageFileId";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(fileId);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getUAMAdminLogoImageStyles(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "uamAdminLogoImageStyles";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setUAMAdminLogoImageStyles(Integer tenantId, String logoImageStyles) {
        String topic = "Logo";
        String section = "Image";
        String name = "uamAdminLogoImageStyles";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(logoImageStyles);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getDashboardLogoImageFileId(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "dashboardLogoImageFileId";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setDashboardLogoImageFileId(Integer tenantId, String fileId) {
        String topic = "Logo";
        String section = "Image";
        String name = "dashboardLogoImageFileId";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(fileId);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getDashboardLogoImageStyles(Integer tenantId) {
        String topic = "Logo";
        String section = "Image";
        String name = "dashboardLogoImageStyles";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    public boolean setDashboardLogoImageStyles(Integer tenantId, String logoImageStyles) {
        String topic = "Logo";
        String section = "Image";
        String name = "dashboardLogoImageStyles";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(logoImageStyles);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public String getERFXNoMaxItems(Integer tenantId) {
        String topic = "ERFX";
        String section = "eRFXNo";
        String name = "MaxItems";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getERFXAuthCode(Integer tenantId) {
        String topic = "ERFX";
        String section = "Auth_Code";
        String name = "GetStatus";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public boolean setERFXAuthCode(Integer tenantId, String eRFXAuthCode) {
        String topic = "ERFX";
        String section = "Auth_Code";
        String name = "GetStatus";
        boolean result = false;

        Optional<TenantConfig> tenantConfig = tenantConfigRepository.getByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        if(tenantConfig.isPresent()) {
            TenantConfig tenantConfigUpdated = tenantConfig.get();

            tenantConfigUpdated.setValue(eRFXAuthCode);
            tenantConfigRepository.save(tenantConfigUpdated);
            result = true;
        }
        return result;
    }

    @Override
    public boolean getStep3(Integer tenantId) {
        String topic = "Configuration";
        String section = "Show/Hide";
        String name = "isShowStep3";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getIsShowServices(Integer tenantId) {
        String topic = "Configuration";
        String section = "Show/Hide";
        String name = "isShowServices";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getIsShowViewRequestDetail(Integer tenantId) {
        String topic = "Configuration";
        String section = "Show/Hide";
        String name = "isShowViewRequestDetail";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getIsShowMenuAssignToMe(Integer tenantId) {
        String topic = "Configuration";
        String section = "Show/Hide";
        String name = "isShowMenuAssignToMe";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getIsShowMenuEditRequestForApprover(Integer tenantId) {
        String topic = "Configuration";
        String section = "Show/Hide";
        String name = "isShowMenuEditRequestForApprover";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getIsShowApprovalReportLine(Integer tenantId) {
        String topic = "Configuration";
        String section = "Show/Hide";
        String name = "isShowApprovalReportLine";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getFilterReportConfiguration(Integer tenantId) {
        String topic = "Configuration";
        String section = "Report";
        String name = "Filtering";
        return Boolean.parseBoolean(tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name));
    }
//    @Override
//    public boolean getConfigurationExistingPriceItemMenu(Integer tenantId) {
//        String topic = "Configuration";
//        String section = "ExistingPriceItem";
//        String name = "Menu";
//        return Boolean.parseBoolean(tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name));
//    }

    @Override
    public boolean getIsAllowDeleteItemForApprover(Integer tenantId) {
        String topic = "Configuration";
        String section = "Show/Hide";
        String name = "isAllowDeleteItemForApprover";
        return Boolean.parseBoolean(tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name));
    }

    @Override
    public String getHistoryModalRequestNameLabel(Integer tenantId) {
        String topic = "Configuration";
        String section = "HistoryModal";
        String name = "RequestNameLabel";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getHistoryModalProjectNameLabel(Integer tenantId) {
        String topic = "Configuration";
        String section = "HistoryModal";
        String name = "ProjectNameLabel";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getHistoryModalProjectNameFormat(Integer tenantId) {
        String topic = "Configuration";
        String section = "HistoryModal";
        String name = "ProjectNameFormat";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getRequestNoteLabel(Integer tenantId) {
        String topic = "Configuration";
        String section = "RequestView";
        String name = "requestNoteLabel";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }
    @Override
    public String getReasonModalTitle(Integer tenantId) {
        String topic = "Configuration";
        String section = "RequestItem";
        String name = "reasonModalTitle";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getViewApproveRequestItemTableTemplate(Integer tenantId) {
        String topic = "Configuration";
        String section = "Template";
        String name = "viewApproveRequestItemTableTemplate";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getDefaultApproverHeadersTemplate(Integer tenantId) {
        String topic = "Configuration";
        String section = "Template";
        String name = "defaultApproverHeadersTemplate";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getDefaultSourcingItemApproverHeaders(Integer tenantId) {
        String topic = "Configuration";
        String section = "Template";
        String name = "defaultSourcingItemApproverHeaders";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getDefaultPhoneFieldName(Integer tenantId) {
        String topic = "Configuration";
        String section = "LogicStep2";
        String name = "defaultPhoneFieldName";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public boolean isAutoAssignToPurchaser(Integer tenantId) {
        String topic = "Configuration";
        String section = "RequestSubmit";
        String name = "isAutoAssignToPurchaser";
        return Boolean.parseBoolean(tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name));
    }

    @Override
    public boolean isEnableWorkflowEngine(Integer tenantId) {
        String topic = "Configuration";
        String section = "WorkflowEngine";
        String name = "Enable";
        return Boolean.parseBoolean(tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name));
    }

    @Override
    public boolean getIsShowRemoveFromMyTaskActionMenu(Integer tenantId) {
        String topic = "Configuration";
        String section = "Show/Hide";
        String name = "isShowRemoveFromMyTaskActionMenu";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public Integer getExportReportYear(Integer tenantId) {
        String topic = "Configuration";
        String section = "ExportReportDateRange";
        String name = "Year";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Integer.parseInt(value);
    }

    @Override
    public boolean getExportReportOffsetCurrentYear(Integer tenantId) {
        String topic = "Configuration";
        String section = "ExportReportDateRange";
        String name = "OffsetCurrentYear";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    //Configuration	ExportReportDateRange	Locked
    @Override
    public boolean getExportReportLockDateRange(Integer tenantId) {
        String topic = "Configuration";
        String section = "ExportReportDateRange";
        String name = "Locked";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getUserLoginRedundancyCheck(Integer tenantId) {
        String topic = "Configuration";
        String section = "UserLogin";
        String name = "RedundancyCheck";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean isEnableDeptApprover(Integer tenantId) {
        String topic = "Configuration";
        String section = "DeptApprover";
        String name = "Enable";
        return Boolean.parseBoolean(tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name));
    }

    @Override
    public String getInvalidErrorMessage(Integer tenantId) {
        String topic = "Configuration";
        String section = "ExportReportDateRange";
        String name = "InvalidErrorMessage";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getExportReportTitleSelectDate(Integer tenantId) {
        String topic = "Configuration";
        String section = "ExportReportDateRange";
        String name = "ExportReportTitleSelectDate";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public boolean getIsShowApprovalApproverGroup(Integer tenantId) {
        String topic = "Configuration";
        String section = "Show/Hide";
        String name = "isShowApprovalApproverGroup";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean IsShowItemNameForFreeItem(Integer tenantId) {
        String topic = "Configuration";
        String section = "FreeItem";
        String name = "isShowItemName";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean isSendOrganizationToERFX(Integer tenantId) {
        String topic = "ERFX";
        String section = "Organization";
        String name = "isSendOrganizationToERFX";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean isDisabledTypeForPurchaserEdit(Integer tenantId) {
        String topic = "Configuration";
        String section = "PurchaserEdit";
        String name = "DisabledType";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getIsSearchByInvitationCode(Integer tenantId) {
        String topic = "Configuration";
        String section = "SupplierWebWork";
        String name = "SearchByInvitationCode";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public String getApprovalSectionTemplate(Integer tenantId) {
        String topic = "Configuration";
        String section = "Template";
        String name = "approvalSectionTemplate";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String[] getCheckWaringChangeFields(Integer tenantId) {
        String topic = "Configuration";
        String section = "LogicStep1";
        String name = "checkWaringChangeFields";
        String results = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return results.trim().split(",",-1);
    }

    @Override
    public String getDeptApproverSectionLabel(Integer tenantId) {
        String topic = "Configuration";
        String section = "DeptApprover";
        String name = "sectionLabel";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getPurchaserSectionLabel(Integer tenantId) {
        String topic = "Configuration";
        String section = "Purchaser";
        String name = "sectionLabel";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getDueDiligenceCheckListForm(Integer tenantId) {
        String topic = "Configuration";
        String section = "PDPA";
        String name = "DueDiligenceCheckListForm";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public String getRequestItemReportLabel(Integer tenantId) {
        String topic = "Configuration";
        String section = "Report";
        String name = "requestItemReportLabel";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public boolean isShowSupplierLocalLanguage(Integer tenantId) {
        String topic = "Configuration";
        String section = "Supplier";
        String name = "LocalLanguage";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public String getSupplierFieldName(Integer tenantId) {
        String topic = "Configuration";
        String section = "Supplier";
        String name = "FieldName";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

    @Override
    public boolean getVisibleSourcingDocNo(Integer tenantId) {
        String topic = "Configuration";
        String section = "Sourcing";
        String name = "visibleSourcingDocNo";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getForceSelectAllItemCopyToPR(Integer tenantId) {
        String topic = "Configuration";
        String section = "CopyToPR";
        String name = "forceSelectAllItemCopyToPR";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getAllowRepeateCopyToPR(Integer tenantId) {
        String topic = "Configuration";
        String section = "CopyToPR";
        String name = "allowRepeateCopyToPR";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public boolean getCopyToPRViaERP(Integer tenantId) {
        String topic = "Configuration";
        String section = "CopyToPR";
        String name = "copyToPRViaERP";
        String value = tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
        return Boolean.parseBoolean(value);
    }

    @Override
    public SourcingRequestDisplayDto getDisplayConfiguration(Tenant tenant) {
        boolean isRequester = AppUtil.isRequester();
        boolean isPurchaser = AppUtil.isPurchaser();

        boolean isShowStep3 = false;
        boolean isShowExistingPriceCreateSourcing = false;
        boolean isShowAllApprovalTap = false;
        boolean isShowSourcingApprovalTap = false;
        String approveSourcingPrivilegeCodes = null;
        boolean isShowServices = getIsShowServices(tenant.getRecId());
        boolean isShowViewRequestDetail = getIsShowViewRequestDetail(tenant.getRecId());
        boolean isShowMenuAssignToMe = getIsShowMenuAssignToMe(tenant.getRecId());
        boolean isShowMenuEditRequestForApprover = getIsShowMenuEditRequestForApprover(tenant.getRecId());
        boolean isShowApprovalReportLine = getIsShowApprovalReportLine(tenant.getRecId());
        boolean isShowRemoveFromMyTaskActionMenu = getIsShowRemoveFromMyTaskActionMenu(tenant.getRecId());
        boolean visibleSourcingDocNo = getVisibleSourcingDocNo(tenant.getRecId());



        boolean isAllowDeleteItemForApprover = getIsAllowDeleteItemForApprover(tenant.getRecId());
        boolean isShowApprovalApproverGroup = getIsShowApprovalApproverGroup(tenant.getRecId());
        boolean isShowForwardApprovalWrokflow = getIsShowForwardApprovalWorkflow(tenant.getRecId());

        String historyModalProjectNameLabel = getHistoryModalProjectNameLabel(tenant.getRecId());
        String historyModalRequestNameLabel = getHistoryModalRequestNameLabel(tenant.getRecId());
        String historyModalProjectNameFormat = getHistoryModalProjectNameFormat(tenant.getRecId());
        String reportProjectFormat = getReportProjectFormat(tenant.getRecId());
        String requestNoteLabel = getRequestNoteLabel(tenant.getRecId());
        String reasonModalTitle = getReasonModalTitle(tenant.getRecId());
        String viewApproveRequestItemTableTemplate = getViewApproveRequestItemTableTemplate(tenant.getRecId());
        String excludeOrganizations = getExcludeOrganizations(tenant.getRecId());

        String approvalSectionTemplate = this.getViewApproveRequestItemTableTemplate(tenant.getRecId());
        String invalidErrorMessage = this.getInvalidErrorMessage(tenant.getRecId());
        String exportReportTitleSelectDate = this.getExportReportTitleSelectDate(tenant.getRecId());
        String defaultApproverHeadersTemplate = this.getDefaultApproverHeadersTemplate(tenant.getRecId());
        String defaultSourcingItemApproverHeaders = this.getDefaultSourcingItemApproverHeaders(tenant.getRecId());
        List<InstanceApproverHeaderDto> defaultApproverHeaders = new ArrayList<>();
        InstanceApproverHeaderDto InstanceApproverHeaderDto = this.getDeptApproverHeaders(tenant);
        defaultApproverHeaders.add(InstanceApproverHeaderDto);
        InstanceApproverHeaderDto InstancePurchaserHeaderDto = this.getPurchaserHeaders(tenant);
        defaultApproverHeaders.add(InstancePurchaserHeaderDto);

        List<SourcingMenuDto> existingPriceSourcingMenu = null;
        String requestItemReportLabel = this.getRequestItemReportLabel(tenant.getRecId());
        String srLogoImageStyles = this.getSRLogoImageStyles(tenant.getRecId());
        String srLogoImageFileId = this.getSRLogoImageFileId(tenant.getRecId());
        String epLogoImageStyles = this.getEPLogoImageStyles(tenant.getRecId());
        String epLogoImageFileId = this.getEPLogoImageFileId(tenant.getRecId());
        String seLogoImageStyles = this.getSELogoImageStyles(tenant.getRecId());
        String seLogoImageFileId = this.getSELogoImageFileId(tenant.getRecId());
        String erfxLogoImageStyles = this.getERFXLogoImageStyles(tenant.getRecId());
        String erfxLogoImageFileId = this.getERFXLogoImageFileId(tenant.getRecId());
        String uamAdminLogoImageStyles = this.getUAMAdminLogoImageStyles(tenant.getRecId());
        String uamAdminLogoImageFileId = this.getUAMAdminLogoImageFileId(tenant.getRecId());
        String dashboardLogoImageStyles = this.getDashboardLogoImageStyles(tenant.getRecId());
        String dashboardLogoImageFileId = this.getDashboardLogoImageFileId(tenant.getRecId());
        String sourcingRequestItemReportName = this.getRequestItemReportName(tenant.getRecId());
        Integer requestReportId = 0;
        Optional<RequestReport> reportRequest = requestReportRepository.findByTenantIdAndPrivilegeCode(tenant.getRecId(),"SSR");
        if(reportRequest.isPresent()) {
            requestReportId = reportRequest.get().getRecId();
        }

        if (isRequester) {
            isShowStep3 = getStep3(tenant.getRecId());
        }

        if (isPurchaser) {
            isShowExistingPriceCreateSourcing = getExistingPriceOptionConfig(tenant.getRecId());
            existingPriceSourcingMenu =
                    isShowExistingPriceCreateSourcing ? sourcingMenuService.getSourcingMenuByTenantId(tenant.getRecId()) : null;
            isShowAllApprovalTap = getApprovalAllTapConfig(tenant.getRecId());
        }

        isShowSourcingApprovalTap = getApprovalSourcingTapConfig(tenant.getRecId());
        approveSourcingPrivilegeCodes = getApproveSourcingPrivilegeCodes(tenant.getRecId());

        ViewRequestDisplayDto viewRequestDisplayDto = ViewRequestDisplayDto.builder()
                .isShowViewRequestDetail(isShowViewRequestDetail)
                .build();

        RequestItemDisplayDto requestItemDisplayDto = RequestItemDisplayDto.builder()
                .isAllowDeleteItemForApprover(isAllowDeleteItemForApprover)
                .reasonModalTitle(reasonModalTitle)
                .build();


        SourcingRequestDisplayDto sourcingRequestDisplayDto = SourcingRequestDisplayDto.builder()
                .tenantId(tenant.getCode())
                .isShowExistingPriceCreateSourcing(isShowExistingPriceCreateSourcing)
                .existingPriceSourcingMenu(existingPriceSourcingMenu)
                .isShowStep3(isShowStep3)
                .isShowAllApprovalTap(isShowAllApprovalTap)
                .isShowSourcingApprovalTap(isShowSourcingApprovalTap)
                .approveSourcingPrivilegeCodes(approveSourcingPrivilegeCodes)
                .isShowServices(isShowServices)
                .viewRequest(viewRequestDisplayDto)
                .requestItem(requestItemDisplayDto)
                .isShowMenuAssignToMe(isShowMenuAssignToMe)
                .isShowMenuEditRequestForApprover(isShowMenuEditRequestForApprover)
                .isShowApprovalReportLine(isShowApprovalReportLine)
                .isShowApprovalApproverGroup(isShowApprovalApproverGroup)
                .isShowRemoveFromMyTaskActionMenu(isShowRemoveFromMyTaskActionMenu)
                .isShowForwardApprovalWorkflow(isShowForwardApprovalWrokflow)
                .visibleSourcingDocNo(visibleSourcingDocNo)


                .historyModalProjectNameLabel(historyModalProjectNameLabel)
                .historyModalRequestNameLabel(historyModalRequestNameLabel)
                .historyModalProjectNameFormat(historyModalProjectNameFormat)
                .requestNoteLabel(requestNoteLabel)
                .viewApproveRequestItemTableTemplate(viewApproveRequestItemTableTemplate)
                .approvalSectionTemplate(approvalSectionTemplate)
                .exportReportErrorMessageInvalidDateRange(invalidErrorMessage)
                .exportReportTitleSelectDate(exportReportTitleSelectDate)
                .defaultApproverHeadersTemplate(defaultApproverHeadersTemplate)
                .defaultSourcingItemApproverHeaders(defaultSourcingItemApproverHeaders)
                .defaultApproverHeaders(defaultApproverHeaders)
                .requestItemReportLabel(requestItemReportLabel)
                .requestItemReportName(sourcingRequestItemReportName)
                .requestReportId(requestReportId)
                .reportProjectFormat(reportProjectFormat)
                .srLogoImageFileId(srLogoImageFileId)
                .srLogoImageStyles(srLogoImageStyles)
                .epLogoImageFileId(epLogoImageFileId)
                .epLogoImageStyles(epLogoImageStyles)
                .seLogoImageFileId(seLogoImageFileId)
                .seLogoImageStyles(seLogoImageStyles)
                .erfxLogoImageFileId(erfxLogoImageFileId)
                .erfxLogoImageStyles(erfxLogoImageStyles)
                .uamAdminLogoImageFileId(uamAdminLogoImageFileId)
                .uamAdminLogoImageStyles(uamAdminLogoImageStyles)
                .dashboardLogoImageFileId(dashboardLogoImageFileId)
                .dashboardLogoImageStyles(dashboardLogoImageStyles)
                .excludeOrganizations(excludeOrganizations)
                .build();

        return sourcingRequestDisplayDto;
    }

    private InstanceApproverHeaderDto getDeptApproverHeaders(Tenant tenant) {
        List<InstanceApproverSectionDto> approverSections = new ArrayList<>();
        InstanceApproverSectionDto approverSection = new InstanceApproverSectionDto();
        approverSection.setSectionName("Approver");
        String deptApproverSectionLabel = this.getDeptApproverSectionLabel(tenant.getRecId());
        approverSection.setSectionLabel(deptApproverSectionLabel);

        List<InstanceApproverDto> instanceApprovers = new ArrayList<>();
        approverSection.setApprovers(instanceApprovers);

        approverSection.setStatus("");
        approverSections.add(approverSection);

        InstanceApproverHeaderDto approverHeader = new InstanceApproverHeaderDto();
        approverHeader.setHeaderName("APPROVER GROUP");
        approverHeader.setApproverSections(approverSections);

        return approverHeader;
    }

    private InstanceApproverHeaderDto getPurchaserHeaders(Tenant tenant) {
        List<InstanceApproverSectionDto> approverSections = new ArrayList<>();
        InstanceApproverSectionDto approverSection = new InstanceApproverSectionDto();
        approverSection.setSectionName("Purchaser");
        String deptApproverSectionLabel = this.getPurchaserSectionLabel(tenant.getRecId());
        approverSection.setSectionLabel(deptApproverSectionLabel);

        List<InstanceApproverDto> instanceApprovers = new ArrayList<>();
        approverSection.setApprovers(instanceApprovers);

        approverSection.setStatus("");
        approverSections.add(approverSection);

        InstanceApproverHeaderDto approverHeader = new InstanceApproverHeaderDto();
        approverHeader.setHeaderName("PURCHASER GROUP");
        approverHeader.setApproverSections(approverSections);

        return approverHeader;
    }

    public Boolean getIsShowForwardApprovalWorkflow(Integer tenantId) {
        String topic = "Configuration";
        String section = "Show/Hide";
        String name = "ForwardApprovalWorkflow";
        return Boolean.parseBoolean(tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name));
    }

    public Boolean getIsSetDeliveryLocationFromStep1ToRequestItem(Integer tenantId) {
        String topic = "Configuration";
        String section = "RequestItem";
        String name = "isSetDeliveryLocationFromStep1ToRequestItem";
        return Boolean.parseBoolean(tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name));
    }

    @Override
    public SourcingRequestLogicDto getLogicConfiguration(Tenant tenant) {
        Integer maximumRecord = getMaximumUploadItem(tenant.getRecId());
        String defaultPhoneFieldName = getDefaultPhoneFieldName(tenant.getRecId());
        Integer year = this.getExportReportYear(tenant.getRecId());
        boolean offsetCurrentYear = this.getExportReportOffsetCurrentYear(tenant.getRecId());
        boolean lockDateRange = this.getExportReportLockDateRange(tenant.getRecId());
        boolean userLoginRedundancyCheck = this.getUserLoginRedundancyCheck(tenant.getRecId());
        String[] checkWaringChangeFields = this.getCheckWaringChangeFields(tenant.getRecId());
        boolean isSetDeliveryLocationFromStep1ToRequestItem = this.getIsSetDeliveryLocationFromStep1ToRequestItem(tenant.getRecId());
        boolean forceSelectAllItemCopyToPR = getForceSelectAllItemCopyToPR(tenant.getRecId());
        boolean allowRepeateCopyToPR = getAllowRepeateCopyToPR(tenant.getRecId());
        boolean copyToPRViaERP = getCopyToPRViaERP(tenant.getRecId());

        Step2LogicDto step2LogicDto = Step2LogicDto.builder()
                .defaultPhoneFieldName(defaultPhoneFieldName)
                .build();

        ExportReportDataRangeDto exportReportDataRangeDto = ExportReportDataRangeDto.builder()
                .year(year)
                .offsetCurrentYear(offsetCurrentYear)
                .build();

        SourcingRequestLogicDto sourcingRequestLogicDto = SourcingRequestLogicDto.builder()
                .tenantId(tenant.getCode())
                .maximumUploadItem(maximumRecord)
                .step2(step2LogicDto)
                .exportReportDataRange(exportReportDataRangeDto)
                .lockDateRange(lockDateRange)
                .userLoginRedundancyCheck(userLoginRedundancyCheck)
                .checkWaringChangeFields(checkWaringChangeFields)
                .isSetDeliveryLocationFromStep1ToRequestItem(isSetDeliveryLocationFromStep1ToRequestItem)
                .forceSelectAllItemCopyToPR(forceSelectAllItemCopyToPR)
                .allowRepeateCopyToPR(allowRepeateCopyToPR)
                .copyToPRViaERP(copyToPRViaERP)
                .build();

        return sourcingRequestLogicDto;
    }

    @Override
    public List<TenantConfigDto> getOurService(Integer tenantId, String timeZone) {
//        boolean isAdmin = AppUtil.getPrivilegeScopes().get("SAM") != null;
        boolean isAccountSettingPermission = AppUtil.getPrivilegeScopes().get("SDN") != null;

        Specification<TenantConfig> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get(Constant.TENANT), tenantId));
            predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get(Constant.TOPIC)), Constant.OUR_SERVICE));
            predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get(Constant.SECTION)), "SourcingRequest"));

//            if (!isAdmin) {
//                predicates.add(criteriaBuilder.notEqual(criteriaBuilder.lower(root.get(Constant.NAME)), Constant.ADMINISTRATION));
//            }
            predicates.add(criteriaBuilder.notEqual(criteriaBuilder.lower(root.get(Constant.NAME)), Constant.ADMINISTRATION));
            if (!isAccountSettingPermission) {
                predicates.add(criteriaBuilder.notEqual(criteriaBuilder.lower(root.get(Constant.NAME)), Constant.ACCOUNT_SETTING));
            }

            predicates.add(criteriaBuilder.notEqual(criteriaBuilder.lower(root.get(Constant.NAME)), "Sourcing Request"));
            query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
            query.orderBy(criteriaBuilder.asc(root.get(Constant.SEQUENCE)));

            return query.getRestriction();
        };

        List<TenantConfig> tenantConfigs = tenantConfigRepository.findAll(specification);
        return TenantConfigMapper.INSTANCE.toTenantConfigDtoList(tenantConfigs, timeZone);
    }

    @Override
    public Integer getMaximumUploadItem(Integer tenantId) {
        String topic = "Configuration";
        String section = "Excel";
        String name = "maximumUploadItem";
        return Integer.valueOf(tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name));
    }

    @Override
    public String getReportProjectFormat(Integer tenantId) {
        String topic = "Configuration";
        String section = "Report";
        String name = "reportProjectFormat";
        return tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name);
    }

}
