package com.pantavanij.sourcingreq.services.domain.mapper;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcing;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcingApprover;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.ExcSourcingPurchaser;
import com.pantavanij.sourcingreq.services.util.DateTimeUtil;
import com.pantavanij.sourcingreq.services.util.UserDetailServiceUtil;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.Collectors;

import static com.pantavanij.sourcingreq.services.enums.SourcingStatus.SOURCING_REJECTED;

@Mapper(uses = {
        TenantExcSourcingStatusMapper.class,
        ApprovalStatusMapper.class,
        DeptApprovalStatusMapper.class,
        RequestMapper.class
})
public interface ExcSourcingMapper {

    ExcSourcingMapper INSTANCE = Mappers.getMapper(ExcSourcingMapper.class);

    @Mapping(source = "tenantExcSourcingStatus", target = "excSourcingStatus")
    @Mapping(source = "approvalStatus", target = "approvalStatus")
    @Mapping(source = "deptApprovalStatus", target = "deptApprovalStatus")
    @Mapping(source = "request", target = "request")
    ExcSourcingDto toExcSourcingDto(ExcSourcing excSourcing);

    // ====== Convert with timezone ======
    default ExcSourcingDto toExcSourcingDto(ExcSourcing excSourcing, String timeZone) {
        ExcSourcingDto dto = toExcSourcingDto(excSourcing);
        if (dto != null) {
            dto.setCreatedDate(DateTimeUtil.convertTimestampByUserTimeZone(dto.getCreatedDate(), timeZone));
            dto.setUpdatedDate(DateTimeUtil.convertTimestampByUserTimeZone(dto.getUpdatedDate(), timeZone));
            if (excSourcing.getRequest() != null) {
                dto.setRequest(RequestMapper.INSTANCE.toRequestDto(excSourcing.getRequest(), timeZone));
                // =========================================
                // 🔹 Filter out request items with sourcingStatus.code = "REJECT"
                // =========================================
                RequestDto requestDto = dto.getRequest();
                if (requestDto != null && requestDto.getRequestItemList() != null) {
                    List<RequestItemV2Dto> filteredItems = requestDto.getRequestItemList().stream()
                            .filter(r -> r.getSourcingStatus() == null
                                    || r.getSourcingStatus().getRecId() == null
                                    || !r.getSourcingStatus().getRecId().equals(SOURCING_REJECTED.id()))
                            .collect(Collectors.toList());
                    requestDto.setRequestItemList(filteredItems);
                }
            }
        }
        return dto;
    }

    /**
     * Map ExcSourcing -> ExcSourcingDto พร้อมรวม header ทั้งสามกลุ่ม
     * - purchaserHeader (จากระบบภายใน)
     * - purchaserApproverList (จาก EPAuth)
     * - deptApproverList (จาก EPAuth)
     */
    default ExcSourcingDto toExcSourcingDto(
            ExcSourcing excSourcing,
            List<EPAuthDeptApproverDto> deptApproverList,
            InstanceApproverHeaderDto purchaserHeader,
            List<EPAuthDeptApproverDto> purchaserApproverList,
            List<ExcSourcingApprover> excSourcingApprovers
    ) {
        ExcSourcingDto dto = toExcSourcingDto(excSourcing);
        if (dto == null) return null;

        // ✅ Filter REJECT items เหมือนของเดิม
        if (excSourcing.getRequest() != null) {
            dto.setRequest(RequestMapper.INSTANCE.toRequestDto(excSourcing.getRequest()));
            RequestDto requestDto = dto.getRequest();
            if (requestDto != null && requestDto.getRequestItemList() != null) {
                List<RequestItemV2Dto> filteredItems = requestDto.getRequestItemList().stream()
                        .filter(r -> r.getSourcingStatus() == null
                                || r.getSourcingStatus().getRecId() == null
                                || !r.getSourcingStatus().getRecId().equals(SOURCING_REJECTED.id()))
                        .collect(Collectors.toList());
                requestDto.setRequestItemList(filteredItems);
            }
        }

        // 1) Dept Approver
        InstanceApproverHeaderDto deptHeader = buildApproverHeaderFromDeptApproverList(
                deptApproverList, excSourcingApprovers, excSourcing
        );

        // 2) Purchaser (EPAuth)
        InstanceApproverHeaderDto purchaserHeaderFromEPAuth = buildPurchasingApproverHeaderFromEPAuth(
                purchaserApproverList,
                safePurchaserList(excSourcing.getExcSourcingPurchasers()),
                excSourcing
        );

        // 3) รวมทั้งหมด purchaserHeader → EPAuth → Dept
        List<InstanceApproverHeaderDto> approverHeaders = new ArrayList<>();
        if (purchaserHeader != null)            approverHeaders.add(purchaserHeader);
        if (deptHeader != null)                 approverHeaders.add(deptHeader);
        if (purchaserHeaderFromEPAuth != null)  approverHeaders.add(purchaserHeaderFromEPAuth);

        dto.setApproverHeaders(approverHeaders);
        return dto;
    }

    // ====== Helper methods ======

    private static List<ExcSourcingPurchaser> safePurchaserList(List<ExcSourcingPurchaser> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * Dept Approver:
     * - ใช้รายชื่อจาก EPAuth เพื่อได้ข้อมูล user (ชื่อ/อีเมล/มือถือ)
     * - เติม status จาก ExcSourcingApprover.deptApprovalStatus
     * - เติม comment จาก ExcSourcingApprover.comment (fallback EPAuth.comment)
     */
    private static InstanceApproverHeaderDto buildApproverHeaderFromDeptApproverList(
            List<EPAuthDeptApproverDto> deptApproverList,
            List<ExcSourcingApprover> excSourcingApprovers,
            ExcSourcing excSourcing
    ) {
        Map<Integer, ExcSourcingApprover> userIdToApprover = mapApproverByUserId(excSourcingApprovers);

        List<InstanceApproverDto> approverList = new ArrayList<>();
        if (deptApproverList != null) {
            for (EPAuthDeptApproverDto src : dedupeBySysUserId(deptApproverList)) {
                InstanceApproverDto approver = new InstanceApproverDto();
                approver.setSysUserId(src.getSysUserId());
                approver.setLoginId(src.getLoginId());
                approver.setFullName(src.getFullName());
                approver.setEmail(src.getEmail());
                approver.setMobilePhone(src.getMobilePhone());
                approver.setPhone(src.getPhone());
                approver.setRequired(true);
                approver.setAddedBy(UserDetailServiceUtil.getFullName(excSourcing.getRequest().getAssignedBy()));

                ExcSourcingApprover ent = userIdToApprover.get(src.getSysUserId());

                // ✅ ใช้ comment จาก entity ของเรา
                String commentFromEntity = ent != null ? safeTrim(ent.getComment()) : null;
                String finalComment = StringUtils.isNotBlank(commentFromEntity)
                        ? commentFromEntity
                        : safeTrim(src.getComment());
                approver.setComment(finalComment);

                // ✅ สถานะจาก entity
                String status = (ent != null && ent.getDeptApprovalStatus() != null)
                        ? StringUtils.defaultString(ent.getDeptApprovalStatus().getName(), "NONE").toUpperCase()
                        : "NONE";
                approver.setStatus(status);

                approverList.add(approver);
            }
        }

        InstanceApproverSectionDto section = new InstanceApproverSectionDto();
        section.setSectionName("Approver");
        section.setSectionLabel("Approver");
        section.setNumberOfApproverRequired(null);
        section.setStatus("");
        section.setApprovers(approverList);
        section.setCanAddNewItem(false);
        section.setEndpointURL(null);

        InstanceApproverHeaderDto header = new InstanceApproverHeaderDto();
        header.setHeaderName("APPROVER GROUP");
        header.setApproverSections(List.of(section));

        return header;
    }

    /**
     * Purchasing Approver:
     * - สร้างรายชื่อจาก ExcSourcingPurchaser โดยตรง
     * - เติม comment จาก ExcSourcingPurchaser.comment
     * - เติม status จาก ExcSourcingPurchaser.approvalStatus
     */
    private static InstanceApproverHeaderDto buildPurchasingApproverHeaderFromEntities(
            List<ExcSourcingPurchaser> purchasers
    ) {
        List<InstanceApproverDto> approverList = new ArrayList<>();
        for (ExcSourcingPurchaser p : purchasers) {
            InstanceApproverDto approver = new InstanceApproverDto();

            if (p.getApprover() != null) {
                approver.setSysUserId(p.getApprover().getUserId());
                approver.setLoginId(p.getApprover().getLoginId());
                approver.setFullName(p.getApprover().getApproverName());
                approver.setEmail(p.getApprover().getEmail());
                approver.setMobilePhone(p.getApprover().getPhone());
                approver.setPhone(p.getApprover().getPhone());
            }

            approver.setRequired(true);

            // ✅ comment จาก entity
            String comment = safeTrim(p.getComment());
            approver.setComment(comment);

            // ✅ status จาก entity
            String status = (p.getApprovalStatus() != null)
                    ? StringUtils.defaultString(p.getApprovalStatus().getName(), "NONE").toUpperCase()
                    : "NONE";
            approver.setStatus(status);

            approverList.add(approver);
        }

        InstanceApproverSectionDto section = new InstanceApproverSectionDto();
        section.setSectionName("PurchasingApprover");
        section.setSectionLabel("Purchasing Approver");
        section.setNumberOfApproverRequired(null);
        section.setStatus("");
        section.setApprovers(approverList);
        section.setCanAddNewItem(false);
        section.setEndpointURL(null);

        InstanceApproverHeaderDto header = new InstanceApproverHeaderDto();
        header.setHeaderName("PURCHASING APPROVER GROUP");
        header.setApproverSections(List.of(section));

        return header;
    }

    private static InstanceApproverHeaderDto buildPurchasingApproverHeaderFromEPAuth(
            List<EPAuthDeptApproverDto> purchaserApproverList,
            List<ExcSourcingPurchaser> excSourcingPurchasers,
            ExcSourcing excSourcing
    ) {
        Map<Integer, ExcSourcingPurchaser> userIdToPurchaser = mapPurchaserByUserId(excSourcingPurchasers);

        List<InstanceApproverDto> approverList = new ArrayList<>();
        if (purchaserApproverList != null) {
            for (EPAuthDeptApproverDto src : dedupeBySysUserId(purchaserApproverList)) {
                InstanceApproverDto approver = new InstanceApproverDto();
                approver.setSysUserId(src.getSysUserId());
                approver.setLoginId(src.getLoginId());
                approver.setFullName(src.getFullName());
                approver.setEmail(src.getEmail());
                approver.setMobilePhone(src.getMobilePhone());
                approver.setPhone(src.getPhone());
                approver.setRequired(true);
                approver.setAddedBy(UserDetailServiceUtil.getFullName(excSourcing.getRequest().getAssignedBy()));

                ExcSourcingPurchaser ent = userIdToPurchaser.get(src.getSysUserId());
                String commentFromEntity = ent != null ? safeTrim(ent.getComment()) : null;
                String finalComment = StringUtils.isNotBlank(commentFromEntity)
                        ? commentFromEntity
                        : safeTrim(src.getComment());
                approver.setComment(finalComment);

                String status = (ent != null && ent.getApprovalStatus() != null)
                        ? StringUtils.defaultString(ent.getApprovalStatus().getName(), "NONE").toUpperCase()
                        : "NONE";
                approver.setStatus(status);

                approverList.add(approver);
            }
        }

        InstanceApproverSectionDto section = new InstanceApproverSectionDto();
        section.setSectionName("PurchasingApprover");
        section.setSectionLabel("Purchasing Approver");
        section.setNumberOfApproverRequired(null);
        section.setStatus("");
        section.setApprovers(approverList);
        section.setCanAddNewItem(false);
        section.setEndpointURL(null);

        InstanceApproverHeaderDto header = new InstanceApproverHeaderDto();
        header.setHeaderName("PURCHASING APPROVER GROUP");
        header.setApproverSections(List.of(section));

        return header;
    }

    // -------- Small Utils --------

    private static String safeTrim(String s) {
        return s == null ? null : s.trim();
    }

    private static Map<Integer, ExcSourcingApprover> mapApproverByUserId(List<ExcSourcingApprover> list) {
        if (list == null) return Collections.emptyMap();
        return list.stream()
                .filter(a -> a.getApprover() != null && a.getApprover().getUserId() != null)
                .collect(Collectors.toMap(
                        a -> a.getApprover().getUserId(),
                        a -> a,
                        (a, b) -> {
                            Date au = a.getUpdatedDate();
                            Date bu = b.getUpdatedDate();
                            if (au == null && bu == null) return b;
                            if (au == null) return b;
                            if (bu == null) return a;
                            return (bu.after(au)) ? b : a;
                        }
                ));
    }

    private static Map<Integer, ExcSourcingPurchaser> mapPurchaserByUserId(List<ExcSourcingPurchaser> list) {
        if (list == null) return Collections.emptyMap();
        return list.stream()
                .filter(p -> p.getApprover() != null && p.getApprover().getUserId() != null)
                .collect(Collectors.toMap(
                        p -> p.getApprover().getUserId(),
                        p -> p,
                        (a, b) -> {
                            Date au = a.getUpdatedDate();
                            Date bu = b.getUpdatedDate();
                            if (au == null && bu == null) return b;
                            if (au == null) return b;
                            if (bu == null) return a;
                            return (bu.after(au)) ? b : a;
                        }
                ));
    }

    private static List<EPAuthDeptApproverDto> dedupeBySysUserId(List<EPAuthDeptApproverDto> src) {
        Map<Integer, EPAuthDeptApproverDto> map = new LinkedHashMap<>();
        for (EPAuthDeptApproverDto d : src) {
            if (d == null) continue;
            map.putIfAbsent(d.getSysUserId(), d);
        }
        return new ArrayList<>(map.values());
    }

    default List<ExcSourcingDto> toDtoList(List<ExcSourcing> excSourcingList, String timeZone) {
        List<ExcSourcingDto> dtoList = new ArrayList<>();
        if (excSourcingList != null) {
            excSourcingList.forEach(item -> dtoList.add(toExcSourcingDto(item, timeZone)));
        }
        return dtoList;
    }

}
