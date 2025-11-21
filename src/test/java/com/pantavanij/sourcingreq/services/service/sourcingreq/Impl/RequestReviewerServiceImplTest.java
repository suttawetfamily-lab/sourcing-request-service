//package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;
//
//import com.pantavanij.sourcingreq.services.client.EpAuthClient;
//import com.pantavanij.sourcingreq.services.domain.dto.RequestReviewerDto;
//import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Request;
//import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.RequestReviewer;
//import com.pantavanij.sourcingreq.services.domain.mapper.RequestReviewerMapper;
//import com.pantavanij.sourcingreq.services.domain.request.RequestReviewerRequest;
//import com.pantavanij.sourcingreq.services.domain.request.ReviewerCommentRequest;
//import com.pantavanij.sourcingreq.services.repository.sourcingreq.ReportLineRepository;
//import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestRepository;
//import com.pantavanij.sourcingreq.services.repository.sourcingreq.RequestReviewerRepository;
//import com.pantavanij.sourcingreq.services.repository.sourcingreq.TenantRepository;
//import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Mockito.mock;
//
//@RunWith(MockitoJUnitRunner.class)
//public class RequestReviewerServiceImplTest {
//    private RequestReviewerRepository requestReviewerRepository = mock(RequestReviewerRepository.class);
//    private RequestRepository requestRepository = mock(RequestRepository.class);
//    private RequestHistoryService requestHistoryService = mock(RequestHistoryService.class);
//    private UaaService uaaService = mock(UaaService.class);
//    private TenantConfigService tenantConfigService = mock(TenantConfigService.class);
//    private TenantService tenantService = mock(TenantService.class);
//    private EpAuthClient epAuthClient = mock(EpAuthClient.class);
//    private ReportLineRepository reportLineRepository = mock(ReportLineRepository.class);
//
//    private RequestReviewerService requestReviewerService = new RequestReviewerServiceImpl(
//            requestReviewerRepository,
//            reportLineRepository,
//            requestRepository,
//            requestHistoryService,
//            uaaService,
//            tenantConfigService,
//            tenantService,
//            epAuthClient
//            );
//
//    @Test
//    public void findByRequest_success() {
//        List<RequestReviewerDto> requestReviewerDtoList = new ArrayList<>();
//        List<RequestReviewer> requestReviewerList = new ArrayList<>();
//        Request request = new Request();
//
//        Mockito.when(requestReviewerRepository.findRequestReviewerByRequest(Mockito.any())).thenReturn(requestReviewerList);
//        if(requestReviewerList != null && !requestReviewerList.isEmpty()){
//            requestReviewerDtoList =
//                    RequestReviewerMapper.INSTANCE.toRequestReviewerDtoList(requestReviewerList, "Asia/Bangkok");
//        }
//        Assert.assertEquals(requestReviewerDtoList, requestReviewerService.findByRequest(Mockito.any()));
//    }
//
//    @Test
//    public void findRequestReviewerByReviewerName_success() {
//        List<Long> requestId = new ArrayList<>();
//        List<RequestReviewer> requestReviewerList = new ArrayList<>();
//
//        Mockito.when(requestReviewerRepository.findRequestReviewerByReviewerName(Mockito.anyString())).thenReturn(requestReviewerList);
//        if(requestReviewerList != null && !requestReviewerList.isEmpty()){
//            for(RequestReviewer reviewer : requestReviewerList){
//                requestId.add(reviewer.getRequest().getRecId());
//            }
//        }
//        Assert.assertEquals(requestId, requestReviewerService.findRequestReviewerByReviewerName(Mockito.anyString()));
//    }
//
//    @Test
//    public void saveCommentReviewer_success() {
//        ReviewerCommentRequest reviewerCommentRequest = new ReviewerCommentRequest();
//        reviewerCommentRequest.setRecId(4L);
//        reviewerCommentRequest.setRequestId(375L);
//        reviewerCommentRequest.setComment("test comment");
//
//        requestReviewerService.saveCommentReviewer(reviewerCommentRequest);
//    }
//
//    @Test
//    public void saveRequestReviewerRequest_success() {
//        List<String> reviewers = new ArrayList<>();
//        reviewers.add("supansa");
//        reviewers.add("purip");
//
//        RequestReviewerRequest requestReviewerRequest = new RequestReviewerRequest();
//        requestReviewerRequest.setRequestId(375L);
//        requestReviewerRequest.setReviewers(reviewers);
//
//        requestReviewerService.saveRequestReviewer(requestReviewerRequest);
//    }
//
//}
