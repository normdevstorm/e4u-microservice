package com.renting.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import com.renting.paymentservice.common.enums.PaymentType;
import com.renting.paymentservice.dto.request.DepositRequestDto;
import com.renting.paymentservice.dto.response.DepositResponseDto;
import com.renting.paymentservice.model.response.GenericException;
import com.renting.paymentservice.model.response.GenericResponse;
import com.renting.paymentservice.service.PaymentService;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@RestController
public class PaymentController {
    @Value("${PAYPAL_CLIENT_ID}")
    private String PAYPAL_CLIENT_ID;

    @Value("${PAYPAL_CLIENT_SECRET}")
    private String PAYPAL_CLIENT_SECRET;

    @Autowired
    private PaymentService paymentService;

    @Bean
    public PaypalServerSdkClient paypalClient() {
        return new PaypalServerSdkClient.Builder()
                .loggingConfig(builder -> builder
                        .level(Level.DEBUG)
                        .requestConfig(logConfigBuilder -> logConfigBuilder.body(true))
                        .responseConfig(logConfigBuilder -> logConfigBuilder.headers(true)))
                .httpClientConfig(configBuilder -> configBuilder
                        .timeout(0))
                .environment(Environment.SANDBOX)
                .clientCredentialsAuth(new ClientCredentialsAuthModel.Builder(
                                PAYPAL_CLIENT_ID,
                                PAYPAL_CLIENT_SECRET
                        ).build()
                )
                .build();
    }
    @Controller
    @RequestMapping("/")
    public class CheckoutController {

        private final ObjectMapper objectMapper;
        private final PaypalServerSdkClient client;

        public CheckoutController(ObjectMapper objectMapper, PaypalServerSdkClient client) {
            this.objectMapper = objectMapper;
            this.client = client;
        }


        @PostMapping("/api/orders/${bookingId}")
        public ResponseEntity<?> createOrder(@PathVariable int bookingId, @RequestBody DepositRequestDto request) {
            try {
//                String cart = objectMapper.writeValueAsString(request.get("cart"));
//                Order response = createOrder(cart);
                Order responseOrder = createOrder(request);
                DepositResponseDto depositResponse = paymentService.createPayment(bookingId, responseOrder, PaymentType.DEPOSIT);
                return ResponseEntity.ok(GenericResponse.<DepositResponseDto>builder().data(depositResponse).success(true).message("Created deposit booking payment, waiting for capture").build());
            } catch (Exception e) {
                e.printStackTrace();
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericException.builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .message(e.getMessage())
                                .details(e.getMessage())
                        .build());
            }
        }

        private Order createOrder(DepositRequestDto request) throws IOException, ApiException {
            // TAKE RESPONSE BODY FROM FRONTEND
            CreateOrderInput createOrderInput = new CreateOrderInput.Builder(
                    null,
                    new OrderRequest.Builder(
                            CheckoutPaymentIntent.fromString("CAPTURE"),
                            Arrays.asList(
                                    new PurchaseUnitRequest.Builder(
                                            new AmountWithBreakdown.Builder(
                                                    request.getCurrency(),
                                                    request.getAmount().toString()
                                            )
//                                                    .breakdown(
//                                                            new AmountBreakdown.Builder()
//                                                                    .itemTotal(
//                                                                            new Money(
//                                                                                    "USD",
//                                                                                    "100"
//                                                                            )
//                                                                    ).build()
//                                                    )
                                                    .build()
                                    )
                                            .description(request.getDescription())
//                                            .items(
//                                                    // lookup item details in `cart` from database
//                                                    Arrays.asList(
//                                                            new Item.Builder(
//                                                                    "T-Shirt",
//                                                                    new Money.Builder("USD","100").build(),
//                                                                    "1"
//                                                            )
//                                                                    .description("Super Fresh Shirt")
//                                                                    .sku("sku01")
//                                                                    .category(ItemCategory.PHYSICAL_GOODS)
//                                                                    .build()
//                                                    )
//                                            )

                                            .build()
                            )
                    )


                            .build()
            ).build();
            OrdersController ordersController = client.getOrdersController();
            ApiResponse<Order> apiResponse = ordersController.createOrder(createOrderInput);
            return apiResponse.getResult();
        }

        @PostMapping("/api/orders/{orderID}/capture")
        public ResponseEntity<?> captureOrder(@PathVariable String orderID) {
            try {
                Order response = captureOrders(orderID);
                DepositResponseDto updatedPayment =  paymentService.updatePaymentStatus(response, response.getStatus());
                return ResponseEntity.ok(GenericResponse.<DepositResponseDto>builder().data(updatedPayment).success(true).message("Created deposit booking payment, waiting for capture").build());
            } catch (Exception e) {
                e.printStackTrace();
                return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericException.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message(e.getMessage())
                        .details(e.getMessage())
                        .build());            }
        }

        private Order captureOrders(String orderID) throws IOException, ApiException {
            CaptureOrderInput ordersCaptureInput = new CaptureOrderInput.Builder(
                    orderID,
                    null)
                    .build();
            OrdersController ordersController = client.getOrdersController();
            ApiResponse<Order> apiResponse = ordersController.captureOrder(ordersCaptureInput);
            return apiResponse.getResult();
        }

        // ADD TWO MORE ENDPOINTS FOR REFUND AND PAYOUT IN SCHEDULED JOBS
    }
}
