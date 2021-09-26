package info.kgeorgiy.ja.istratov.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class IterativeParallelism implements info.kgeorgiy.java.advanced.concurrent.ScalarIP {
    // java -cp . -p . -m info.kgeorgiy.java.advanced.concurrent scalar info.kgeorgiy.ja.istratov.concurrent.IterativeParallelism

    // java -cp . -p . -m info.kgeorgiy.java.advanced.mapper scalar info.kgeorgiy.ja.istratov.concurrent.IterativeParallelism

    private final ParallelMapper mapper;
    public IterativeParallelism() {
        mapper = null;
    }
    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }
    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return multiThreads(threads, values,
                stream -> stream.max(comparator).orElseThrow(),
                stream -> stream.max(comparator).orElseThrow());
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, Collections.reverseOrder(comparator));
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return multiThreads(threads, values,
                stream -> stream.allMatch(predicate),
                stream -> stream.allMatch(bool -> bool));
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return multiThreads(threads, values,
                stream -> stream.anyMatch(predicate),
                stream -> stream.anyMatch(bool -> bool));
    }

    private int getNum(int i, int size, int mod) {
        return (i * size) + Math.min(i, mod);
    }

    private <T, R> R multiThreads(final int threadsCount, List<? extends T> values,
                                  Function<Stream<? extends T>, R> function,
                                  Function<Stream<? extends R>, R> resultCollector) throws InterruptedException {
        if (threadsCount < 1) {
            throw new IllegalArgumentException("Threads must be more than 0");
        }
        if (values == null || values.size() == 0) {
            throw new IllegalArgumentException("values must be not null and more than 0");
        }
        final int threads = Math.min(threadsCount, values.size());
        List<Stream<? extends T>> subLists = new ArrayList<>();
        List<Thread> threadList = new ArrayList<>();
        List<R> result = new ArrayList<>();
        int mod = values.size() % threads;
        final int size = values.size() / threads;
        for (int i = 0; i < threads; i++) {
            final int l = getNum(i, size, mod);
            final int r = getNum(i + 1, size, mod);
            subLists.add(values.subList(l, r).stream());
        }
        if (mapper != null) {
            return resultCollector.apply(mapper.map(function, subLists).stream());
        } else {
            for (int i = 0; i < threads; i++) {
                result.add(null);
            }
            for (int i = 0; i < threads; i++) {
                final int finalI = i;
                Thread thread = new Thread(() -> result.set(
                        finalI, function.apply(
                                subLists.get(finalI))));
                thread.start();
                threadList.add(thread);
            }
            for (Thread t : threadList) {
                t.join();
            }
        }
        return resultCollector.apply(result.stream());
    }
    public static void main(String []args) {
        int size = 54 / 7;
        int mod = 54 % 7;
        IterativeParallelism iterativeParallelism = new IterativeParallelism();
        for (int i = 0; i < 20; i++) {
            int a = iterativeParallelism.getNum(i, size, mod);
            int b = iterativeParallelism.getNum(i + 1, size, mod);
            System.out.println("i = " + i + "; f = " + a + "; r - l = " + (b - a));
        }
        try {
            ParallelMapper mapper = new ParallelMapperImpl(5);
            System.out.println(new IterativeParallelism(mapper).maximum(5, List.of(3, 5, 1, 2, 4), Comparator.naturalOrder()));
            mapper.close();
        } catch (InterruptedException ignored) {}
    }
}
