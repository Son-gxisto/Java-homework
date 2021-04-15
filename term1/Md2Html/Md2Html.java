package md2html;

import java.io.*;
import java.lang.StringBuilder;
import java.lang.String;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Md2Html {
    private static final Map<String, String> m2h = Map.of(
            "*", "em",
            "_", "em",
            "__", "strong",
            "**", "strong",
            "`", "code",
            "--", "s",
            "++", "u",
            "<", "&lt;",
            ">", "&gt;",
            "&", "&amp;"
    );

    private final static int TAG_MAX_LENGTH = 2;

    public static void initOpenTagList(Map<String, Integer> t) {
        int k = -2;
        t.put("*", k);
        t.put("**", k);
        t.put("_", k);
        t.put("__", k);
        t.put("`", k);
        t.put("--", k);
        t.put("++", k);
    }

    public static int getHeaderNum(String t) {
        int num = 0;
        char c = t.charAt(num);
        while (c == '#' && num < t.length() - 1) {
            num++;
            c = t.charAt(num);
        }
        if (c == ' ') {
            return num;
        }
        return 0;
    }

    public static String getHeaderNumOrParagraph(StringBuilder t, int num) {
        if (num != 0 && num != t.length() && t.charAt(num) == ' ') {
            return "h" + Integer.toString(num);
        } else {
            return "p";
        }
    }

    public static String getOpenTag(String t) {
        return "<" + t + ">";
    }

    public static String getClosedTag(String t) {
        return "</" + t + ">";
    }

    public static boolean isVoid(String s) {
        return s == null || s.isEmpty();
    }

    public static void main(String[] args) throws IOException {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(args[0]),
                                "utf8"
                        )
                );
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(args[1]),
                                "utf8"
                        )
                )
        ) {
            String t = in.readLine();
            while (t != null && t.isEmpty()) { // говно, переделывай
                t = in.readLine();
            }
            while (t != null) {
                StringBuilder buf = new StringBuilder();
                while (!isVoid(t)) { //чтение абзаца
                    buf.append(t);
                    t = in.readLine();
                    if (!isVoid(t)) {
                        buf.append('\n');
                    }
                }
                int i = getHeaderNum(buf.toString()); //i - отступ слева(число, если абзац- заголовок, иначе - 0)
                String h = getHeaderNumOrParagraph(buf, i); //Записали заголовок, добавим его в начале и в конце буфера
                List<StringBuilder> inHtml = new ArrayList<StringBuilder>(); // буфер для перевода строки из markdown в html
                inHtml.add(new StringBuilder(getOpenTag(h))); //добавили тег заголовка\параграфа в начало
                Map<String, Integer> OpenTagNumList = new HashMap<>();//Лист элементов с номерами открывающих (или одиночных открывающих) тегов
                initOpenTagList(OpenTagNumList);
                if (!h.equals("p")) { //если абзац-заголовок, после # идет лишний пробел
                    i++; //(несколько пробелов не оговаривалось, легко заменить на пропуск всех пробелов)
                }
                while (i < buf.length()) {
                    if (i < buf.length()-1 && buf.charAt(i) == '\\') { // несколько экранированных символов подряд \*\*\*
                        i++;
                        inHtml.get(inHtml.size() - 1).append(buf.charAt(i));
                        i++;
                        continue;
                    }
                    String tag = "";
                    boolean isTag = false;
                    for (int j = TAG_MAX_LENGTH; j > 0; j--) {
                        if (i + j > buf.length()) {
                            continue;
                        }
                        tag = buf.substring(i, i + j);
                        if (m2h.containsKey(tag)) { //Проверка, что это символ разметки
                            int k = OpenTagNumList.getOrDefault(tag, -1); //Проверка, что это не особый символ (<,>)
                            if (k == -1) {
                                inHtml.get(inHtml.size() - 1).append(m2h.get(tag)); //какая-то фигня
                            } else if (k == -2) { //Если к=-2, то мы встретили открывающий тег, запоминаем его позицию
                                OpenTagNumList.put(tag, inHtml.size() - 1);
                                inHtml.add(new StringBuilder());
                            } else {
                                inHtml.get(k).append(getOpenTag(m2h.get(tag)));
                                inHtml.get(inHtml.size() - 1).append(getClosedTag(m2h.get(tag)));
                                OpenTagNumList.put(tag, -2);
                            }

                            isTag = true;
                            break;
                        }

                    }
                    if (!isTag) {
                        inHtml.get(inHtml.size()-1).append(buf.charAt(i));
                        i++;
                    } else {
                        i+=tag.length();
                    }
                }
                inHtml.get(inHtml.size() - 1).append(getClosedTag(h));
                for (Map.Entry<String, Integer> elem : OpenTagNumList.entrySet()) {
                    if (elem.getValue() != -2) {
                        inHtml.get(elem.getValue()).append(elem.getKey());
                    }
                }
                for (StringBuilder stringBuilder : inHtml) {
                    out.write(stringBuilder.toString());
                    System.out.print(stringBuilder.toString());
                }
                System.out.println();
                out.newLine();
                while (t != null && t.equals("")) {
                    t = in.readLine();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Input or Output not found" + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encoding exception" + e.getMessage());
        } catch (IOException e) {
            System.out.println("Input or output error" + e.getMessage());
        }
    }
}