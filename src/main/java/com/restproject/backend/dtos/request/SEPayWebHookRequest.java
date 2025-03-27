package com.restproject.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SEPayWebHookRequest {
    @NotNull
    Long id;

    @NotNull
    @NotBlank
    String gateway; //--User's Bank Name

    @NotNull
    @NotBlank
    String transactionDate;

    @NotNull
    @NotBlank
    String accountNumber;   //--User's Account Number

    @NotNull
    @NotBlank
    String code;

    @NotNull
    @NotBlank
    String content;

    @NotNull
    @NotBlank
    String transferType;

    @NotNull
    Long transferAmount;    //--Input Money

    @NotNull
    Long accumulated;

    @NotNull
    @NotBlank
    String subAccount;  //--Sub-account of User (or null)

    @NotNull
    @NotBlank
    String referenceCode;

    @NotNull
    @NotBlank
    String description;
}
