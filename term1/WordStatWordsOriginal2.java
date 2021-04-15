import java.util.Map;
import java.io.*;
import java.lang.Character;
import java.util.Set;
import java.util.TreeMap;
import java.util.NavigableMap;
import java.nio.charset.StandardCharsets;

public class WordStatWordsOriginal2 {
    public static boolean isGoodSymbol(char c) {
        return Character.isLetter(c) ||
                Character.getType(c) == Character.DASH_PUNCTUATION ||
                c == '\'';
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Low arguments");
            return;
        }
        NavigableMap<String, Integer> words = new TreeMap<>();
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(args[0])),
                            StandardCharsets.UTF_8
                    )
            );
            try {
                String line = in.readLine();
                while (line != null) {
                    int bleft = 0;
                    while (bleft < line.length()) {
                        while (bleft < line.length() &&
                                !isGoodSymbol(line.charAt(bleft))) {
                            bleft++;
                        }
                        if (bleft < line.length()) {
                            int bright = 0;
                            while (bleft + bright < line.length() &&
                                    isGoodSymbol(line.charAt(bleft + bright))) {
                                bright++;
                            }
                            String word = line.substring(bleft, bleft + bright);
                            bleft = bleft + bright;
                            word = word.toLowerCase();
                            int t = words.getOrDefault(word, 0);
                            words.put(word, t + 1);
                        }
                    }
                    line = in.readLine();
                }

            } finally {
                in.close();
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("Charset error " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Input error " + e.getMessage());
        }
        try {
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(new File(args[1])),
                            StandardCharsets.UTF_8
                    )
            );
            try {
                for (Map.Entry<String, Integer> elem : words.entrySet()) {
                    out.write(elem.getKey() + " " + elem.getValue() + "\n");
                }
            } finally {
                out.close();
            }
        } catch (IOException e) {
            System.out.println("Output error " + e.getMessage());
        }
    }
}