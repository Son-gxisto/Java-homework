package markup;
import java.util.List;
public class Emphasis extends AbstractMarks {
    public Emphasis(List<Marks> elements) {
        super(elements);
    }
	
	@Override
	String getHtml() {
		return "em";
	}
	
	@Override
	String getMarkdown() {
		return "*";
	}
}
