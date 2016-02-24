package se.alkohest.irkksome.util;

import java.io.IOException;
import java.security.Key;

public class KeyProvider {
    private static KeyProvider instance;

    private final String pubkey;
    private final char[] privkey;

    public static void initialize(Key pubkey, Key privkey) throws IOException {
        instance = new KeyProvider(KeyEncodingUtil.encodePublicKey(pubkey), KeyEncodingUtil.encodePrivateKey(privkey));
    }

    public static boolean hasKeys() {
        return instance != null && getPubkey() != null && getPrivkey() != null;
    }

    public static String getPubkey() {
        return instance.pubkey;
    }

    public static char[] getPrivkey() {
        return instance.privkey;
    }

    private KeyProvider(String pubkey, char[] privkey) {
        this.pubkey = pubkey;
        this.privkey = privkey;
    }
}
