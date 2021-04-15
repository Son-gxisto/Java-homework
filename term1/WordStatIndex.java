import java.util.Map;
import java.io.*;
import java.lang.Character;
import java.util.Set;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.TreeMap;
public class WordStatLineIndex {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            return;
        }
        try {
            Scanner in = new Scanner(new File(args[0]));
            try {
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(new File(args[1])),
                                StandardCharsets.UTF_8
                        )
                );
				try {
					Map<String, ArrayList<Integer>> words = new TreeMap<>();
					String line = in.nextLine();
					int bright,bleft;
					bleft = 0;
					String word;
					int n = 0;
					while (line != null) {
						n++;
						int linenum = 0;
						while (bleft < line.length()) {
							while (bleft < line.length() &&
									!(Character.isLetter(line.charAt(bleft)) ||
											Character.getType(line.charAt(bleft)) == Character.DASH_PUNCTUATION ||
											line.charAt(bleft) == '\'')) {
								bleft++;
							}
							if (bleft < line.length()) {
								bright = 0;
								while (bleft + bright < line.length() &&
										(Character.isLetter(line.charAt(bleft + bright)) ||
												Character.getType(line.charAt(bleft + bright)) == Character.DASH_PUNCTUATION ||
												line.charAt(bleft + bright) == '\'')) {
									bright++;
								}
								word = line.substring(bleft, bleft + bright);
								bleft = bleft + bright;
								bright = 0;
								word = word.toLowerCase();
								ArrayList<Integer> t = words.get(word);
								if (t == null) {
									t = new ArrayList();
									t.add(0);	
								}
								t.set(0, t.get(0) + 1);
								t.add(n);
								t.add(++linenum);
								words.put(word, t);
							}
						}
						bleft = 0;
						line = in.nextLine();
					}
					for (Map.Entry<String, ArrayList<Integer>> elem : words.entrySet()) {
						out.write(elem.getKey() + " ");
						out.write(elem.getValue() + " "); //slovo count stroka:nomer stroka:nomer
						for(int g = 0; g < (elem.getValue().size() - 1)/2 - 1; g++) {
							out.write(elem.getValue().get(2*g+1) + ":" + elem.getValue().get(2*g+2) + " ");
						}
						out.write(elem.getValue().get(elem.getValue().size()-2) + ":" + elem.getValue().get(elem.getValue().size()-1) + "\n");
					}
				} finally {
					out.close();	
				}
            } catch (IOException e) {
                System.out.println("Give me file: " + e.getMessage());
            } finally {
				in.close();
			}
        } catch(UnsupportedEncodingException e) {
			System.out.println("Your charset is bad " + e.getMessage());
		} catch(IOException e) {
			System.out.println("Give me file " + e.getMessage());
		}
	}
}