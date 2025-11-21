package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.client.EpAuthClient;
import com.pantavanij.sourcingreq.services.domain.dto.ReviewerDto;
import com.pantavanij.sourcingreq.services.domain.dto.UserDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Reviewer;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.ReviewerRequest;
import com.pantavanij.sourcingreq.services.domain.request.ReviewerSearchRequest;
import com.pantavanij.sourcingreq.services.domain.response.ReviewerResponse;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.ReviewerRepository;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReviewerServiceImplTest {

    @Mock
    private ReviewerRepository reviewerRepository;

    @Mock
    private TenantService tenantService;

    @Mock
    private UaaService uaaService;

    @Mock
    private EPAuthService epAuthService;

    @Mock
    private TenantConfigService tenantConfigService;

    @Mock
    private EpAuthClient epAuthClient;

    @InjectMocks
    private ReviewerServiceImpl reviewerService;

    private Reviewer reviewer;
    private Tenant tenant;
    private UserDto userDto;

    @Before
    public void setUp() {
        tenant = new Tenant();
        tenant.setRecId(1);
        tenant.setCode("TEST");

        reviewer = new Reviewer();
        reviewer.setRecId(1);
        reviewer.setUserId(100);
        reviewer.setReviewerName("Test Reviewer");
        reviewer.setEmail("test@test.com");
        reviewer.setPhone("1234567890");
        reviewer.setTenant(tenant);

        userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setTenantId("TEST");
    }

    @Test
    public void getRequestReviewer_Success() {
        ReviewerSearchRequest request = new ReviewerSearchRequest();
        request.setExceptReviewers(new ArrayList<>());
        Pageable pageable = PageRequest.of(0, 10);
        List<Reviewer> reviewers = List.of(reviewer);
        Page<Reviewer> reviewerPage = new PageImpl<>(reviewers);

        when(reviewerRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(reviewerPage);

        ReviewerResponse response = reviewerService.getRequestReviewer(request, pageable);

        assertNotNull(response);
        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getData().size());
    }

    @Test
    public void saveReviewer_Success() {
        ReviewerDto reviewerDto = new ReviewerDto();
        reviewerDto.setUserId("100");
        reviewerDto.setReviewerName("Test Reviewer");
        reviewerDto.setEmail("test@test.com");

        try (MockedStatic<AppUtil> appUtil = Mockito.mockStatic(AppUtil.class)) {
            appUtil.when(AppUtil::getUser).thenReturn(userDto);
            when(tenantService.findByCode(anyString())).thenReturn(tenant);
            when(reviewerRepository.findByUserId(anyInt())).thenReturn(Optional.empty());
            when(reviewerRepository.save(any(Reviewer.class))).thenReturn(reviewer);

            Integer result = reviewerService.saveReviewer(reviewerDto);

            assertNotNull(result);
            assertEquals(Integer.valueOf(1), result);
        }
    }

    @Test
    public void createReviewer_Success() {
        ReviewerRequest request = new ReviewerRequest();
        request.setReviewerName("Test Reviewer");
        request.setEmail("test@test.com");
        request.setSequence(0);

        try (MockedStatic<AppUtil> appUtil = Mockito.mockStatic(AppUtil.class)) {
            appUtil.when(AppUtil::getTenantId).thenReturn("TEST");
            appUtil.when(AppUtil::getUserName).thenReturn("testuser");

            when(tenantService.findByCode(anyString())).thenReturn(tenant);
            when(reviewerRepository.findFirstByTenantRecIdOrderBySequenceDesc(anyInt()))
                    .thenReturn(Optional.empty());
            when(reviewerRepository.save(any(Reviewer.class))).thenReturn(reviewer);
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");

            ReviewerDto result = reviewerService.createReviewer(request);

            assertNotNull(result);
            assertEquals("Test Reviewer", result.getReviewerName());
        }
    }

    @Test
    public void deleteReviewer_Success() {
        try (MockedStatic<AppUtil> appUtil = Mockito.mockStatic(AppUtil.class)) {
            appUtil.when(AppUtil::getTenantId).thenReturn("TEST");

            when(tenantService.findByCode(anyString())).thenReturn(tenant);
            when(reviewerRepository.findReviewerByRecIdAndTenant(anyInt(), anyInt()))
                    .thenReturn(Optional.of(reviewer));
            doNothing().when(reviewerRepository).deleteReviewerByRecId(anyInt());
            doNothing().when(reviewerRepository).reOrderSequenceByTenantRecId(anyInt());

            boolean result = reviewerService.deleteReviewer(1);

            assertTrue(result);
            verify(reviewerRepository).deleteReviewerByRecId(1);
        }
    }

    @Test
    public void findReviewerByRecId_Success() {
        try (MockedStatic<AppUtil> appUtil = Mockito.mockStatic(AppUtil.class)) {
            appUtil.when(AppUtil::getTenantId).thenReturn("TEST");
            appUtil.when(AppUtil::getUser).thenReturn(userDto);

            when(tenantService.findByCode(anyString())).thenReturn(tenant);
            when(reviewerRepository.findReviewerByRecIdAndTenant(anyInt(), anyInt()))
                    .thenReturn(Optional.of(reviewer));
            when(uaaService.getUserTimeZone(any(), any())).thenReturn("UTC");

            ReviewerDto result = reviewerService.findReviewerByRecId(1);

            assertNotNull(result);
            assertEquals("Test Reviewer", result.getReviewerName());
        }
    }
}
