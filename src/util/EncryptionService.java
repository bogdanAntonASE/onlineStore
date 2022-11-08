package util;

public final class EncryptionService {

    public static String encrypt(char[] password) {
        for (int i = 0; i < password.length; i++) {
            password[i] += password.length;
        }

        return String.valueOf(password);
    }

    private EncryptionService() {}
}
