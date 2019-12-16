package controlers;


import java.util.regex.Pattern;

public class Validator {

    private static final String REGEX_EMAIL = "";
    private static final String REGEX_TEXT_ONLY = "";
    private static final String REGEX_NUMBER_ONLY = "";
    private static final String REGEX_USERNAME = "";


    private Validator() {

    }

    public static boolean isValidEmail(String text) {
        final Pattern pattern = Pattern.compile(REGEX_EMAIL);
//        return pattern.matcher(text).matches();
        return true;
    }

    public static boolean isUsername(String text) {
        final Pattern pattern = Pattern.compile(REGEX_USERNAME);
//        return pattern.matcher(text).matches();
        return true;
    }

    public static boolean isTextOnly(String text) {
        final Pattern pattern = Pattern.compile(REGEX_TEXT_ONLY);
        return pattern.matcher(text).matches();
    }

    public static boolean isNumberOnly(String text) {
        final Pattern pattern = Pattern.compile(REGEX_NUMBER_ONLY);
        return pattern.matcher(text).matches();
    }


    /**
     * for future update
     */
    public static String generateHash(String string) {
        // hashing code
        return string;
    }


}
