package sk.jancar.bsc.monitor;

import sk.jancar.bsc.model.PaymentTracker;

import java.io.PrintStream;
import java.math.BigDecimal;

/**
 * Monitors payments and writes net amounts to a provided stream
 */
public class DataWriter implements Runnable {

    private final PaymentTracker paymentTracker;
    private final PrintStream stream;

    /**
     * A constructor
     * @param paymentTracker an "injected" payment tracker object (service) (non-null)
     * @param outputStream a stream to write data to (non-null)
     */
    public DataWriter(PaymentTracker paymentTracker, PrintStream outputStream) {
        this.paymentTracker = paymentTracker;
        this.stream = outputStream;
    }

    /**
     * Writes (once) a current state of net amounts for all registered currencies to a supplied stream.
     * If a net amount is equal to 0, it is omitted.
     * If an exchange rate to USD is provided for a currency, a net amount in USD is also outputted.
     * Does not close the supplied output stream.
     */
    public void run() {
        stream.println("# Net amounts:");

        //consecutive calls to e.getValue() should return the same value (captured in MapEntry; see ConcurrentHashMap.MapEntry)
        paymentTracker.getNetAmounts().entrySet().stream()
            .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) != 0)
            .forEach(e -> {
                BigDecimal inUsd = paymentTracker.getNetAmountInUsd(e.getKey(), e.getValue()); //using *the* already retrieved value for a conversion

                if (inUsd == null) {
                    stream.format("%s %s%n", e.getKey(), e.getValue()); //%s: canonical rep. since a format was not given
                } else {
                    stream.format("%s %s (USD %s)%n", e.getKey(), e.getValue(), inUsd);
                }
            });

        stream.println("# -------------");
    }

}
