import java.io.*;
import java.util.Arrays;

public class ReverseTranspose {

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        int[][] matr = new int[1000_000][1];
        int[] size = new int[1000_000];
        int maxLength = 0;
        while (in.hasNextLine()) {
            int i = 0;
            Scanner line = new Scanner(in.nextLine());
            while (line.hasNextInt()) {
                if (size[i] >= matr[i].length) {
                    matr[i] = Arrays.copyOf(matr[i], (matr[i].length * 3) / 2 + 1);
                }
                matr[i][size[i++]++] = line.nextInt();
            }
            if (i > maxLength) {
                maxLength = i;
            }
        }
		
        for (int i = 0; i < maxLength; i++) {
            for (int j = 0; j < size[i]; j++) {
                System.out.print(matr[i][j] + " ");
            }
            System.out.println();
        }
    }
}