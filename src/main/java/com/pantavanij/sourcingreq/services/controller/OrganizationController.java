package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.constraint.SearchTermConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.OrganizationClientDto;
import com.pantavanij.sourcingreq.services.domain.mapper.OrganizationMapper;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.enums.OrganizationOption;
import com.pantavanij.sourcingreq.services.service.sourcingreq.UaaService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class OrganizationController {

    private final UaaService uaaService;

    @GetMapping(value = "/organization/option", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getOrganizationOption() {
        List<OptionDto> organizationOptionList = new ArrayList<>();
        for (OrganizationOption option : OrganizationOption.values()) {
            organizationOptionList.add(new OptionDto(option.getValue().toString(), option.getCode(), option.getDescription(), option.getIsDefault()));
        }

        if (!organizationOptionList.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse(organizationOptionList));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(null, new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description())));
        }
    }

    @GetMapping(value = "/organization", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getOrganization(
            @RequestParam(value = "exclude", required = false) String excludeOrganizations) {

        String tenantId = AppUtil.getTenantId();
        String idp = AppUtil.getIdp();
        String username = AppUtil.getUserName();

        // 🔹 แปลง string list → List<Integer>
        List<Integer> excludeList = null;
        if (excludeOrganizations != null && !excludeOrganizations.trim().isEmpty()) {
            excludeList = Arrays.stream(excludeOrganizations.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }

        OrganizationClientDto organizationClientDto =
                uaaService.getOrganizationByTenantIdAndUserName(tenantId, idp, username, excludeList);

        // ป้องกัน NullPointerException
        List<OptionDto> projectOption = new ArrayList<>();
        if (organizationClientDto != null && organizationClientDto.getBorgUserList() != null) {
            projectOption = OrganizationMapper.INSTANCE.toOrganizationOptionDto(organizationClientDto.getBorgUserList());
        }

        return ResponseEntity.ok().body(new ApiResponse<>(projectOption));
    }




}
