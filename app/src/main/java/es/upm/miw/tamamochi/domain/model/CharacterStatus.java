package es.upm.miw.tamamochi.domain.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import es.upm.miw.tamamochi.R;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Temperature;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Co2;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Humidity;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Light;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Measurement;

public enum CharacterStatus {
    COLD(R.string.issueTempTooLow, R.string.resolutionTempTooLow, 1.0),
    HOT(R.string.issueTempTooHigh, R.string.resolutionTempTooHigh, 1.0),
    HUMID(R.string.issueHumidityTooHigh, R.string.resolutionHumidityTooHigh, 0.1),
    DRY(R.string.issueHumidityTooLow, R.string.resolutionHumidityTooLow, 0.1),
    BRIGHT_LIGHT(R.string.issueLightTooBright, R.string.resolutionLightTooBright, 0.3),
    HIGH_CO2(R.string.issueCo2TooHigh, R.string.resolutionCo2TooHigh, 1.2);

    /* https://organosdepalencia.com/biblioteca/articulo/read/76694-cual-es-la-temperatura-minima-recomendada-por-la-oms */
    static final double COLD_THRESHOLD = 18;
    static final double HOT_THRESHOLD = 24;

    /* https://www.endesa.com/es/blog/blog-de-endesa/consejos-de-ahorro/humedad-ideal-casa#:~:text=La%20Agencia%20de%20protecci%C3%B3n%20ambiental,y%20menos%20cuanto%20m%C3%A1s%20fr%C3%ADo. */
    static final double HUMID_THRESHOLD = 50;
    static final double DRY_THRESHOLD = 30;

    /* https://www.thoughtco.com/lighting-levels-by-room-1206643 */
    static final double BRIGHT_LIGHT_THRESHOLD = 1100;

    /* https://www.solerpalau.com/es-es/blog/efectos-co2/ */
    static final double HIGH_CO2_THRESHOLD = 1200;

    final int issueStringId;
    final int resolutionStringId;
    final double lifeDrainPerMinute;

    private CharacterStatus(int issueStringId, int resolutionStringId, double lifeDrainPerMinute) {
        this.issueStringId = issueStringId;
        this.resolutionStringId = resolutionStringId;
        this.lifeDrainPerMinute = lifeDrainPerMinute;
    }

    public int getIssueStringId() {
        return issueStringId;
    }

    public int getResolutionStringId() {
        return resolutionStringId;
    }

    public static List<CharacterStatus> getCharacterStatusList(Measurement measurement) {
        List<CharacterStatus> characterStatusList = new ArrayList<>();

        double temp = Double.parseDouble(measurement.getTemperature().get(0).getValue());
        double hum = Double.parseDouble(measurement.getHumidity().get(0).getValue());
        double light = Double.parseDouble(measurement.getLight().get(0).getValue());
        double co2 = Double.parseDouble(measurement.getCo2().get(0).getValue());

        if(temp < COLD_THRESHOLD) {
            characterStatusList.add(CharacterStatus.COLD);
        } else if(temp > HOT_THRESHOLD) {
            characterStatusList.add(CharacterStatus.HOT);
        }
        if (hum < DRY_THRESHOLD) {
            characterStatusList.add(CharacterStatus.DRY);
        } else if (hum > HUMID_THRESHOLD) {
            characterStatusList.add(CharacterStatus.HUMID);
        }
        if (light > BRIGHT_LIGHT_THRESHOLD) {
            characterStatusList.add(CharacterStatus.BRIGHT_LIGHT);
        }
        if (co2 > HIGH_CO2_THRESHOLD) {
            characterStatusList.add(CharacterStatus.HIGH_CO2);
        }
        return characterStatusList;
    }

    public double getLifeDrainPerMinute() {
        return lifeDrainPerMinute;
    }
}
