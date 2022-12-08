package com.example.dmtrabajo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registro extends AppCompatActivity {

    private DBManager gestorDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        EditText userText = (EditText) this.findViewById(R.id.edLogin);
        EditText passText = (EditText) this.findViewById(R.id.edPasswd);
        Button btnRegistro = (Button) this.findViewById(R.id.btnRegister);
        btnRegistro.setEnabled(false);
        Button btnBack = (Button) this.findViewById(R.id.btnBack);

        this.gestorDB = new DBManager(this.getApplicationContext());

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = userText.getText().toString();
                String pass = passText.getText().toString();

                if(Registro.this.gestorDB.existUser(user)){
                    Toast.makeText(Registro.this, R.string.existUsr,
                            Toast.LENGTH_SHORT).show();
                }else{
                    Intent reg = new Intent();

                    reg.putExtra("login",user);
                    reg.putExtra("pass",pass);

                    Registro.this.setResult(Login.RESULT_OK,reg);
                    Registro.this.finish();
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registro.this.setResult(Activity.RESULT_CANCELED);
                Registro.this.finish();
            }
        });

        userText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(userText.getText().toString().trim().length() > 0 &&
                        passText.getText().toString().trim().length() > 0){
                    if(!Registro.this.gestorDB.existUser(userText.getText().toString())){
                        btnRegistro.setEnabled(true);
                    }else{
                        Toast.makeText(Registro.this, R.string.existUsr,
                                Toast.LENGTH_SHORT).show();
                        btnRegistro.setEnabled(false);
                    }

                }else{
                    btnRegistro.setEnabled(false);
                }
            }
        });

        passText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(userText.getText().toString().trim().length() > 0 &&
                        passText.getText().toString().trim().length() > 0){
                    if(!Registro.this.gestorDB.existUser(userText.getText().toString())){
                        btnRegistro.setEnabled(true);
                    }else{
                        Toast.makeText(Registro.this, R.string.existUsr,
                                Toast.LENGTH_SHORT).show();
                        btnRegistro.setEnabled(false);
                    }

                }else{
                    btnRegistro.setEnabled(false);
                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        this.gestorDB.close();
    }
}