package com.example.security.tools;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import java.util.Random;

public interface ITools {

    //return password hashé
    public default String getStringSha3(String password) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
        byte[] digest = digestSHA3.digest(password.getBytes());
        return Hex.toHexString(digest);
    }

    public default int generateRandmoKid() {
        Random rand = new Random();
        int randomKid = rand.nextInt(2);
        return randomKid;

    }
}
