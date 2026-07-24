package com.invencore.app.service.impl;

import com.invencore.app.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger log = LoggerFactory.getLogger(StripeServiceImpl.class);

    @Value("${stripe.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public PaymentIntent createPaymentIntent(Long montoCentavos, String moneda, String descripcion) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(montoCentavos)
                .setCurrency(moneda)
                .setDescription(descripcion)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        try {
            return PaymentIntent.create(params);
        } catch (StripeException e) {
            log.error("Stripe createPaymentIntent failed: amount={} {} description='{}' stripeCode={} stripeMessage={} stripeError={}",
                    montoCentavos, moneda, descripcion,
                    e.getCode(), e.getMessage(),
                    e.getStripeError() != null ? e.getStripeError().toJson() : "N/A");
            throw e;
        }
    }

    @Override
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            log.error("Stripe retrievePaymentIntent failed: paymentIntentId={} stripeCode={} stripeMessage={} stripeError={}",
                    paymentIntentId,
                    e.getCode(), e.getMessage(),
                    e.getStripeError() != null ? e.getStripeError().toJson() : "N/A");
            throw e;
        }
    }
}
