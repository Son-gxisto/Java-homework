package info.kgeorgiy.ja.istratov.walk;
import java.io.*;
import java.nio.Buffer;
import java.nio.file.*;
import java.util.Arrays;

public class Walk {
    private static final int BUFFER_SIZE = 8192;

    // :NOTE: \n
    public static void print(String str) {
        // :NOTE: for exceptions we should use System.err
        System.err.print(str + System.lineSeparator());
    }

    public static long evaluatePJW(String path) throws IOException {
        // :NOTE: 8192 should be const
        byte[] bytes = new byte[BUFFER_SIZE];
        long hash = 0;
        int length;
        try {
            FileInputStream inputStream = new FileInputStream(path);
            while ((length = inputStream.read(bytes)) != -1) {
                for (int i = 0; i < length; i++) {
                    hash = (hash << 8) + (bytes[i] & 0xff);
                    final long high = hash & 0xff00_0000_0000_0000L;
                    if (high != 0) {
                        hash = (hash ^ (high >> 48)) & ~high;
                    }
                }
            }
            // :NOTE: ignored message
        } catch (IOException e) {
            print("Error while evaluating hash:" + e.getMessage());
            hash = 0;
        }
        return hash;
    }

    public static void main(String[] args) {
        // :NOTE: should be final
        final Path inPath, outPath;
        if (args != null && args.length == 2 && args[0] != null && args[1] != null) {
            try {
                inPath = Paths.get(args[0]);
            } catch (InvalidPathException | SecurityException e) {
                print("Wrong name for input file: " + args[0]);
                return;
            }
            try {
                outPath = Paths.get(args[1]);
            } catch (InvalidPathException | SecurityException e) {
                print("Wrong name for output file: " + args[1]);
                return;
            }
        } else {
            print("Wrong number of arguments, usage: java Walk inputPath outputPath");
            return;
        }
        if (outPath.getParent() != null) {
            try {
                Files.createDirectories(outPath.getParent());
            } catch (IOException e) {
                print("error while creating output dir:" + e.getMessage());
                return;
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(inPath)) {
            try (BufferedWriter writer = Files.newBufferedWriter(outPath)) {
                String name;
                while ((name = reader.readLine()) != null) {
                    try {
                        long hash = evaluatePJW(name);
                        writer.write(String.format("%016x", hash) + " " + name);
                        writer.newLine();
                    } catch (AccessDeniedException | SecurityException e) {
                        print("Access denied while writing:" + name);
                    } catch (IOException e) {
                        print("some IOException:" + e.getCause() + e.getMessage());
                    }
                }
            } catch (NoSuchFileException e) {
                print("No such file:" + outPath.getFileName() + e.getMessage());
            } catch (AccessDeniedException | SecurityException e) {
                print("Access denied for:" + outPath.getFileName() + e.getMessage());
            } catch (IOException e) {
                print("Something wrong with output file:" + e.getClass().getName());
            }
        } catch (NoSuchFileException e) {
            print("No such file:" + inPath.getFileName() + e.getMessage());
        } catch (AccessDeniedException | SecurityException e) {
            print("Access denied for:" + inPath.getFileName() + e.getMessage());
            // :NOTE: one catch for two cases
        } catch (IOException e) {
            print("Something wrong with input or output file:" + e.getClass().getName());
        }
    }
}