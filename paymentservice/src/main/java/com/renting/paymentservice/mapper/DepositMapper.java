package com.renting.paymentservice.mapper;

import com.paypal.sdk.models.Order;
import com.renting.paymentservice.dto.response.DepositResponseDto;
import com.renting.paymentservice.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepositMapper {
    @Mapping(target = "paypalOrderId", source = "order.id")
    @Mapping(target = "id", source = "payment.id")
    @Mapping(target = "status", expression = "java(payment.getStatus())")
    @Mapping(target = "bookingId", source = "payment.bookingId")
    DepositResponseDto toResponseDto(Order order, Payment payment);
}
