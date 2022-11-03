
package es.upm.miw.tamamochi.model.pojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sensors {

    @SerializedName("co2")
    @Expose
    private List<Co2> co2 = null;
    @SerializedName("humidity")
    @Expose
    private List<Humidity> humidity = null;
    @SerializedName("light")
    @Expose
    private List<Light> light = null;
    @SerializedName("soilTemp1")
    @Expose
    private List<SoilTemp1> soilTemp1 = null;
    @SerializedName("soilTemp2")
    @Expose
    private List<SoilTemp2> soilTemp2 = null;
    @SerializedName("temperature")
    @Expose
    private List<Temperature> temperature = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Sensors() {
    }

    /**
     * 
     * @param light
     * @param soilTemp2
     * @param co2
     * @param soilTemp1
     * @param temperature
     * @param humidity
     */
    public Sensors(List<Co2> co2, List<Humidity> humidity, List<Light> light, List<SoilTemp1> soilTemp1, List<SoilTemp2> soilTemp2, List<Temperature> temperature) {
        super();
        this.co2 = co2;
        this.humidity = humidity;
        this.light = light;
        this.soilTemp1 = soilTemp1;
        this.soilTemp2 = soilTemp2;
        this.temperature = temperature;
    }

    public List<Co2> getCo2() {
        return co2;
    }

    public void setCo2(List<Co2> co2) {
        this.co2 = co2;
    }

    public List<Humidity> getHumidity() {
        return humidity;
    }

    public void setHumidity(List<Humidity> humidity) {
        this.humidity = humidity;
    }

    public List<Light> getLight() {
        return light;
    }

    public void setLight(List<Light> light) {
        this.light = light;
    }

    public List<SoilTemp1> getSoilTemp1() {
        return soilTemp1;
    }

    public void setSoilTemp1(List<SoilTemp1> soilTemp1) {
        this.soilTemp1 = soilTemp1;
    }

    public List<SoilTemp2> getSoilTemp2() {
        return soilTemp2;
    }

    public void setSoilTemp2(List<SoilTemp2> soilTemp2) {
        this.soilTemp2 = soilTemp2;
    }

    public List<Temperature> getTemperature() {
        return temperature;
    }

    public void setTemperature(List<Temperature> temperature) {
        this.temperature = temperature;
    }

}
