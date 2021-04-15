package markup;
import java.util.List;
public class Strong extends AbstractMarks {
    public Strong(List<Marks> elements) {
        super(elements);
    }
	@Override
	String getHtml() {
		return "strong";
	}
	
	@Override
	String getMarkdown() {
		return "__";
	}
}
