package info.kgeorgiy.ja.istratov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

//java -cp . -p . -m info.kgeorgiy.java.advanced.hello client info.kgeorgiy.ja.istratov.hello.HelloUDPClient
//jav -cp . -p . -m info.kgeorgiy.java.advanced.hello server info.kgeorgiy.ja.istratov.hello.HelloUDPServer
public class HelloUDPClient implements HelloClient {
    public static void main(final String[] args) {
        HelloUDPNonblockingClient.mainImpl(args, new HelloUDPClient());
    }
    /**
     * Runs Hello client.
     * This method should return when all requests completed.
     *
     * @param host     server host
     * @param port     server port
     * @param prefix   request prefix
     * @param threads  number of request threads
     * @param requests number of requests per thread.
     */
    @Override
    public void run(final String host, final int port, final String prefix, final int threads, final int requests) {
        final InetSocketAddress address = new InetSocketAddress(host, port);
        final ExecutorService send = Executors.newFixedThreadPool(threads);
        // :NOTE: IntStream
        IntStream.range(0, threads).forEach(num ->
                send.submit(() -> {
                    try (final DatagramSocket socket = new DatagramSocket()) {
                        socket.setSoTimeout(200);
                        final int size = socket.getReceiveBufferSize();
                        final DatagramPacket response = new DatagramPacket(new byte[size], size);
                        final DatagramPacket packet = new DatagramPacket(new byte[0], 0, address);
                        for (int j = 0; j < requests; j++) {
                            final String t = prefix + num + "_" + j;
                            final byte[] request = t.getBytes(StandardCharsets.UTF_8);
                            // :NOTE: Новый?
                            packet.setData(request);
                            String res;
                            do {
                                try {
                                    socket.send(packet);
                                    socket.receive(response);
                                } catch (final SocketTimeoutException e) {
                                    System.err.println("Socket timeout: " + e.getMessage());
                                } catch (final PortUnreachableException e) {
                                    System.err.println("Can't connect to " + port + e.getMessage());
                                } catch (final IOException e) {
                                    System.err.println("some IOException:" + e.getMessage());
                                }
                                res = new String(response.getData(), response.getOffset(), response.getLength(), StandardCharsets.UTF_8);
                            } while (!res.contains(t));
                            //System.out.println(res);
                        }
                    } catch (final SocketException e) {
                        System.err.println("Socket creating error:" + e.getMessage());
                    }
                })
        );
        // :NOTE: Не дождались
        send.shutdown();
        try {
            if (!send.awaitTermination(20, TimeUnit.SECONDS)) {
                send.shutdownNow();
            }
        } catch (final InterruptedException ignored) {}
    }
}
