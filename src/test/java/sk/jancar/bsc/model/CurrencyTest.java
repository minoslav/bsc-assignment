package sk.jancar.bsc.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CurrencyTest {

    private Currency eur1, eur2, usd;

    @Before
    public void setUp() throws Exception {
        eur1 = Currency.of("EUR");
        eur2 = Currency.of("EUR");
        usd = Currency.of("USD");
    }


    @Test
    public void testEqualsAndHashCode() throws Exception {
        assertEquals(eur1, eur2);
        assertEquals(eur1.hashCode(), eur2.hashCode());

        assertNotEquals(eur1, usd);
        assertNotEquals(eur2, usd);
    }

    @Test
    public void testReferencePool() throws Exception {
        assertSame(eur1, eur2);
        assertNotSame(eur1, usd);
        assertNotSame(eur2, usd);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCodeLowercase() throws Exception {
        Currency.of("eur");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCodeLong() throws Exception {
        Currency.of("LONG");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCodeShort() throws Exception {
        Currency.of("SH");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCodeEmpty() throws Exception {
        Currency.of("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCodeNull() throws Exception {
        Currency.of(null);
    }


}