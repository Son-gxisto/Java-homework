package info.kgeorgiy.ja.istratov.statistic;

import info.kgeorgiy.ja.istratov.statistic.SummaryStats;
import info.kgeorgiy.ja.istratov.statistic.TextStatistics;
import org.junit.BeforeClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import static org.junit.Assert.*;

public class StatisticTest {
    private static TextStatistics stats;
    private static final Locale RU_LOCALE = new Locale("ru");
    private static String filePrefix = "info/kgeorgiy/ja/istratov/statistic/tests/";
    @BeforeClass
    public static void beforeClass() {
        if (Files.exists(Path.of("src"))) {
            filePrefix = "src/" + filePrefix;
        } else if(Files.exists(Path.of("java-solutions"))) {
            filePrefix = "java-solutions/" + filePrefix;
        }
        stats = new TextStatistics(Locale.ENGLISH);
    }

    //show russian and english statistic output for russian text
    @org.junit.Test
    public void demonstrateRussianInput() {
        String file = filePrefix + "ru.in";
        stats = new TextStatistics(RU_LOCALE);
        stats.getWriteStats(RU_LOCALE, file);
        stats.getWriteStats(Locale.ENGLISH, file);
    }

    //show russian and english statistic output for english text
    @org.junit.Test
    public void demonstrateEnglishInput() {
        String file = filePrefix + "en.in";
        stats = new TextStatistics(Locale.ENGLISH);
        stats.getWriteStats(RU_LOCALE, file);
        stats.getWriteStats(Locale.ENGLISH, file);
    }
    //show russian and english statistic output for chinese text
    @org.junit.Test
    public void demonstrateChineseInput() {
        String file = filePrefix + "ch.in";
        stats = new TextStatistics(Locale.CHINA);
        stats.getWriteStats(RU_LOCALE, file, file + "ru.out");
        stats.getWriteStats(Locale.ENGLISH, file, file + "en.out");
    }

    @org.junit.Test
    public void dateAvgTest() {
        String file = filePrefix + "date.in";
        stats = new TextStatistics(Locale.ENGLISH);
        SummaryStats result = stats.getStats(file);
        stats.getWriteStats(RU_LOCALE, file);
        assertEquals(3, result.getDates().getGlobalCount());
        assertEquals(new Date(1622322000000L), new Date((long) result.getDates().getAverage()));
    }

    @org.junit.Test
    public void wrongInputOutputLocale() {
        String file = filePrefix + "ru.in";
        stats = new TextStatistics(Locale.JAPANESE);
        stats.getWriteStats(Locale.JAPANESE, file, file + "wrong.out");
    }

    @org.junit.Test
    public void nullEnTest() {
        String file = filePrefix + "ru.in";
        stats = new TextStatistics(null);
        stats.getWriteStats(null, null, null);
        System.out.println("---");
        stats.getWriteStats(null, file , null);
        System.out.println("---");
        stats.getWriteStats(null, file, file + "null.out");
    }

    @org.junit.Test
    public void nullRuTest() {
        String file = filePrefix + "ru.in";
        stats = new TextStatistics(null);
        stats.getWriteStats(RU_LOCALE, null, null);
        System.out.println("---");
        stats.getWriteStats(RU_LOCALE, file , null);
        System.out.println("---");
        stats.getWriteStats(null, file, file + "null.out");
    }

    @org.junit.Test
    public void wrongEncodingTest() {
        String file = filePrefix + "prest.txt";
        stats = new TextStatistics(RU_LOCALE);
        stats.getWriteStats(RU_LOCALE, file);
    }

    @org.junit.Test
    public void wrongInputFileName() {
        stats = new TextStatistics(RU_LOCALE);
        stats.getWriteStats(RU_LOCALE, "not exist");
    }
}
