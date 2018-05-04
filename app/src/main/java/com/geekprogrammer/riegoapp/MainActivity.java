package com.geekprogrammer.riegoapp;

import android.app.AlertDialog;
import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Helper.NotificationHelper;
import com.geekprogrammer.riegoapp.Services.ServicesBackground;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    TextView stateBluetooth;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        stateBluetooth = (TextView)findViewById(R.id.bluetoothState);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Fechas no asignadas", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkState();
        upServiceStatus();
    }

    private void upServiceStatus() {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle("Riegos Programados");
        mDialog.setIcon(R.drawable.ic_invert_colors_black);
        mDialog.setMessage("Ingrese los datos de riego programados");
        mDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startService(new Intent(MainActivity.this, ServicesBackground.class));
            }
        });
        mDialog.show();
    }

    private void startFragment(){
        toolbar.setTitle("Dispositivos");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.screen_area, new HomeFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Destruido","Tarea iniciada");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //
        //startService(new Intent(MainActivity.this, ServicesBackground.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_ENABLE_BT && bluetoothAdapter.isEnabled()){
            Toast.makeText(this, "Bluetooth Encendido", Toast.LENGTH_SHORT).show();
            startFragment();
            stateBluetooth.setVisibility(View.INVISIBLE);
        }else{
            stateBluetooth.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No se Activo el Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkState() {
        if (bluetoothAdapter == null){
            Toast.makeText(this, "El dispositivo no soporta el Bluetooth", Toast.LENGTH_SHORT).show();
        }else{
            if (!bluetoothAdapter.isEnabled()){
                Intent enablebt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enablebt, REQUEST_ENABLE_BT);
            }else{
                Toast.makeText(this, "Bluetooth Encendido", Toast.LENGTH_SHORT).show();
                startFragment();
            }
        }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            //checkState();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_regados) {
            ft.replace(R.id.screen_area, new HomeFragment());
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_fechas_riego) {
            toolbar.setTitle("Rangos y fechas");
            ft.replace(R.id.screen_area, new DatetimeFragment());
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_dispositivos) {

        } else if (id == R.id.nav_configuracion) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
