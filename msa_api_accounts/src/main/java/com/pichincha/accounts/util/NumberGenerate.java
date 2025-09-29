package com.pichincha.accounts.util;

import java.security.SecureRandom;

public class NumberGenerate {
    private static final SecureRandom random = new SecureRandom();
    private static final int ACCOUNT_NUMBER_LENGHT = 6;
    private static final int CLIENT_ID_LENGTH = 8;

    public static String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder(ACCOUNT_NUMBER_LENGHT);
        for (int i = 0; i < ACCOUNT_NUMBER_LENGHT; i++) {
            int digit = random.nextInt(10);
            accountNumber.append(digit);
        }
        return accountNumber.toString();
    }

    public static String generateClientId() {
        StringBuilder clientId = new StringBuilder(CLIENT_ID_LENGTH);
        for (int i = 0; i < CLIENT_ID_LENGTH; i++) {
            int digit = random.nextInt(10);
            clientId.append(digit);
        }
        return clientId.toString();
    }
}
