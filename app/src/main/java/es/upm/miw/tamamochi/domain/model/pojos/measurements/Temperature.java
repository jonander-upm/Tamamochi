package es.upm.miw.tamamochi.domain.model.pojos.measurements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Temperature {

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
    public Temperature() {
    }

    /**
     * 
     * @param value
     * @param ts
     */
    public Temperature(Long ts, String value) {
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
        return "Temperature{" +
                "ts=" + ts +
                ", value='" + value + '\'' +
                '}';
    }
}
