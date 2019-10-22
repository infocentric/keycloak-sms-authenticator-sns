package six.six.gateway.aspsms;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import six.six.gateway.SMSService;
import six.six.keycloak.KeycloakSmsConstants;

import java.io.IOException;
import java.util.Optional;

import javax.ws.rs.QueryParam;

/**
 * ASPSMS Service implementation
 */
public class ASPSMSService implements SMSService {

    private static Logger logger = Logger.getLogger(ASPSMSService.class);

    private final ASPSMSRestService remoteService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String originator;

    public ASPSMSService(String host, String originator) {
        this.originator = originator;
        this.remoteService = buildClient(host);
    }

    private static ASPSMSRestService buildClient(String host) {
        ResteasyClient client = new ResteasyClientBuilder().disableTrustManager().build();
        ResteasyWebTarget target = client.target(host);
        return target
                .proxyBuilder(ASPSMSRestService.class)
                .classloader(ASPSMSRestService.class.getClassLoader())
                .build();
    }

    public boolean send(String phoneNumber, String message, String login, String pw) {
        try {
            String resultM = remoteService.send(createBody(login, pw, phoneNumber, message));
            ASPSMSResponse response = mapper.readValue(resultM, ASPSMSResponse.class);
            boolean result = response.getStatusInfo().equals("OK");
            if (!result) {
                logger.error("Fail to send SMS by ASPSMS: " + resultM);
            }
            return result;
        } catch (IOException e) {
            logger.error("Unable to parse ASPSMS response body");
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String createBody(String login, String password, String phonenumber, String message) {
        ASPSMSBodyRequest aspsmsBody = new ASPSMSBodyRequest(login, password, phonenumber, message, originator);
        try {
            return mapper.writeValueAsString(aspsmsBody);
        } catch (JsonProcessingException e) {
            logger.error("Unable to parse ASPSMS request body");
            throw new RuntimeException("Unable to parse ASPSMS request body", e);
        }
    }

}
