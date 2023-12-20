package com.example.paymentservice.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public class FakeBalanceGenerator {
    private static final int MAX_BALANCE_VALUE = 10_000;
    private static final int SCALE = 2;

    public static BigDecimal generateCardBalance() {
        double balance = ThreadLocalRandom.current().nextDouble(0, MAX_BALANCE_VALUE);
        return BigDecimal.valueOf(balance).setScale(SCALE, RoundingMode.HALF_UP);
    }
}
