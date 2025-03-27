package com.restproject.backend.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum DefaultSEPayParams {
    //--Select query
    DATE_MIN("$[date_min]$"),
    DATE_MAX("$[date_max]$"),
    LIMIT("$[limit]$"),
    TRANS_REF_CODE("$[ref_code]$"),
    USER_ACCOUNT("$[user_account]$"),
    //--Create QR Code
    ACCOUNT_TARGET("$[account_target]$"),
    BANK_TARGET("$[bank_target]$"),
    AMOUNT("$[amount]$"),
    DESCRIPTION("$[description]$"),
    ;
    String value;
    DefaultSEPayParams(String value) {
        this.value = value;
    }
}
