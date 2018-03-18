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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RateDataReaderTest {

    private PaymentTracker pt;
    private ByteArrayOutputStream bos;
    private PrintStream stream;

    @Before
    public void setUp() throws Exception {
        pt = new PaymentTrackerImpl();
        bos = new ByteArrayOutputStream();
        stream = new PrintStream(bos);
    }

    private RateDataReader setUpRateDataReader() {
        return new RateDataReader(pt, new ByteArrayInputStream(bos.toByteArray()));
    }


    @Test
    public void testReading() throws Exception {
        stream.println("CZK 23.45");
        stream.println("EUR 0.8181");
        stream.println("CZK 34.56");

        setUpRateDataReader().run();

        assertBDEquals(new BigDecimal("34.56"), pt.getUsdRate(Currency.of("CZK")));
        assertBDEquals(new BigDecimal("0.8181"), pt.getUsdRate(Currency.of("EUR")));
        assertNull(pt.getUsdRate(Currency.of("USD")));
    }


    @Test
    public void testQuitting() throws Exception {
        stream.println("EUR 10");
        stream.println("EUR 20");
        stream.println("quit");
        stream.println("EUR 30"); //ignored
        stream.println("EUR 40");

        setUpRateDataReader().run();

        assertBDEquals(new BigDecimal("20"), pt.getUsdRate(Currency.of("EUR")));
    }


    private void assertBDEquals(BigDecimal expected, BigDecimal actual) {
        assertTrue(expected.compareTo(actual) == 0);
    }

}