package sk.jancar.bsc.model;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentTracker {

    int USD_SCALE = 2;

    void addPayment(Currency currency, BigDecimal amount);

    void setUsdRate(Currency currency, BigDecimal rate) throws PTLogicException;

    Map<Currency, BigDecimal> getNetAmounts();

    BigDecimal getNetAmount(Currency currency);

    BigDecimal getUsdRate(Currency currency);

    BigDecimal getNetAmountInUsd(Currency currency);
}
