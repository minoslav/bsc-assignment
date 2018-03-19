package sk.jancar.bsc.model;

import org.junit.*;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PaymentTrackerTest {

    private PaymentTracker pt;

    @Before
    public void setUp() throws Exception {
        pt = new PaymentTrackerImpl();
    }


    @Test
    public void testMath() throws Exception {
        assertNull(pt.getNetAmount(Currency.of("EUR")));
        addAndCheck("EUR", "5000", "5000");
        addAndCheck("EUR", "3000", "8000");
        addAndCheck("EUR", "-8000", "0");
        addAndCheck("EUR", "-5.23", "-5.23");
        addAndCheck("EUR", "+5.23", "0");
        check("EUR", "0.0");
        check("EUR", "0.00");
        addAndCheck("EUR", "-5.231", "-5.231");
        addAndCheck("EUR", ".23", "-5.001");
        addAndCheck("EUR", "5", "-0.001");
        addAndCheck("EUR", "0.001", "0");
    }

    @Test
    public void testNotInterferingCurrencies() throws Exception {
        assertNull(pt.getNetAmount(Currency.of("USD")));
        assertNull(pt.getNetAmount(Currency.of("EUR")));

        add("USD", "50");
        check("USD", "50");
        assertNull(pt.getNetAmount(Currency.of("EUR")));

        add("EUR", "80");
        check("USD", "50");
        check("EUR", "80");

        add("USD", "-50");
        check("USD", "0");
        check("EUR", "80");

        add("EUR", "-80");
        check("USD", "0");
        check("EUR", "0");
    }

    @Test
    public void testGetNetAmounts() throws Exception {
        assertNotNull(pt.getNetAmounts());
        assertTrue(pt.getNetAmounts().size() == 0);

        add("SKK", "50.26");
        add("EUR", "-1");
        add("EUR", "10");
        add("HRK", "7");

        assertTrue(pt.getNetAmounts().size() == 3);

        assertTrue(pt.getNetAmounts().keySet().contains(Currency.of("SKK")));
        assertTrue(pt.getNetAmounts().keySet().contains(Currency.of("EUR")));
        assertTrue(pt.getNetAmounts().keySet().contains(Currency.of("HRK")));

        assertBDEquals(d("9"), pt.getNetAmounts().get(Currency.of("EUR")));
        assertBDEquals(d("9"), pt.getNetAmount(Currency.of("EUR")));
    }

    @Test
    public void testUsdConversions() throws Exception {
        assertNull(pt.getUsdRate(Currency.of("EUR")));
        assertNull(pt.getNetAmountInUsd(Currency.of("EUR")));

        pt.setUsdRate(Currency.of("EUR"), d("999"));
        pt.setUsdRate(Currency.of("EUR"), d("1.22965"));
        assertBDEquals(d("1.22965"), pt.getUsdRate(Currency.of("EUR")));

        add("EUR", "1");
        check("EUR", "1");
        assertBDEquals(d("1.22965"), pt.getNetAmountInUsd(Currency.of("EUR")));

        add("EUR", "-1");
        check("EUR", "0");
        assertBDEquals(d("0"), pt.getNetAmountInUsd(Currency.of("EUR")));
        assertBDEquals(d("0.00"), pt.getNetAmountInUsd(Currency.of("EUR")));

        add("EUR", "12.456");
        check("EUR", "12.456");
        // 12.456 * 1.22965 = 15.3165204
        assertBDEquals(d("15.3165204"), pt.getNetAmountInUsd(Currency.of("EUR")));
    }

    @Test(expected = PTLogicException.class)
    public void testInvalidNegativeRate() throws Exception {
        pt.setUsdRate(Currency.of("SKK"), d("-5"));
    }

    @Test(expected = PTLogicException.class)
    public void testInvalidZeroRate() throws Exception {
        pt.setUsdRate(Currency.of("SKK"), d("0"));
    }

    @Test(expected = PTLogicException.class)
    public void testInvalidUsdToUsdRate() throws Exception {
        pt.setUsdRate(Currency.of("USD"), d("0.99999"));
    }

    @Test
    public void testSomeValidRates() throws Exception {
        pt.setUsdRate(Currency.of("SKK"), d("0.99999"));
        pt.setUsdRate(Currency.of("SKK"), d("1"));
        pt.setUsdRate(Currency.of("SKK"), d("1.00"));

        pt.setUsdRate(Currency.of("USD"), d("1"));
        pt.setUsdRate(Currency.of("USD"), d("1.00"));
    }


    //private helper methods for concise test cases

    private BigDecimal d(String val) { return new BigDecimal(val); }

    private void assertBDEquals(BigDecimal expected, BigDecimal actual) {
        assertTrue(expected.compareTo(actual) == 0);
    }

    private void add(String currency, String amount) {
        pt.addPayment(Currency.of(currency), d(amount));
    }

    private void check(String currency, String expected) {
        assertBDEquals(d(expected), pt.getNetAmount(Currency.of(currency)));
    }

    private void addAndCheck(String currency, String amount, String expected) {
        add(currency, amount);
        check(currency, expected);
    }

}