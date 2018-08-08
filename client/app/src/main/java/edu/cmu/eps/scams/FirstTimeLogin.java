package edu.cmu.eps.scams;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Date;

import edu.cmu.eps.scams.logic.ApplicationLogicFactory;
import edu.cmu.eps.scams.logic.ApplicationLogicResult;
import edu.cmu.eps.scams.logic.ApplicationLogicTask;
import edu.cmu.eps.scams.logic.IApplicationLogic;
import edu.cmu.eps.scams.logic.IApplicationLogicCommand;
import edu.cmu.eps.scams.logic.model.AppSettings;
import edu.cmu.eps.scams.logic.model.Telemetry;
import edu.cmu.eps.scams.utilities.TimestampUtility;


public class FirstTimeLogin extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FirstTimeLogin";
    private IApplicationLogic logic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_login);
        this.logic = ApplicationLogicFactory.build(this);

        Button submitButton = (Button) findViewById(R.id.submitButton);

        submitButton.setOnClickListener(this);
    }


    // Define the logic for the initial page
    @Override
    public void onClick(View view) {
        String userType = "";
        EditText nameEdit = (EditText) findViewById(R.id.nameTextEdit);
        boolean primaryChecked = ((RadioButton) findViewById(R.id.primaryRadioButton)).isChecked();
        boolean reviewerChecked = ((RadioButton) findViewById(R.id.reviewerRadioButton)).isChecked();
        if (primaryChecked) {
            userType = "Primary User";
        }
        if (reviewerChecked) {
            userType = "Reviewer";
        }
        if (!primaryChecked && !reviewerChecked) {
            return;
        }
        final String userAction = userType;
        final String name = nameEdit.getText().toString();
        if (name.isEmpty() || name.equalsIgnoreCase("name")) {
            return;
        } else {
            ApplicationLogicTask task = new ApplicationLogicTask(
                    this.logic,
                    progress -> {
                    },
                    result -> {
                        Intent intent = new Intent(FirstTimeLogin.this, MainActivity.class);
                        startActivity(intent);
                    }
            );
            task.execute((IApplicationLogicCommand) logic -> {
                AppSettings settings = logic.getAppSettings();
                Log.d(TAG, String.format("Retrieved settings: %s", settings.toString()));
                Log.d(TAG, "Updating settings with new user");
                Telemetry installTelemetry = new Telemetry("system.install", TimestampUtility.now());
                installTelemetry.getProperties().put("userType", userAction);
                logic.sendTelemetry(installTelemetry);
                logic.updateAppSettings(new AppSettings(
                        settings.getIdentifier(),
                        true,
                        settings.getSecret(),
                        String.format("{ \"userType\": \"%s\", \"name\": \"%s\" }", userAction, name),
                        settings.getRecovery()
                ));
                return new ApplicationLogicResult(logic.getAppSettings());
            });
        }
    }

}