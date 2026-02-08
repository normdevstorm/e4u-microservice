package com.renting.paymentservice.dto.request;

import lombok.Data;

@Data
public class DepositRequestDto {
    private Double amount;
    private String currency;
    private String description;
}
