package edu.cmu.eps.scams;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;

import edu.cmu.eps.scams.logic.ApplicationLogicFactory;
import edu.cmu.eps.scams.logic.ApplicationLogicResult;
import edu.cmu.eps.scams.logic.ApplicationLogicTask;
import edu.cmu.eps.scams.logic.IApplicationLogic;
import edu.cmu.eps.scams.logic.IApplicationLogicCommand;
import edu.cmu.eps.scams.logic.model.AppSettings;
import edu.cmu.eps.scams.permissions.PermissionsFacade;
import edu.cmu.eps.scams.services.ServicesFacade;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSIONS_REQUEST_CODE = 555;
    private static final String TAG = "MainActivity";
    private IApplicationLogic logic;
    private AppSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Context context = this;
        // Get local settings from local database
        this.logic = ApplicationLogicFactory.build(this);
        // Start a task to check the session status
        ApplicationLogicTask task = new ApplicationLogicTask(
                this.logic,
                progress -> {

                },
                result -> {
                    AppSettings settings = (AppSettings) result.getAppSettings();
                    this.settings = settings;
                    Log.d(TAG, String.format("Retrieved settings: %s", settings.toString()));
                    // The first time login for a device
                    if (settings.isRegistered() == false) {
                        Log.d(TAG, "Phone is not previously registered");
                        // user is not logged in redirect him to Login Activity
                        Intent i = new Intent(context, FirstTimeLogin.class);
                        // Closing all the Activities
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        // Add new Flag to start new Activity
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        // Staring Login Activity
                        context.startActivity(i);
                    }
                    // Not the first time login
                    else {
                        Log.d(TAG, "Phone is previously registered");
                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        View headerView = navigationView.getHeaderView(0);
                        TextView navNameView = (TextView) headerView.findViewById(R.id.navNameView);
                        try {
                            if (navNameView != null) {
                                navNameView.setText(settings.getName());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Get mobile phone permissions
                        if (PermissionsFacade.isPermissionGranted(this, Manifest.permission.RECORD_AUDIO) &&
                                PermissionsFacade.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Log.d(TAG, "Permissions previously granted, starting services");
                            try {
                                ServicesFacade.startServices(this, settings.getUserType());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "Permissions missing, requesting from user");
                            PermissionsFacade.requestPermission(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
                        }
                        // QR code image generation based on user's qrString
                        ImageView qrCode=(ImageView) findViewById(R.id.my_qrcode);

                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        //System.out.println("here");
                        try {
                            // Get the qrString from local database
                            String codeString = String.format("{\"identifier\": \"%s\", \"name\": \"%s\"}", settings.getIdentifier(), settings.getName());
                            //System.out.println("here");
                            //System.out.println(codeString);
                            BitMatrix bitMatrix = multiFormatWriter.encode(codeString, BarcodeFormat.QR_CODE,200,200);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                            // Generate the code and put the image to the view
                            qrCode.setImageBitmap(bitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        // runs the task's code on a background thread
        task.execute((IApplicationLogicCommand) logic -> new ApplicationLogicResult(logic.getAppSettings()));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent intent = new Intent(this, ScannerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_connections) {
            Intent intent = new Intent(this, FriendlistActivity.class);
            startActivity(intent);
        }
        // Additional functionalities
            else if (id == R.id.nav_report) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "User granted permissions, starting services");
                    try {
                        ServicesFacade.startServices(this, this.settings.getUserType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        }
    }
}
