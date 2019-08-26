package ng.mobilea.airvoucher.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.telephony.TelephonyManager;
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
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ng.mobilea.airvoucher.R;
import ng.mobilea.airvoucher.fragments.Report;
import ng.mobilea.airvoucher.fragments.SmsFrag;
import ng.mobilea.airvoucher.services.WorkerService;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SmsFrag.OnSmsFragment, Report.OnReport {

    private MenuItem item;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initPermissions();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            fragmentHandler(getSmsFrag());
        }
    }

    private void initPermissions() {
        Dexter.withActivity(Home.this)
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED

                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.isAnyPermissionPermanentlyDenied()) {
                    String message = "As for terms and conditions the following permissions are required for AirVoucher in order to server its purpose:\n";
                    int deniedPermissions = report.getDeniedPermissionResponses().size();
                    int count = 1;
                    for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
                        if (deniedPermissions == count)
                            message += " and " + response.getPermissionName() + ".";
                        else
                            message += response.getPermissionName() + ",";
                        count++;
                    }

                    popUpPermissions("Permissions", message);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void popUpPermissions(String title, String message) {
        try {
            builder = new AlertDialog.Builder(Home.this, R.style.SimpleBlackDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(title);
            builder.setIcon(R.mipmap.ic_launcher);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    initPermissions();
                }
            });
            dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Home.this, message != null ? message : "NO_MESSAGE", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1)
                super.onBackPressed();
            else {
                popBox("Are you sure you want to quit?");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        this.item = searchItem;

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Query the list of element within a proper fragment.
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if(fragment instanceof SmsFrag || fragment instanceof Report){
                    if(item != null && !item.isVisible()){
                        item.setVisible(true);
                    }
                    if(fragment instanceof SmsFrag){
                        SmsFrag smsFrag = (SmsFrag) fragment;
                        smsFrag.filter(newText);
                    }else if (fragment instanceof Report){
                        Report report = (Report) fragment;
                        report.filter(newText);
                    }

                }else{
                    if(item != null){
                        item.setVisible(false);
                    }
                }
                return true;
            }
        });
        // Define the listener
        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when action item collapses
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;
            }
        };
        // Assign the listener to that action item
        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_report) {
            fragmentHandler(getReportFrag());
            return true;
        }else if (id == R.id.action_search){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            fragmentHandler(getSmsFrag());
        } else if (id == R.id.report) {
            fragmentHandler(getReportFrag());
        } else if (id == R.id.exit) {

            popBox("Are you sure you want to quit?");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fragmentHandler(Object object) {
        if (object instanceof SmsFrag || object instanceof Report) {
            if(item != null && !item.isVisible()){
                item.setVisible(true);
            }
        } else {
            if(item != null){
                item.setVisible(false);
            }
        }
        Fragment fragment = (Fragment) object;
        String backStateName = fragment.getClass().getSimpleName();

        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    private void popBox(String message){
        try {
            builder = new AlertDialog.Builder(Home.this, R.style.SimpleBlackDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
            });
            dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Home.this, message != null ? message : "NO_MESSAGE", Toast.LENGTH_SHORT).show();
        }
    }

    private SmsFrag getSmsFrag(){
        return SmsFrag.newInstance();
    }

    private Report getReportFrag(){
        return Report.newInstance();
    }

    @Override
    public void onSmsFragment(Object object) {

    }

    @Override
    public void onReportInteraction(Object object) {

    }
}
