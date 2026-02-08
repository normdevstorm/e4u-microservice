package com.renting.paymentservice.dto.response;

import com.paypal.sdk.models.Order;
import com.renting.paymentservice.common.enums.Status;
import lombok.Data;

@Data
public class DepositResponseDto {
    private int id;
    private int bookingId;
    private String paypalOrderId;
    private Status status;
}
