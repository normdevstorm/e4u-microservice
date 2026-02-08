package com.renting.paymentservice.repository;

import com.renting.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findPaymentByPaypalOrderId(String paypalOrderId);
}
