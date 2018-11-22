package de.paktosan.openid.connect;

import de.paktosan.openid.connect.rest.TokenAnswer;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.json.JsonArray;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class OpenIdClient {
    private Client jerseyClient = ClientBuilder.newClient();
    private String securityEndpoint;
    private WebTarget securityTarget;
    private String clientId;
    private String redirectURL;

    public OpenIdClient(String securityEndpoint, String clientId, String redirectURL) {
        if (securityEndpoint == null || securityEndpoint.equals("")) {
            throw new IllegalArgumentException("Security endpoint has to be set!");
        } else this.securityEndpoint = securityEndpoint;
        if (clientId == null || clientId.equals("")) {
            throw new IllegalArgumentException("ClientID has to be set!");
        } else this.clientId = clientId;
        if (redirectURL == null || redirectURL.equals("")) {
            throw new IllegalArgumentException("Redirect URL has to be set!");
        } else this.redirectURL = redirectURL;
        securityTarget = jerseyClient.target(securityEndpoint);
    }

    /**
     * Generates the URL the user will be redirected to to authenticate.
     *
     * @param scopes       Optional, "openid" will always be added automatically.
     * @param state        Optional, used for CSRF and XSRF mitigation
     * @param nonce        Optional, to mitigate replay attacks
     * @param display      Optional, specifies how the Authorization Server displayd authentication and consent user interface
     * @param prompt       Optional, specifies whether the Authorization Server prompts the End-User for reauthentication and consent.
     * @param maxAge       Optional, specifies the allowable elapsed time in seconds since the last time the End-User was actively authenticated by the OpenID provider.
     * @param uiLocales    Optional, specifies the preferred interface language of the End-User.
     * @param claimLocales Optional, specifies the preferred claim language of the End-User.
     * @param idTokenHint  Optional, provided to the Authorization Server as a hint about current or past sessions of the End-User.
     * @param loginHint    Optional, login identifier the user might use.
     * @param acr_values   Optional
     * @return URL the user will be redirected to
     */
    public String getAuthenticationRequestURL(List<String> scopes, String state, String nonce,
                                              Display display, Prompt prompt, int maxAge, List<Locale> uiLocales,
                                              List<Locale> claimLocales, String idTokenHint, String loginHint,
                                              String acr_values) {
        StringBuilder builder = new StringBuilder();
        builder.append(securityEndpoint).append("/authorize");
        builder.append("?response_type=code");
        builder.append("&client_id=").append(clientId);
        if (scopes.contains("openid")) {
            StringBuilder formattedScopes = new StringBuilder();
            Iterator<String> scopeIterator = scopes.iterator();
            while (scopeIterator.hasNext()) {
                formattedScopes.append(scopeIterator.next());
                if (scopeIterator.hasNext()) formattedScopes.append(" ");
            }
            builder.append("&scope=").append(encodeStringForURL(formattedScopes.toString()));
        } else throw new IllegalArgumentException("Scopes do not contain \"openid\"");
        builder.append("&redirect_uri=").append(encodeStringForURL(redirectURL));
        if (state != null) builder.append("&state=").append(encodeStringForURL(state));
        if (nonce != null) builder.append("&nonce=").append(encodeStringForURL(nonce));
        if (display != null) builder.append("&display=").append(encodeStringForURL(display.name()));
        if (prompt != null) builder.append("&prompt=").append(encodeStringForURL(prompt.name()));
        if (maxAge > 0) builder.append("&max_age=").append(maxAge);
        if (uiLocales != null && !uiLocales.isEmpty()) {
            StringBuilder formattedUiLocales = new StringBuilder();
            Iterator<Locale> localeIterator = uiLocales.iterator();
            while (localeIterator.hasNext()) {
                formattedUiLocales.append(localeIterator.next());
                if (localeIterator.hasNext()) formattedUiLocales.append(" ");
            }
            builder.append("&ui_locales=").append(encodeStringForURL(formattedUiLocales.toString()));
        }
        if (claimLocales != null && !claimLocales.isEmpty()) {
            StringBuilder formattedClaimLocales = new StringBuilder();
            Iterator<Locale> localeIterator = claimLocales.iterator();
            while (localeIterator.hasNext()) {
                formattedClaimLocales.append(localeIterator.next());
                if (localeIterator.hasNext()) formattedClaimLocales.append(" ");
            }
            builder.append("&claims_locales=").append(encodeStringForURL(formattedClaimLocales.toString()));
        }
        if (idTokenHint != null) builder.append("&id_token_hint=").append(encodeStringForURL(idTokenHint));
        if (loginHint != null) builder.append("&login_hint=").append(encodeStringForURL(loginHint));
        if (acr_values != null) builder.append("&acr_values=").append(encodeStringForURL(acr_values));
        return builder.toString();
    }

    /**
     * Requests the access token including additional information from the authorization server.
     *
     * @param code the code issued by the authorization server to get the access token.
     * @return Access token and additional information about it.
     */
    public TokenAnswer getToken(String code) {
        Form form = new Form();
        form.param("grant_type", "authorization_code");
        form.param("code", code);
        form.param("redirect_uri", redirectURL);
        return securityTarget.path("token").request(MediaType.APPLICATION_JSON).
                post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), TokenAnswer.class);
    }

    /**
     * Gets information about the user the accessToken belongs to.
     *
     * @param accessToken Token we got when the user allowed us access.
     * @return We just hand back the JsonArray because the output this endpoint can be different depending on the server
     * that was used.
     */
    public JsonArray getUserInfo(String accessToken) {
        return securityTarget.path("userinfo").request(MediaType.APPLICATION_JSON).
                header(HttpHeaders.AUTHORIZATION, "Bearer " + encodeStringForURL(accessToken)).get(JsonArray.class);
    }

    /**
     * Encodes Strings so they can be used in an URL
     *
     * @param string The string to encode
     * @return The encoded string
     */
    private static String encodeStringForURL(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            //We are ignoring this exception since we always use UTF-8
            return "";
        }
    }

    public String getSecurityEndpoint() {
        return securityEndpoint;
    }

    public void setSecurityEndpoint(String securityEndpoint) {
        this.securityEndpoint = securityEndpoint;
        securityTarget = jerseyClient.target(securityEndpoint);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    //only returned for testing TODO dont be lazy and use reflection instead
    public WebTarget getSecurityTarget() {
        return securityTarget;
    }
}
