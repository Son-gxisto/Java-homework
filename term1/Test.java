import java.io.*;
public class Test {
	public static void main(String args[]) throws IOException {
		Scanner in = new Scanner(System.in);
		int a = 0;
		while (in.hasNextInt()) {
			a += in.nextInt();
		}
		System.out.println(a);
	}
}