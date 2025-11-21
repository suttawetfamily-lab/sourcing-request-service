package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.ContractDetailClientDto;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.OrganizationClientDto;
import com.pantavanij.sourcingreq.services.domain.mapper.OrganizationMapper;
import com.pantavanij.sourcingreq.services.domain.request.AuthenticationRequest;
import com.pantavanij.sourcingreq.services.domain.request.RefreshAuthenticationRequest;
import com.pantavanij.sourcingreq.services.domain.request.UpdateSessionRequest;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.DeleteSessionResponse;
import com.pantavanij.sourcingreq.services.domain.response.VerifySessionResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.EPAuthService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class UaaController {

    private static final Logger logger = LoggerFactory.getLogger(UaaController.class);
    private final UaaService uaaService;
    private final EPAuthService epAuthService;

    @PostMapping("/oauth/token")
    public ResponseEntity getToken(@Valid @RequestBody AuthenticationRequest request) {
        Map<String, Object> responseMap = uaaService.getToken(request);
        logger.info("getToken responseMap : {} ", responseMap);
        if (responseMap.isEmpty()) {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E1004, String.format(ApiMessage.E1004.description(), "")), HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok().body(responseMap);
    }

    @PostMapping("/oauth/refresh/token")
    public ResponseEntity getRefreshToken(@Valid @RequestBody RefreshAuthenticationRequest request) {
        Map<String, Object> responseMap = uaaService.getRefreshToken(request);
        if (responseMap.isEmpty()) {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E1004, String.format(ApiMessage.E1004.description(), "")), HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok().body(responseMap);
    }

    @PostMapping(value = "/oauth/token/exchange")
    public ResponseEntity getTokenExchange(@RequestParam("refreshToken") String refreshToken) {
        Map<String, Object> responseMap = uaaService.getTokenExchange(refreshToken);
        if (responseMap.isEmpty()) {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E1004, String.format(ApiMessage.E1004.description(), "")), HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok().body(responseMap);
    }

    @GetMapping(value = "/contract-detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ContractDetailClientDto>> getContractDetail() {
        String tenantId = AppUtil.getTenantId();
        String idp = AppUtil.getIdp();
        String username = AppUtil.getUserName();
        ContractDetailClientDto contractDetailClientDto = uaaService.getContractDetail(tenantId, idp, username, null);
        contractDetailClientDto.setRequester(AppUtil.isRequester());
        contractDetailClientDto.setPurchaser(AppUtil.isPurchaser());
        contractDetailClientDto.setReviewer(AppUtil.isReviewer());
        contractDetailClientDto.setReportLine(AppUtil.isReportLine());
        return ResponseEntity.ok().body(new ApiResponse(contractDetailClientDto));
    }

//    @GetMapping(value = "/organization", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<List<OptionDto>>> getOrganization() {
//        String tenantId = AppUtil.getTenantId();
//        String idp = AppUtil.getIdp();
//        String username = AppUtil.getUserName();
//        OrganizationClientDto organizationClientDto =
//                uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username);
//        List<OptionDto> projectOption =
//                OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());
//        return ResponseEntity.ok().body(new ApiResponse(projectOption));
//    }

    @PostMapping("/session-expire/checking")
    public ResponseEntity<VerifySessionResponse> checkSessionExpired(@RequestBody UpdateSessionRequest request) {
        VerifySessionResponse response = epAuthService.updateSession(request);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/session/delete")
    public ResponseEntity<DeleteSessionResponse> deleteSession() {
        DeleteSessionResponse response = epAuthService.deleteUserSession(AppUtil.getUserName(), AppUtil.getTenantId());
        return ResponseEntity.ok().body(response);
    }

}

