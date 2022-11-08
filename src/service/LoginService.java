package service;

import exceptions.UserNotFoundException;
import exceptions.WrongPasswordException;
import models.User;

import java.util.List;
import java.util.Optional;

public class LoginService {

    public static User checkIfUserPresent(List<User> knownUsers, String userName) {
        Optional<User> user = knownUsers.stream()
                .filter(currentUser -> currentUser.getUserName().equals(userName))
                .findAny();

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Username %s not found in database.", userName));
        }
        return user.get();
    }
    public static void checkIfPasswordCorrect(User user, String password) {
        if (!user.getPassword().equals(password)) {
            throw new WrongPasswordException(String.format("Wrong password for user %s.", user.getUserName()));
        }
    }

    private LoginService() {}
}
