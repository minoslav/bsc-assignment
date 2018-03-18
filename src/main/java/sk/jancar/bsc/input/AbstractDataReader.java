package sk.jancar.bsc.input;

import sk.jancar.bsc.model.Currency;

import java.io.*;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractDataReader implements Runnable {

    private final InputStream inputStream;

    public AbstractDataReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void run() {
        try {
            readStream(inputStream);
        } catch (IOException e) {
            System.err.println("IO error occured: " + e.getMessage());
        }
    }

    private void readStream(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        while (true) {
            String line = br.readLine();
            if (line == null || line.equals("quit")) {
                break;
            }
            parseLine(line);
        }
    }

    public boolean parseLine(String line) {
        Matcher matcher = Pattern.compile("(" + Currency.FORMAT_REGEX + ")\\s+(.+)").matcher(line);
        if (matcher.matches()) {
            try {
//                System.out.println(">>> " + matcher.group(2) + ":" + new BigDecimal(matcher.group(2)));
                actOnInput(Currency.of(matcher.group(1)), new BigDecimal(matcher.group(2)));
                return true;
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format, skipping the line");
                return false;
            }
        } else {
            System.err.println("Invalid input line or currency format, skipping the line");
            return false;
        }
    }

    abstract void actOnInput(Currency currency, BigDecimal number);

}
