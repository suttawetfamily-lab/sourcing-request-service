package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthBasicAuthenClient;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingMenuDto;
import com.pantavanij.sourcingreq.services.domain.dto.SourcingRequestDisplayDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenantConfigServiceImplTest {
    private EpAuthBasicAuthenClient epAuthBasicAuthenClient = mock(EpAuthBasicAuthenClient.class);
    private SourcingMenuService sourcingMenuService = mock(SourcingMenuService.class);
    private TenantConfigRepository tenantConfigRepository = mock(TenantConfigRepository.class);
    private TenantService tenantService = mock(TenantService.class);
    private RequestReportRepository tenantRequestReportRepository = mock(RequestReportRepository.class);
    private TenantConfigService tenantConfigService = new TenantConfigServiceImpl(epAuthBasicAuthenClient, sourcingMenuService, tenantConfigRepository, tenantService, tenantRequestReportRepository);

    @Test
    public void getRequestItemTemplate_success() {
        tenantConfigService.getRequestItemTemplate(1);

        verify(tenantConfigRepository, times(1))
                .getValueByTenantIdAndTopicAndSectionAndName(1, "Template", "RequestItem", "NA");
    }


    @Test
    public void testGetLogoImageUrl() {
        Integer tenantId = 1;
        String topic = "Logo";
        String section = "Image";
        String name = "LOGO_IMAGE_URL";
        String mockValue = "https://ep.pantavanij.com/images/logo/true.svg";

        when(tenantConfigRepository.getValueByTenantIdAndTopicAndSectionAndName(tenantId, topic, section, name))
                .thenReturn(mockValue);

        String actualResult = tenantConfigService.getSRLogoImageFileId(tenantId);

        assertEquals(mockValue, actualResult);
    }

    @Test
    public void hasRoleRequester_getDisplayConfiguration_returnRequesterConfiguration() {
        String tenantCode = "TRUE";
        Integer tenantId = 1;
        Tenant mockTenant = Tenant.builder().recId(tenantId).code(tenantCode).build();

        when(tenantConfigRepository
                .getValueByTenantIdAndTopicAndSectionAndName(tenantId,"Configuration", "Show/Hide", "isShowStep3"))
                .thenReturn("true");

        SourcingRequestDisplayDto expectedResult = SourcingRequestDisplayDto.builder()
                .tenantId(mockTenant.getCode())
                .isShowExistingPriceCreateSourcing(false)
                .existingPriceSourcingMenu(null)
                .isShowStep3(true)
                .isShowAllApprovalTap(false)
                .build();

        SourcingRequestDisplayDto actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::isRequester).thenReturn(true);
            mockedStatic.when(AppUtil::isPurchaser).thenReturn(false);
            actualResult = tenantConfigService.getDisplayConfiguration(mockTenant);
        }

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void hasRolePurchaser_getDisplayConfiguration_returnPurchaserConfiguration() {
        String tenantCode = "TRUE";
        Integer tenantId = 1;
        Tenant mockTenant = Tenant.builder().recId(tenantId).code(tenantCode).build();

        List<SourcingMenuDto> sourcingMenuDtoList = Arrays.asList(
                SourcingMenuDto.builder()
                        .sourcingMenuId(tenantId.longValue())
                        .name("Existing Price")
                        .build()
        );

        when(tenantConfigRepository
                .getValueByTenantIdAndTopicAndSectionAndName(tenantId,"Configuration", "ExistingPriceItem", "isShowExistingPriceCreateSourcing"))
                .thenReturn("true");
        when(tenantConfigRepository
                .getValueByTenantIdAndTopicAndSectionAndName(tenantId,"Configuration", "Approval", "isShowAllApprovalTap"))
                .thenReturn("false");
        when(sourcingMenuService.getSourcingMenuByTenantId(tenantId)).thenReturn(sourcingMenuDtoList);

        SourcingRequestDisplayDto expectedResult = SourcingRequestDisplayDto.builder()
                .tenantId(mockTenant.getCode())
                .isShowExistingPriceCreateSourcing(true)
                .existingPriceSourcingMenu(sourcingMenuDtoList)
                .isShowStep3(false)
                .isShowAllApprovalTap(false)
                .build();

        SourcingRequestDisplayDto actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::isRequester).thenReturn(false);
            mockedStatic.when(AppUtil::isPurchaser).thenReturn(true);
            actualResult = tenantConfigService.getDisplayConfiguration(mockTenant);
        }

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void hasRoleRequesterAndPurchaser_getDisplayConfiguration_returnRequesterAndPurchaserConfiguration() {
        String tenantCode = "TRUE";
        Integer tenantId = 1;
        Tenant mockTenant = Tenant.builder().recId(tenantId).code(tenantCode).build();

        List<SourcingMenuDto> sourcingMenuDtoList = Arrays.asList(
                SourcingMenuDto.builder()
                        .sourcingMenuId(tenantId.longValue())
                        .name("Existing Price")
                        .build()
        );

        when(tenantConfigRepository
                .getValueByTenantIdAndTopicAndSectionAndName(tenantId,"Configuration", "Show/Hide", "isShowStep3"))
                .thenReturn("true");
        when(tenantConfigRepository
                .getValueByTenantIdAndTopicAndSectionAndName(tenantId,"Configuration", "ExistingPriceItem", "isShowExistingPriceCreateSourcing"))
                .thenReturn("true");
        when(tenantConfigRepository
                .getValueByTenantIdAndTopicAndSectionAndName(tenantId,"Configuration", "Approval", "isShowAllApprovalTap"))
                .thenReturn("true");
        when(sourcingMenuService.getSourcingMenuByTenantId(tenantId)).thenReturn(sourcingMenuDtoList);

        SourcingRequestDisplayDto expectedResult = SourcingRequestDisplayDto.builder()
                .tenantId(mockTenant.getCode())
                .isShowExistingPriceCreateSourcing(true)
                .existingPriceSourcingMenu(sourcingMenuDtoList)
                .isShowStep3(true)
                .isShowAllApprovalTap(true)
                .build();

        SourcingRequestDisplayDto actualResult;
        try (MockedStatic mockedStatic = mockStatic(AppUtil.class)) {
            mockedStatic.when(AppUtil::isRequester).thenReturn(true);
            mockedStatic.when(AppUtil::isPurchaser).thenReturn(true);
            actualResult = tenantConfigService.getDisplayConfiguration(mockTenant);
        }

        assertEquals(expectedResult, actualResult);
    }

}