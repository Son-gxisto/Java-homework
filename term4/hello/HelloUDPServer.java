package info.kgeorgiy.ja.istratov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPServer implements HelloServer {
    private ExecutorService getter, senders;
    private DatagramSocket socket;


    public static void main(final String[] args) {
        HelloUDPNonblockingServer.mainImpl(args, new HelloUDPServer());
    }

    /**
     * Starts a new Hello server.
     * This method should return immediately.
     *
     * @param port    server port.
     * @param threads number of working threads.
     */
    @Override
    public void start(final int port, final int threads) {
        final int size;
        try {
            socket = new DatagramSocket(port);
            // :NOTE: Лишний поток
            getter = Executors.newSingleThreadExecutor();
            senders = Executors.newFixedThreadPool(Math.max(threads - 1, 1));
            size = socket.getReceiveBufferSize();
        } catch (final SocketException e) {
            System.err.println("Socket creating error:" + e.getMessage());
            return;
        }
        getter.submit(() -> {
            while (!socket.isClosed()) {
                try {
                    final DatagramPacket request = new DatagramPacket(new byte[size], size);
                    socket.receive(request);
                    final DatagramPacket response = new DatagramPacket(new byte[0], 0);
                    senders.submit(() -> {
                        try {
                            final String t = "Hello, " + new String(request.getData(),
                                    request.getOffset(),
                                    request.getLength(),
                                    StandardCharsets.UTF_8);
                            // :NOTE: Новый
                            response.setData(t.getBytes(StandardCharsets.UTF_8));
                            response.setSocketAddress(request.getSocketAddress());
                            socket.send(response);
                        } catch (final IOException e) {
                            System.err.println("sending IOException:" + e.getMessage());
                        }
                    });
                } catch (final SocketTimeoutException e) {
                    System.err.println("Socket timeout: " + e.getMessage());
                } catch (final PortUnreachableException e) {
                    System.err.println("Can't connect to " + port + e.getMessage());
                } catch (final IOException e) {
                    System.err.println("Socket IOException:" + e.getMessage());
                }
            }
        });
    }

    /**
     * Stops server and deallocates all resources
     */
    @Override
    public void close() {
        getter.shutdownNow();
        senders.shutdown();
        socket.close();
        try {
            if (!senders.awaitTermination(10, TimeUnit.SECONDS)) {
                senders.shutdownNow();
            }
        } catch (final InterruptedException ignored) {}
    }
}
