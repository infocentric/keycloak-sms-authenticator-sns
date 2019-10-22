package six.six.gateway.aspsms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ASPSMSBodyRequest implements Serializable {
    
    private static final long serialVersionUID = -7099067957650901552L;

    @NotNull
    @JsonProperty("UserName")
    private String username;

    @NotNull
    @JsonProperty("Password")
    private String password;
    
    @NotNull
    @JsonProperty("Originator")
    private String originator;
    
    @NotNull
    @JsonProperty("Recipients")
    private List<String> recipients = new ArrayList<>();
    
    @NotNull
    @JsonProperty("MessageText")
    private String messageText;
    
    @NotNull
    @JsonProperty("ForceGSM7bit")
    private boolean forceGSM7bit = true;
    
    public ASPSMSBodyRequest(String login, String password, String phonenumber, String message, String originator) {
        this.username = login;
        this.password = password;
        this.recipients.add(phonenumber);
        this.messageText = message;
        this.originator = originator;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }
    
    public List<String> getRecipients() {
        return recipients;
    }
    
    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isForceGSM7bit() {
        return forceGSM7bit;
    }

    public void setForceGSM7bit(boolean forceGSM7bit) {
        this.forceGSM7bit = forceGSM7bit;
    }

}
