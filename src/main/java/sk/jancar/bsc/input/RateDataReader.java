package sk.jancar.bsc.input;

import sk.jancar.bsc.model.Currency;
import sk.jancar.bsc.model.PTLogicException;
import sk.jancar.bsc.model.PaymentTracker;

import java.io.InputStream;
import java.math.BigDecimal;

/**
 * A reader class for exchange rates to USD
 */
public class RateDataReader extends AbstractDataReader {

    private final PaymentTracker paymentTracker;

    /**
     * A constructor
     * @param paymentTracker an "injected" payment tracker object (service) (non-null)
     * @param inputStream a stream to read exchange rates from (non-null)
     */
    public RateDataReader(PaymentTracker paymentTracker, InputStream inputStream) {
        super(inputStream);
        this.paymentTracker = paymentTracker;
    }

    /**
     * Stores an exchange rate read from the stream.
     * Invalid rates are skipped.
     * @param currency parsed currency object (non-null)
     * @param number parsed (decimal) number (non-null)
     */
    void actOnInput(Currency currency, BigDecimal number) {
        try {
            paymentTracker.setUsdRate(currency, number);
        } catch (PTLogicException e) {
            System.err.println("Skipping because of a logic error: " + e.getMessage());
        }
    }

}
