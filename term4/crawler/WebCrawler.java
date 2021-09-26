package info.kgeorgiy.ja.istratov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

//java -cp . -p . -m info.kgeorgiy.java.advanced.crawler easy info.kgeorgiy.ja.istratov.crawler.WebCrawler
public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final ExecutorService downloaders, extractors;

    public WebCrawler(final Downloader downloader, final int downloaders, final int extractors, final int perHost) {
        this.downloader = downloader;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
    }

    private static void main(final String[] args) {
        if (args.length != 5) {
            System.err.println("Wrong number of arguments, usage: url [depth [downloads [extractors [perHost]]]]");
        }
        try {
            final Crawler crawler = new WebCrawler(new CachingDownloader(),
                    getOrDefault(args, 2, 4),
                    getOrDefault(args, 3, 4),
                    getOrDefault(args, 4, 4));
            crawler.download(args[0], getOrDefault(args, 1, 1));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static int getOrDefault(final String[] args, final int i, final int value) {
        try {
            return Integer.parseInt(args[i]);
        } catch (final IndexOutOfBoundsException e) {
            return value;
        }
    }
    // :NOTE: Форматирование кода
    /**
     * Downloads web site up to specified depth.
     *
     * @param url   start <a href="http://tools.ietf.org/html/rfc3986">URL</a>.
     * @param depth download depth.
     * @return download result.
     */
    @Override
    public Result download(final String url, final int depth) {
        final Set<String> visited = ConcurrentHashMap.newKeySet();
        final Set<String> result = ConcurrentHashMap.newKeySet();
        visited.add(url);
        final Map<String, IOException> errors = new ConcurrentHashMap<>();
        final Phaser phaser = new Phaser(1);
        download(url, depth, result, visited, errors, phaser);
        phaser.arriveAndAwaitAdvance();
        return new Result(List.copyOf(result), errors);
    }

    private void download(final String url, final int depth, final Set<String> result, final Set<String> visited, final Map<String, IOException> errors, final Phaser phaser) {
        phaser.register();
        downloaders.submit(() -> {
            try {
                final Document document = downloader.download(url);
                result.add(url);
                if (depth > 1) {
                    phaser.register();
                    extractors.submit(() -> {
                        List<String> list = new ArrayList<>();
                        try {
                            document.extractLinks().stream().filter(visited::add)
                                    .forEach(list::add);
                            list.forEach(link -> download(link, depth - 1, result, visited, errors, phaser));
                        } catch (final IOException e) {
                            errors.put(url, e);
                        } finally {
                            phaser.arrive();
                        }
                    });
                }
            } catch (final IOException e) {
                errors.put(url, e);
            } finally {
                phaser.arrive();
            }
        });
    }

    /**
     * Closes this web-crawler, relinquishing any allocated resources.
     */
    // :NOTE: Не дождались
    @Override
    public void close() {
        downloaders.shutdown();
        extractors.shutdown();
        try {
            if (!downloaders.awaitTermination(10, TimeUnit.SECONDS)) {
                downloaders.shutdownNow();
            }
            if  (!extractors.awaitTermination(10, TimeUnit.SECONDS)) {
                extractors.shutdownNow();
            }
        } catch (final InterruptedException ignored) {}
    }
}
