import java.util.Arrays;
import java.io.*;

public class ReverseTransposeInt {
	public void add(int[] a, int x) {
		int t = a.length;
		if 
		a = Arrays.CopyOf(a, a.length*1.5 + 1);
		a[t] = x;
	}
    public static void main(String args[]) throws IOException {
        int[][] matr = new int[1000_000][5];
        int k, maxLineLength = 0;
        Scanner inpline = new Scanner(System.in);
        while (inpline.hasNextLine()) {
            k = 0;
            Scanner inp = new Scanner(inpline.nextLine());
            while (inp.hasNextInt()) {
                add(matr[k++],inp.nextInt());
            }
			if (k > maxLineLength) {
				maxLineLength = k;
			}
        }
		for (k = 0; k < maxLineLength; k++) {
			for (int g = 0; g < matr[k].length; g++) {
				System.out.print(matr[k][g] + " ");
			}
			System.out.println();
		}
        inpline.close();
    }
}