package com.renting.paymentservice.entity;

import com.renting.paymentservice.common.enums.PaymentType;
import com.renting.paymentservice.common.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private int bookingId;
    private double amount;
    private String currency;
    private PaymentType paymentType;
    private Status status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    // Information for paypal
    private String paypalOrderId;
}
