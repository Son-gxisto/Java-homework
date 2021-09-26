package info.kgeorgiy.ja.istratov.statistic.bundle;

import java.util.ListResourceBundle;

public class Bundle_en extends ListResourceBundle {
    private static final Object[][] CONTENTS = {
            {"Analyzed file", "Analyzed file"},

            {"numSentences", "Number of sentences"},
            {"numWords", "Number of words"},
            {"numNumbers", "Number of numbers"},
            {"numCurrency", "Number of currency"},
            {"numDates", "Number of dates"},

            {"statSentences", "Statistic by sentences"},
            {"statWords", "Statistic by words"},
            {"statNumbers", "Statistic by numbers"},
            {"statCurrency", "Statistic by currency"},
            {"statDates", "Statistic by dates"},

            {"unique", "Number of unique"},
            {"minl", "Minimum length"},
            {"maxl", "Maximum length"},
            {"minv", "Minimum value"},
            {"maxv", "Maximum value"},
            {"avgl", "Average length"},
            {"avgv", "Average value"},
            {"outFileNull", "Output filename is null"},
            {"inFileNull", "Input filename is null"},
            {"statsNull", "Stats is null"},
            {"outError", "File reading error"},
            {"inLocaleNull", "Input locale is null, used default locale"}
    };

    @Override
    protected Object[][] getContents() {
        return CONTENTS;
    }
}
