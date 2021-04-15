import java.util.List;
import java.util.ArrayList;

public class Wordff {
	private List<pair> noil = new ArrayList<pair>();
	public void add(int l, int n) {
		pair t = new pair(l, n);
		noil.add(t);
	}
	
	public String cout(int i) {
		return line + ':' + num;
	}
	
	public int getCount() {
		return noil.size();
	}
}