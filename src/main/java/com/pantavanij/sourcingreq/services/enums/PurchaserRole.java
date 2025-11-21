package com.pantavanij.sourcingreq.services.enums;

public enum PurchaserRole {
    PURCHASER_ROLE_NONE(0, "none","None"),
    PURCHASER_ROLE_BUYER_PURCHASER(1, "buyer_purchaser","Buyer/Purchaser"),
    PURCHASER_ROLE_BUYER_PURCHASER_LEAD(2, "buyer_purchaser_lead","Buyer/Purchaser Lead"),
    PURCHASER_ROLE_HEAD_OF_PROCUREMENT(3, "head_of_procurement","Head of procurement");

    //Head of procurement

    private final Integer id;
    private final String code;
    private final String description;

    PurchaserRole(Integer id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public Integer id() {
        return this.id;
    }

    public String code() {
        return this.code;
    }

    public String description() {
        return this.description;
    }

    public boolean isBuyerPurchaser(Integer typeId, String code) {
        return (typeId == PurchaserRole.PURCHASER_ROLE_BUYER_PURCHASER.id && !code.equalsIgnoreCase(PurchaserRole.PURCHASER_ROLE_BUYER_PURCHASER.code));
    }

    public boolean isBuyerPurchaserLead(Integer typeId, String code) {
        return (typeId == PurchaserRole.PURCHASER_ROLE_BUYER_PURCHASER_LEAD.id && !code.equalsIgnoreCase(PurchaserRole.PURCHASER_ROLE_BUYER_PURCHASER_LEAD.code));
    }

    public boolean isHeadOfProcurement(Integer typeId, String code) {
        return (typeId == PurchaserRole.PURCHASER_ROLE_HEAD_OF_PROCUREMENT.id && !code.equalsIgnoreCase(PurchaserRole.PURCHASER_ROLE_HEAD_OF_PROCUREMENT.code));
    }
}
