package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.constraint.SearchTermConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.Tenant;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.service.sourcingreq.SubCategoryService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.validation.*;
import javax.validation.constraints.*;
import java.util.List;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
@Validated
public class SubCategoryController {

    private final SubCategoryService subCategoryService;
    private final TenantService tenantService;

    @GetMapping(value = "/sub-category", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getSubCategoryBySearchTerm(
            @RequestParam @SearchTermConstraint String searchTerm,
            @RequestParam @Nullable Integer categoryId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null)  throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());

        List<OptionDto> subCategoryOption = subCategoryService.getSubCategoryByTenantIdAndSearchTermAndCategoryId(
                tenant.getRecId(),
                searchTerm,
                categoryId);
        return ResponseEntity.ok().body(new ApiResponse(subCategoryOption));
    }

    @PreAuthorize("hasAuthority('SAM')")
    @GetMapping(value = "/sub-category/view/{subCategoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity viewSubCategory(@PathVariable Integer subCategoryId) {
       SubCategoryDto subCategoryDto = subCategoryService.getSubCategoryById(subCategoryId);
       if (subCategoryDto != null) {
           return new ResponseEntity<>(new ApiResponse<>(subCategoryDto), HttpStatus.OK);
       }
       return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/sub-category/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity submitSubCategory(@RequestBody SubCategoryRequest request) {
        SubCategoryDto subCategoryDto = subCategoryService.createSubCategory(request);
        if (subCategoryDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(subCategoryDto, new ApiResponseStatus(ApiMessage.I1002, ApiMessage.I1002.description())), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PostMapping(value = "/sub-category/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateSubCategory(@RequestBody @Valid SubCategoryRequest request) {
        SubCategoryDto subCategoryDto = subCategoryService.updateSubCategory(request);
        if (subCategoryDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(subCategoryDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @PutMapping(value = "/sub-category/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateSubCategorySequence(@RequestBody SequenceRequest request) {
        SubCategoryDto subCategoryDto = subCategoryService.updateSubCategorySequence(request);
        if (subCategoryDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(subCategoryDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SAM')")
    @DeleteMapping(value = "/sub-category/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteSubCategory(@PathVariable @NotNull @Positive Integer id, @RequestParam(required = false, defaultValue = "false") boolean isReOrderSequence) {
        SubCategoryDto subCategoryDto = subCategoryService.getSubCategoryById(id);
        if (subCategoryDto != null) {
            int result = subCategoryService.deleteSubCategoryByRecId(id, isReOrderSequence);
            if (result != 0 && result != -1) {
                return new ResponseEntity<>(new ApiResponseStatus(), HttpStatus.OK);
            } else if (result == -1) {
                return new ResponseEntity<>(new ApiResponseStatus(
                        ApiMessage.E7100,
                        String.format(ApiMessage.E7100.description(), "This subcategory \"" + subCategoryDto.getName() + "\" cannot be delete because the resource is referenced by other entities.")), HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This subcategory id: " + id)), HttpStatus.NOT_FOUND);
    }

}
