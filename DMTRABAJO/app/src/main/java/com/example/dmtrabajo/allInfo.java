package com.example.dmtrabajo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class allInfo extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityResultLauncher<Intent> activityResultLauncher;
    TextView tvNombre,tvTipo,tvDesc;
    Button btVuelve;
    GoogleMap gMap;
    private String user ,finca;
    private String tipo,descripcion ;
    private DBManager gestorDB;
    Polygon poligono = null;
    List<LatLng> latLongLista = new ArrayList<>();
    List<Marker> marcadores = new ArrayList<>();
    ArrayList<String> marcas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_info);

        btVuelve=findViewById(R.id.btVuelta);
        tvNombre = findViewById(R.id.Nombre);
        tvTipo = findViewById(R.id.Tipo);
        tvDesc = findViewById(R.id.Descripcion);

        Intent sendData = this.getIntent();
        user = sendData.getExtras().getString("usuario", "ERROR");
        finca = sendData.getExtras().getString("_id", "ERROR");
        tipo =sendData.getExtras().getString("tipo", "");
        descripcion =sendData.getExtras().getString("descripcion", "");
        tvNombre.setText(finca);
        tvTipo.setText(tipo);
        tvDesc.setText(descripcion);

        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);

        btVuelve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allInfo.this.setResult(Activity.RESULT_CANCELED);
                allInfo.this.finish();
            }
        });

        ActivityResultContract<Intent, ActivityResult> contract =
                new ActivityResultContracts.StartActivityForResult();
        ActivityResultCallback<ActivityResult> callback =
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent fincaDatos = result.getData();
                            String nombre = fincaDatos.getExtras().getString("nombre", "ERROR");
                            String cat = fincaDatos.getExtras().getString("tipo", "ERROR");
                            String desc = fincaDatos.getExtras().getString("descripcion", "ERROR");
                            allInfo.this.gestorDB.deleteFinca(nombre,user);
                            allInfo.this.gestorDB.addFinca(nombre,cat,desc,user);
                            tvNombre.setText(nombre);
                            tvTipo.setText(cat);
                            tvDesc.setText(desc);
                        }

                        clear();
                        loadFromMemory();
                    }
                };
        this.activityResultLauncher = this.registerForActivityResult(contract, callback);

        this.gestorDB = new DBManager(this.getApplicationContext());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap= googleMap;

        loadFromMemory();

    }
    public void clear(){
        if(poligono != null) poligono.remove();
        for(Marker marker : marcadores) marker.remove();
        latLongLista.clear();
        marcadores.clear();
        marcas.clear();
    }
    public void loadFromMemory(){
        float lat =1f;
        float lon=2f;
        Cursor cursor = this.gestorDB.getMarcas(user,finca);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){

                lat=cursor.getFloat(1);
                lon=cursor.getFloat(2);
                createPoint(lat,lon);
                cursor.moveToNext();

            }
        }
        cursor.close();

        draw();
    }

    public void createPoint(float lat , float lng){
        LatLng marca;
        marca = new LatLng(lat,lng);
        MarkerOptions markerOptions = new MarkerOptions().position(marca);
        Marker marker = gMap.addMarker(markerOptions);
        marcadores.add(marker);

        marcas.add("Punto " + marcas.size() + " Latitud:" + lat + " Longitud:"
                + lng +" ");

        latLongLista.add(marca);
    }
    public void draw (){
        if(!latLongLista.isEmpty()){
            if(poligono != null) poligono.remove();
            PolygonOptions polygonOptions = new PolygonOptions().addAll(latLongLista).clickable(true);
            poligono = gMap.addPolygon(polygonOptions);
            poligono.setStrokeColor(Color.BLACK);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        this.getMenuInflater().inflate(R.menu.infomenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        boolean toret = false;

        switch(menuItem.getItemId()){
            case R.id.editPuntos:
                Intent editPuntos = new Intent(allInfo.this, Mapa.class);
                editPuntos.putExtra("usuario",user);
                editPuntos.putExtra("finca",finca);
                activityResultLauncher.launch(editPuntos);
                toret = true;
                break;
            case R.id.editInfo:
                Intent editInfo = new Intent(allInfo.this, AddFinca.class);
                editInfo.putExtra("_id",finca);
                editInfo.putExtra("tipo",tipo);
                editInfo.putExtra("descripcion",descripcion);
                //editInfo.putExtra("user",user);
                activityResultLauncher.launch(editInfo);
                toret = true;
                break;
        }

        return toret;
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.gestorDB.close();
    }

}