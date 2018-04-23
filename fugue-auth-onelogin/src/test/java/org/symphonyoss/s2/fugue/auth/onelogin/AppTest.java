package org.symphonyoss.s2.fugue.auth.onelogin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.xml.sax.SAXException;

import com.onelogin.sdk.conn.Client;
import com.onelogin.sdk.exception.Error;

import com.onelogin.sdk.model.App;
import com.onelogin.sdk.model.Event;
import com.onelogin.sdk.model.EventType;
import com.onelogin.sdk.model.Group;
import com.onelogin.sdk.model.MFA;
import com.onelogin.sdk.model.RateLimit;
import com.onelogin.sdk.model.Role;
import com.onelogin.sdk.model.SAMLEndpointResponse;
import com.onelogin.sdk.model.SessionTokenInfo;
import com.onelogin.sdk.model.SessionTokenMFAInfo;
import com.onelogin.sdk.model.User;


public class AppTest
{
    public static void main( String[] args ) throws IOException, Error, OAuthSystemException, OAuthProblemException, URISyntaxException, XPathExpressionException, ParserConfigurationException, SAXException, NoSuchFieldException
    {
        Client client = new Client();

        /* Get an AccessToken */
        client.getAccessToken();

        /* Refresh an AccessToken */
        client.refreshToken();

        /* Revoke an AccessToken */
        client.revokeToken();

        // By default methods call internally to getAccessToken()
        // if there is not valid access_token

        /* Get rate limits */
        RateLimit ratelimit = client.getRateLimit();

        /* Get Custom Attributes */
        List<String> globalCustomAttributes = client.getCustomAttributes();

        /* Get Users with no query parameters */
        List<User> users = client.getUsers();

        /* Get Users with query parameters */
        HashMap<String,String> userQueryParameters = new HashMap<String,String>();
        userQueryParameters.put("email", "user@example.com");
        List<User> usersFiltered = client.getUsers(userQueryParameters);

        HashMap<String,String> userQueryParameters2 = new HashMap<String,String>();
        userQueryParameters2.put("email", "usermfa@example.com");
        List<User> usersFiltered2 = client.getUsers(userQueryParameters2);

        /* Get User By ID */
        User user = client.getUser(usersFiltered.get(0).id);
        User userMFA = client.getUser(usersFiltered2.get(0).id);

        /* Update User with specific id */
        Map<String, Object> updateUserParams = user.getUserParams();
        updateUserParams.put("firstname", "modified_firstname");
        user = client.updateUser(user.id, updateUserParams);

        /* Get Global Roles */
        List<Role> roleIds = client.getRoles();

//        /* Get Role */
//        Role role = client.(roleIds.get(0));
//        Role role2 = client.getRole(roleIds.get(1));
//
//        /* Assign & Remove Roles On Users */
//        List<Long> newRoleIds = new ArrayList<Long>();
//        newRoleIds.add(role.id);
//        newRoleIds.add(role2.id);
//        client.assignRoleToUser(user.id, newRoleIds);
//        user = client.getUser(user.id);
//        newRoleIds.remove(role2.id);
//        client.removeRoleFromUser(user.id, newRoleIds);
//        user = client.getUser(user.id);
//
//        /* Sets Password by ID Using Cleartext */
//        String password = "Aa765431-XxX";
//        client.setPasswordUsingClearText(user.id, password, password);
//
//        /* Sets Password by ID Using Salt and SHA-256 */
//        password = "Aa765432-YyY";
//        String salt = "11xxxx1";
//        String hashedSaltedPassword = DigestUtils.sha256Hex(salt+password);
//        client.setPasswordUsingHashSalt(userMFA.id, hashedSaltedPassword, hashedSaltedPassword, "salt+sha256", salt);
//
//        /* Set Custom Attribute Value to User */
//        Map<String, Object> customAttributes = new HashMap<String,Object>();
//        customAttributes.put("customattr1", "xxxx");
//        customAttributes.put("customattr2", "yyyy");
//        client.setCustomAttributeToUser(user.id, customAttributes);
//
//        /* Log Out User */
//        client.logUserOut(user.id);
//
//        /* Lock User */
//        client.lockUser(user.id, 1);  // Lock the user 1 min
//
//        /* Get User apps */
//        List<App> userApps = client.getUserApps(user.id);
//
//        /* Get User Roles */
//        List<Integer> userRolesIds = client.getUserRoles(user.id);
//
//        /* Create user */
//        Map<String, Object> newUserParams = new HashMap<String, Object>();
//        newUserParams.put("email", "testcreate_1@example.com");
//        newUserParams.put("firstname", "testcreate_1_fn");
//        newUserParams.put("lastname", "testcreate_1_ln");
//        newUserParams.put("username", "testcreate_1@example.com");
//        User createdUser = client.createUser(newUserParams);
//
//        /* Delete user */
//        Boolean removed = client.deleteUser(createdUser.id);
//
//        /* Get EventTypes */
//        List<EventType> eventTypes = client.getEventTypes();
//
//        /* Get Events */
//        List<Event> events = client.getEvents();
//
//        /* Get 30 Events */
//        List<Event> events2 = client.getEvents(30);
//
//        /* Get Event */
//        Event event = client.getEvent(events.get(0).id);
//
//        /* Create Event */
//        int eventTypeId = 000;
//        Long accountId = 00000L;
//        String actorSystem = 00;
//
//        Map<String, Object> eventParams = new HashMap<String, Object>();
//        eventParams.put("event_type_id", eventTypeId);
//        eventParams.put("account_id", accountId);
//        eventParams.put("actor_system", actorSystem);
//        eventParams.put("user_id", user.id);
//        eventParams.put("user_name", user.username);
//        eventParams.put("custom_message", "test creating event");
//        client.createEvent(eventParams);
//
//        /* Get Filtered Events */
//        HashMap<String, String> eventQueryParameters = new HashMap<String, String>();
//        eventQueryParameters.put("event_type_id", Long.toString(eventTypeId));
//        eventQueryParameters.put("user_id", Long.toString(user.id));
//        List<Event> filteredEvents = client.getEvents(eventQueryParameters);
//
//        /* Get Groups */
//        List<Group> groups = client.getGroups();
//
//        /* Get Group */
//        Group group = client.getGroup(groups.get(0).id);
//
//        String appId = "000000";
//
//        /* Get SAMLResponse directly */
//        SAMLEndpointResponse samlEndpointResponse = client.getSAMLAssertion("user@example.com", "Aa765431-XxX", appId, "example-onelogin-subdomain");
//
//        /* Get SAMLResponse after MFA */
//        SAMLEndpointResponse samlEndpointResponse2 = client.getSAMLAssertion("usermfa@example.com", "Aa765432-YyY", appId, "example-onelogin-subdomain");
//        MFA mfa = samlEndpointResponse2.getMFA();
//        String otpCode2 = "000000";
//        SAMLEndpointResponse samlEndpointResponseAfterVerify = client.getSAMLAssertionVerifying(appId, String.valueOf(mfa.getDevices().get(0).getID()), mfa.getStateToken(), otpCode2, null);
//
//        /* Create Session Login Token */
//        Map<String, Object> sessionLoginTokenParams = new HashMap<String, Object>();
//        sessionLoginTokenParams.put("username_or_email", "user@example.com");
//        sessionLoginTokenParams.put("password", "Aa765431-XxX");
//        sessionLoginTokenParams.put("subdomain", "example-onelogin-subdomain");
//        SessionTokenInfo sessionTokenData = (SessionTokenInfo) client.createSessionLoginToken(sessionLoginTokenParams);
//
//        /* Create Session Via API Token */
//        String cookieHeader = client.createSessionViaToken(sessionTokenData.sessionToken);
//
//        /* Create Session Login Token MFA , after verify */
//        Map<String, Object> sessionLoginTokenMFAParams = new HashMap<String, Object>();
//        sessionLoginTokenMFAParams.put("username_or_email", "usermfa@example.com");
//        sessionLoginTokenMFAParams.put("password", "Aa765432-YyY");
//        sessionLoginTokenMFAParams.put("subdomain", "example-onelogin-subdomain");
//        SessionTokenMFAInfo sessionTokenMFAData = (SessionTokenMFAInfo) client.createSessionLoginToken(sessionLoginTokenMFAParams);
//        String otpCode = "645645";
//        SessionTokenInfo sessionTokenData2 = client.getSessionTokenVerified(Long.toString(sessionTokenMFAData.devices.get(0).getID()), sessionTokenMFAData.stateToken, otpCode);
//
//        long userId = 00000000;
//
//        # Get Available Authentication Factors
//        List<AuthFactor> authFactors = client.getFactors(userId);
//
//        # Enroll an Authentication Factor
//        AuthFactor enrollFactor = client.enrollFactor(userId, authFactors.get(0).id, 'My Device', '+14156456830');
//
//        # Get Enrolled Authentication Factors
//        List<OTPDevice> otpDevices = client.getEnrolledFactors(userId);
//
//        long deviceId = 0000000;
//
//        # Activate an Authentication Factor
//        FactorEnrollmentResponse enrollmentResponse = client.activateFactor(userId, deviceId);
//
//        String otpToken= "XXXXXXXXXX";
//
//        # Verify an Authentication Factor
//        Boolean verified = client.verifyFactor(userId, deviceId, otpToken);
//
//        /* Generate Invite Link */
//        String urlLink = client.generateInviteLink("user@example.com");
//
//        /* Send Invite Link */
//        Boolean sent  = client.sendInviteLink("user@example.com");
//
//        /* Get Apps to Embed for a User */
//        String embedToken = "30e256c101cd0d2e731de1ec222e93c4be8a1578"
//        List<App> apps = client.getEmbedApps(embedToken, "user@example.com");
    }
}