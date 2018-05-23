package com.geekprogrammer.riegoapp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Common.Common;
import com.geekprogrammer.riegoapp.Helper.DatabaseHelper;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Pig Shower");

        DatabaseHelper db = new DatabaseHelper(this);

        Common.getDevicePaired(this);
        Common.mCurrentDevice = db.getDevice("PigShowerBt");

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
        //checkStatusConnection();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Servicio", "Iniciado");
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
        //if (id == R.id.on_bluetooth) {
            //checkState();
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_regados) {
            try{
                Intent btA = new Intent(MainActivity.this, BluetoothActivity.class);
                btA.putExtra("devices_address", Common.mCurrentDevice.getDevice_uid());
                startActivity(btA);
                finish();
            }catch (NullPointerException npe){
                Log.e("NullPE No Mac", npe.getMessage());
                Toast.makeText(this, "No hay MAC vinculada al Bluetooth", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_fechas_riego) {
            startActivity(new Intent(MainActivity.this, DatetimeActivity.class));
            finish();
        } else if (id == R.id.nav_dispositivos) {
            startActivity(new Intent(MainActivity.this, PairedActivity.class));
            finish();
        } else if (id == R.id.nav_configuracion) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
