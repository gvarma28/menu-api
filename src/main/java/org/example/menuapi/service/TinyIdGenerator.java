package org.example.menuapi.service;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class TinyIdGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TINY_ID_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder sb = new StringBuilder(TINY_ID_LENGTH);
        for (int i = 0; i < TINY_ID_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
