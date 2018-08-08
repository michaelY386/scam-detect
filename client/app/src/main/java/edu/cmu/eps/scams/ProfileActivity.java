package edu.cmu.eps.scams;

        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.View;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.EditText;
        import android.widget.TextView;

        import org.json.JSONException;

        import java.util.List;

        import edu.cmu.eps.scams.logic.ApplicationLogicFactory;
        import edu.cmu.eps.scams.logic.ApplicationLogicResult;
        import edu.cmu.eps.scams.logic.ApplicationLogicTask;
        import edu.cmu.eps.scams.logic.IApplicationLogic;
        import edu.cmu.eps.scams.logic.IApplicationLogicCommand;
        import edu.cmu.eps.scams.logic.model.AppSettings;
        import edu.cmu.eps.scams.logic.model.History;

public class ProfileActivity extends AppCompatActivity {
    private IApplicationLogic logic;

    // More targeted data should be displayed here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.logic = ApplicationLogicFactory.build(this);
        // Task to create a list whenever the History page is shown
        // Retrieve updated call history information from the local database
        ApplicationLogicTask task = new ApplicationLogicTask(
                this.logic,
                progress -> {
                },
                result -> {
                    AppSettings settings = (AppSettings) result.getAppSettings();
                    TextView nameView = (TextView) findViewById(R.id.nameTextView);
                    TextView userTypeView = (TextView) findViewById(R.id.typeTextView);
                    try {
                        nameView.setText(settings.getName());
                        userTypeView.setText(settings.getUserType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
        );
        // runs the task's code on a background thread
        task.execute((IApplicationLogicCommand) logic -> new ApplicationLogicResult(logic.getAppSettings()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
