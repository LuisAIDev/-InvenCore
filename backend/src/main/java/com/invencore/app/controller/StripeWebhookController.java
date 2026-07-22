package com.invencore.app.controller;

import com.invencore.app.service.PedidoService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Webhooks de Stripe (sin autenticación, firma verificada)")
public class StripeWebhookController {

    private final PedidoService pedidoService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/api/publico/stripe/webhook")
    @Operation(summary = "Webhook de Stripe",
               description = "Recibe eventos de Stripe (payment_intent.succeeded). "
                           + "La autenticación se realiza mediante la firma del webhook.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evento procesado correctamente"),
        @ApiResponse(responseCode = "400", description = "Firma inválida o evento malformado")
    })
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(400).body("Firma inválida");
        }

        if (!"payment_intent.succeeded".equals(event.getType())) {
            return ResponseEntity.ok("Evento ignorado: " + event.getType());
        }

        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);
        if (paymentIntent == null) {
            return ResponseEntity.badRequest().body("No se pudo deserializar el PaymentIntent");
        }

        pedidoService.confirmarPagoWebhook(paymentIntent.getId());
        return ResponseEntity.ok("OK");
    }
}
