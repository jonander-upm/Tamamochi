package es.upm.miw.tamamochi.model.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthorizationBearer {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("refreshToken")
    @Expose
    private String refreshToken;
    @SerializedName("scope")
    @Expose
    private Object scope;

    /**
     * No args constructor for use in serialization
     * 
     */
    public AuthorizationBearer() {
    }

    /**
     * 
     * @param scope
     * @param token
     * @param refreshToken
     */
    public AuthorizationBearer(String token, String refreshToken, Object scope) {
        super();
        this.token = token;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Object getScope() {
        return scope;
    }

    public void setScope(Object scope) {
        this.scope = scope;
    }

}
