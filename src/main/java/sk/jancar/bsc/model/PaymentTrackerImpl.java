package sk.jancar.bsc.model;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentTrackerImpl implements PaymentTracker {

    private final Map<Currency, BigDecimal> netAmounts = new ConcurrentHashMap<>();
    private final Map<Currency, BigDecimal> usdRates = new ConcurrentHashMap<>();
    private final Map<Currency, BigDecimal> unmodifiableNetAmounts = Collections.unmodifiableMap(netAmounts);


    @Override
    public void addPayment(Currency currency, BigDecimal amount) {
        netAmounts.compute(currency, (c, a) -> //ConcurrentHashMap.compute: guaranteed to be ATOMIC (retrieve, calc, update)
            (a != null ? a : BigDecimal.ZERO).add(amount)
        );
    }

    @Override
    public void setUsdRate(Currency currency, BigDecimal rate) throws PTLogicException {
        if (currency.equals(Currency.of("USD")) && rate.compareTo(BigDecimal.ONE) != 0) {
            throw new PTLogicException("USD to USD rate must be 1");
        }

        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PTLogicException("Rate must be positive");
        }

        usdRates.put(currency, rate);
    }

    @Override
    public BigDecimal getNetAmount(Currency currency) {
        return netAmounts.get(currency);
    }

    @Override
    public BigDecimal getUsdRate(Currency currency) {
        return usdRates.get(currency);
    }

    @Override
    public BigDecimal getNetAmountInUsd(Currency currency) {
        return getNetAmountInUsd(currency, getNetAmount(currency));
    }

    @Override
    public BigDecimal getNetAmountInUsd(Currency currency, BigDecimal netAmount) {
        BigDecimal usdRate = getUsdRate(currency);
        if (usdRate == null || netAmount == null) return null;
        return netAmount.multiply(usdRate);
    }

    @Override
    public Map<Currency, BigDecimal> getNetAmounts() {
        return unmodifiableNetAmounts;
    }
}
