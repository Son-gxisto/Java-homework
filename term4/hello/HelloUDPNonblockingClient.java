package info.kgeorgiy.ja.istratov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.*;

//java -cp . -p . -m info.kgeorgiy.java.advanced.hello client info.kgeorgiy.ja.istratov.hello.HelloUDPNonblockingClient
//java -cp . -p . -m info.kgeorgiy.java.advanced.hello server info.kgeorgiy.ja.istratov.hello.HelloUDPNonblockingServer
public class HelloUDPNonblockingClient implements HelloClient {
    // :NOTE: Констранта
    //private static final int BUFFER_SIZE = 65000;
    public static final int TIMEOUT = 128;

    @Override
    public void run(final String host, final int port, final String prefix, final int threads, final int requests) {
        try (final Selector selector = Selector.open()) {
            final Map<DatagramChannel, ChannelInfo> channels = new HashMap<>();
            final InetSocketAddress address = new InetSocketAddress(host, port);
            for (int i = 0; i < threads; i++) {
                final DatagramChannel channel = DatagramChannel.open();
                channel.connect(address);
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_WRITE);
                channels.put(channel, new ChannelInfo(i, 0));
            }
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int threadsCompleted = 0;
            while (!Thread.interrupted()) {
                selector.select(TIMEOUT);
                // :NOTE: selector.selectedKeys().isEmpty()
                if (selector.selectedKeys().isEmpty()) {
                    for (final SelectionKey k : selector.keys()) {
                        k.interestOps(SelectionKey.OP_WRITE);
                    }
                }
                for (final Iterator<SelectionKey> keys = selector.selectedKeys().iterator(); keys.hasNext();) {
                    try {
                        final SelectionKey key = keys.next();
                        if (key.isReadable()) {
                            final DatagramChannel c = (DatagramChannel) key.channel();
                            ChannelInfo channelInfo = channels.get(c); // :NOTE: Производительность
                            //final ByteBuffer buffer = ByteBuffer.allocate(c.socket().getReceiveBufferSize());
                            c.receive(buffer);
                            final String response = new String(buffer.array(), StandardCharsets.UTF_8).trim();
                            if (!response.contains(
                                    createRequest(prefix, channelInfo.getNum(), channelInfo.getCount()))) {
                                continue;
                            }
                            if (channelInfo.getAdd() >= requests) {
                                threadsCompleted++;
                            }
                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                        if (threadsCompleted == threads) {
                            // :NOTE: finally
                            closeChannels(channels);
                            return;
                        }
                        if (key.isWritable()) {
                            final DatagramChannel c = (DatagramChannel) key.channel();
                            ChannelInfo channelInfo = channels.get(c);
                            final int i = channelInfo.getNum();
                            final int count = channelInfo.getCount();
                            key.interestOps(SelectionKey.OP_READ);
                            if (count > requests - 1) {
                                break;
                            }
                            final String request = createRequest(prefix, i, count);
                            c.write(ByteBuffer.wrap(request.getBytes(StandardCharsets.UTF_8)));
                        }
                    } finally {
                        keys.remove();
                    }
                }
            }
            // :NOTE: ??
            closeChannels(channels);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
    private void closeChannels(Map<DatagramChannel, ChannelInfo> channels) throws IOException {
        for (final DatagramChannel c : channels.keySet()) {
            c.close();
        }
    }
    public String createRequest(final String prefix, final int ind, final int num) {
        return prefix + ind + "_" + num;
    }
    public static void main(final String[] args) {
        mainImpl(args, new HelloUDPNonblockingClient());
    }
    public static void mainImpl(final String[] args, final HelloClient client) {
        if (args.length != 5) {
            System.err.println("Wrong number of arguments");
            return;
        }
        try {
            final String host = args[0];
            final int port = Integer.parseInt(args[1]);
            final String prefix = args[2];
            final int threads = Integer.parseInt(args[3]);
            final int requests = Integer.parseInt(args[4]);
            client.run(host, port, prefix, threads, requests);
        } catch (final Exception e) {
            System.err.println("Usage: host port prefix threadsCount requestsCount; " + e.getMessage());
        }
    }
    private static class ChannelInfo {
        private final int num;
        private int count;
        ChannelInfo(final int num, int count) {
            this.num = num;
            this.count = count;
        }

        public int getNum() {
            return num;
        }

        public int getCount() {
            return count;
        }

        public int getAdd() {
            return ++this.count;
        }
    }
}
