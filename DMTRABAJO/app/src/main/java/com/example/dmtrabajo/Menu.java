package com.example.dmtrabajo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Menu extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String user ;
    private DBManager gestorDB;
    private SimpleCursorAdapter adapterDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Spinner edBuscar = (Spinner) this.findViewById(R.id.edBuscar);
        FloatingActionButton btnAdd = (FloatingActionButton) this.findViewById(R.id.btnAdd);
        ListView lstV = (ListView) this.findViewById(R.id.lstV);
        TextView testTw = (TextView) this.findViewById(R.id.test);


        this.gestorDB = new DBManager(this.getApplicationContext());

        Intent sendData = this.getIntent();
        user = sendData.getExtras().getString("usuario", "ERROR");

        lstV.setLongClickable( true );
        testTw.setText("Benvido: " + user);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add = new Intent(Menu.this,AddFinca.class);
                //add.putExtra("user",user);
                activityResultLauncher.launch(add);
            }
        });

        edBuscar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor;
                if(i != 0){
                    cursor = Menu.this.gestorDB.getTipos(user,edBuscar.getSelectedItem().toString());

                }else{
                    cursor = Menu.this.gestorDB.getFincas(user);

                }
                Menu.this.adapterDB.changeCursor(cursor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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


                            if(Menu.this.gestorDB.addFinca(nombre, cat, desc, user)){
                                Toast t = Toast.makeText(Menu.this, R.string.exitFinca,
                                        Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                t.show();
                            }else{
                                Toast t = Toast.makeText(Menu.this, R.string.errorFinca,
                                        Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                t.show();
                            }
                            Menu.this.adapterDB.changeCursor(Menu.this.gestorDB.getFincas(user));
                        }
                    }
                };
        this.activityResultLauncher = this.registerForActivityResult(contract, callback);

        lstV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewFinca(i);
            }
        });

        lstV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                delete(pos);

                return true;
            }
        });

    }

    public  void delete (int pos){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Estas seguro que queres borrar o terreo");
        builder.setPositiveButton("Si",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cursor cursor = Menu.this.adapterDB.getCursor();
                cursor.moveToPosition(pos);
                String finca = cursor.getString(0);
                Menu.this.gestorDB.deleteFinca(finca,user);
                Menu.this.gestorDB.deleteMarcas(user,finca);
                resetLista();
            }
        });
        builder.setNegativeButton("Non",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }


    public void openMap(int pos){
        Intent maps = new Intent(Menu.this,Mapa.class);
        Cursor cursor = this.adapterDB.getCursor();
        cursor.moveToPosition(pos);
        String finca = cursor.getString(0);
        maps.putExtra("usuario",user);
        maps.putExtra("finca",finca);
        activityResultLauncher.launch(maps);
    }

    public void viewFinca(int pos){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Queres ver a informacion deste terreo?");
        builder.setPositiveButton("Si",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewAllFinca(pos);
            }
        });
        builder.setNegativeButton("Non",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.gestorDB.close();
        this.adapterDB.getCursor().close();
    }
    public void viewAllFinca(int pos){


        Intent all = new Intent(Menu.this,allInfo.class);
        Cursor cursor = this.adapterDB.getCursor();
        cursor.moveToPosition(pos);
        String _id = cursor.getString(0);
        String tipo = cursor.getString(1);
        String descripcion = cursor.getString(2);
        all.putExtra("usuario",user);
        all.putExtra("_id",_id);
        all.putExtra("tipo",tipo);
        all.putExtra("descripcion",descripcion);
        activityResultLauncher.launch(all);


    }

    @Override
    protected void onStart() {
        super.onStart();
        resetLista();

    }

    public void resetLista(){
        final ListView lstV = (ListView) this.findViewById(R.id.lstV);

        this.adapterDB = new SimpleCursorAdapter(
                this,
                R.layout.lstv_display,
                null,
                new String[] { DBManager.FINCA_NOMBRE, DBManager.FINCA_TIPO },
                new int[] { R.id.name, R.id.cat},
                0
        );


        lstV.setAdapter( this.adapterDB );

        Menu.this.adapterDB.changeCursor(Menu.this.gestorDB.getFincas(user));
    }

}