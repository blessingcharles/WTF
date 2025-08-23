package com.th3h04x.constant;

public enum ColumnName {

    //used in the top panel
    SERIAL_NO("S.NO"),
    REQUEST("Request"),
    SCANNER_NAME("scanner name"),
    ISSUE("issue"),

    // bottom panel
    EXPANDED_REQUEST("Request"),
    EXPANDED_MODIFIED_REQUEST("Modified Request"),
    EXPANDED_RESPONSE("Response");

    private final String name ;

    ColumnName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
