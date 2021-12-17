package org.esei.dm2.gestiondieta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {
    public static final String DB_NOMBRE = "GestionDieta";
    public static final int DB_VERSION = 14;
    private static DBManager instancia;

    public static final String TABLA_USUARIO = "usuario";
    public static final String TABLA_ALIMENTO = "alimento";
    public static final String TABLA_HISTORICO = "historico";
    public static final String COL_NOM_USUARIO = "_id";
    public static final String COL_PASSWORD = "pwd";
    public static final String COL_ADMIN = "admin";
    public static final String USUARIO_ALTURA = "altura";
    public static final String USUARIO_PESO = "peso";
    public static final String USUARIO_SEXO = "sexo";
    public static final String USUARIO_EDAD = "edad";
    public static final String USUARIO_OBJETIVO = "objetivo";
    public static final String NOMBRE_ALIMENTO = "_id";
    public static final String CANTIDAD_ALIMENTO = "cantidad";
    public static final String CALORIAS_ALIMENTO = "calorias";
    public static final String VISIBILIDAD_ALIMENTO = "visible";
    public static final String ID_HISTORICO = "_id";
    public static final String FECHA = "fecha";
    public static final String NOMBRE_USUARIO = "nombre_usuario";
    public static final String ALIMENTOS_CONSUMIDOS = "alimentos_consumidos";
    public static final String CALORIAS_QUEMADAS = "calorias_quemadas";






    private DBManager(Context context)
    {
        super( context, DB_NOMBRE, null, DB_VERSION);
    }

    public static DBManager getManager(Context c)//si esta nulo instancia la base de datos y la devuelve
            //de no ser asi, devuelve simplemente la instancia
    {
        if ( instancia == null ) {
            instancia = new DBManager(c);
        }
        return instancia;//devuelve instancia de base de datos
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.i(  "DBManager",
                "Creando BBDD " + DB_NOMBRE + " v" + DB_VERSION);

        try {
            db.beginTransaction();
            db.execSQL( "CREATE TABLE IF NOT EXISTS " + TABLA_USUARIO + "( "
                    + COL_NOM_USUARIO + " string(255) PRIMARY KEY NOT NULL, "
                    + COL_PASSWORD + " string(255) NOT NULL, "
                    + COL_ADMIN + " bool NOT NULL,"
                    + USUARIO_ALTURA + " INTEGER DEFAULT NULL, "
                    + USUARIO_PESO + " INTEGER DEFAULT NULL, "
                    + USUARIO_SEXO + " TEXT DEFAULT NULL, "
                    + USUARIO_EDAD + " INTEGER DEFAULT NULL,"
                    + USUARIO_OBJETIVO + " TEXT DEFAULT NULL"
                    +")");
            db.execSQL( "CREATE TABLE IF NOT EXISTS " + TABLA_ALIMENTO + "( "
                    + NOMBRE_ALIMENTO + " string(255) PRIMARY KEY NOT NULL, "
                    + CANTIDAD_ALIMENTO + " INTEGER NOT NULL, "
                    + CALORIAS_ALIMENTO + " INTEGER NOT NULL,"
                    + VISIBILIDAD_ALIMENTO + " bool NOT NULL"
                    +")");
            db.execSQL( "CREATE TABLE IF NOT EXISTS " + TABLA_HISTORICO + "( "
                    + ID_HISTORICO + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + NOMBRE_USUARIO + " string(255) NOT NULL,"
                    + FECHA + " date NOT NULL, "
                    + CALORIAS_QUEMADAS + " double NOT NULL DEFAULT 0,"
                    + ALIMENTOS_CONSUMIDOS + " string(255) NOT NULL"
                    +")");
            db.setTransactionSuccessful();
        }
        catch(SQLException exc)
        {
            Log.e( "DBManager.onCreate", exc.getMessage() );
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i(  "DBManager",
                "DB: " + DB_NOMBRE + ": v" + oldVersion + " -> v" + newVersion );

        try {
            db.beginTransaction();
            db.execSQL( "DROP TABLE IF EXISTS " + TABLA_USUARIO );
            db.execSQL( "DROP TABLE IF EXISTS " + TABLA_ALIMENTO );
            db.execSQL( "DROP TABLE IF EXISTS " + TABLA_HISTORICO );
            db.setTransactionSuccessful();
        }  catch(SQLException exc) {
            Log.e( "DBManager.onUpgrade", exc.getMessage() );
        }
        finally {
            db.endTransaction();
        }

        this.onCreate( db );
    }

    /** Devuelve todos los usuaros en la BD
     * @return Un Cursor con los usuarios. */
    public Cursor getUsuarios()
    {
        return this.getReadableDatabase().query( TABLA_USUARIO,
                null, null, null, null, null, COL_NOM_USUARIO );
    }

    public Cursor getUsuariosNormales()
    {
        return this.getReadableDatabase().query( TABLA_USUARIO,
                null, COL_ADMIN + "=?", new String[]{ "0" }, null, null, COL_NOM_USUARIO );
    }

    public Cursor getAdmins()
    {
        return this.getReadableDatabase().query( TABLA_USUARIO,
                null, COL_ADMIN + "=?", new String[]{ "1" }, null, null, COL_NOM_USUARIO );
    }

    public Cursor getAlimento(String nombre)
    {
        return this.getReadableDatabase().query( TABLA_ALIMENTO,
                new String[]{NOMBRE_ALIMENTO, CANTIDAD_ALIMENTO, CALORIAS_ALIMENTO}, NOMBRE_ALIMENTO + "=?", new String[]{ nombre }, null, null, null );
    }

    public Cursor getAlimentos()
    {
        return this.getReadableDatabase().query( TABLA_ALIMENTO,
                null, null, null, null, null, NOMBRE_ALIMENTO );
    }

    public Cursor getAlimentosVisibles()
    {
        return this.getReadableDatabase().query( TABLA_ALIMENTO,
                null, VISIBILIDAD_ALIMENTO + "=?", new String[]{ "1" }, null, null, NOMBRE_ALIMENTO );
    }

    public Cursor getAlimentosOcultos()
    {
        return this.getReadableDatabase().query( TABLA_ALIMENTO,
                null, VISIBILIDAD_ALIMENTO + "=?", new String[]{ "0" }, null, null, NOMBRE_ALIMENTO );
    }

    public Cursor getHistorialUsuario(String nombre)
    {
        return this.getReadableDatabase().query( TABLA_HISTORICO,
               null, NOMBRE_USUARIO + "=?", new String[]{ nombre }, null, null, FECHA +" DESC");
    }

    /** Devuelve el usuario con el nombre de usuario especificado
     * @return Un Cursor con el usuario. */
    public Cursor getUsuario(String nombre){

        return this.getReadableDatabase().rawQuery(" SELECT " +COL_NOM_USUARIO+", " +USUARIO_ALTURA+", "+USUARIO_PESO+", "+USUARIO_SEXO+", " +USUARIO_EDAD+", " +USUARIO_OBJETIVO+" FROM " +TABLA_USUARIO+ " WHERE " + COL_NOM_USUARIO +"=?",new String[]{nombre});
    }

    /** Inserta un nuevo item.
     * @param username El username del item.
     * @param password La password del item.
     * @return true si se pudo insertar (o modificar), false en otro caso.
     */
    public boolean insertaItem(String username, String password, int admin)
    {
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put( COL_NOM_USUARIO, username );
        values.put( COL_PASSWORD, password );
        values.put( COL_ADMIN, admin );

        try {
            db.beginTransaction();
            /*cursor = db.query( TABLA_USUARIO,
                    null,
                    COL_NOM_USUARIO + "=?",
                    new String[]{ username },
                    null, null, null, null );

            if ( cursor.getCount() > 0 ) {
                db.update( TABLA_USUARIO,
                        values, COL_NOM_USUARIO + "= ?", new String[]{ username } );
            } else {*/
            db.insertWithOnConflict( TABLA_USUARIO, null, values, SQLiteDatabase.CONFLICT_ABORT );
            //}
            db.setTransactionSuccessful();
            toret = true;
        } catch(SQLException exc)
        {
            Log.e( "DBManager.inserta", exc.getMessage() );
        }
        finally {
            if ( cursor != null ) {
                cursor.close();
            }

            db.endTransaction();
        }

        return toret;
    }

    public boolean insertaAlimento(String nombre, int cantidad, int calorias)
    {
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put( NOMBRE_ALIMENTO, nombre );
        values.put( CANTIDAD_ALIMENTO, cantidad );
        values.put( CALORIAS_ALIMENTO, calorias );
        values.put( VISIBILIDAD_ALIMENTO, false );

        try {
            db.beginTransaction();
            db.insertWithOnConflict( TABLA_ALIMENTO, null, values, SQLiteDatabase.CONFLICT_ABORT );
            //}
            db.setTransactionSuccessful();
            toret = true;
        } catch(SQLException exc)
        {
            Log.e( "DBManager.inserta", exc.getMessage() );
            toret = false;
        }
        finally {
            if ( cursor != null ) {
                cursor.close();
            }

            db.endTransaction();
        }

        return toret;
    }

    public boolean insertaHistorico(String nombre, String fecha, double calorias, String alimentos)
    {
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put( NOMBRE_USUARIO, nombre );
        values.put( FECHA, fecha );
        values.put( CALORIAS_QUEMADAS, calorias );
        values.put( ALIMENTOS_CONSUMIDOS, alimentos );

        try {
            db.beginTransaction();
            db.insert( TABLA_HISTORICO, null, values );
            //}
            db.setTransactionSuccessful();
            toret = true;
        } catch(SQLException exc)
        {
            Log.e( "DBManager.inserta", exc.getMessage() );
            toret = false;
        }
        finally {
            if ( cursor != null ) {
                cursor.close();
            }

            db.endTransaction();
        }

        return toret;
    }


    public boolean modificaItem(String username, String password, int admin)
    {
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put( COL_NOM_USUARIO, username );
        values.put( COL_PASSWORD, password );
        values.put( COL_ADMIN, admin );

        try {
            db.beginTransaction();
            /*cursor = db.query( TABLA_USUARIO,
                    null,
                    COL_NOM_USUARIO + "=?",
                    new String[]{ username },
                    null, null, null, null );

            if ( cursor.getCount() > 0 ) {*/
            db.updateWithOnConflict( TABLA_USUARIO,
                    values, COL_NOM_USUARIO + "= ?", new String[]{ username }, SQLiteDatabase.CONFLICT_IGNORE );
            /*} else {
                db.insert( TABLA_USUARIO, null, values );
            }*/

            db.setTransactionSuccessful();
            toret = true;
        } catch(SQLException exc)
        {
            Log.e( "DBManager.modifica", exc.getMessage() );
        }
        finally {
            if ( cursor != null ) {
                cursor.close();
            }

            db.endTransaction();
        }

        return toret;
    }

    public boolean ocultaDesoculta(String nombre, int visibilidad){
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if(visibilidad == 1)
            values.put( VISIBILIDAD_ALIMENTO, false );
        else values.put(VISIBILIDAD_ALIMENTO, true);

        try {
            db.beginTransaction();
            db.update( TABLA_ALIMENTO,
                    values, NOMBRE_ALIMENTO + "= ?", new String[]{ nombre } );
            db.setTransactionSuccessful();
            toret = true;
        } catch(SQLException exc)
        {
            Log.e( "DBManager.modifica", exc.getMessage() );
        }
        finally {
            if ( cursor != null ) {
                cursor.close();
            }

            db.endTransaction();
        }

        return toret;
    }

    public boolean modificaAlimento(String nombre, int cantidad, int calorias )
    {
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put( NOMBRE_ALIMENTO, nombre );
        values.put( CANTIDAD_ALIMENTO, cantidad );
        values.put( CALORIAS_ALIMENTO, calorias );

        try {
            db.beginTransaction();
            db.updateWithOnConflict( TABLA_ALIMENTO,
                    values, NOMBRE_ALIMENTO + "= ?", new String[]{ nombre }, SQLiteDatabase.CONFLICT_IGNORE );
            db.setTransactionSuccessful();
            toret = true;
        } catch(SQLException exc)
        {
            Log.e( "DBManager.modifica", exc.getMessage() );
        }
        finally {
            if ( cursor != null ) {
                cursor.close();
            }

            db.endTransaction();
        }

        return toret;
    }

    public boolean modificaHistorico(String nombre, String fecha, double calorias, String alimentos) {
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CALORIAS_QUEMADAS, calorias);
        values.put(ALIMENTOS_CONSUMIDOS, alimentos);

        try {
            db.beginTransaction();
            db.update(TABLA_HISTORICO,
                    values,  NOMBRE_USUARIO + " = ? AND " + FECHA + " = ?", new String[]{nombre, fecha});
            db.setTransactionSuccessful();
            toret = true;
        } catch (SQLException exc) {
            Log.e("DBManager.modifica", exc.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            db.endTransaction();
        }

        return toret;
    }



    /** Elimina un elemento de la base de datos
     * @param username El identificador del elemento.
     * @return true si se pudo eliminar, false en otro caso.
     */
    public boolean eliminaItem(String username)
    {
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            db.delete( TABLA_USUARIO, COL_NOM_USUARIO + "=?", new String[]{ username } );
            db.setTransactionSuccessful();
            toret = true;
        } catch(SQLException exc) {
            Log.e( "DBManager.elimina", exc.getMessage() );
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    public boolean eliminaAlimento(String nombre)
    {
        boolean toret = false;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();
            db.delete( TABLA_ALIMENTO, NOMBRE_ALIMENTO + "=?", new String[]{ nombre } );
            db.setTransactionSuccessful();
            toret = true;
        } catch(SQLException exc) {
            Log.e( "DBManager.elimina", exc.getMessage() );
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    /** Comprueba si existe el usuario.
     * @param username El username a buscar.
     * @return true si el usuario existe, false en otro caso.
     */
    public int existeItem(String username, String password)
    {
        Cursor cursor = null;
        int toret = -1;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            db.beginTransaction();
            cursor = db.query( TABLA_USUARIO,                                   // FROM tabla
                    null,                                              // SELECT (null equivale a *)
                    COL_NOM_USUARIO + "=? AND " + COL_PASSWORD + "=?", // WHERE
                    new String[]{ username, password },                         // valor(es) de la(s) "?" en el WHERE
                    null, null, null, null );

            if ( cursor.getCount() > 0 ) { // si existe el usuario
                cursor.moveToFirst();
                toret = cursor.getInt(2); // 0 si no es admin, 1 si lo es
            }
            db.setTransactionSuccessful();
        } catch(SQLException exc)
        {
            Log.e( "DBManager.inserta", exc.getMessage() );
        }
        finally {
            if ( cursor != null ) {
                cursor.close();
            }

            db.endTransaction();
        }

        return toret;
    }

    public boolean existeHistorico(String username, String fecha)
    {
        Cursor cursor = null;
        boolean toret = false;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            db.beginTransaction();
            cursor = db.query( TABLA_HISTORICO,                                   // FROM tabla
                    null,                                              // SELECT (null equivale a *)
                    NOMBRE_USUARIO + "=? AND " + FECHA + "=?", // WHERE
                    new String[]{ username, fecha },                         // valor(es) de la(s) "?" en el WHERE
                    null, null, null, null );

            if ( cursor.getCount() > 0 ) { // si existe el usuario
                toret=true;
            }
            db.setTransactionSuccessful();
        } catch(SQLException exc)
        {
            Log.e( "DBManager.inserta", exc.getMessage() );
        }
        finally {
            if ( cursor != null ) {
                cursor.close();
            }

            db.endTransaction();
        }

        return toret;
    }

    public void updateUser(String nombre, int altura, int peso, String sexo, int edad, String objetivo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(USUARIO_ALTURA, altura);
        values.put(USUARIO_PESO, peso);
        values.put(USUARIO_SEXO, sexo);
        values.put(USUARIO_EDAD, edad);
        values.put(USUARIO_OBJETIVO, objetivo);

        db.beginTransaction();
        db.update(TABLA_USUARIO, values, COL_NOM_USUARIO +"= ?", new String[]{nombre} );
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}