package com.pantavanij.sourcingreq.services.client;

import com.pantavanij.sourcingreq.services.config.FeignClientConfig;
import com.pantavanij.sourcingreq.services.domain.request.WorkflowInstanceApprovalRequest;
import com.pantavanij.sourcingreq.services.domain.request.WorkflowInstanceRequest;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.interceptor.MethodExecuteTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "workflow-client", url = "${base.url.api.workflow}", configuration = FeignClientConfig.class)
public interface WorkflowClient {

    String AUTH_TOKEN = "Authorization";

    @MethodExecuteTime
    @PostMapping(value = "/api/projects/sourcingreq/workflow-instance/{workflowInstanceId}/start",
            produces = "application/json")
    ApiResponseStatus startWorkflow(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                    @PathVariable("workflowInstanceId") Long workflowInstanceId,
                                    @RequestBody Map<String, String> map);

    @MethodExecuteTime
    @PostMapping(value = "/api/projects/sourcingreq/workflow-instance/workflow/{workflowId}",
            produces = "application/json")
    WorkflowInstanceSaveResponse generateWorkflow(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                                  @PathVariable("workflowId") Long workflowId,
                                                  @RequestBody WorkflowInstanceRequest request);

    @MethodExecuteTime
    @GetMapping(value = "/api/projects/sourcingreq/workflow-instance/{workflowId}",
            produces = "application/json")
    WorkflowDefaultApproverResponse getWorkflowApprover(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                                                     @PathVariable("workflowId") Long workflowId);

    @MethodExecuteTime
    @PostMapping(value = "/api/projects/sourcingreq/workflow-instance/{workflowId}/{approvalType}",
            produces = "application/json")
    WorkflowInstanceApprovalResponse approval(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                              @PathVariable("workflowId") Long workflowId,
                                              @PathVariable("approvalType") String approvalType,
                                              @RequestBody WorkflowInstanceApprovalRequest request);

    @MethodExecuteTime
    @PostMapping(value = "/api/projects/sourcingreq/workflow-instance/{workflowId}/cancel?remark={remark}",
            produces = "application/json")
    WorkflowInstanceApprovalResponse cancel(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                              @PathVariable("workflowId") Long workflowId,
                                              @PathVariable("remark") String remark,
                                              @RequestBody WorkflowInstanceApprovalRequest request);

    @MethodExecuteTime
    @GetMapping(value = "/api/projects/sourcingreq/workflow-instance-approver", produces = "application/json")
    WorkflowInstanceApproverResultPageResponse approval(@RequestHeader(AUTH_TOKEN) String bearerToken,
                                                        @RequestParam("pageNumber") Integer pageNumber,
                                                        @RequestParam("pageSize") Integer pageSize,
                                                        @RequestParam("approvalType") List<String> approvalType,
                                                        @RequestParam("workflowId") List<Long> workflowId,
                                                        @RequestParam("workflowInstanceId") List<Long> workflowInstanceId,
                                                        @RequestParam("status") List<String> status,
                                                        @RequestParam("referApproverId") List<String> referApproverId,
                                                        @RequestParam("refDocumentId") List<String> refDocumentId,
                                                        @RequestParam("sortBy") List<String> sortBy,
                                                        @RequestParam("sortDirection") List<String> sortDirection);
}
