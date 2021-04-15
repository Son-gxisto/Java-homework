import java.io.*;
import java.nio.charset.StandardCharsets;

public class Scanner {
    private BufferedReader input;
    private String thatString;
    private boolean hasThatString = false;
    private int leftLineBorder;
    private String word;
	private boolean ScannerIsString;	
	
    public Scanner(InputStream object) {
        input = new BufferedReader(new InputStreamReader(object));
    }

    public Scanner(String line) {
        thatString = line;
        hasThatString = true;
		ScannerIsString = true;
    }

    public Scanner(File object) throws IOException {
        input = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(object),
                        "utf8"
                )
        );
    }	
	
    private static boolean isGoodSymbol(char c) {
        return Character.isLetter(c) ||
                Character.getType(c) == Character.DASH_PUNCTUATION ||
                c == '\'';
    }

	private void initLine() throws IOException {
        if (!hasThatString) {
            hasThatString = true;
            thatString = input.readLine();
            leftLineBorder = 0;
        }
    }
	
    private void nextWord() throws IOException {
        initLine();
        int num = leftLineBorder;
        while (num < thatString.length() && Character.isWhitespace(thatString.charAt(num))) {
            num++;
        }
        int rightWordBorder = num;
        while (rightWordBorder < thatString.length() && !Character.isWhitespace(thatString.charAt(rightWordBorder))) {
            rightWordBorder++;
        }
        leftLineBorder = rightWordBorder;
        word = thatString.substring(num, rightWordBorder);
    }

    public boolean hasNextLine() throws IOException {
        if (hasThatString) {
            return hasThatString;
        }
        thatString = input.readLine();
        if (thatString != null) {
            hasThatString = true;
            leftLineBorder = 0;
            return hasThatString;
        }
        return false;
    }

    public boolean hasNextInt() throws IOException {
        initLine();
        int num = leftLineBorder;
        while (num < thatString.length() && Character.isWhitespace(thatString.charAt(num))) {
            num++;
        }
        if (num == thatString.length() || !(thatString.charAt(num) == '+' || thatString.charAt(num) == '-' || Character.isDigit(thatString.charAt(num)))) {
            return false;
        }
		if (thatString.charAt(num) == '-' || thatString.charAt(num) == '+') {
			num++;
		}
        while (num < thatString.length() && Character.isDigit(thatString.charAt(num))) {
            num++;
        }
        if (num != thatString.length() && !Character.isWhitespace(thatString.charAt(num))) {
            return false;
        }
        return true;
    }

    public String nextLine() throws IOException {
        if (hasThatString) {
            hasThatString = false;
            int t = leftLineBorder;
            leftLineBorder = 0;
            return thatString.substring(t, thatString.length());
        }
        return input.readLine();
    }

	public String nextTWord() throws IOException {
		initLine();
		int num = leftLineBorder;
		while (num < thatString.length() &&
				!isGoodSymbol(thatString.charAt(num))) {
			num++;
		}
		if (num < thatString.length()) {
			int bright = num;
			while (bright < thatString.length() &&
					isGoodSymbol(thatString.charAt(bright))) {
				bright++;
			}
			leftLineBorder = bright;		
		}
		return thatString.substring(num, leftLineBorder);
	}
	
	public boolean hasNextTWord() throws IOException {
		initLine();
		int num = leftLineBorder;
		while (num < thatString.length() &&
				!isGoodSymbol(thatString.charAt(num))) {
			num++;
		}
		hasThatString = num != thatString.length() && isGoodSymbol(thatString.charAt(num));
		return hasThatString;
	}
	
/*	public boolean hasNextWord() throws IOException {
		initLine();
		int num = leftLineBorder;
		while (num < thatString.length() && Character.isWhitespace(thatString.charAt(num))) {
			num++;
		}
		if (num == thatString.length()) {
			return false;
		} else {
			return true;
		}
	} */

    public int nextInt() throws IOException {
        nextWord();
        return Integer.parseInt(word);
    }

    public void close() throws IOException {
		if (!ScannerIsString) {
			input.close();
		}
    }
}
