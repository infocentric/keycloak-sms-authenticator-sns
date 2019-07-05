package six.six.gateway.aspsms;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * ASPSMS service description
 */

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ASPSMSRestService {

    @POST
    @Path("/SendSimpleTextSMS")
    String send(String body);

}

