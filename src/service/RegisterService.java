package service;

import exceptions.RegistrationException;
import models.User;
import util.EncryptionService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class RegisterService {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");
    private static final Pattern USER_PATTERN = Pattern.compile("[a-zA-Z]{3,}");

    public static User pickUserName(Scanner scanner, List<User> knownUsers) {
        System.out.println("Please choose an username (at least 3 characters):");

        String userName = scanner.next();
        Optional<User> user;

        User result = new User();

        int noOfTries = 3;
        int k = 0;

        while (k++ < noOfTries) {
            if (!USER_PATTERN.matcher(userName).matches()) {
                System.out.println("Your username does not have at least 3 characters... Try again " +
                        "(try " + k + " out of " + noOfTries + "):");
                userName = scanner.next();
                continue;
            }

            String finalUserName = userName;
            user = knownUsers.stream()
                    .filter(currentUser -> currentUser.getUserName().equals(finalUserName))
                    .findAny();

            if (user.isEmpty()) {
                result.setUserName(userName);
                break;
            } else {
                System.out.println("This username is already taken, please choose another one. " +
                        "(try " + k + " out of " + noOfTries + "):");
                userName = scanner.next();
            }
        }

        return !result.getUserName().isEmpty() ? result : null;
    }

    public static void pickPassword(Scanner scanner, User user) throws RegistrationException {
        System.out.println("Please choose a password. At least 8 chars (one number and a capital letter).");
        //Console console = System.console();

        int noOfTries = 3;
        int k = 0;

        //String password = String.valueOf(console.readPassword());
        String password = scanner.next();
        while (k++ < noOfTries) {
            if (!PASSWORD_PATTERN.matcher(password).matches()) {
                System.out.println("Wrong format. At least 8 chars (one number and a capital letter)." +
                        " (try " + k + " out of " + noOfTries + ")");
                password = scanner.next();
            } else {
                String encryptedPassword = EncryptionService.encrypt(password.toCharArray());
                user.setPassword(encryptedPassword);
                break;
            }
        }

        if (k == noOfTries + 1 && user.getPassword().isEmpty()) {
            throw new RegistrationException("You failed to pick a correct password," +
                    " respectively at least 8 chars (one number and a capital letter).");
        }
    }

    private RegisterService() {}

    public static boolean changePassword(Scanner scanner, User user) throws RegistrationException {
        System.out.println("Please introduce your password again:");
        String oldPassword = scanner.next();

        if (EncryptionService.encrypt(oldPassword.toCharArray()).equals(user.getPassword())) {
            System.out.println("Please type in your new password:");
            pickPassword(scanner, user);
            return true;
        } else {
            return false;
        }
    }
}
