package info.kgeorgiy.ja.istratov.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {
    private final Queue<Runnable> elements;
    private final Queue<Thread> threads;

    public ParallelMapperImpl(final int maxThreadsCount) {
        if (maxThreadsCount < 1) {
            throw new IllegalArgumentException("Threads must be more than 0");
        }
        elements = new ArrayDeque<>();
        threads = new ArrayDeque<>();
        for (int i = 0; i < maxThreadsCount; i++) {
            Thread t = new Thread(() -> {
                    while (!Thread.interrupted()) {
                        try {
                            evaluateElement();
                        } catch (InterruptedException ignored) { break; }
                    }
            });
            threads.add(t);
            t.start();
        }
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        final List<R> result = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            result.add(null);
        }
        for (int i = 0; i < args.size(); i++) {
            final int finalI = i;
            addElement(() -> {
                result.set(finalI, f.apply(args.get(finalI)));
                synchronized (counter) {
                    counter.incrementAndGet();
                    counter.notify();
                }
            });
        }
        synchronized (counter) {
            while (counter.get() < args.size()) {
                counter.wait();
            }
        }
        return result;
    }

    @Override
    public void close() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    void addElement(Runnable e) {
        synchronized (elements) {
            elements.add(e);
            elements.notifyAll();
        }
    }

    void evaluateElement() throws InterruptedException {
        Runnable e;
        synchronized (elements) {
            while (elements.isEmpty()) {
                elements.wait();
            }
            e = elements.poll();
            elements.notifyAll();
        }
        e.run();
    }
}