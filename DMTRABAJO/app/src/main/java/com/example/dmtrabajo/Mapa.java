package com.example.dmtrabajo;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

public class Mapa extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap gMap;
    Button btDraw,btClear,btnAddPt,btVolver;
    TextView tvName;
    Polygon poligono = null;
    List<LatLng> latLongLista = new ArrayList<>();
    List<Marker> marcadores = new ArrayList<>();
    ArrayList<String> marcas = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    private String user ;
    private String finca ;
    private DBManager gestorDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        Intent sendData = this.getIntent();
        user = sendData.getExtras().getString("usuario", "ERROR");
        finca = sendData.getExtras().getString("finca", "ERROR");
        btClear = findViewById(R.id.bt_clear);
        btDraw = findViewById(R.id.bt);
        btnAddPt = findViewById(R.id.btAddPnt);
        btVolver = findViewById(R.id.btVuelta);
        tvName = findViewById(R.id.finca);
        tvName.setText(finca);

        ListView lvItems = (ListView) this.findViewById( R.id.lvItems );
        this.gestorDB = new DBManager(this.getApplicationContext());



        arrayAdapter = new ArrayAdapter( this.getApplicationContext(), android.R.layout.simple_selectable_list_item, marcas);
        lvItems.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);

        btDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mapa.this.gestorDB.deleteMarcas(user,finca);
                loadOnMemory();
                draw();
            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(poligono != null) poligono.remove();
                for(Marker marker : marcadores) marker.remove();
                latLongLista.clear();
                marcadores.clear();
                marcas.clear();

                Mapa.this.gestorDB.deleteMarcas(user,finca);
                arrayAdapter.notifyDataSetChanged();
            }
        });

        btnAddPt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPt();
            }
        });

        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mapa.this.setResult(Activity.RESULT_CANCELED);
                Mapa.this.finish();
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                edit(i);
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        this.gestorDB.close();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap= googleMap;
        loadFromMemory();
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                Marker marker = gMap.addMarker(markerOptions);
                latLongLista.add(latLng);
                marcadores.add(marker);
                float lat = (float)latLng.latitude;
                float lon = (float)latLng.longitude;
                marcas.add("Punto " + marcas.size() + " Latitud:" + lat + " Longitud:"
                + lon +" ");
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    public void addPt(){
        AlertDialog builder=new AlertDialog.Builder(this).create();

        final View customLayout = getLayoutInflater().inflate(R.layout.editlatlong_layout,null);
        final EditText edLati = (EditText) customLayout.findViewById(R.id.edLat);
        final EditText edLngt = (EditText) customLayout.findViewById(R.id.edLng);
        final Button btAceptar = (Button) customLayout.findViewById(R.id.btnAñadir);
        final Button btVolver = (Button) customLayout.findViewById(R.id.btnVolver);
        btAceptar.setEnabled(false);

        edLati.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edLati.getText().toString().trim().length() > 0
                        && edLngt.getText().toString().trim().length() > 0){
                    btAceptar.setEnabled(true);
                }else{
                    btAceptar.setEnabled(false);
                }
            }
        });

        edLngt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edLati.getText().toString().trim().length() > 0
                        && edLngt.getText().toString().trim().length() > 0){
                    btAceptar.setEnabled(true);
                }else{
                    btAceptar.setEnabled(false);
                }
            }
        });

        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mapa.this.gestorDB.addMarca(user,Float.parseFloat(edLati.getText().toString()),Float.parseFloat(edLngt.getText().toString()),finca,marcas.size());
                createPointinPos(Float.parseFloat(edLati.getText().toString()),Float.parseFloat(edLngt.getText().toString()),marcas.size());
                builder.dismiss();

            }
        });

        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        builder.setView( customLayout);
        builder.show();

    }

    public void loadOnMemory(){
        for(int i=0;i<latLongLista.size();i++){
            LatLng pos = latLongLista.get(i);
            float lat = (float)pos.latitude;
            float lon = (float)pos.longitude;
            Mapa.this.gestorDB.addMarca(user,lat,lon,finca,i);
        }


    }

    public void draw (){
        if(!latLongLista.isEmpty()){
            if(poligono != null) poligono.remove();
            PolygonOptions polygonOptions = new PolygonOptions().addAll(latLongLista).clickable(true);
            poligono = gMap.addPolygon(polygonOptions);
            poligono.setStrokeColor(Color.BLACK);
        }
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
    public void createPointinPos(float lat , float lng,int pos){
        if(pos< marcas.size()){
            Marker mark = marcadores.get(pos);
            mark.remove();
            marcadores.remove(pos);
            marcas.remove(pos);
            latLongLista.remove(pos);
        }
        LatLng marca;
        marca = new LatLng(lat,lng);
        MarkerOptions markerOptions = new MarkerOptions().position(marca);
        Marker marker = gMap.addMarker(markerOptions);
        marcadores.add(pos,marker);

        marcas.add(pos,"Punto " + pos + " Latitud:" + lat + " Longitud:"
                + lng +" ");

        latLongLista.add(pos,marca);
        arrayAdapter.notifyDataSetChanged();
        draw();
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
        arrayAdapter.notifyDataSetChanged();
        draw();
    }

    public void edit (int pos){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Quieres editar este punto?");
        builder.setPositiveButton("Si",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editCord(pos);
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.create().show();
    }

    public void  editCord(int pos){
        AlertDialog builder=new AlertDialog.Builder(this).create();

        LatLng latLng = latLongLista.get(pos);
        float lat = (float)latLng.latitude;
        float lon = (float)latLng.longitude;

        final View customLayout = getLayoutInflater().inflate(R.layout.editlatlong_layout,null);
        final EditText edLati = (EditText) customLayout.findViewById(R.id.edLat);
        final EditText edLngt = (EditText) customLayout.findViewById(R.id.edLng);
        final Button btAceptar = (Button) customLayout.findViewById(R.id.btnAñadir);
        final Button btVolver = (Button) customLayout.findViewById(R.id.btnVolver);

        edLati.setText(Float.toString(lat));
        edLngt.setText(Float.toString(lon));

        edLati.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edLati.getText().toString().trim().length() > 0
                        && edLngt.getText().toString().trim().length() > 0){
                    btAceptar.setEnabled(true);
                }else{
                    btAceptar.setEnabled(false);
                }
            }
        });

        edLngt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edLati.getText().toString().trim().length() > 0
                        && edLngt.getText().toString().trim().length() > 0){
                    btAceptar.setEnabled(true);
                }else{
                    btAceptar.setEnabled(false);
                }
            }
        });

        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mapa.this.gestorDB.deleteMarca(user,finca,pos);
                Mapa.this.gestorDB.addMarca(user,Float.parseFloat(edLati.getText().toString()),Float.parseFloat(edLngt.getText().toString()),finca,pos);
                createPointinPos(Float.parseFloat(edLati.getText().toString()),Float.parseFloat(edLngt.getText().toString()),pos);
                builder.dismiss();

            }
        });

        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        builder.setView( customLayout);
        builder.show();
    }

}