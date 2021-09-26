package info.kgeorgiy.ja.istratov.statistic;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.*;
import java.util.*;
import java.util.stream.Collectors;

public class TextStatistics {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Wrong number of arguments, usage: inputLocale outputLocale inputFile outputFile");
        }
        Locale in = new Locale(args[0]);
        Locale out = new Locale(args[1]);
        TextStatistics stats = new TextStatistics(in);
        stats.getWriteStats(out, args[2], args[3]);
    }

    private final Locale locale;
    private final NumberFormat numberFormat, currencyFormat;
    private final List<DateFormat> dateFormats;
    private static final String bundlePath = String.join(".",
            "info", "kgeorgiy", "ja", "istratov", "statistic", "bundle", "Bundle_");
    private static final String statPrefix = "stat";
    private static final String numPrefix = "num";
    private ResourceBundle bundle;

    public TextStatistics() {
        this(Locale.getDefault());
    }

    public TextStatistics(Locale locale) {
        if (locale == null) {
            System.out.println("Input locale is null, used default locale");
            locale = Locale.getDefault();
        }
        this.locale = locale;
        this.numberFormat = NumberFormat.getNumberInstance(locale);
        this.currencyFormat = NumberFormat.getCurrencyInstance(locale);
        this.dateFormats = List.of(
                DateFormat.getDateInstance(DateFormat.SHORT, locale),
                DateFormat.getDateInstance(DateFormat.LONG, locale),
                DateFormat.getDateInstance(DateFormat.FULL, locale),
                DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
        );
        this.bundle = null;
    }

    public SummaryStats getStats(String fileName) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fileName), StandardCharsets.UTF_8))) {
                     //Files.newBufferedReader(Path.of(fileName), StandardCharsets.UTF_8)) {
             String text = reader.lines().collect(Collectors.joining());
             SummaryStats stats = new SummaryStats(locale);
             BreakIterator bi = BreakIterator.getWordInstance(locale);
             bi.setText(text);
            for (int right = bi.first(), left = 0; right != BreakIterator.DONE; left = right, right = bi.next()) {
                String cur = text.substring(left, right).trim();
                if (cur.isEmpty()) continue;
                if (!parseDate(text, stats.getDates(), left)) {
                    if (!parseNumber(text, currencyFormat, stats.getCurrency(), left)) {
                        if (!parseNumber(text, numberFormat, stats.getNumbers(), left)) {
                            parseString(cur, stats.getWords());
                        }
                    }
                }
            }
            bi = BreakIterator.getSentenceInstance();
            bi.setText(text);
            for (int right = bi.first(), left = 0; right != BreakIterator.DONE; left = right, right = bi.next()) {
                parseString(text.substring(left, right).trim(), stats.getSentences());
            }
            return stats;
        } catch (IOException e) {
            System.err.println(bundle.getString("outError") + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println(bundle.getString("inFileNull"));
        }
        return null;
    }

    private boolean parseNumber(String s, NumberFormat f, NumberStats stats, int position) {
        Number num = f.parse(s, new ParsePosition(position));
        if (num != null) {
            stats.add(num.doubleValue());
            return true;
        }
        return false;
    }

    private boolean parseDate(String s, Stats<Date> stats, int position) {
        for (DateFormat f : dateFormats) {
            Date d = f.parse(s, new ParsePosition(position));
            if (d != null) {
                stats.add(d);
                return true;
            }
        }
        return false;
    }

    private boolean parseString(String s, Stats<String> stats) {
        if (s == null || s.isEmpty() || !Character.isLetter(s.charAt(0))) {
            return false;
        }
        stats.add(s);
        return true;
    }

    public void getWriteStats(Locale outputLocale, String input) {
        getWriteStats(outputLocale, input, input + ".out");
    }
    public void getWriteStats(Locale outputLocale, String input, String output) {
        try {
            bundle = ResourceBundle.getBundle(bundlePath + outputLocale);
        } catch (MissingResourceException e) {
            if (bundle == null) {
                System.out.println("output locale not supported, used english");
                bundle = ResourceBundle.getBundle(bundlePath + "en");
            }
        }
        SummaryStats stats = getStats(input);
        if (stats == null) {
            System.err.println(bundle.getString("statsNull"));
            return;
        }
        if (output == null) {
            System.err.println(bundle.getString("outFileNull"));
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(output))) {
            List<Object> args = List.of(getFormattedPoint(bundle.getString("Analyzed file"), input),
                    getNumber(stats.getSentences(), "Sentences"),
                    getNumber(stats.getWords(), "Words"),
                    getNumber(stats.getNumbers(), "Numbers"),
                    getNumber(stats.getCurrency(), "Currency"),
                    getNumber(stats.getDates(), "Dates"),
                    stringOnlyStats(stats.getSentences(), "Sentences"),
                    stringOnlyStats(stats.getWords(), "Words"),
                    numOnlyStats(stats.getNumbers(), "Numbers"),
                    numOnlyStats(stats.getCurrency(), "Currency"),
                    dateOnlyStats(stats.getDates(), "Dates"));
            writer.write(MessageFormat.format(getArrayFormat(args.size()), args.toArray()));
            /*
            writer.write(String.join( System.lineSeparator(),
                    bundle.getString("Analyzed file") + " " + input,
            getNumber(stats.getSentences(), "Sentences"),
            getNumber(stats.getWords(), "Words"),
            getNumber(stats.getNumbers(), "Numbers"),
            getNumber(stats.getCurrency(), "Currency"),
            getNumber(stats.getDates(), "Dates"),
            stringOnlyStats(stats.getSentences(), "Sentences"),
            stringOnlyStats(stats.getWords(), "Words"),
            numOnlyStats(stats.getNumbers(), "Numbers"),
            numOnlyStats(stats.getCurrency(), "Currency"),
            dateOnlyStats(stats.getDates(), "Dates"))); */
        } catch (IOException e) {
            System.err.println(bundle.getString("outError") + e.getMessage());
        }
    }

    private String getArrayFormat(int n) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < n - 1; i++) {
            res.append("{").append(i).append("}").append(System.lineSeparator());
        }
        res.append("{").append(n - 1).append("}");
        return res.toString();
    }

    private String getFormattedPoint(Object key, Object value) {
        return MessageFormat.format("{0} {1}", key, value);
    }

    private String getNumber(Stats<?> stats, String name) {
        return getFormattedPoint(bundle.getString(numPrefix + name), stats.getGlobalCount());
    }

    private List<String> statsToString(Stats<?> stats, String name) {
        List<String> t = new ArrayList<>(List.of(
                bundle.getString(statPrefix + name),
                getFormattedPoint(bundle.getString("unique"), stats.getGlobalCount())));
        if (stats.getGlobalCount() == 0) {
            return t;
        }
        t.addAll(List.of(getFormattedPoint(bundle.getString("minv"), stats.getMin()),
                getFormattedPoint(bundle.getString("maxv"), stats.getMax())));
        return t;
    }

    private String dateOnlyStats(Stats<?> stats, String name) {
        List<String> t = statsToString(stats, name);
        if (stats.getGlobalCount() > 0) {
            t.add(bundle.getString("avgv") + " " + new Date((long) stats.getAverage()));
        }
        return MessageFormat.format(getArrayFormat(t.size()), t.toArray());
    }

    private String numOnlyStats(Stats<?> stats, String name) {
        List<String> t = statsToString(stats, name);
        if (stats.globalCount > 0) {
            t.add(getFormattedPoint(bundle.getString("avgv"), stats.getAverage()));
        }
        return MessageFormat.format(getArrayFormat(t.size()), t.toArray());
    }

    private String stringOnlyStats(Stats<?> stats, String name) {
        List<String> t = statsToString(stats, name);
        if (stats.getGlobalCount() > 0) {
            t.addAll(List.of(
                    getFormattedPoint(bundle.getString("minl"), stats.getMinLength()),
                    getFormattedPoint(bundle.getString("maxl"), stats.getMaxLength()),
                    getFormattedPoint(bundle.getString("avgl"), stats.getAverage())));
        }
        return MessageFormat.format(getArrayFormat(t.size()), t.toArray());
    }
}