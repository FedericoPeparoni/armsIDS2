package ca.ids.abms.plugins.kcaa.eaip.modules.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/eaip_payments")
public class PaymentController {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<Payment> findPaymentByReqId(
        @RequestParam(name = "reqId") String reqId
    ) {
        LOG.debug("REST request to get Payment by PaymentNumber: {}", reqId);
        Payment payment = paymentService.findByReqId(reqId);

        return ResponseEntity.ok().body(payment);
    }

}
