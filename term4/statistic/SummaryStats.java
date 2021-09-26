package info.kgeorgiy.ja.istratov.statistic;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SummaryStats {
    public NumberStats getCurrency() {
        return currency;
    }

    public NumberStats getNumbers() {
        return numbers;
    }

    public Stats<String> getWords() {
        return words;
    }

    public Stats<String> getSentences() {
        return sentences;
    }

    public Stats<Date> getDates() {
        return dates;
    }

    private final NumberStats currency, numbers;
    private final Stats<String> words, sentences;
    private final Stats<Date> dates;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SummaryStats)) return false;
        SummaryStats that = (SummaryStats) o;
        return Objects.equals(currency, that.currency) &&
                Objects.equals(numbers, that.numbers) &&
                Objects.equals(words, that.words) &&
                Objects.equals(sentences, that.sentences) &&
                Objects.equals(dates, that.dates);
    }

    public SummaryStats(Locale locale) {
        currency = new NumberStats(locale);
        numbers = new NumberStats(locale);
        words = new Stats<>(locale);
        sentences = new Stats<>(locale);
        dates= new DateStats(locale);
    }

    public Map<String, Integer> getSummary() {
        return Map.of(
                "numbers", numbers.getGlobalCount(),
                "currency", currency.getGlobalCount(),
                "date", dates.getGlobalCount(),
                "words", words.getGlobalCount(),
                "sentences", sentences.getGlobalCount()
        );
    }

    @Override
    public String toString() {
        return "numbers: " + numbers.toString() +  "\n" +
                "currency: " + currency.toString() + "\n" +
                "dates: " + dates.toString() + "\n" +
                "words: " + words.toString() + "\n" +
                "sentences: " + sentences.toString();
    }
}
