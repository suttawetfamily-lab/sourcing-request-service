package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.BifrostClient;
import com.pantavanij.sourcingreq.services.domain.dto.SupplierOptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierWebworksDto;
import com.pantavanij.sourcingreq.services.domain.dto.elasticsearch.SupplierContactsDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExistingPriceItemSupplier;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Supplier;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.mapper.SupplierMapper;
import com.pantavanij.sourcingreq.services.domain.request.SupplierWebWorkSearchByTPShortNameRequest;
import com.pantavanij.sourcingreq.services.domain.request.SupplierWebWorksSearchRequest;
import com.pantavanij.sourcingreq.services.domain.request.supplier.CreateSupplierRequest;
import com.pantavanij.sourcingreq.services.domain.response.CreateSupplierResponse;
import com.pantavanij.sourcingreq.services.domain.response.SupplierResponse;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierContactResponse;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierWebWorkResponse;
import com.pantavanij.sourcingreq.services.domain.response.elasticsearch.SupplierWebWorksResponse;
import com.pantavanij.sourcingreq.services.domain.response.TenantInfoResponse;
import com.pantavanij.sourcingreq.services.exception.DataNotFoundException;
import com.pantavanij.sourcingreq.services.exception.ExternalServiceException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ExistingPriceItemSupplierRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.SupplierRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SupplierService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantConfigService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SupplierServiceImpl implements SupplierService {

    private final BifrostClient bifrostClient;
    private final SupplierRepository supplierRepository;
    private final ExistingPriceItemSupplierRepository existingPriceItemSupplierRepository;
    private final TenantConfigService tenantConfigService;
    private final UaaService uaaService;
    private final SupplierSearchServiceImpl supplierSearchService;
    private final ContactSearchServiceImpl contactSearchService;

    @Override
    public List<SupplierOptionDto> getSupplierByTenantAndSearchTerm(Tenant tenant, String searchTerm) {
        List<Supplier> supplierList = new ArrayList<>();

        int limit = Integer.valueOf(tenantConfigService.getLimitSearchSupplierWebwork(tenant.getRecId()));
        boolean isSearchByInvitationCode = tenantConfigService.getIsSearchByInvitationCode(tenant.getRecId());

        List<TenantInfoResponse> response = uaaService.getTenantInfo(tenant.getCode());
        SupplierWebWorksSearchRequest supplierWebWorksSearchRequest = new SupplierWebWorksSearchRequest();
        supplierWebWorksSearchRequest.setSearching(StringUtils.isNotBlank(searchTerm) ? searchTerm : "");
        supplierWebWorksSearchRequest.setInvitationCode(isSearchByInvitationCode ? (response != null && !response.isEmpty() ? response.get(0).getInvitation_code() : AppUtil.getTenantId()) : "");
        supplierWebWorksSearchRequest.setLimit(limit);
        List<SupplierWebworksDto> supplierWebworksDtoList = this.getSupplierWebWork(supplierWebWorksSearchRequest);

        if(supplierWebworksDtoList != null && !supplierWebworksDtoList.isEmpty()){
            for(SupplierWebworksDto supplierWebworksDto : supplierWebworksDtoList){
                Optional<Supplier> supplierOptional = supplierRepository.getSupplierByTenantAndShortName(tenant, supplierWebworksDto.getTPShortName().trim());
                Integer supplierId = 0;

                if(supplierOptional.isPresent()){
                    supplierId = supplierOptional.get().getRecId();
                }

                Supplier supplier = new Supplier();
                supplier.setRecId(supplierId);
                supplier.setShortName(supplierWebworksDto.getTPShortName());
                //supplier.setFullName(supplierWebworksDto.getFullCompanyNameLocal() + "(" + supplierWebworksDto.getBranchNameLocal() + ")");
                supplier.setFullCompanyNameLocal(supplierWebworksDto.getFullCompanyNameLocal());
                supplier.setFullCompanyNameEN(supplierWebworksDto.getFullCompanyNameEN());
                supplier.setCompanyNameLocal(supplierWebworksDto.getCompanyNameLocal());
                supplier.setCompanyNameEN(supplierWebworksDto.getCompanyNameEN());
                supplier.setBranchNameLocal(supplierWebworksDto.getBranchNameLocal());
                supplier.setBranchNameEN(supplierWebworksDto.getBranchNameEN());
                supplier.setTaxId(supplierWebworksDto.getTaxId());
                supplierList.add(supplier);
            }
        }

        boolean isShowSupplierLocalLanguage = tenantConfigService.isShowSupplierLocalLanguage(tenant.getRecId());
        String supplierFieldName = tenantConfigService.getSupplierFieldName(tenant.getRecId());
        return SupplierMapper.INSTANCE.toSupplierOptionDto(supplierList, isShowSupplierLocalLanguage, supplierFieldName);
    }

    @Override
    public Boolean isExistingERPSupplier(String taxRegistrationNumber, Integer tenantId) {
        Boolean result = false;
        String uri = String.format("%s?onlyData=true&limit=1&q=TaxRegistrationNumber=%s",tenantConfigService.getGetSupplier(tenantId), taxRegistrationNumber);

        try {

            SupplierResponse supplierResponse = bifrostClient.getOracleSupplier(uri);
            if(supplierResponse != null && supplierResponse.getCount() == 1){
                result = true;
            }
        } catch (HttpClientErrorException | ExternalServiceException | DataNotFoundException ex) {
            return false; //throw new RuntimeException(ex);
        }

        return result;
    }

    @Override
    public CreateSupplierResponse createERPSupplier(CreateSupplierRequest createSupplierRequest, Integer tenantId) {
        CreateSupplierResponse createSupplierResponse = null;
        try {

            String uri = String.format("%s",tenantConfigService.getCreateSupplier(tenantId));
            createSupplierResponse = bifrostClient.createOracleSupplier(uri, createSupplierRequest);
        } catch (HttpClientErrorException | ExternalServiceException | DataNotFoundException ex) {
            throw ex;
        }

        return createSupplierResponse;
    }

    @Override
    public List<SupplierOptionDto> getSupplierByRequestId(Tenant tenant, Long requestId, String searchTerm) {
        List<Supplier> supplierList = new ArrayList<>();

        int limit = Integer.valueOf(tenantConfigService.getLimitSearchSupplierWebwork(tenant.getRecId()));
        boolean isSearchByInvitationCode = tenantConfigService.getIsSearchByInvitationCode(tenant.getRecId());

        List<TenantInfoResponse> response = uaaService.getTenantInfo(tenant.getCode());
        SupplierWebWorksSearchRequest supplierWebWorksSearchRequest = new SupplierWebWorksSearchRequest();
        supplierWebWorksSearchRequest.setSearching(StringUtils.isNotBlank(searchTerm) ? searchTerm : "");
        supplierWebWorksSearchRequest.setInvitationCode(isSearchByInvitationCode ? (response != null && !response.isEmpty() ? response.get(0).getInvitation_code() : AppUtil.getTenantId()) : "");
        supplierWebWorksSearchRequest.setLimit(limit);
        List<SupplierWebworksDto> supplierWebworksDtoList = this.getSupplierWebWork(supplierWebWorksSearchRequest);

        if(supplierWebworksDtoList != null && !supplierWebworksDtoList.isEmpty()){
            for(SupplierWebworksDto supplierWebworksDto : supplierWebworksDtoList){
                Optional<Supplier> supplierOptional = supplierRepository.getSupplierByTenantAndShortName(tenant, supplierWebworksDto.getTPShortName().trim());
                Integer supplierId = 0;

                if(supplierOptional.isPresent()){
                    supplierId = supplierOptional.get().getRecId();
                }

                Supplier supplier = new Supplier();
                supplier.setRecId(supplierId);
                supplier.setShortName(supplierWebworksDto.getTPShortName());
                //supplier.setFullName(supplierWebworksDto.getFullCompanyNameLocal() + "(" + supplierWebworksDto.getBranchNameLocal() + ")");
                supplier.setFullCompanyNameLocal(supplierWebworksDto.getFullCompanyNameLocal());
                supplier.setFullCompanyNameEN(supplierWebworksDto.getFullCompanyNameEN());
                supplier.setCompanyNameLocal(supplierWebworksDto.getCompanyNameLocal());
                supplier.setCompanyNameEN(supplierWebworksDto.getCompanyNameEN());
                supplier.setBranchNameLocal(supplierWebworksDto.getBranchNameLocal());
                supplier.setBranchNameEN(supplierWebworksDto.getBranchNameEN());
                supplier.setTaxId(supplierWebworksDto.getTaxId());
                supplierList.add(supplier);
            }
        }

        List<ExistingPriceItemSupplier> existingPriceItemSupplierList = existingPriceItemSupplierRepository.findExistingPriceItemSupplierByRequest(requestId);
        for(ExistingPriceItemSupplier existingPriceItemSupplier : existingPriceItemSupplierList) {
            boolean isMatch = supplierList.stream().anyMatch(s -> s.getShortName().equalsIgnoreCase(existingPriceItemSupplier.getSupplierShortName()));
            if(!isMatch) {
                Supplier supplier = new Supplier();
                supplier.setRecId(existingPriceItemSupplier.getSupplier().getRecId());
                supplier.setShortName(existingPriceItemSupplier.getSupplier().getShortName());
                supplier.setFullCompanyNameLocal(existingPriceItemSupplier.getSupplier().getFullCompanyNameLocal());
                supplier.setFullCompanyNameEN(existingPriceItemSupplier.getSupplier().getFullCompanyNameEN());
                supplier.setCompanyNameLocal(existingPriceItemSupplier.getSupplier().getCompanyNameLocal());
                supplier.setCompanyNameEN(existingPriceItemSupplier.getSupplier().getCompanyNameEN());
                supplier.setBranchNameLocal(existingPriceItemSupplier.getSupplier().getBranchNameLocal());
                supplier.setBranchNameEN(existingPriceItemSupplier.getSupplier().getBranchNameEN());
                supplier.setTaxId(existingPriceItemSupplier.getSupplier().getTaxId());
                supplierList.add(supplier);
            }
        }

        boolean isShowSupplierLocalLanguage = tenantConfigService.isShowSupplierLocalLanguage(tenant.getRecId());
        String supplierFieldName = tenantConfigService.getSupplierFieldName(tenant.getRecId());
        return SupplierMapper.INSTANCE.toSupplierOptionDto(supplierList, isShowSupplierLocalLanguage, supplierFieldName);
    }


    @Override
    public Supplier getSupplierByShortName(String shortName, Integer tenantId) {
        return supplierRepository.getSupplierByShortName(shortName.trim(), tenantId);
    }

    @Override
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    public SupplierWebworksDto getSupplierWebWorkByTPShortName(SupplierWebWorkSearchByTPShortNameRequest supplierWebWorkSearchByTPShortNameRequest) {
        try {
            SupplierWebWorkResponse response = supplierSearchService.getSupplier(supplierWebWorkSearchByTPShortNameRequest);
            return response.getData();
        } catch (RestClientException | ExternalServiceException | DataNotFoundException ex) {
            return null;
        }
    }

    @Override
    public List<SupplierContactsDto> getSupplierContactByTPShortName(String tpShortName) {
        try {
            SupplierContactResponse response = contactSearchService.getContact(tpShortName);
            return response.getData();
        } catch (RestClientException | ExternalServiceException | DataNotFoundException ex) {
            return null;
        }
    }

    private List<SupplierWebworksDto> getSupplierWebWork(SupplierWebWorksSearchRequest supplierWebWorksSearchRequest) {
        try {
            SupplierWebWorksResponse response = supplierSearchService.searchCompany(supplierWebWorksSearchRequest);
            return response.getData();
        } catch (HttpClientErrorException | ExternalServiceException | DataNotFoundException ex) {
            return null;
        }
    }
}
