package markup;
import java.util.List;

public abstract class AbstractMarks implements Marks {
    private List<Marks> elements;
	
    public AbstractMarks(List<Marks> elements) {
        this.elements = elements;
    }
	
	protected abstract String getHtml();
	protected abstract String getMarkdown();	
	
	@Override
    public void toMarkdown(StringBuilder sb) {
		String markEl = getMarkdown();
        sb.append(markEl);
        for (Marks el : elements) {
            el.toMarkdown(sb);
        }
        sb.append(markEl);
    }
	
	@Override
	public void toHtml(StringBuilder sb) {
		String markEl = getHtml();
		sb.append("<").append(markEl).append(">");
		for (Marks el : elements) {
			el.toHtml(sb);
		}
		sb.append("</").append(markEl).append(">");
	}

}
