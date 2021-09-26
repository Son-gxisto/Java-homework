package info.kgeorgiy.ja.istratov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPNonblockingServer implements HelloServer {
    private ExecutorService server, tasks;
    private Selector selector;

    @Override
    public void start(final int port, final int threads) {
        try {
            selector = Selector.open();
        } catch (final IOException e) {
            System.err.println("Server starting error:" + e.getMessage());
        }
        server = Executors.newSingleThreadExecutor();
        tasks = Executors.newFixedThreadPool(threads);
        //ByteBuffer buffer = ByteBuffer.allocate(1024);
        server.submit(() -> {
            try (final DatagramChannel channel = DatagramChannel.open()) {
                channel.bind(new InetSocketAddress(port));
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
                while (!Thread.interrupted()) {
                    selector.select(50);
                    for (final Iterator<SelectionKey> keys = selector.selectedKeys().iterator(); keys.hasNext();) {
                        final SelectionKey key = keys.next();
                        keys.remove();
                        //if (!key.isValid()) continue;
                        if (key.isReadable()) {
                            tasks.submit(() -> {
                                try {
                                    final DatagramChannel c = (DatagramChannel) key.channel();
                                    if (key.isReadable()) {
                                        ByteBuffer buffer;
                                        buffer = ByteBuffer.allocate(1024);
                                        final SocketAddress address = c.receive(buffer);
                                        buffer = ByteBuffer.wrap(("Hello, " + new String(
                                                buffer.array(), StandardCharsets.UTF_8
                                        )).trim().getBytes(StandardCharsets.UTF_8));
                                        c.send(buffer, address);
                                    }
                                } catch (final IOException e) {
                                    System.err.println(e.getMessage());
                                }
                            });
                        }
                    }
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void close() {
        server.shutdown();
        tasks.shutdown();
        try {
            while (!(server.awaitTermination(100, TimeUnit.MILLISECONDS) &&
            tasks.awaitTermination(100, TimeUnit.MILLISECONDS))) {
                server.shutdownNow();
                tasks.shutdownNow();
            }
        } catch (final InterruptedException ignored) {}
        try {
            selector.close();
        } catch (final IOException ignored) {}
    }
    public static void main(final String[] args) {
        mainImpl(args, new HelloUDPNonblockingServer());
    }
    public static void mainImpl(final String[] args, final HelloServer server) {
        if (args.length != 2) {
            System.err.println("Wrong number of arguments");
            return;
        }
        try {
            final int port = Integer.parseInt(args[0]);
            final int threads = Integer.parseInt(args[1]);
            server.start(port, threads);
        } catch (final Exception e) {
            System.err.println("Wrong number of arguments, usage: port threads");
        }
    }
}
