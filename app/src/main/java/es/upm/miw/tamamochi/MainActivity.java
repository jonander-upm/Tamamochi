package es.upm.miw.tamamochi;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import es.upm.miw.tamamochi.activities.CharacterCreationActivity;
import es.upm.miw.tamamochi.domain.model.Character;
import es.upm.miw.tamamochi.domain.model.Environment;
import es.upm.miw.tamamochi.domain.model.TamamochiViewModel;
import es.upm.miw.tamamochi.domain.repositories.CharacterRepository;
import es.upm.miw.tamamochi.domain.services.ExternalWeatherService;
import es.upm.miw.tamamochi.domain.services.ServiceRestarter;
import es.upm.miw.tamamochi.domain.services.TamamochiNotificationManager;
import es.upm.miw.tamamochi.domain.services.TelemetryPollingService;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MiW";
    private FirebaseAuth mAuth;

    Button playButton;
    Button loginButton;

    private TamamochiViewModel tamamochiViewModel;
    private CharacterRepository characterRepository;

    ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instantiateLayoutItems();

        mAuth = FirebaseAuth.getInstance();
        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                    @Override
                    public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                        onSignInResult(result);
                    }
                }
        );

        tamamochiViewModel = TamamochiViewModel.getInstance();
        characterRepository = CharacterRepository.getInstance();
    }

    private void instantiateLayoutItems() {
        playButton = findViewById(R.id.playButton);
        loginButton = findViewById(R.id.loginButton);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        setButtonListeners();
        startServices();
    }

    private void setButtonListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());
                // Create and launch sign-in intent
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build();
                signInLauncher.launch(signInIntent);
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = tamamochiViewModel.getCurrentUser();
                characterRepository.findByUidAndAlive(user.getUid(), true).observe(MainActivity.this, new Observer<Character>() {
                    @Override
                    public void onChanged(Character character) {
                        Intent intent;
                        if(character == null) {
                            intent = new Intent(getApplicationContext(), CharacterCreationActivity.class);
                            startActivity(intent);
                        } else {

                        }
                    }
                });
            }
        });
    }

    public void startServices() {
        startService(new Intent(this, ExternalWeatherService.class));
        if (!isMyServiceRunning(TelemetryPollingService.class)) {
            Intent pollingServiceIntent = new Intent(this, TelemetryPollingService.class);
            startService(pollingServiceIntent);
        }
        if (!isMyServiceRunning(TamamochiNotificationManager.class)) {
            Intent notifServiceIntent = new Intent(this, TamamochiNotificationManager.class);
            startService(notifServiceIntent);
        }
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ServiceRestarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            updateUI(user);
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null) {
            tamamochiViewModel.setCurrentUser(currentUser);
            playButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.menuConstraintView),
                    getString(R.string.welcomeMessage) + " " + currentUser.getDisplayName() + "!",
                    Snackbar.LENGTH_SHORT).show();
        } else {
            playButton.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }
}