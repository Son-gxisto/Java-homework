package info.kgeorgiy.ja.istratov.statistic.bundle;

import java.util.ListResourceBundle;

public class Bundle_ru extends ListResourceBundle {
    private static final Object[][] CONTENTS = {
            {"Analyzed file", "Analyzed file"},

            {"numSentences", "Количество предложений"},
            {"numWords", "Количество слов"},
            {"numNumbers", "Количество чисел"},
            {"numCurrency", "Количество валюты"},
            {"numDates", "Количество дат"},

            {"statSentences", "Статистика по предложениям"},
            {"statWords", "Статистика по словам"},
            {"statNumbers", "Статистика по числам"},
            {"statCurrency", "Статистика по валюте"},
            {"statDates", "Статистика по датам"},

            {"unique", "Количество уникальных"},
            {"minl", "Минимальная длина"},
            {"maxl", "Максимальная длина"},
            {"minv", "Минимальное значение"},
            {"maxv", "Максимальное значение"},
            {"avgl", "Средняя длина"},
            {"avgv", "Среднее значение"},
            {"outFileNull", "Выходной файл null"},
            {"inFileNull", "Входной файл null"},
            {"statsNull", "Статистика null"},
            {"outError", "Ошибка чтения файла"},
            {"inLocaleNull", "Локаль ввода null, используется локаль по умолчанию"}
    };

    @Override
    protected Object[][] getContents() {
        return CONTENTS;
    }
}
