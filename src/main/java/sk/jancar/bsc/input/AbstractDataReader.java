package sk.jancar.bsc.input;

import sk.jancar.bsc.model.Currency;

import java.io.*;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates reading from a given stream in the given format (lines of "CURRENCY number").
 * An action to be taken on read data needs to be overridden.
 */
public abstract class AbstractDataReader implements Runnable {

    private final InputStream inputStream;

    /**
     * A constructor
     * @param inputStream a stream to read lines from (non-null)
     */
    protected AbstractDataReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Gets the stream configured during construction
     * @return the provided input stream (non-null)
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Reads from the provided stream line by line and calls parseLine on each line until EOF or a line containing "quit".
     * IOException during reading is swallowed and stops the reading.
     * The stream is not closed in the end.
     */
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

    /**
     * Parses a given line and takes an action on it (actOnInput method). A line should be in the format:
     * "CURRENCY_CODE SPACE NUMBER", where CURRENCY_CODE format depends on the Currency class and its code format, SPACE is one or more whitespaces,
     * and NUMBER is in a format parsable by the BigDecimal class.
     * If the line is not in the specified format, no action is taken on it (the line is skipped).
     * @param line a line to be parsed (non-null)
     * @return true if parsed successfully, false otherwise
     */
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

    /**
     * A hook method for overriding in a concrete reader. Defines what to do with a parsed line.
     * @param currency parsed currency object (non-null)
     * @param number parsed (decimal) number (non-null)
     */
    abstract protected void actOnInput(Currency currency, BigDecimal number);

}
