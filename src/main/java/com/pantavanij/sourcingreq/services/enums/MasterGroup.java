package com.pantavanij.sourcingreq.services.enums;

public enum MasterGroup {
    MASTER_GROUP_CATEGORY("category", "CATEGORY"),
    MASTER_GROUP_SUB_CATEGORY("sub-category", "SUB-CATEGORY"),
    MASTER_GROUP_PROJECT_CURRENT_DATA("project/current-data", "PROJECT/CURRENT-DATA"),
    MASTER_GROUP_PROJECT_UPLOAD_STATUS("project/upload-status", "PROJECT/UPLOAD-STATUS"),
    MASTER_GROUP_DEPARTMENT("department", "DEPARTMENT"),
    MASTER_GROUP_REQUESTER("requester", "REQUESTER"),
    MASTER_GROUP_PURCHASER("purchaser", "PURCHASER"),
    MASTER_GROUP_APPROVER("approver", "APPROVER"),
    MASTER_GROUP_REVIEWER("reviewer", "REVIEWER"),
    MASTER_GROUP_REPORTLINE("report-line", "REPORT-LINE"),
    MASTER_GROUP_TENANT_CONFIG("tenant-config", "TENANT-CONFIG"),
    MASTER_GROUP_TENANT_SECTION("tenant-section", "TENANT-SECTION"),
    MASTER_GROUP_TENANT_SECTION_DETAIL("tenant-section-detail", "TENANT-SECTION-DETAIL"),
    MASTER_GROUP_REQUEST_GRID_FIELD("request-grid-field", "REQUEST-GRID-FIELD"),
    MASTER_GROUP_REQUEST_ITEM_GRID_FIELD("request-item-grid-field", "REQUEST-ITEM-GRID-FIELD"),
    MASTER_GROUP_CURRENCY("currency", "CURRENCY"),
    MASTER_GROUP_UNIT("unit", "UNIT"),
    MASTER_GROUP_TENANT_CURRENCY("tenant-currency", "TENANT-CURRENCY"),
    MASTER_GROUP_TENANT_UNIT("tenant-unit", "TENANT-UNIT"),
    MASTER_GROUP_TYPE("type", "TYPE"),
    MASTER_GROUP_REQUEST_REPORT("request-report", "REQUEST-REPORT"),
    MASTER_GROUP_REQUEST_ITEM_REPORT("request-item-report", "REQUEST-ITEM-REPORT"),
    MASTER_GROUP_MENU_PRIVILEGE("menu-privilege", "MENU-PRIVILEGE"),
    MASTER_GROUP_TENANT("tenant", "TENANT"),
    MASTER_GROUP_TENANT_SOURCING_STATUS("tenant-sourcing-status", "TENANT-SOURCING-STATUS");

    private final String groupCode;
    private final String groupName;

    MasterGroup(String groupName, String groupCode) {
        this.groupName = groupName;
        this.groupCode = groupCode;
    }

    public String groupName() {
        return this.groupName;
    }

    public String groupCode() {
        return this.groupCode;
    }

    public static MasterGroup findByStrGroup(String groupName) {
        MasterGroup result = null;
        for (MasterGroup masterGroup : values()) {
            if (masterGroup.groupName.equalsIgnoreCase(groupName) || masterGroup.groupCode.equalsIgnoreCase(groupName)) {
                result = masterGroup;
                break;
            }
        }
        return result;
    }

}
