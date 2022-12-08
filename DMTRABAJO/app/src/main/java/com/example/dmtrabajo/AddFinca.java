package com.example.dmtrabajo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddFinca extends AppCompatActivity {

    private DBManager gestorDB;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_finca);
        this.gestorDB = new DBManager(this.getApplicationContext());

        EditText edNombre = (EditText) this.findViewById(R.id.edNombre);
        Spinner edTipo = (Spinner) this.findViewById(R.id.edTipo);
        EditText edDesc = (EditText) this.findViewById(R.id.edDesc);

        Button btAdd = (Button) this.findViewById(R.id.btnA);
        Button btVolver =(Button) this.findViewById(R.id.btnBack);


        final Intent sendData = this.getIntent();
        btAdd.setEnabled(false);
        if(sendData.getExtras() != null){
            final String _id = sendData.getExtras().getString("_id", "");
            final String tipo = sendData.getExtras().getString("tipo", "");
            final String descripcion = sendData.getExtras().getString("descripcion", "");
            //user = sendData.getExtras().getString("user", "");


            edNombre.setText(_id);
            edNombre.setEnabled(false);
            btAdd.setEnabled(true);
            Adapter adapter = edTipo.getAdapter();
            int n = adapter.getCount();
            int i = 0;
            while(i<n && !edTipo.getItemAtPosition(i).equals(tipo)){
                i++;
            }
            edTipo.setSelection(i);

            edDesc.setText(descripcion);
        }

        edNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edNombre.getText().toString().trim().length()>0){
                    if(!AddFinca.this.gestorDB.existFinca(edNombre.getText().toString())){
                        btAdd.setEnabled(true);
                    }else{
                        Toast.makeText(AddFinca.this, R.string.existFinca,
                                Toast.LENGTH_SHORT).show();
                        btAdd.setEnabled(false);
                    }
                }else{
                    btAdd.setEnabled(false);
                }
            }
        });

        btAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String nombre = edNombre.getText().toString();
                final String tipo = edTipo.getSelectedItem().toString();
                final String desc = edDesc.getText().toString();

                final Intent retData = new Intent();

                retData.putExtra("nombre", nombre);
                retData.putExtra("tipo", tipo);
                retData.putExtra("descripcion", desc);


                AddFinca.this.setResult(Activity.RESULT_OK, retData);
                AddFinca.this.finish();
            }
        });
        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddFinca.this.setResult(Activity.RESULT_CANCELED);
                AddFinca.this.finish();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        this.gestorDB.close();
    }
}