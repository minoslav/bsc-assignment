package sk.jancar.bsc.input;

import sk.jancar.bsc.model.Currency;
import sk.jancar.bsc.model.PaymentTracker;

import java.io.InputStream;
import java.math.BigDecimal;

public class PaymentDataReader extends AbstractDataReader {

    private final PaymentTracker paymentTracker;

    public PaymentDataReader(PaymentTracker paymentTracker, InputStream inputStream) {
        super(inputStream);
        this.paymentTracker = paymentTracker;
    }

    void actOnInput(Currency currency, BigDecimal number) {
        paymentTracker.addPayment(currency, number);
    }

}
