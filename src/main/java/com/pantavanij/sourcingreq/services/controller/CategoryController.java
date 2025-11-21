package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.constraint.SearchTermConstraint;
import com.pantavanij.sourcingreq.services.domain.dto.OptionDto;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategoryDto;
import com.pantavanij.sourcingreq.services.domain.dto.category.CategorySearchDto;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.embededid.*;
import com.pantavanij.sourcingreq.services.domain.mapper.CategoryMapper;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponse;
import com.pantavanij.sourcingreq.services.domain.response.ApiResponseStatus;
import com.pantavanij.sourcingreq.services.domain.response.category.CategoryResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import com.pantavanij.sourcingreq.services.exception.BusinessException;
import com.pantavanij.sourcingreq.services.interceptor.ControllerExecuteTime;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.CategoryService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.TenantService;
import com.pantavanij.sourcingreq.services.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.REQUEST_NO;
import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;
    private final PurchaserRepository purchaserRepository;
    private final TenantService tenantService;

    @GetMapping(value = "/category", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OptionDto>>> getCategoryBySearchTerm(@RequestParam @SearchTermConstraint String searchTerm, @RequestHeader(value = "organization", required = false) Integer organizationId) {
        String tenantCode = AppUtil.getTenantId();
        Tenant tenant = tenantService.findByCode(tenantCode);

        if (tenant == null)  throw new BusinessException(ApiMessage.E7016, ApiMessage.E7016.description());

        List<OptionDto> categoryOption = categoryService.getCategoryByTenantIdAndSearchTerm(tenant.getRecId(), searchTerm, organizationId);
        return ResponseEntity.ok().body(new ApiResponse(categoryOption));
    }

    //    @PreAuthorize("hasAnyAuthority('SAM')")
    @ControllerExecuteTime
    @GetMapping(value = "/category/{categoryId}/view")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryView(@PathVariable Integer categoryId) {
        CategoryDto categoryDto = categoryService.findCategoryByRecId(categoryId);
        if (categoryDto != null) {
            return new ResponseEntity(new ApiResponse(categoryDto), HttpStatus.OK);
        }
        return new ResponseEntity(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @PreAuthorize("hasAnyAuthority('SAM')")
    @ControllerExecuteTime
    @PostMapping(value = "/category/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchCategory(@RequestBody @Valid CategorySearchRequest categorySearchRequest) {
        Sort sort;
        int page = categorySearchRequest.getPage();
        int size = categorySearchRequest.getPageSize();
        String sortBy = categorySearchRequest.getSortBy();
        String sortOrder = categorySearchRequest.getSortOrder();

        if ("".equals(sortOrder) || sortOrder == null && "".equals(sortBy) || sortBy == null) {
            sort = Sort.by("recId").descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortOrder != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            if (!sortBy.equalsIgnoreCase("sequence")) {
                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, SEQUENCE.description()));
            }
            sort = Sort.by(orders);
        }
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        CategorySearchDto categorySearchDto = categoryService.searchCategoryByCondition(categorySearchRequest, pageable);
        CategoryResponse response = CategoryResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(categorySearchDto.getPageSize())
                .page(page)
                .total(categorySearchDto.getTotal())
                .totalPage(categorySearchDto.getTotalPage())
                .data(CategoryMapper.INSTANCE.toCategorySearchDataDto(categorySearchDto.getCategoryDtoList())).build();
        if (categorySearchDto.getCategoryDtoList() != null && !categorySearchDto.getCategoryDtoList().isEmpty()) {
            return ResponseEntity.ok().body(response);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @ControllerExecuteTime
    @PostMapping(value = "category/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCategory(@RequestBody @Valid CategoryRequest request) {
        CategoryDto categoryDto = categoryService.saveCategory(request);
        if (categoryDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(categoryDto, new ApiResponseStatus(ApiMessage.I1002, ApiMessage.I1002.description())), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ControllerExecuteTime
    @PostMapping(value = "category/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCategory(@RequestBody @Valid CategoryRequest request) {
        List<Purchaser> purchasers = new ArrayList<>();
        if (!request.getPurchasersId().isEmpty()) {
            for (Integer purchaserId: request.getPurchasersId()) {
                Optional<Purchaser> purchaserOptional = purchaserRepository.findFirstByRecId(purchaserId);
                purchaserOptional.ifPresent(purchasers::add);
            }
        }

        CategoryDto categoryDto = categoryService.updateCategory(request, purchasers);
        if (categoryDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(categoryDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping(value = "/category/update/sequence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCategorySequence(@RequestBody @Valid SequenceRequest request) {
        CategoryDto categoryDto = categoryService.updateCategorySequence(request);
        if (categoryDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(categoryDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping(value = "category/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteCategory(@PathVariable Integer categoryId) {
        CategoryDto categoryDto = categoryService.findCategoryByRecId(categoryId);
        if (categoryDto != null) {
            int result = categoryService.deleteCategory(categoryId);
            if (result != 0 && result != -1) {
                return new ResponseEntity<>(new ApiResponseStatus(), HttpStatus.OK);
            } else if (result == -1) {
                return new ResponseEntity<>(new ApiResponseStatus(
                        ApiMessage.E7100,
                        String.format(ApiMessage.E7100.description(), "This category \"" + categoryDto.getName() + "\" cannot be delete because the resource is referenced by other entities.")), HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7096, String.format(ApiMessage.E7096.description(), "This category id: " + categoryId)), HttpStatus.NOT_FOUND);
    }

}
