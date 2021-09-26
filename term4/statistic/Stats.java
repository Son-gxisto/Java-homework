package info.kgeorgiy.ja.istratov.statistic;

import java.text.BreakIterator;
import java.text.Collator;
import java.util.*;

public class Stats<T extends Comparable<? super T>> {
    private Collator collator;
    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getGlobalCount() {
        return globalCount;
    }

    public void setGlobalCount(int globalCount) {
        this.globalCount = globalCount;
    }

    public long getGlobalLength() {
        return globalLength;
    }

    public void setGlobalLength(long globalLength) {
        this.globalLength = globalLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stats)) return false;
        Stats<?> stats = (Stats<?>) o;
        return getMinLength() == stats.getMinLength() &&
                getMaxLength() == stats.getMaxLength() &&
                getGlobalCount() == stats.getGlobalCount() &&
                getGlobalLength() == stats.getGlobalLength() &&
                Objects.equals(getMin(), stats.getMin()) &&
                Objects.equals(getMax(), stats.getMax());
    }

    protected T min, max;
    protected int minLength, maxLength;
    protected String minStr, maxStr;
    protected int globalCount;
    protected long globalLength;
    protected final Map<T, Integer> count;
    protected final Set<T> elements;
    protected final Locale locale;

    public Stats(Locale locale) {
        count = new HashMap<>();
        elements = new HashSet<>();
        minLength = Integer.MAX_VALUE;
        maxLength = Integer.MIN_VALUE;
        collator = Collator.getInstance(locale);
        this.locale = locale;
    }

    public void add(T e) {
        if (e == null) {
            return;
        }
        globalCount++;
        count.put(e, count.getOrDefault(e, 0) + 1);
        final int l = getLength(e.toString());
        globalLength += l;
        if (elements.add(e)) {
            if (e instanceof String) {
                if (min == null || collator.compare(e, min) < 0) {
                    min = e;
                }
                if (max == null || collator.compare(e, max) > 0) {
                    max = e;
                }
            } else {
                if (min == null || e.compareTo(min) < 0) {
                    min = e;
                }
                if (max == null || e.compareTo(max) > 0) {
                    max = e;
                }
            }
            if (l < minLength) {
                minLength = l;
                minStr = e.toString();
            }
            if (l > maxLength) {
                maxLength = l;
                maxStr = e.toString();
            }
        }
    }

    private int getLength(String s) {
        BreakIterator iterator = BreakIterator.getCharacterInstance(locale);
        iterator.setText(s);
        int res = -1;
        int i = iterator.first();
        while (i != BreakIterator.DONE) {
            i = iterator.next();
            res++;
        }
        if (res != iterator.last() - iterator.first()) {
            System.out.println("res:" + res + "; " + (iterator.last() - iterator.first()));
        }
        return res;
    }

    public double getAverage() {
        return ((double) globalLength) / globalCount;
    }

    @Override
    public String toString() {
        return count.toString();
    }
}
