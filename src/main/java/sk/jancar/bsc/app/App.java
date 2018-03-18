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
        boolean ratesFromStdIn = false;

        for (String arg : args) {
            if (arg.startsWith("-p=")) {
                paymentFile = arg.substring(3);
            } else if (arg.startsWith("-r=")) {
                rateFile = arg.substring(3);
            } else if (arg.equals("-rs")) {
                ratesFromStdIn = true;
            } else {
                System.err.println("Invalid argument ignored: " + arg);
            }
        }

        new App().start(paymentFile, rateFile, ratesFromStdIn);
    }

    public void start(String paymentFile, String rateFile, boolean ratesFromStdIn) {
        Timer t = null;
        List<AbstractDataReader> fileInputs = new ArrayList<>();
        List<AbstractDataReader> stdInputs = new ArrayList<>();

        try {

            //model
            PaymentTracker paymentTracker = new PaymentTrackerImpl();

            //monitor
            Runnable outputWriter = new DataWriter(paymentTracker, System.out);

            //input(s)
            if (rateFile != null) {
                fileInputs.add(new RateDataReader(paymentTracker, new FileInputStream(rateFile)));
            }
            if (paymentFile != null) {
                fileInputs.add(new PaymentDataReader(paymentTracker, new FileInputStream(paymentFile)));
            }
            if (ratesFromStdIn) {
                stdInputs.add(new RateDataReader(paymentTracker, System.in));
            }
            stdInputs.add(new PaymentDataReader(paymentTracker, System.in));


            //run
            t = runAsyncRepeatedly(outputWriter, 2000);
            fileInputs.forEach(this::runAsync);
            stdInputs.forEach(this::runSync);

        } catch (FileNotFoundException e) {
            System.err.println("A given file not found, stopping: " + e.getMessage());

        } finally {
            if (t != null) t.cancel();

            fileInputs.forEach((i) -> closeQuietly(i.getInputStream()));
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
