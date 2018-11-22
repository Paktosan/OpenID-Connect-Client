package de.paktosan.openid.connect;

/**
 * Specifies how the Authorization Server should display the authentication and consent user interface pages to the End-User.
 */
public enum Display {
    /**
     * Full User Agent page view
     */
    page,
    /**
     * Popup User Agent window
     */
    popup,
    /**
     * For devices leveraging a touch interface
     */
    touch,
    /**
     * "Feature phone" type
     */
    wap
}
