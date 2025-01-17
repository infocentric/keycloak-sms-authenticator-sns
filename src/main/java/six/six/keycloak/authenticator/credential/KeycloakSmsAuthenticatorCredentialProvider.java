package six.six.keycloak.authenticator.credential;

import org.keycloak.common.util.Time;
import org.keycloak.credential.*;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.cache.OnUserCache;
import six.six.keycloak.KeycloakSmsConstants;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nickpack on 09/08/2017.
 */
public class KeycloakSmsAuthenticatorCredentialProvider implements CredentialProvider, CredentialInputValidator, CredentialInputUpdater, OnUserCache {
    
    private static final String CODE_CACHE_KEY = KeycloakSmsAuthenticatorCredentialProvider.class.getName() + "." + KeycloakSmsConstants.USR_CRED_MDL_SMS_CODE;
    private static final String EXP_TIME_CACHE_KEY = KeycloakSmsAuthenticatorCredentialProvider.class.getName() + "." + KeycloakSmsConstants.USR_CRED_MDL_SMS_EXP_TIME;

    private final KeycloakSession session;

    public KeycloakSmsAuthenticatorCredentialProvider(KeycloakSession session) {
        this.session = session;
    }

    private CredentialModel getSecret(RealmModel realm, UserModel user) {
        CredentialModel secret = null;
        if (user instanceof CachedUserModel) {
            CachedUserModel cached = (CachedUserModel) user;
            secret = (CredentialModel) cached.getCachedWith().get(CODE_CACHE_KEY);

        } else {
            List<CredentialModel> creds = session.userCredentialManager().getStoredCredentialsByType(realm, user, KeycloakSmsConstants.USR_CRED_MDL_SMS_CODE);
            if (!creds.isEmpty()) secret = creds.get(0);
        }
        return secret;
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!(KeycloakSmsConstants.USR_CRED_MDL_SMS_CODE.equals(input.getType())
                || KeycloakSmsConstants.USR_CRED_MDL_SMS_EXP_TIME.equals(input.getType()))) return false;
        if (!(input instanceof UserCredentialModel)) return false;
        UserCredentialModel credInput = (UserCredentialModel) input;
        
        List<CredentialModel> creds = session.userCredentialManager().getStoredCredentialsByType(realm, user, input.getType());
        if (creds.isEmpty()) {
            CredentialModel secret = new CredentialModel();
            secret.setType(credInput.getType());
            secret.setValue(credInput.getValue());
            secret.setCreatedDate(Time.currentTimeMillis());
            session.userCredentialManager().createCredential(realm, user, secret);
        } else {
            creds.get(0).setValue(credInput.getValue());
            session.userCredentialManager().updateCredential(realm, user, creds.get(0));
        }
        session.userCache().evict(realm, user);
        return true;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        if (!(KeycloakSmsConstants.USR_CRED_MDL_SMS_CODE.equals(credentialType)
                || KeycloakSmsConstants.USR_CRED_MDL_SMS_EXP_TIME.equals(credentialType))) return;
        session.userCredentialManager().disableCredentialType(realm, user, credentialType);
        session.userCache().evict(realm, user);

    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        if (!session.userCredentialManager().getStoredCredentialsByType(realm, user, KeycloakSmsConstants.USR_CRED_MDL_SMS_CODE).isEmpty()) {
            Set<String> set = new HashSet<>();
            set.add(KeycloakSmsConstants.USR_CRED_MDL_SMS_CODE);
            return set;
        } else {
            return Collections.<String>emptySet();
        }

    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return KeycloakSmsConstants.USR_CRED_MDL_SMS_CODE.equals(credentialType)
                || KeycloakSmsConstants.USR_CRED_MDL_SMS_EXP_TIME.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return KeycloakSmsConstants.USR_CRED_MDL_SMS_CODE.equals(credentialType)
                && getSecret(realm, user) != null;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!(KeycloakSmsConstants.USR_CRED_MDL_SMS_CODE.equals(input.getType())
                || KeycloakSmsConstants.USR_CRED_MDL_SMS_EXP_TIME.equals(input.getType()))) return false;
        if (!(input instanceof UserCredentialModel)) return false;

        String secret = getSecret(realm, user).getValue();

        return secret != null && ((UserCredentialModel) input).getValue().equals(secret);
    }

    @Override
    public void onCache(RealmModel realm, CachedUserModel user, UserModel delegate) {
        List<CredentialModel> creds = session.userCredentialManager().getStoredCredentialsByType(realm, user, KeycloakSmsConstants.USR_CRED_MDL_SMS_CODE);
        if (!creds.isEmpty()) {
            user.getCachedWith().put(CODE_CACHE_KEY, creds.get(0));
        }
        creds = session.userCredentialManager().getStoredCredentialsByType(realm, user, KeycloakSmsConstants.USR_CRED_MDL_SMS_EXP_TIME);
        if (!creds.isEmpty()) {
            user.getCachedWith().put(EXP_TIME_CACHE_KEY, creds.get(0));
        }
    }
}
