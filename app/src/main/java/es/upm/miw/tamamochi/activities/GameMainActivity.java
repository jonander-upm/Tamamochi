package es.upm.miw.tamamochi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import java.util.List;

import es.upm.miw.tamamochi.R;
import es.upm.miw.tamamochi.domain.model.Character;
import es.upm.miw.tamamochi.domain.model.CharacterAge;
import es.upm.miw.tamamochi.domain.model.CharacterStatus;
import es.upm.miw.tamamochi.domain.model.CharacterType;
import es.upm.miw.tamamochi.domain.model.TamamochiViewModel;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Measurement;
import es.upm.miw.tamamochi.domain.model.pojos.weather.ExternalWeather;

public class GameMainActivity extends AppCompatActivity {
    private TamamochiViewModel tamamochiViewModel;
    Character character;
    List<CharacterStatus> characterStatusList;

    private Handler handler;
    public static final long DEFAULT_SYNC_INTERVAL = 30 * 1000;
    public static final long STATUS_SHOW_INTERVAL = 10 * 1000;

    private static final String WEATHER_ICON_URL = "http://openweathermap.org/img/wn/";
    private static final String WEATHER_ICON_FORMAT = "@2x.png";

    private int statusIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);
        tamamochiViewModel = TamamochiViewModel.getInstance();
        tamamochiViewModel.getCharacter().observe(this, new Observer<Character>() {
            @Override
            public void onChanged(Character newCharacter) {
                character = newCharacter;
                initializeCharacter();
            }
        });
        statusIndex = 0;
        LinearLayout statusLayout = findViewById(R.id.statusLayout);
        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStatus();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        tamamochiViewModel.getCharacterStatusList().observe(this, new Observer<List<CharacterStatus>>() {
            @Override
            public void onChanged(List<CharacterStatus> statusList) {
                characterStatusList = statusList;
            }
        });
        tamamochiViewModel.getInternalEnvironment().observe(this, new Observer<Measurement>() {
            @Override
            public void onChanged(Measurement environment) {
                updateInternalEnvironmentData(environment);
            }
        });
        tamamochiViewModel.getExternalEvironment().observe(this, new Observer<ExternalWeather>() {
            @Override
            public void onChanged(ExternalWeather externalWeather) {
                ImageView ivWeatherIcon = findViewById(R.id.weatherIcon);
                String weatherIcon = externalWeather.getWeather().get(0).getIcon();
                String iconUrl = WEATHER_ICON_URL + weatherIcon + WEATHER_ICON_FORMAT;
                Picasso.get()
                        .load(iconUrl)
                        .into(ivWeatherIcon);
            }
        });
        handler = new Handler();
        handler.post(runnableUpdateCharacter);
        handler.post(runnableShowCharacterStatus);
    }

    private void updateInternalEnvironmentData(Measurement internalEnvironment) {
        TextView tvTemperature = findViewById(R.id.tvTemperature);
        TextView tvHumidity = findViewById(R.id.tvHumidity);
        TextView tvLight = findViewById(R.id.tvLight);
        TextView tvCo2 = findViewById(R.id.tvCo2);
        String temperatureData = getString(R.string.temperature) + internalEnvironment.getTemperature().get(0).getValue() + "°C";
        String humidityData = getString(R.string.humidity) + internalEnvironment.getHumidity().get(0).getValue() + "%";
        String lightData = getString(R.string.light) + internalEnvironment.getTemperature().get(0).getValue() + "Lux";
        String co2Data = getString(R.string.co2) + internalEnvironment.getTemperature().get(0).getValue() + "ppm";
        tvTemperature.setText(temperatureData);
        tvHumidity.setText(humidityData);
        tvLight.setText(lightData);
        tvCo2.setText(co2Data);
    }

    public void initializeCharacter() {
        TextView tvCharacterName = findViewById(R.id.tvCharacterName);
        TextView tvCharacterAge = findViewById(R.id.tvCharacterAge);
        ImageView ivCharacter = findViewById(R.id.character);
        TextView tvHealthPoints = findViewById(R.id.tvHealthPoints);

        String ageString = character.getCharacterAgeDays() + " " + getString(R.string.txtDays);
        tvCharacterName.setText(character.getCharacterName());
        tvCharacterAge.setText(ageString);

        CharacterAge characterAge = character.getAlive()
                ? CharacterAge.getCharacterAge(character.getCharacterBirthDate())
                : CharacterAge.DEAD;
        CharacterType characterType = character.getCharacterType();
        ivCharacter.setImageResource(characterType.getDrawableIdByAge(characterAge));
        String healthPoints = character.getLife() + "HP";
        tvHealthPoints.setText(healthPoints);
    }

    Runnable runnableUpdateCharacter = new Runnable() {
        @Override
        public void run() {
            if(character != null) {
                initializeCharacter();
            }
            handler.postDelayed(runnableUpdateCharacter, DEFAULT_SYNC_INTERVAL);
        }
    };

    public void showStatus() {
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvResolution = findViewById(R.id.tvResolution);
        TextView tvStatusPage = findViewById(R.id.tvStatusPage);
        LinearLayout statusLayout = findViewById(R.id.statusLayout);
        if(character != null && character.getAlive()) {
            if(characterStatusList != null && characterStatusList.size() > 0) {
                statusIndex = (statusIndex + 1) % characterStatusList.size();
                CharacterStatus showStatus = characterStatusList.get(statusIndex);
                tvStatus.setText(getString(showStatus.getIssueStringId()));
                tvResolution.setText(getString(showStatus.getResolutionStringId()));
                String statusPage = (statusIndex+1) + " " + getString(R.string.statusPageSeparator) + " " + characterStatusList.size();
                tvStatusPage.setText(statusPage);
            } else {
                tvStatus.setText(getString(R.string.txtCharacterOk));
                tvResolution.setText("");
                tvStatusPage.setText("");
            }
        } else {
            tvStatus.setText(getString(R.string.txtCharacterDead));
            tvResolution.setText(R.string.txtClickToRestart);
            tvStatusPage.setText("");
            statusLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CharacterCreationActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    Runnable runnableShowCharacterStatus = new Runnable() {
        @Override
        public void run() {
            showStatus();
            handler.postDelayed(runnableShowCharacterStatus, STATUS_SHOW_INTERVAL);
        }
    };
}
