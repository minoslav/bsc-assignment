package sk.jancar.bsc.app;

import sk.jancar.bsc.input.AbstractDataReader;
import sk.jancar.bsc.input.PaymentDataReader;
import sk.jancar.bsc.input.RateDataReader;
import sk.jancar.bsc.model.PaymentTrackerImpl;
import sk.jancar.bsc.model.PaymentTracker;
import sk.jancar.bsc.monitor.DataWriter;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class App {

    public static void main(String[] args) {
        String paymentFile = null;
        String rateFile = null;

        for (String arg : args) {
            if (arg.startsWith("-p=")) {
                paymentFile = arg.substring(3);
            } else if (arg.startsWith("-r=")) {
                rateFile = arg.substring(3);
            }
        }

        new App().start(paymentFile, rateFile);
    }

    public void start(String paymentFile, String rateFile) {
        Timer t = null;
        List<AbstractDataReader> inputs = new ArrayList<>();

        try {

            //model
            PaymentTracker paymentTracker = new PaymentTrackerImpl();

            //monitor
            Runnable outputWriter = new DataWriter(paymentTracker, System.out);

            //input(s)
            if (rateFile != null) {
                inputs.add(new RateDataReader(paymentTracker, new FileInputStream(rateFile)));
            }
            if (paymentFile != null) {
                inputs.add(new PaymentDataReader(paymentTracker, new FileInputStream(paymentFile)));
            }
            inputs.add(new RateDataReader(paymentTracker, System.in));
            inputs.add(new PaymentDataReader(paymentTracker, System.in));


            //run
            t = runAsyncRepeatedly(outputWriter, 2000);
            inputs.forEach(this::runSync);

        } catch (FileNotFoundException e) {
            System.err.println("A given file not found, stopping: " + e.getMessage());

        } finally {
            if (t != null) t.cancel();

            inputs.forEach((i) -> closeQuietly(i.getInputStream()));
        }
    }

    private void closeQuietly(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) { /* swallow */ }
        }
    }

    private void runSync(Runnable task) {
        task.run();
    }

    private void runAsync(Runnable task) {
        new Thread(task).start();
    }

    private Timer runAsyncRepeatedly(Runnable task, long interval) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, 0, interval);
        return timer;
    }
}
