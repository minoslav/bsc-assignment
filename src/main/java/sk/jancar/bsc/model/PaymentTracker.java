package sk.jancar.bsc.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * An interface of the main "service" of the app.
 * The service manages operations on payments and exchange rates to USD.
 * Operations are thread-safe.
 */
public interface PaymentTracker {

    /**
     * Registers a given payment. Payments are summed for a particular currency.
     * @param currency a currency (non-null)
     * @param amount an amount (non-null)
     */
    void addPayment(Currency currency, BigDecimal amount);

    /**
     * Sets an exchange rate for a conversion of summed amount of a given currency to USD.
     * Overwrites a previously set rate for the currency.
     * @param currency a currency (CUR) (non-null)
     * @param rate a rate, i.e. CUR/USD = rate, i.e. 1 CUR = rate USD (non-null)
     * @throws PTLogicException when the rate is not possible, e.g. less than or equal to 0
     */
    void setUsdRate(Currency currency, BigDecimal rate) throws PTLogicException;

    /**
     * Gets the unmodifiable view of currency-to-net-amount map
     * @return the map (non-null)
     */
    Map<Currency, BigDecimal> getNetAmounts();

    /**
     * Gets a net amount for a given currency
     * @param currency a currency (non-null)
     * @return a net amount (may be null if no payment in the given currency was made)
     */
    BigDecimal getNetAmount(Currency currency);

    /**
     * Gets the last set exchange rate of a given currency to USD
     * @param currency a currency (non-null)
     * @return a rate (may be null if not set yet)
     */
    BigDecimal getUsdRate(Currency currency);

    /**
     * Gets a net amount for a given currency converted to USD using the last set exchange rate
     * @param currency a currency (non-null)
     * @return a net amount in USD (may be null if the rate has not been set yet or if no payment in the currency was made)
     */
    BigDecimal getNetAmountInUsd(Currency currency);

    /**
     * Converts a given net amount in a given currency to USD using the last set exchange rate
     * @param currency a currency (non-null)
     * @param netAmount a net amount in the currency to be converted (may be null)
     * @return a net amount in USD (may be null if the rate has not been set yet or if the provided netAmount is null)
     */
    BigDecimal getNetAmountInUsd(Currency currency, BigDecimal netAmount);
}
