package info.kgeorgiy.ja.istratov.statistic;

import java.util.Locale;

public class NumberStats extends Stats<Double> {
    private double sum;

    public NumberStats(Locale locale) {
        super(locale);
        sum = 0;
    }

    @Override
    public void add(Double e) {
        super.add(e);
        sum += e;
    }

    @Override
    public double getAverage() {
        return sum / globalCount;
    }
}
