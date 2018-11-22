package de.paktosan.openid.connect;

/**
 * Specifies whether the Authorization Server prompts the End-User for reauthentication and consent.
 */
public enum Prompt {
    /**
     * The Authorization Server MUST NOT display any authentication or consent user interface pages.
     * Errors will be returned if an End-User is not already authenticated or the Client does not have pre-configured
     * consent for the requested Claims or does not fulfill other conditions for processing the request.
     * This can be used as a method to check for existing authentication and/or consent.
     */
    none,
    /**
     * The Authorization Server SHOULD prompt the End-User for reauthentication. If it cannot reauthenticate the End-User,
     * it will return an error.
     */
    login,
    /**
     * The Authorization Server SHOULD prompt the End-User for consent before returning information to the Client.
     * If it cannot obtain consent, it will return an error.
     */
    consent,
    /**
     * The Authorization Server SHOULD prompt the End-User to select a user account. This enables an End-User who has
     * multiple accounts at the Authorization Server to select amongst the multiple accounts that they might have current sessions for.
     * If it cannot obtain an account selection choice made by the End-User, it will return an error.
     */
    select_account
}
