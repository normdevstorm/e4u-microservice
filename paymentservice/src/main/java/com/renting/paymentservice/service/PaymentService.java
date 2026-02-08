package com.renting.paymentservice.service;

import com.paypal.sdk.models.Order;
import com.paypal.sdk.models.OrderStatus;
import com.paypal.sdk.models.PurchaseUnit;
import com.renting.paymentservice.common.enums.PaymentType;
import com.renting.paymentservice.common.enums.Status;
import com.renting.paymentservice.dto.response.DepositResponseDto;
import com.renting.paymentservice.entity.Payment;
import com.renting.paymentservice.mapper.DepositMapper;
import com.renting.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    private PaymentRepository paymentRepository;
    private DepositMapper depositMapper;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, DepositMapper depositMapper) {
        this.paymentRepository = paymentRepository;
        this.depositMapper = depositMapper;
    }

    public DepositResponseDto createPayment(int bookingId, Order responseOrder, PaymentType paymentType) {
        PurchaseUnit purchaseUnit = responseOrder.getPurchaseUnits().get(0);
        Payment payment = Payment.builder().paymentType(paymentType)
                .amount(Double.parseDouble( purchaseUnit.getAmount().getValue()))
                .currency(purchaseUnit.getAmount().getCurrencyCode())
                .createdAt(LocalDateTime.parse(responseOrder.getCreateTime()))
                .notes(purchaseUnit.getDescription())
                .status(mapStatusFromOrderStatus(responseOrder.getStatus()))
                .paidAt(LocalDateTime.parse( responseOrder.getUpdateTime()))
                .bookingId(bookingId)
                .paypalOrderId(responseOrder.getId())
                .build();
        Payment savedPayment = paymentRepository.save(payment);
       return depositMapper.toResponseDto(responseOrder, savedPayment);
    }

    private Status mapStatusFromOrderStatus(OrderStatus orderStatus){
        return switch (orderStatus) {
            case PAYER_ACTION_REQUIRED, CREATED, SAVED, APPROVED -> Status.PENDING;
            case COMPLETED -> Status.COMPLETED;
            default -> Status.FAILED;
        };
    }

    public DepositResponseDto updatePaymentStatus(Order order, OrderStatus status){
        try {
            Payment payment = paymentRepository.findPaymentByPaypalOrderId(order.getId());
            payment.setStatus(mapStatusFromOrderStatus(status));
            return depositMapper.toResponseDto(order, paymentRepository.save(payment));
        } catch (Exception e) {
            throw new RuntimeException("Payment with order ID " + order.getId() + " not found.");
        }
    }
}
