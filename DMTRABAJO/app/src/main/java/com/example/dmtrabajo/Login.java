package com.example.dmtrabajo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends AppCompatActivity {

    private DBManager gestorDB;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        EditText userText = (EditText) this.findViewById(R.id.edLog);
        EditText passText = (EditText) this.findViewById(R.id.edPass);
        Button btnLog = (Button) this.findViewById(R.id.btLog);
        btnLog.setEnabled(false);
        Button btnRegistrar = (Button) this.findViewById(R.id.btReg);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registro = new Intent(Login.this, Registro.class);
                activityResultLauncher.launch(registro);
            }
        });

        ActivityResultContract<Intent, ActivityResult> contract =
                new ActivityResultContracts.StartActivityForResult();

        ActivityResultCallback<ActivityResult> callback =
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            //Obtenemos los datos recibidos
                            Intent res = result.getData();
                            String login = res.getExtras().getString("login", "ERROR");
                            String pass = res.getExtras().getString("pass", "ERROR");

                            if(Login.this.gestorDB.addUser(login, pass)){
                                //En caso de que el usuario se registre exitosamente
                                Toast.makeText(Login.this, R.string.exitReg,
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                if(Login.this.gestorDB.existUser(login)){
                                    Toast.makeText(Login.this, R.string.existUsr,
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(Login.this, R.string.failReg,
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    }
                };

        this.gestorDB = new DBManager(this.getApplicationContext());
        this.activityResultLauncher = this.registerForActivityResult(contract, callback);

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = userText.getText().toString();
                String pass = passText.getText().toString();
                Intent log = new Intent();

                log.putExtra("login",user);
                log.putExtra("pass",pass);
                Login.this.setResult(Login.RESULT_OK,log);
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
                    if(!Login.this.gestorDB.existUser(userText.getText().toString())){
                        btnLog.setEnabled(false);
                    }else{
                        btnLog.setEnabled(true);
                    }

                }else{
                    btnLog.setEnabled(false);
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
                    if(!Login.this.gestorDB.existUser(userText.getText().toString())){
                        btnLog.setEnabled(false);
                    }else{
                        btnLog.setEnabled(true);
                    }

                }else{
                    btnLog.setEnabled(false);
                }
            }
        });

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Login.this.gestorDB.chekLog(userText.getText().toString(),
                        passText.getText().toString())){

                    Intent registerActivity = new Intent(Login.this, Menu.class);
                    registerActivity.putExtra("usuario",userText.getText().toString());
                    activityResultLauncher.launch(registerActivity);

                }else {
                    Toast.makeText(Login.this, R.string.malPass,
                            Toast.LENGTH_SHORT).show();
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