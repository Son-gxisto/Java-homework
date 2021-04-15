package markup;
 import java.util.List;

public class Strikeout extends AbstractMarks {
    public Strikeout(List<Marks> elements) {
        super(elements);
    }
	
	@Override
	String getHtml() {
		return "s";
	}
	
	@Override
	String getMarkdown() {
		return "~";
	}
}
