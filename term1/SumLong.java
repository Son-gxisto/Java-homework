public class SumLong {
    public static void main(String args[]) {
    int num,k;
    long sum = 0;
    for (int i = 0; i < args.length; i++) {
        num = 0;
        k = 0; 
        while (num < args[i].length()) {
            while(num < args[i].length() && Character.isWhitespace(args[i].charAt(num))){
                num++;
            }
            if (num < args[i].length()) {
                k = 0;
             	while (num + k <args[i].length() && !Character.isWhitespace(args[i].charAt(num + k))){
                    k++;
            	}    
                sum = sum + Long.parseLong(args[i].substring(num, num + k));
                num = num + k + 1;
            }
        }
    }
    System.out.print(sum);
    }
}