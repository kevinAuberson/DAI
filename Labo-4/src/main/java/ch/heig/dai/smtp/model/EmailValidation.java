package ch.heig.dai.smtp.model;

import java.util.regex.*;

/**
 * @author Kevin Auberson, Adrian Rogner
 * @version 1.0
 * @file EmailValidation.java
 * @brief Represents a simple email validation utility.
 * @date 2020-03-25
 * <p>
 * The EmailValidation class provides functionality to validate an email address
 * using a regular expression pattern.
 * </p>
 */
public class EmailValidation {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+$&*-]+(?:\\.[a-zA-Z0-9_+$&*-]+)*" +
            "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Checks whether the provided email is valid or not.
     *
     * @param email The email address to be validated.
     * @return True if the email is valid, false otherwise.
     */
    public static boolean isValid(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}