package sk.jancar.bsc.input;

import sk.jancar.bsc.model.Currency;
import sk.jancar.bsc.model.PaymentTracker;

import java.io.InputStream;
import java.math.BigDecimal;

/**
 * A reader class for payments
 */
public class PaymentDataReader extends AbstractDataReader {

    private final PaymentTracker paymentTracker;

    /**
     * A constructor
     * @param paymentTracker an "injected" payment tracker object (service) (non-null)
     * @param inputStream a stream to read payments from (non-null)
     */
    public PaymentDataReader(PaymentTracker paymentTracker, InputStream inputStream) {
        super(inputStream);
        this.paymentTracker = paymentTracker;
    }

    /**
     * Stores a payment read from the stream
     * @param currency parsed currency object (non-null)
     * @param number parsed (decimal) number (non-null)
     */
    void actOnInput(Currency currency, BigDecimal number) {
        paymentTracker.addPayment(currency, number);
    }

}
