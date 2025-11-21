package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.request.*;
import com.pantavanij.sourcingreq.services.domain.response.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import com.pantavanij.sourcingreq.services.util.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.*;

import static com.pantavanij.sourcingreq.services.enums.SearchRequestType.SEQUENCE;

@RestController
@RequestMapping("${api.endpoint.version}")
@RequiredArgsConstructor
public class EmailActivityController {

    private final EmailActivityService emailActivityService;

    @GetMapping(value = "/email-activity-all")
    public ResponseEntity getAllEmailActivity() {
        List<EmailActivityDto> emailActivityDtoList = emailActivityService.getAllEmailActivity();
        if ( emailActivityDtoList != null && !emailActivityDtoList.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(emailActivityDtoList), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('SMR')")
    @PostMapping(value = "/email-activity")
    public ResponseEntity createEmailActivity(@RequestBody @Valid EmailActivityRequest request) {
        EmailActivityDto emailActivityDto = emailActivityService.createEmailActivity(request);
        if (emailActivityDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(emailActivityDto), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7085, ApiMessage.E7085.description()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('SMR')")
    @PostMapping(value = "/email-activity-search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity emailActivitySearch(@RequestBody @Valid EmailActivitySearchRequest request) {
        Sort sort;
        int page = request.getPage();
        int size = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        if (("".equals(sortBy) || sortBy == null) && ("".equals(sortOrder) || sortOrder == null)) {
            sort = Sort.by(Constant.ACTIVITY_NAME).descending();
        } else {
            List<Sort.Order> orders = new ArrayList<>();
            assert sortBy != null;
            orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            if (!sortBy.equalsIgnoreCase("sequence")) {
                orders.add(new Sort.Order(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, SEQUENCE.description()));
            }
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        EmailActivitySearchDto emailActivitySearchDto = emailActivityService.searchEmailActivityByConditions(request, pageable);
        EmailActivitySearchResponse response = EmailActivitySearchResponse.builder()
                .status(new ApiResponseStatus())
                .pageSize(emailActivitySearchDto.getPageSize())
                .page(page)
                .total(emailActivitySearchDto.getTotal())
                .totalPage(emailActivitySearchDto.getTotalPage())
                .data(emailActivitySearchDto.getEmailActivityDtoList())
                .build();

        if (emailActivitySearchDto.getEmailActivityDtoList() != null && !emailActivitySearchDto.getEmailActivityDtoList().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiErrorResponse(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('SMR')")
    @PutMapping(value = "/email-activity")
    public ResponseEntity updateEmailActivity(@RequestBody @Valid EmailActivityRequest request) {
        EmailActivityDto emailActivityDto = emailActivityService.updateEmailActivity(request);
        if (emailActivityDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(emailActivityDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('SMR')")
    @DeleteMapping(value = "/email-activity/{id}")
    public ResponseEntity deleteEmailActivity(@PathVariable Long id) {
        EmailActivityDto emailActivityDto = emailActivityService.deleteById(id);
        if (emailActivityDto != null) {
            return new ResponseEntity<>(new ApiResponse<>(emailActivityDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponseStatus(ApiMessage.E7010, ApiMessage.E7010.description()), HttpStatus.NOT_FOUND);
    }


}
