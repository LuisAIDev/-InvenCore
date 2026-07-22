package com.invencore.app.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {
    PaymentIntent createPaymentIntent(Long montoCentavos, String moneda, String descripcion) throws StripeException;
    PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException;
}
