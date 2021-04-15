import java.util.Arrays;
import java.io.*;

public class ReverseEven {
    public static void main(String args[]) throws IOException {
        int[][] matr = new int[1000_000][];
        int k;
        int t[] = new int[1000_000];
        int i = 0;
        Scanner inpline = new Scanner(System.in);
        while (inpline.hasNextLine()) {
            String line = inpline.nextLine();
            k = 0;
            Scanner inp = new Scanner(line);
            while (inp.hasNextInt()) {
                t[k++] = inp.nextInt();
            }
            matr[i++] = Arrays.copyOf(t, k);
        }
        for (int g = i - 1; g >= 0; g--) {
            for (k = matr[g].length - 1; k >= 0; k--) {
                if (matr[g][k] % 2 == 0) {
                    System.out.print(matr[g][k] + " ");
                }
            }
            System.out.println();
        }
        inpline.close();
    }
}