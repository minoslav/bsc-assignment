package sk.jancar.bsc.input;

import sk.jancar.bsc.model.Currency;
import sk.jancar.bsc.model.PTLogicException;
import sk.jancar.bsc.model.PaymentTracker;

import java.io.InputStream;
import java.math.BigDecimal;

public class RateDataReader extends AbstractDataReader {

    private final PaymentTracker paymentTracker;

    public RateDataReader(PaymentTracker paymentTracker, InputStream inputStream) {
        super(inputStream);
        this.paymentTracker = paymentTracker;
    }

    void actOnInput(Currency currency, BigDecimal number) {
        try {
            paymentTracker.setUsdRate(currency, number);
        } catch (PTLogicException e) {
            System.err.println("Skipping because of a logic error: " + e.getMessage());
        }
    }

}
