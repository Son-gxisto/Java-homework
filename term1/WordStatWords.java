import java.util.Map;
import java.io.*;
import java.lang.Character;
import java.util.Set;
import java.util.TreeMap;
import java.util.NavigableMap;
import java.nio.charset.StandardCharsets;

public class WordStatWords {
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
            Scanner in = new Scanner(new File(args[0]));
            try {
                while (in.hasNextLine()) {
					Scanner inLine = new Scanner(in.nextLine());
                    while (inLine.hasNextTWord()) {						
						String word = inLine.nextTWord().toLowerCase();
						int t = words.getOrDefault(word, 0);
						words.put(word, t + 1);
					}
                }

            } finally {
                in.close();
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("Charset error " + e.getMessage());
			return;
        } catch (IOException e) {
            System.out.println("Input error " + e.getMessage());
			return;
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
                    out.write(elem.getKey() + " " + elem.getValue());
					out.newLine();
                }
            } finally {
                out.close();
            }
        } catch (IOException e) {
            System.out.println("Output error " + e.getMessage());
        }
    }
}