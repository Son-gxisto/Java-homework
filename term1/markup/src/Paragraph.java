package markup;
import java.util.List;

public class Paragraph implements Marks{
    private List<Marks> elements;

    public Paragraph(List<Marks> elements) {
        this.elements = elements;
    }

    public void toMarkdown(StringBuilder sb) {
        for (Marks el : elements) {
            el.toMarkdown(sb);
        }
    }
	
	public void toHtml(StringBuilder sb) {
		for (Marks el : elements) {
			el.toHtml(sb);
		}
	}
}
