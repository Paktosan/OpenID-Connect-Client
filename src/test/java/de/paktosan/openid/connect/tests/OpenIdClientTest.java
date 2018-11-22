package de.paktosan.openid.connect.tests;


import de.paktosan.openid.connect.Display;
import de.paktosan.openid.connect.OpenIdClient;
import de.paktosan.openid.connect.Prompt;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OpenIdClientTest {

    @Test
    public void testCreation() {
        assertThrows(IllegalArgumentException.class, () -> new OpenIdClient(null, "something", "something"));
        assertThrows(IllegalArgumentException.class, () -> new OpenIdClient("something", null, "something"));
        assertThrows(IllegalArgumentException.class, () -> new OpenIdClient("something", "something", null));
        assertThrows(IllegalArgumentException.class, () -> new OpenIdClient("", "something", "something"));
        assertThrows(IllegalArgumentException.class, () -> new OpenIdClient("something", "", "something"));
        assertThrows(IllegalArgumentException.class, () -> new OpenIdClient("something", "something", ""));
        OpenIdClient client = new OpenIdClient("indeed", "something", "great");
        assertEquals("indeed", client.getSecurityEndpoint());
        assertEquals("something", client.getClientId());
        assertEquals("great", client.getRedirectURL());
    }

    @Test
    public void testSetters() {
        /*OpenIdClient client = new OpenIdClient("very", "much");
        client.setSecurityEndpoint("something");
        client.setClientId("great");
        assertEquals("something", client.getSecurityEndpoint());
        assertEquals("great", client.getClientId());*/
        OpenIdClient client = new OpenIdClient("loads", "very", "much");
        client.setSecurityEndpoint("much");
        client.setClientId("loads");
        client.setRedirectURL("very");
        assertEquals("loads", client.getClientId());
        assertEquals("very", client.getRedirectURL());
        assertEquals("much", client.getSecurityEndpoint());
        assertEquals("much", client.getSecurityTarget().getUri().toString());
    }

    @Test
    public void testInvalidScopes() {
        OpenIdClient client = new OpenIdClient("very", "something", "great");
        List<String> scopes = new ArrayList<>();
        scopes.add("email");
        assertThrows(IllegalArgumentException.class, () -> client.getAuthenticationRequestURL(scopes,
                null, null, null, null, 0, null, null, null,
                null, null));
    }

    @Test
    public void testMinimalArguments() {
        OpenIdClient client = new OpenIdClient("http://example.org", "great", "http://localhost/back");
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("email");
        assertEquals("http://example.org/authorize?response_type=code&client_id=great&scope=openid+email&redirect_uri=http%3A%2F%2Flocalhost%2Fback",
                client.getAuthenticationRequestURL(scopes, null, null,
                        null, null, 0, null, null, null,
                        null, null));
    }

    @Test
    public void testCompleteExample() {
        OpenIdClient client = new OpenIdClient("http://example.org", "great", "http://localhost/back");
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("email");
        List<Locale> locales = new ArrayList<>();
        locales.add(new Locale("de", "de"));
        locales.add(new Locale("en", "gb"));
        assertEquals("http://example.org/authorize?response_type=code&client_id=great&scope=openid+email&redirect_uri=http%3A%2F%2Flocalhost%2Fback&state=greatState&nonce=suchNonce&display=page&prompt=select_account&max_age=420&ui_locales=de_DE+en_GB&claims_locales=de_DE+en_GB&id_token_hint=tokenHint&login_hint=loginHint",
                client.getAuthenticationRequestURL(scopes, "greatState", "suchNonce", Display.page,
                        Prompt.select_account, 420, locales, locales, "tokenHint",
                        "loginHint", null));
    }
}
