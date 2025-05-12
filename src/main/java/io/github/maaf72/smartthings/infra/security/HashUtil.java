package io.github.maaf72.smartthings.infra.security;

import com.password4j.AbstractHashingFunction;
import com.password4j.Argon2Function;
import com.password4j.Password;
import com.password4j.types.Argon2;

public class HashUtil {
  private static final AbstractHashingFunction HASH_FUNCTION = Argon2Function.getInstance(14, 20, 1, 32, Argon2.ID);

  public static String hash(String password) {
    return Password.hash(password).addRandomSalt(16).with(HASH_FUNCTION).getResult();
  }

  public static Boolean verify(String password, String hash) {
    return Password.check(password, hash).with(HASH_FUNCTION);
  }
}
