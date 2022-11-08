package service;

import exceptions.InvalidAnswerException;
import java.util.regex.Pattern;

public class RegexValidator {

    private static final Pattern PRODUCT_NAME_DESCRIPTION_FORMAT = Pattern.compile("(?:[a-zA-Z_@./#&+'%}{|-][- ]?){3,}");
    private static final Pattern YES_NO_FORMAT = Pattern.compile("^(?:Yes|No|yes|no|y|Y|n|N)$");

    public static boolean handleYesNo(String answer) throws InvalidAnswerException {
        boolean matches = YES_NO_FORMAT.matcher(answer).matches();
        if (!matches) {
            throw new InvalidAnswerException("Invalid answer!");
        }

        return switch (answer) {
            case "Yes", "yes", "Y", "y" -> true;
            case "No", "no", "N", "n" -> false;
            default -> throw new InvalidAnswerException("You should answer with Yes/No/yes/no/y/Y/n/N");
        };
    }

    public static void checkProductNameOrDescription(String productNameOrDescription) throws InvalidAnswerException {
        boolean matches = PRODUCT_NAME_DESCRIPTION_FORMAT.matcher(productNameOrDescription).matches();
        if (!matches) {
            throw new InvalidAnswerException("Invalid product name!");
        }
    }
    private RegexValidator() {}
}
