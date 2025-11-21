package com.pantavanij.sourcingreq.services.enums;

public enum ApiMessage {
    I1001("Success"),
    I1002("Creation successful"),
    I1003("Data successfully updated"),
    I1004("Data successfully deleted"),
    I1005("Create user from sso"),
    I1006("Login from SSO success"),
    I1007("Logout success"),
    I1008("Data successfully rejected"),
    E1001("Unexpected internal server error."),
    E1002("Invalid parameters"),
    E1003("Invalid data"),
    E1004("Unauthorized%s"),
    E1005("Access Denied%s"),
    E1006("Error while contacting IDP"),
    E1007("Error while getting user detail"),
    E1008("Error while connecting to Supplier service"),
    E1009("User cannot be found"),
    E1010("Company cannot be found"),
    E1011("Error creating user while contacting IDP"),
    E1012("Error logging out with tenantId : %s and user : %s"),
    E1013("Unauthorized invalid token"),
    E1014("Unexpected external server error."),

    //LIST OF SOURCING REQUEST ERROR MESSAGE IS START AT E7001
    E7001("The upload failed due to oversize"),
    E7002("Invalid format."),
    E7006("Location is not found"),
    E7007("Failed to get location"),
    E7008("Unit is not found"),
    E7009("Failed to get unit"),
    E7010("Data is not found"),
    E7011("Uaa service error: %s"),
    E7012("Can't delete specific Attachment due to the RecId is not available"),
    E7013("Can't delete specific Request Attachment due to the either RequestId or AttachmentId is not available"),
    E7014("Can't delete specific RequestItem Attachment due to the either RequestItemId or AttachmentId  is not available"),
    E7015("Can't delete specific Existing Price Attachment due to the either ExistingPriceItemId or AttachmentId  is not available"),
    E7016("Tenant is not found"),
    E7017("Failed to get tenant"),
    E7018("Can't insert specific Request Attachment due to the either RequestId or AttachmentId is not available"),
    E7019("Can't insert specific RequestItem Attachment due to the either RequestItemId or AttachmentId  is not available"),
    E7020("Can't insert specific Existing Price Attachment due to the either ExistingPriceItemId or AttachmentId  is not available"),
    E7021("Budget Type is not found"),
    E7022("Failed to get Budget Type"),
    E7023("Project is not found"),
    E7024("Failed to get Project"),
    E7025("Department is not found"),
    E7026("Failed to get Department"),
    E7027("TenantConfig is not found"),
    E7028("Failed to get TenantConfig"),
    E7029("Workflow API error: %s"),
    E7030("Cannot generate workflow instance"),
    E7031("%s must not be null or empty"),
    E7032("%s exceed limit %s chars"),
    E7033("Can't delete specific Request due to the permission has conflicted"),
    E7034("Keywords must be at least 3 characters to be able search"),
    E7035("Supplier is not found"),
    E7036("Failed to get supplier"),
    E7037("Can't assign specific Request due to the request has either been assigned to another user or the permission has conflicted"),
    E7038("Can't remove specific Request due to the request must either be completed by creating the sourcing request for all items or the permission has conflicted"),
    E7039("Can't delete specific Request Item due to the RequestItemId is either not available or the permission has conflicted"),
    E7040("eRFX API error: %s"),
    E7041("RequestItem is not found"),
    E7042("SourcingStatus is not found"),
    E7043("Failed to save Attachment"),
    E7044("Failed to save ExistingPriceItemAttachment"),
    E7045("Failed to save RequestItemAttachment"),
    E7046("Can't populate data due to the user permission has conflicted"),
    E7047("Maximum sizing is 25 MB"),
    E7048("File Format can be .xls, .xlsx only"),
    E7049("Maximum 200 rows (200 items per request)"),
    E7050("Invalid Format"),
    E7051("Add Items is limited to the maximum of 200 items"),
    E7052("Required Field"),
    E7053("Upload file failed"),
    E7054("Upload file failed because file is not data"),
    E7055("Copy to PR failed: %s"),
    E7056("Last row of excel"),
    E7057("Unit price/Quantity must be greater than 0"),
    E7058("Header column name incorrect"),
    E7059("Branch TPShortName %s is not found"),
    E7060("SupplierWebWork is not found"),
    E7061("Conditions must not be null or empty"),
    E7062("Sourcing Menu is not found"),
    E7063("Can't reject specific Request Item due to the RequestItemId is either not available or the permission has conflicted"),
    E7064("User is not allowed"),
    E7065("Objective is not found"),
    E7066("Category is not found"),
    E7067("Sub category is not found"),
    E7068("Currency is not found"),
    E7069("Purpose of Request is not found"),
    E7070("Invalid PreSpan/PostSpan value due the number is out of range (0-24)"),
    E7071("Invalid Span value due the number is out of range (1-24)"),
    E7072("Add Items is limited to the maximum of 500 items"),
    E7073("Please ensure all items' subcategories are processed by the same purchaser."),
    E7074("Report Line is not found"),
    E7075("Maximum character limit exceeded. Please shorten your input."),
    E7076("EForm API error: %s"),
    E7077("Bid Validity End Date should greater than Bid Validity Start Date"),
    E7078("Attachment is not found"),
    E7079("Supplier data unavailable. Please contact our system support team for assistance."),
    E7080("The subcategory is not available. Please select another subcategory."),
    E7081("The unit is not available. Please select another unit."),
    E7082("Permission is not allowed."),
    E7083("Cannot create eRFX as it exceeds the limit of attachments per request/request-item."),
    E7084("Report Line user is invalid."),
    E7085("Something went wrong!"),
    E7086("Purchaser is not found"),
    E7087("The request cannot be canceled as the status has already changed."),
    E7088("This sourcing request was processed by another user."),
    E7089("The Delivery Location is not available. Please select another Delivery Location."),
    E7090("The Contact Name is not available. Please select another Contact Name."),
    E7091("The Phone is not available. Please select another Phone."),
    E7092("The Location is not available. Please select another Location."),
    E7093("Invalid date format."),
    E7094("Contact phone numbers contain only numbers (0-9) and the symbols (-), (#), and (,)."),
    E7095("IP address is not authorized to access this resource."),
    E7096("%s is not found."),
    E7097("%s is required!"),
    E7098("Nothing %s to %s"),
    E7099("%s is already exist."),
    E7100("%s")

    ;

    private final String description;

    ApiMessage(String description) {
        this.description = description;
    }


    public String description() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.name().concat(": ").concat(this.description);
    }

    public static String description(String name) {
        for (ApiMessage statusCodeEnum : ApiMessage.values()) {
            if (statusCodeEnum.name().equalsIgnoreCase(name)) {
                return statusCodeEnum.description;
            }
        }
        return "";
    }
}
