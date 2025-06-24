package org.duahifnv.filehosting.model.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.duahifnv.filehosting.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserEntityListener {
    private static final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
    private final PasswordEncoder passwordEncoder;

    @PrePersist
    @PreUpdate
    public void encodePassword(User user) {
        if (!isBCrypted(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }

    public static boolean isBCrypted(String potentialHash) {
        return BCRYPT_PATTERN.matcher(potentialHash).matches();
    }
}
