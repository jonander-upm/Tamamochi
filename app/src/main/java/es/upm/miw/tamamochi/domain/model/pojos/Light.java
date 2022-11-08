
package es.upm.miw.tamamochi.domain.model.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Light {

    @SerializedName("ts")
    @Expose
    private Long ts;
    @SerializedName("value")
    @Expose
    private String value;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Light() {
    }

    /**
     * 
     * @param value
     * @param ts
     */
    public Light(Long ts, String value) {
        super();
        this.ts = ts;
        this.value = value;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Light{" +
                "ts=" + ts +
                ", value='" + value + '\'' +
                '}';
    }
}
