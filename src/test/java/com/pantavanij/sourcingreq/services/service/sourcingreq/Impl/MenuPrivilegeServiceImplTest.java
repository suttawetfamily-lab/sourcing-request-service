package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.MenuPrivilegeRepository;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestItemReportRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuPrivilegeServiceImplTest {

    @Mock
    private MenuPrivilegeRepository menuPrivilegeRepository;

    @Mock
    private RequestItemReportRepository requestItemReportRepository;

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private MenuPrivilegeServiceImpl menuPrivilegeService;

    private static final String TENANT_ID = "TEST_TENANT";
    private Tenant tenant;
    private MenuPrivilege menuPrivilege;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder()
                .recId(1)
                .code(TENANT_ID)
                .name("Test Tenant")
                .build();

        menuPrivilege = MenuPrivilege.builder()
                .recId(1)
                .tenant(tenant)
                .pathUrl("/test")
                .typeId(1)
                .privilegeCode("READ")
                .label("Test Menu")
                .build();
    }

    @Test
    void createMenuPrivilege_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn(TENANT_ID);
            when(tenantService.findByCode(TENANT_ID)).thenReturn(tenant);

            MenuPrivilegeRequest request = new MenuPrivilegeRequest();
            request.setRecId(1);
            request.setPathUrl("/test");
            request.setTypeId(1);
            request.setPrivilegeCode("READ");
            request.setLabel("Test");

            when(menuPrivilegeRepository.findByTenantRecIdAndRecId(tenant.getRecId(), request.getRecId()))
                    .thenReturn(Optional.empty());
            when(menuPrivilegeRepository.save(any(MenuPrivilege.class))).thenReturn(menuPrivilege);

            Integer result = menuPrivilegeService.createMenuPrivilege(request);

            assertNotNull(result);
            assertEquals(1, result);
            verify(menuPrivilegeRepository).save(any(MenuPrivilege.class));
        }
    }

    @Test
    void getMenuPrivilege_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            Map<String, String> privilegeScopes = new HashMap<>();
            privilegeScopes.put("read", "read");

            appUtilMock.when(AppUtil::getTenantId).thenReturn(TENANT_ID);
            appUtilMock.when(AppUtil::getPrivilegeScopes).thenReturn(privilegeScopes);

            menuPrivilege.setPrivilegeCode("read");

            when(tenantService.findByCode(TENANT_ID)).thenReturn(tenant);
            when(menuPrivilegeRepository.getMenuPrivilege(tenant.getRecId()))
                    .thenReturn(Collections.singletonList(menuPrivilege));
            when(requestItemReportRepository.findRequestItemReportByTenant(tenant.getRecId()))
                    .thenReturn(new ArrayList<>());

            List<MenuPrivilegeDto> result = menuPrivilegeService.getMenuPrivilege();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
        }
    }

    @Test
    void deleteMenuPrivilege_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn(TENANT_ID);
            when(tenantService.findByCode(TENANT_ID)).thenReturn(tenant);
            when(menuPrivilegeRepository.findByTenantRecIdAndRecId(tenant.getRecId(), 1))
                    .thenReturn(Optional.of(menuPrivilege));
            when(requestItemReportRepository.findByMenuPrivilege_RecIdIn(anyList()))
                    .thenReturn(Collections.emptyList());

            Integer result = menuPrivilegeService.deleteMenuPrivilegeById(1);

            assertEquals(1, result);
            verify(menuPrivilegeRepository).delete(menuPrivilege);
        }
    }

    @Test
    void checkPermission_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            appUtilMock.when(AppUtil::getTenantId).thenReturn(TENANT_ID);
            when(tenantService.findByCode(TENANT_ID)).thenReturn(tenant);

            Map<String, String> privilegeMap = Map.of("READ", "read");
            when(menuPrivilegeRepository.findAll(any(Specification.class)))
                    .thenReturn(Collections.singletonList(menuPrivilege));

            MenuPrivilegeDto result = menuPrivilegeService.checkPermission("/test", 1, privilegeMap);

            assertNotNull(result);
        }
    }
    @Test
    void searchMenuPrivilegeByCondition_Success() {
        try (MockedStatic<AppUtil> appUtilMock = mockStatic(AppUtil.class)) {
            // Only mock what's needed
            appUtilMock.when(AppUtil::getTenantId).thenReturn(TENANT_ID);

            Pageable pageable = PageRequest.of(0, 10);
            Page<MenuPrivilege> page = new PageImpl<>(Collections.singletonList(menuPrivilege), pageable, 1);
            when(menuPrivilegeRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            MenuPrivilegeSearchDto result = menuPrivilegeService.searchMenuPrivilegeByCondition(
                new MenuPrivilegeSearchRequest(),
                pageable
            );

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertFalse(result.getMenuPrivilegeList().isEmpty());
            assertEquals(0, result.getPage());

            // Verify the mock was used
            verify(menuPrivilegeRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }
}