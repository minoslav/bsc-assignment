package sk.jancar.bsc.input;

import org.junit.Before;
import org.junit.Test;
import sk.jancar.bsc.model.Currency;
import sk.jancar.bsc.model.PaymentTracker;
import sk.jancar.bsc.model.PaymentTrackerImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PaymentDataReaderTest {

    private PaymentTracker pt;
    private ByteArrayOutputStream bos;
    private PrintStream stream;

    @Before
    public void setUp() throws Exception {
        pt = new PaymentTrackerImpl();
        bos = new ByteArrayOutputStream();
        stream = new PrintStream(bos);
    }

    private PaymentDataReader setUpPaymentDataReader() {
        return new PaymentDataReader(pt, new ByteArrayInputStream(bos.toByteArray()));
    }


    @Test
    public void testBasicReading() throws Exception {
        stream.println("CZK 30");
        stream.println("EUR 50");
        stream.println("CZK -30");
        stream.println("USD 100");
        stream.println("EUR 70");

        setUpPaymentDataReader().run();

        assertBDEquals(BigDecimal.ZERO, pt.getNetAmount(Currency.of("CZK")));
        assertBDEquals(new BigDecimal(120), pt.getNetAmount(Currency.of("EUR")));
        assertBDEquals(new BigDecimal(100), pt.getNetAmount(Currency.of("USD")));
        assertNull(pt.getNetAmount(Currency.of("SKK")));
    }

    @Test
    public void testDecimals() throws Exception {
        stream.println("CZK 12.34");

        setUpPaymentDataReader().run();

        assertBDEquals(new BigDecimal("12.34"), pt.getNetAmount(Currency.of("CZK")));
    }

    @Test
    public void testQuitting() throws Exception {
        stream.println("EUR 10");
        stream.println("EUR 20");
        stream.println("quit");
        stream.println("EUR 30"); //ignored
        stream.println("EUR 40");

        setUpPaymentDataReader().run();

        assertBDEquals(new BigDecimal("30"), pt.getNetAmount(Currency.of("EUR")));
    }

    @Test
    public void testParsingLine() throws Exception {
        PaymentDataReader dataReader = setUpPaymentDataReader();

        assertTrue(dataReader.parseLine("EUR 100"));
        assertTrue(dataReader.parseLine("EUR 100.00"));
        assertTrue(dataReader.parseLine("EUR 100.00"));

        assertFalse(dataReader.parseLine("  EUR 100"));
        assertFalse(dataReader.parseLine("EUR 100  "));
        assertFalse(dataReader.parseLine("eur 100"));
        assertFalse(dataReader.parseLine("ABCD 100"));
        assertFalse(dataReader.parseLine("AB 100"));
        assertFalse(dataReader.parseLine(" 100"));
        assertFalse(dataReader.parseLine("1"));
        assertFalse(dataReader.parseLine("EUR:100"));
        assertFalse(dataReader.parseLine(""));
    }


    private void assertBDEquals(BigDecimal expected, BigDecimal actual) {
        assertTrue(expected.compareTo(actual) == 0);
    }

}