package es.upm.miw.tamamochi.domain.model;

public class Environment {
    String weatherIcon;
    Double temperature;
    Double humidity;
    Double co2;
    Double light;

    public static Builder builder() {
        return new Builder();
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getCo2() {
        return co2;
    }

    public void setCo2(Double co2) {
        this.co2 = co2;
    }

    public Double getLight() {
        return light;
    }

    public void setLight(Double light) {
        this.light = light;
    }

    public static class Builder {
        private final Environment environment;

        public Builder() {
            environment = new Environment();
        }

        public Builder temperature(Double temperature) {
            this.environment.temperature = temperature;
            return this;
        }

        public Builder humidity(Double humidity) {
            this.environment.humidity = humidity;
            return this;
        }

        public Builder co2(Double co2) {
            this.environment.co2 = co2;
            return this;
        }

        public Builder light(Double light) {
            this.environment.light = light;
            return this;
        }

        public Environment build() {
            return this.environment;
        }
    }
}
