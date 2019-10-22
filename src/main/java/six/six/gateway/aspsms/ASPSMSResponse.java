package six.six.gateway.aspsms;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ASPSMSResponse implements Serializable {
    
    private static final long serialVersionUID = -106600202592439623L;

    @NotNull
    @JsonProperty("StatusCode")
    private String statusCode;

    @NotNull
    @JsonProperty("StatusInfo")
    private String statusInfo;
    
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getStatusCode() {
        return statusCode;
    }
    
    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }
    
    public String getStatusInfo() {
        return statusInfo;
    }
    
}
