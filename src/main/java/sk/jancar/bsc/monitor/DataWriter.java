package sk.jancar.bsc.monitor;

import sk.jancar.bsc.model.PaymentTracker;

import java.io.PrintStream;
import java.math.BigDecimal;

public class DataWriter implements Runnable {

    private final PaymentTracker paymentTracker;
    private final PrintStream stream;


    public DataWriter(PaymentTracker paymentTracker, PrintStream outputStream) {
        this.paymentTracker = paymentTracker;
        this.stream = outputStream;
    }

    public void run() {
        stream.println("# Net amounts:");

        paymentTracker.getNetAmounts().entrySet().stream()
            .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) != 0)
            .forEach(e -> {
                BigDecimal inUsd = paymentTracker.getNetAmountInUsd(e.getKey());

                if (inUsd == null) {
                    stream.format("%s %s%n", e.getKey(), e.getValue()); //%s: canonical rep. since a format was not given
                } else {
                    stream.format("%s %s (USD %s)%n", e.getKey(), e.getValue(), inUsd);
                }
            });

        stream.println("# -------------");
    }

}
