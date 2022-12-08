package com.example.dmtrabajo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {

    public static final String DB_NAME = "LEIRAZ";
    public static final int DB_VERSION = 6;

    public static final String TABLA_USUARIO = "user";
    public static final String USUARIO_LOGIN = "login";
    public static final String USUARIO_PASS = "pass";

    public static final String TABLA_FINCA = "finca";
    public static final String FINCA_NOMBRE = "_id";
    public static final String FINCA_TIPO = "tipo";
    public static final String FINCA_PERSONA = "user";
    public static final String FINCA_DESC = "descripcion";

    public static final String TABLA_MARCAS = "Marca";
    public static final String MARCAS_ID = "_idMarca";
    public static final String MARCAS_LAT = "lat";
    public static final String MARCAS_LON = "lon";
    public static final String MARCAS_FINCA = "_id";
    public static final String MARCAS_NUMERO = "num";

    public DBManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        Log.i("DBManager",
                "Creating DB" + DB_NAME + " v" + DB_VERSION);

        try{
            db.beginTransaction();
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLA_USUARIO + "( "
                    + USUARIO_LOGIN + " string(255) PRIMARY KEY NOT NULL, "
                    + USUARIO_PASS + " string(255) NOT NULL)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLA_FINCA + "( "
                    + FINCA_NOMBRE + " string(255) NOT NULL, "
                    + FINCA_TIPO + " string(255) NOT NULL,"
                    + FINCA_DESC + "  string(255) NOT NULL,"
                    + FINCA_PERSONA + " string(255) NOT NULL,"
                    + "PRIMARY KEY (" + FINCA_NOMBRE + " ," + FINCA_PERSONA + "))");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLA_MARCAS + "( "
                    + MARCAS_ID + " string(255) NOT NULL, "
                    + MARCAS_LAT + " float NOT NULL, "
                    + MARCAS_LON + " float NOT NULL, "
                    + MARCAS_FINCA + " float NOT NULL, "
                    + MARCAS_NUMERO + " float NOT NULL, "
                    + "PRIMARY KEY (" + MARCAS_ID + " ," + MARCAS_FINCA + " ," + MARCAS_NUMERO + "))");
            db.setTransactionSuccessful();
        }catch(SQLException exc){
            Log.e("DBManager.onCreate", exc.getMessage());
        }finally{
            db.endTransaction();
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.i("DBManager",
                "DB: " +  DB_NAME + ": v " +  oldVersion + " -> v" + newVersion);

        try{
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS "+  TABLA_USUARIO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLA_FINCA);
            db.execSQL("DROP TABLE IF EXISTS " + TABLA_MARCAS);
            db.setTransactionSuccessful();
        }catch(SQLException exc){
            Log.e("DBManager.onUpgrade", exc.getMessage());
        }finally{
            db.endTransaction();
        }

        this.onCreate(db);
    }

    public boolean addUser(String login, String pass){
        boolean toret = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USUARIO_LOGIN, login);
        values.put(USUARIO_PASS, pass);

        try{
            db.beginTransaction();
            cursor = db.query(TABLA_USUARIO,
                    null,
                    USUARIO_LOGIN + "=?",
                    new String[]{login},
                    null, null, null, null);
            if(cursor.getCount() == 0){
                db.insert(TABLA_USUARIO, null, values);
                db.setTransactionSuccessful();
                toret = true;
            }

        }catch(SQLException exc){
            Log.e("DBManager.addUser", exc.getMessage());

        }finally{
            if(cursor != null){
                cursor.close();
            }
            db.endTransaction();

        }

        return toret;
    }

    public  boolean existUser(String login){
        boolean toret = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            cursor = db.query(TABLA_USUARIO,
                    null,
                    USUARIO_LOGIN + "=?" , new String[]{login},
                    null, null, null, null);
            if(cursor.getCount() > 0){
                toret=true;
            }
            db.setTransactionSuccessful();
        }catch(SQLException exc){
            Log.e("DBManager.existUser", exc.getMessage());
        }finally{
            if(cursor != null) {
                cursor.close();
            }
            db.endTransaction();
        }

        return  toret;

    }

    public  boolean existFinca(String finca){
        boolean toret = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            cursor = db.query(TABLA_FINCA,
                    null,
                    FINCA_NOMBRE + "=?" , new String[]{finca},
                    null, null, null, null);
            if(cursor.getCount() > 0){
                toret=true;
            }
            db.setTransactionSuccessful();
        }catch(SQLException exc){
            Log.e("DBManager.existUser", exc.getMessage());
        }finally{
            if(cursor != null) {
                cursor.close();
            }
            db.endTransaction();
        }

        return  toret;

    }

    public boolean chekLog(String login, String pass){
        boolean toret = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            cursor = db.query(TABLA_USUARIO,
                    null,
                    USUARIO_LOGIN + "=? AND " + USUARIO_PASS + "=?" , new String[]{login,pass},
                    null, null, null, null);
            if(cursor.getCount() > 0){
                toret = true;
            }
            db.setTransactionSuccessful();
        }catch(SQLException exc){
            Log.e("DBManager.chekLog", exc.getMessage());
        }finally{
            if(cursor != null) {
                cursor.close();
            }
            db.endTransaction();
        }

        return  toret;
    }

    public boolean addFinca( String nombre ,String tipo ,String descrp  ,String user  ){
        boolean toret = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FINCA_NOMBRE,nombre);
        values.put(FINCA_TIPO,tipo);
        values.put(FINCA_DESC,descrp);
        values.put(FINCA_PERSONA,user);

        try{
            db.beginTransaction();
            cursor = db.query(TABLA_FINCA,
                    null,
                    FINCA_NOMBRE + "=? AND " +  FINCA_PERSONA + "=?",
                    new String[]{ nombre, user},
                    null, null, null, null);
            if(cursor.getCount() > 0){
                db.update(TABLA_FINCA,
                        values, FINCA_NOMBRE + "=? AND " + FINCA_PERSONA + "=?", new String[]{nombre, user});
            }else{
                db.insert(TABLA_FINCA, null, values);
            }

            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
            Log.e("DBManager.addFinca", exc.getMessage());
        }finally{
            if(cursor != null) {
                cursor.close();
            }
            db.endTransaction();
        }



        return  toret;
    }

    public boolean deleteFinca(String nombre,String user){
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();
            //despues eliminamos la finca
            db.delete(TABLA_FINCA, FINCA_NOMBRE + "=? AND " +  FINCA_PERSONA + "=?",
                    new String[]{nombre, user});
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
            Log.e("DBManager.deleteFinca", exc.getMessage());
        }finally {
            db.endTransaction();
        }

        return toret;

    }

    public boolean addMarca ( String nombre ,float lat , float lon,  String finca, int num){
        boolean toret = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MARCAS_ID,nombre);
        values.put(MARCAS_LAT,lat);
        values.put(MARCAS_LON,lon);
        values.put(MARCAS_FINCA,finca);
        values.put(MARCAS_NUMERO,num);

        try{
            db.beginTransaction();
            cursor = db.query(TABLA_MARCAS,
                    null,
                    MARCAS_ID + "=? AND " +  MARCAS_FINCA + "=? AND " + MARCAS_NUMERO
                            + "="+num+" ", new String[]{nombre,finca},
                    null, null, null, null);
            if(cursor.getCount() > 0){
                db.update(TABLA_MARCAS,
                        values, MARCAS_ID + "=? AND " + MARCAS_FINCA + "=? AND " +
                                MARCAS_NUMERO + "="+num+" AND " + MARCAS_LAT + "=" + lat +" AND " +  MARCAS_LON
                                + "="+ lon +"", new String[]{nombre,finca});
            }else{
                db.insert(TABLA_MARCAS, null, values);
            }

            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
            Log.e("DBManager.addMarca", exc.getMessage());
        }finally{
            if(cursor != null) {
                cursor.close();
            }
            db.endTransaction();
        }

        return  toret;

    }

    public boolean deleteMarcas(String nombre,String finca){
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();

            db.delete(TABLA_MARCAS, MARCAS_ID + "=? AND " +
                    MARCAS_FINCA + "=? ",new String[]{nombre,finca});
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
            Log.e("DBManager.deleteMarca", exc.getMessage());
        }finally {
            db.endTransaction();
        }

        return toret;
    }

    public Cursor getMarcas(String user, String finca){
        return  this.getReadableDatabase().query(TABLA_MARCAS,
                null, MARCAS_ID + "=? AND "+MARCAS_FINCA+"=? ", new String[]{user,finca}, null, null, null);
    }

    public Cursor getTipos(String user, String tipo){
        return this.getReadableDatabase().query(TABLA_FINCA,
                null, FINCA_PERSONA + "=? AND "+FINCA_TIPO+"=? ", new String[]{user,tipo}, null, null, null);
    }

    public boolean deleteMarca(String nombre,String finca, int num){
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.beginTransaction();

            db.delete(TABLA_MARCAS, MARCAS_ID + "=? AND " + MARCAS_NUMERO + "="+num+" AND " +
                    MARCAS_FINCA + "=? ",new String[]{nombre,finca});
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
            Log.e("DBManager.deleteMarca", exc.getMessage());
        }finally {
            db.endTransaction();
        }

        return toret;
    }

    public Cursor getFincas(String user){
        return this.getReadableDatabase().query(TABLA_FINCA,
                null, FINCA_PERSONA + "=? ", new String[]{user}, null, null, null);
    }


}


