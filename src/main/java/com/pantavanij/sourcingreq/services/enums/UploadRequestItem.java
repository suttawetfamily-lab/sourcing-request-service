package com.pantavanij.sourcingreq.services.enums;

public enum UploadRequestItem {
    COLUMN_INDEX_PURPOSE_OF_REQUEST(0),
    COLUMN_INDEX_ITEM_NAME(1),
    COLUMN_INDEX_ITEM_DESCRIPTION(2),
    COLUMN_INDEX_QUANTITY_CONDITION(3),
    COLUMN_INDEX_UNIT(4),
    COLUMN_INDEX_DELIVERY_LOCATION(5),
    COLUMN_INDEX_LOCATION(6),
    COLUMN_INDEX_CONTACT_NAME(7),
    COLUMN_INDEX_PHONE(8);

    private final Integer id;

    UploadRequestItem(Integer id) {
        this.id = id;
    }

    public Integer id() {
        return this.id;
    }
}
