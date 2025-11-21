package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.service.sourcingreq.MenuPrivilegeService;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.ApiMessage.E1004;
import static com.pantavanij.sourcingreq.services.enums.ApiMessage.E1005;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class MenuPrivilegeController {

    private static final Logger logger = LoggerFactory.getLogger(MenuPrivilegeController.class);
    private final MenuPrivilegeService menuPrivilegeService;


    @GetMapping(value = "/privilege/menu", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getMenuPrivilege() {
        List<MenuPrivilegeDto> menuPrivilegeDtoList = menuPrivilegeService.getMenuPrivilege();
        if (menuPrivilegeDtoList != null) {
            return ResponseEntity.ok().body(menuPrivilegeDtoList);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/privilege/menu/{pathUrl}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity checkPermission(@Valid @PathVariable String pathUrl) {
        logger.info("checkPermission pathUrl : {} ", pathUrl);
        MenuPrivilegeDto menuPrivilegeDto = menuPrivilegeService.checkPermission(pathUrl, 1);
        if (menuPrivilegeDto.getPathUrl() != null && !"".equals(menuPrivilegeDto.getPathUrl())) {
            return new ResponseEntity<>(new MenuPrivilegeResponse(menuPrivilegeDto.getPathUrl(), true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(E1005, String.format(E1005.description(), "")), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/privilege/menu/master-data/{pathUrl}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity checkMasterDataPermission(@Valid @PathVariable String pathUrl) {
        logger.info("checkPermission pathUrl : {} ", pathUrl);
        MenuPrivilegeDto menuPrivilegeMasterTypeDto = menuPrivilegeService.checkPermission(pathUrl, 2);
        if (menuPrivilegeMasterTypeDto.getPathUrl() != null && !"".equals(menuPrivilegeMasterTypeDto.getPathUrl())) {
            return new ResponseEntity<>(new MenuPrivilegeResponse(menuPrivilegeMasterTypeDto.getPathUrl(), true), HttpStatus.OK);
        }
        MenuPrivilegeDto menuPrivilegeDynamicTypeDto = menuPrivilegeService.checkPermission(pathUrl, 3);
        if (menuPrivilegeDynamicTypeDto.getPathUrl() != null && !"".equals(menuPrivilegeDynamicTypeDto.getPathUrl())) {
            return new ResponseEntity<>(new MenuPrivilegeResponse(menuPrivilegeDynamicTypeDto.getPathUrl(), true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(E1005, String.format(E1005.description(), "")), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/privilege/menu/dynamic/{pathUrl}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity checkDynamicPermission(@Valid @PathVariable String pathUrl) {
        logger.info("checkPermission pathUrl : {} ", pathUrl);
        MenuPrivilegeDto menuPrivilegeDto = menuPrivilegeService.checkPermission(pathUrl, 3);
        if (menuPrivilegeDto.getPathUrl() != null && !"".equals(menuPrivilegeDto.getPathUrl())) {
            return new ResponseEntity<>(new MenuPrivilegeResponse(menuPrivilegeDto.getPathUrl(), true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(E1004, String.format(E1004.description(), "")), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(value = "/privilege/menu/dynamic", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getMenuPrivilegeDynamic(@RequestParam("privilegeCode") String privilegeCode) {
        List<MenuPrivilegeObjDto> menuPrivilegeDtoList = menuPrivilegeService.getMenuPrivilegeByTypeIdPrivilegeCode(1, privilegeCode);
        if (menuPrivilegeDtoList != null && !menuPrivilegeDtoList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(menuPrivilegeDtoList), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/privilege/menu/all-privilege-code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllMenuPrivilegeCode() {
        List<OptionDto> optionDtoList = menuPrivilegeService.getAllMenuPrivilegeCode();
        if (optionDtoList != null && !optionDtoList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(optionDtoList), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/privilege/menu/view/{menuPrivilegeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getMenuPrivilegeByTenantId(@PathVariable("menuPrivilegeId") Integer menuPrivilegeId) {
        MenuPrivilegeDto menuPrivilegeDto = menuPrivilegeService.getMenuPrivilegeById(menuPrivilegeId);
        if (menuPrivilegeDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(menuPrivilegeDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "Privilege menu ID: " + menuPrivilegeId)), HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/privilege/menu/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchMenuPrivilege(@RequestBody @Valid MenuPrivilegeSearchRequest request) {
        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(Constant.REC_ID).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        MenuPrivilegeSearchDto menuPrivilegeSearchDto = menuPrivilegeService.searchMenuPrivilegeByCondition(request, pageable);
        MenuPrivilegeSearchResponse response = MenuPrivilegeSearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(size)
                .page(page)
                .total(menuPrivilegeSearchDto.getTotal())
                .totalPage(menuPrivilegeSearchDto.getTotalPage())
                .data(menuPrivilegeSearchDto.getMenuPrivilegeList())
                .build();

        if (menuPrivilegeSearchDto.getMenuPrivilegeList() != null && !menuPrivilegeSearchDto.getMenuPrivilegeList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/privilege/menu/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createMenuPrivilege(@RequestBody @Valid MenuPrivilegeRequest request) {
        Integer result = menuPrivilegeService.createMenuPrivilege(request);
        if (result != 0 && result != -1) {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.CREATED);
        } else if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(
                    ApiMessage.E7099,
                    String.format(ApiMessage.E7099.description(), "This Menu-privilege [PathUrl] " + request.getPathUrl())
            ), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/privilege/menu/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateMenuPrivilege(@RequestBody @Valid MenuPrivilegeRequest request) {
        Integer result = menuPrivilegeService.updateMenuPrivilege(request);
        if (result != 0 && result != -1) {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.OK);
        } else if (result == 0) {
            return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This Menu-privilege ID: " + request.getRecId())), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @DeleteMapping(value = "/privilege/menu/{menuPrivilegeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteMenuPrivilege(@PathVariable("menuPrivilegeId") Integer menuPrivilegeId) {
        Integer result = menuPrivilegeService.deleteMenuPrivilegeById(menuPrivilegeId);
        if (result != 0 && result != -1) {
            return new ResponseEntity<>(new ApiResponse<>(result), HttpStatus.OK);
        } else if (result == -1) {
            return new ResponseEntity<>(new ApiResponseStatus(
                    ApiMessage.E7100,
                    String.format(ApiMessage.E7100.description(), "This Menu-privilege ID: " + menuPrivilegeId + " cannot be delete because the resource is referenced by other entities.")), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This Menu-privilege ID: " + menuPrivilegeId)), HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('SMU')")
    @PutMapping(value = "privilege/menu/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateMenuPrivilegeSequence(@RequestBody @Valid SequenceRequest request) {
        MenuPrivilegeDto menuPrivilegeDto = menuPrivilegeService.updateMenuPrivilegeSequence(request);
        if (menuPrivilegeDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(menuPrivilegeDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

