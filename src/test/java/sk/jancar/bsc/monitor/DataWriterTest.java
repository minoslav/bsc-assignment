package sk.jancar.bsc.monitor;

import org.junit.Before;
import org.junit.Test;
import sk.jancar.bsc.model.Currency;
import sk.jancar.bsc.model.PaymentTracker;
import sk.jancar.bsc.model.PaymentTrackerImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class DataWriterTest {

    private PaymentTracker pt;
    private DataWriter dataWriter;
    private ByteArrayOutputStream bos;

    @Before
    public void setUp() throws Exception {
        pt = new PaymentTrackerImpl();
        bos = new ByteArrayOutputStream();
        dataWriter = new DataWriter(pt, new PrintStream(bos));
    }


    @Test
    public void testBasicOutputting() throws Exception {
        pt.addPayment(Currency.of("EUR"), new BigDecimal(500));
        pt.addPayment(Currency.of("EUR"), new BigDecimal(-200));
        pt.addPayment(Currency.of("USD"), new BigDecimal(-21));

        dataWriter.run();
        String output = bos.toString();

        assertTrue(output.contains("EUR 300"));
        assertTrue(output.contains("USD -21"));
    }

    @Test
    public void testDecimals() throws Exception {
        pt.addPayment(Currency.of("CZK"), new BigDecimal("-12.345"));

        dataWriter.run();

        assertTrue(bos.toString().contains("CZK -12.345"));
    }

    @Test
    public void testConverted() throws Exception {
        pt.setUsdRate(Currency.of("EUR"), new BigDecimal("1.25"));
        pt.addPayment(Currency.of("EUR"), new BigDecimal("1.6"));
        pt.addPayment(Currency.of("CZK"), new BigDecimal("200"));

        dataWriter.run();
        String output = bos.toString();

        assertTrue(output.contains("EUR 1.6 (USD 2.000)"));
        assertTrue(output.contains("CZK 200"));
    }
}