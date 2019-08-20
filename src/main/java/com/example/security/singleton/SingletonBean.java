package com.example.security.singleton;

import com.example.security.SecurityExampleApplication;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.lang.JoseException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
@Scope("singleton")
public class SingletonBean {

    private static List<JsonWebKey> jsonWebKeys;

    public SingletonBean() {
        System.out.println("********SingletonBean**********");
        jsonWebKeys = new ArrayList();
        for (int i = 0; i < 3; i++) {
            JsonWebKey jsonWebKey = null;
            try {
                int kid = i;
                jsonWebKey = RsaJwkGenerator.generateJwk(2048);
                jsonWebKey.setKeyId(String.valueOf(kid));
                jsonWebKeys.add(jsonWebKey);
                System.out.println("JsonWebKeys number : " + i + " generate");
            } catch (JoseException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<JsonWebKey> getJsonWebKeys(){
        return jsonWebKeys;
    }
}
