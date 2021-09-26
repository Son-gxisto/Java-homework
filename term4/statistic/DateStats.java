package info.kgeorgiy.ja.istratov.statistic;

import java.util.Date;
import java.util.Locale;

public class DateStats extends Stats<Date> {
    private long sum;

    public DateStats(Locale locale) {
        super(locale);
        sum = 0;
    }

    @Override
    public void add(Date e) {
        super.add(e);
        sum += e.getTime();
    }

    @Override
    public double getAverage() {
        return (double) (sum / globalCount);
    }

}
