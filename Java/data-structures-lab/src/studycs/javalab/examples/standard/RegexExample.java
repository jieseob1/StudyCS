package studycs.javalab.examples.standard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexExample {
    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");

    private RegexExample() {
    }

    public static List<String> extractEmails(String text) {
        Matcher matcher = EMAIL.matcher(text);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    public static void runDemo() {
        System.out.println("Pattern/Matcher extractEmails: " + extractEmails("mail dev@test.com and admin@test.com"));
    }
}
