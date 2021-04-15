import java.util.Map;
import java.io.*;
import java.lang.Character;
import java.util.Set;
import java.util.TreeMap;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

public class WordStatLineIndex {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Low arguments");
            return;
        }
        Map<String, Wordff> words = new TreeMap<>();
        try (Scanner in = new Scanner(new File(args[0]))){
			int n = 0;
			while (in.hasNextLine()) {
				n++;
				int linenum = 0;
				Scanner inLine = new Scanner(in.nextLine());
				while (inLine.hasNextTWord()) {
					String word = inLine.nextTWord();
					word = word.toLowerCase();
					Wordff t = words.get(word);
					if (t == null) {
						t = new Wordff();
					}
					t.add(n, ++linenum);
					words.put(word, t);
				}
			}
        }
		
        try (BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(new File(args[1])),
                            StandardCharsets.UTF_8
                    )))
			{
            try {
				for (Map.Entry<String, Wordff> elem : words.entrySet()) {
					out.write(elem.getKey() + ' ' + elem.getValue().getCount());
					for(int g = 0; g < elem.getValue().getCount(); g++) {
						out.write(' ' + elem.getValue().cout(g));
					}
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