package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Pr;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PRServiceImplTest {

    @InjectMocks
    private PrServiceImpl prService;
    @Mock
    private PrRepository prRepository;

    @Mock
    private TenantServiceImpl tenantService;
    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private ExistingPriceItemServiceImpl existingPriceItemService;
    @Mock
    private ExistingPriceItemRepository existingPriceItemRepository;

    @Mock
    private ProjectServiceImpl projectService;
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private DepartmentServiceImpl departmentService;
    @Mock
    private DepartmentRepository departmentRepository;

    private Tenant tenant;
    private RequestDto requestDto;
    private ExistingPriceItemResponseDto existingPriceItemResponseDto;


    @Before
    public void init() {
        tenant = new Tenant();
        tenant.setRecId(1);
        tenant.setCode("ait");
        tenant.setName("ait");
        tenant.setDescription("test");
        tenant.setCreatedBy("test");
        tenant.setCreatedDate(DateTimeUtil.getTimestampUTC());
        requestDto = new RequestDto();
        requestDto.setRecId(Long.valueOf(1));
        existingPriceItemResponseDto = new ExistingPriceItemResponseDto();
        existingPriceItemResponseDto.setRecId(Long.valueOf(1));

        ReflectionTestUtils.setField(prService, "tenantService", tenantService);
        ReflectionTestUtils.setField(prService, "projectService", projectService);
        ReflectionTestUtils.setField(prService, "departmentService", departmentService);
        ReflectionTestUtils.setField(prService, "existingPriceItemService", existingPriceItemService);
    }

    @Test
    public void testSetDepartmentDto_caseRequestTypeEqualOne_shouldReturnDepartmentDto() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // arrange
        Method method = PrServiceImpl.class.getDeclaredMethod("setDepartmentDto", Integer.class, Integer.class, String.class);
        method.setAccessible(true);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setRecId(1);
        projectDto.setCode("ProjectCode");
        projectDto.setName("ProjectName");

        when(projectService.getProjectByProjectCode(anyString(), anyInt())).thenReturn(projectDto);

        // act
        DepartmentDto actual = (DepartmentDto) method.invoke(prService, 1, 1, "AIT");

        // assert
        assertEquals(1, actual.getRecId());
        assertEquals("ProjectCode", actual.getCode());
        assertEquals("ProjectName", actual.getName());
    }

    @Test
    public void testSetDepartmentDto_caseRequestTypeEqualTwo_shouldReturnDepartmentDto() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // arrange
        Method method = PrServiceImpl.class.getDeclaredMethod("setDepartmentDto", Integer.class, Integer.class, String.class);
        method.setAccessible(true);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setRecId(2);
        projectDto.setCode("ProjectCode");
        projectDto.setName("ProjectName");

        when(projectService.getProjectByProjectCode(anyString(), anyInt())).thenReturn(projectDto);

        // act
        DepartmentDto actual = (DepartmentDto) method.invoke(prService, 1, 2, "AIT");

        // assert
        assertEquals(2, actual.getRecId());
        assertEquals("ProjectCode", actual.getCode());
        assertEquals("ProjectName", actual.getName());
    }

    @Test
    public void testSetDepartmentDto_caseRequestTypeEqualThree_shouldReturnDepartmentDto() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // arrange
        Method method = PrServiceImpl.class.getDeclaredMethod("setDepartmentDto", Integer.class, Integer.class, String.class);
        method.setAccessible(true);

        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setRecId(3);
        departmentDto.setCode("DepartmentCode");
        departmentDto.setName("DepartmentName");

        when(departmentService.getDepartmentByDepartmentCode(anyString(), anyInt())).thenReturn(departmentDto);

        // act
        DepartmentDto actual = (DepartmentDto) method.invoke(prService, 1, 3, "AIT");

        // assert
        assertEquals(3, actual.getRecId());
        assertEquals("DepartmentCode", actual.getCode());
        assertEquals("DepartmentName", actual.getName());
    }

    @Test
    public void testSetExistingPriceItemDto_sendTenantIdRequestIdAndRequestItemDtos_shouldFoundExistingPriceItemDto() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // arrange
        Method method = PrServiceImpl.class.getDeclaredMethod("setExistingPriceItemDto", Integer.class, Long.class, List.class);
        method.setAccessible(true);
        RequestItemDto requestItemDto = new RequestItemDto();
        requestItemDto.setRecId(Long.valueOf(1));
        List<RequestItemDto> requestItemDtoList = Collections.singletonList(requestItemDto);
        when(existingPriceItemService.getExistingPriceItemByRequestIdAndRequestItemId(anyLong(), anyLong(), anyInt(),"")).thenReturn(existingPriceItemResponseDto);

        // act
        List<RequestItemDto> actual = (List<RequestItemDto>) method.invoke(prService, 1, Long.valueOf(1), requestItemDtoList);

        // assert
        assertNotNull(actual);
        assertNotNull(actual.get(0).getExistingPriceItemDto());
        assertEquals(Long.valueOf(1), actual.get(0).getRecId());
        assertEquals(1, actual.size());
    }

    @Test
    public void testSavePrNumber_sendPrNumber_shouldReturnPr() {
        String tenantId = "1";
        String prNumber = "12345";
        String userName = "My UserName";
        Timestamp mockTimestamp = TestUtil.getMockTimestamp();
        Tenant mockTenant = Tenant.builder().recId(1).name("TRUE").build();

        Pr expectedResult = Pr.builder().prNumber(prNumber).createdBy(userName).createdDate(mockTimestamp).tenant(mockTenant).build();

        when(tenantService.findByCode(tenantId)).thenReturn(mockTenant);

        try (MockedStatic mockDateTimeUtil = mockStatic(DateTimeUtil.class, CALLS_REAL_METHODS);
             MockedStatic mockAppUtil = mockStatic(AppUtil.class)) {
            mockAppUtil.when(AppUtil::getTenantId).thenReturn(tenantId);
            mockAppUtil.when(AppUtil::getUserName).thenReturn(userName);
            mockDateTimeUtil.when(DateTimeUtil::getTimestampUTC).thenReturn(mockTimestamp);
            prService.savePrNumber(prNumber);
        }

        verify(prRepository, times(1)).save(expectedResult);
    }

    @Test
    public void testSaveRequestItemPR_sendPrIdAndRequestItemsId_shouldInvokeOneTimeOnly() {
        // arrange
        prService = mock(PrServiceImpl.class);
        List<Long> requestItemsId = new ArrayList<>();

        // act
        prService.saveRequestItemPR(0, requestItemsId);

        // assert
        verify(prService, times(1)).saveRequestItemPR(anyInt(), anyList());
    }

    @Test
    public void testCreateHeaders_shouldReturnHttpHeader() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, UnsupportedEncodingException {
        // arrange
        Method method = PrServiceImpl.class.getDeclaredMethod("createHeaders");
        method.setAccessible(true);

        // act
        HttpHeaders actual = (HttpHeaders) method.invoke(prService);
        String[] actualArr = actual.get("Authorization").get(0).split(" ");
        byte[] decodedBytes = Base64.getDecoder().decode(actualArr[1].getBytes());
        String actualUsernameAndPassword = new String(decodedBytes, StandardCharsets.UTF_8);

        // assert
        assertEquals("Basic", actualArr[0]);
        assertEquals("sourcingreq:P@ssw0rd", actualUsernameAndPassword);
        assertNotNull(actual.containsKey("Authorization"));
        assertNotNull(actual);
    }

    @Test
    public void testGenerateRefPrNumber_findMaxPRNumberNotFound_shouldReturnPrNumberEndWithOne() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // arrange
        Method method = PrServiceImpl.class.getDeclaredMethod("generateRefPrNumber", Long.class, String.class, Tenant.class);
        method.setAccessible(true);
        Long requestId = Long.valueOf(1122);
        String requestNo = "00123";
        Tenant tenant = new Tenant();
        tenant.setRecId(1);
        when(prRepository.findMaxPRNumberByRequestId(anyLong(), anyInt())).thenReturn(Optional.empty());

        // act
        String actual = (String) method.invoke(prService, requestId, requestNo, tenant);

        // assert
        assertEquals("RefPR_00123-01", actual);
    }

    @Test
    public void testGenerateRefPrNumber_findMaxPRNumberFoundMaxPrNumber_shouldReturnPrNumberPlusOne() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // arrange
        Method method = PrServiceImpl.class.getDeclaredMethod("generateRefPrNumber", Long.class, String.class, Tenant.class);
        method.setAccessible(true);
        Long requestId = Long.valueOf(1122);
        String requestNo = "00123";
        Tenant tenant = new Tenant();
        tenant.setRecId(1);
        when(prRepository.findMaxPRNumberByRequestId(anyLong(), anyInt())).thenReturn(Optional.of("1"));

        // act
        String actual = (String) method.invoke(prService, requestId, requestNo, tenant);

        // assert
        assertEquals("RefPR_00123-02", actual);
    }

}
