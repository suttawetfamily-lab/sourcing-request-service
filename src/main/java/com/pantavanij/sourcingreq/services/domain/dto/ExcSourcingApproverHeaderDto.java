package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcSourcingApproverHeaderDto {

    private String headerName;
    private List<ExcSourcingApproverSectionDto> approverSections;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExcSourcingApproverSectionDto {
        private String sectionName;
        private String sectionLabel;
        private Integer numberOfApproverRequired;
        private String status;
        private List<ExcSourcingApproverDto> approvers;
        private Boolean canAddNewItem;
        private String endpointURL;
        private Boolean canEdit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExcSourcingApproverDto {
        private Long requestDeptApproverId;
        private Integer sysUserId;
        private String loginId;
        private String fullName;
        private String email;
        private String mobilePhone;
        private String phone;
        private String timezone;
        private String status;
        private String comment;
        private Timestamp commentDate;
        private String department;
        private String name;
        private Boolean isDefault;
        private String delegatedBy;
        private Timestamp delegatedDate;
        private Timestamp forwardedDate;
        private String addedBy;
        private Boolean required;
        private Integer key;
    }
}
