package org.esei.dm2.gestiondieta;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ListaUsuarios extends AppCompatActivity {
    private ActivityResultLauncher<Intent> activityResultLauncherEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.setContentView( R.layout.activity_lista_usuarios);

        ListView lvLista = this.findViewById( R.id.lvLista );
        ImageButton btInserta = this.findViewById( R.id.btInserta );

        // Inserta
        btInserta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lanzaEditor( "", true );
            }
        });


        ActivityResultContract<Intent, ActivityResult> contract =
                new ActivityResultContracts.StartActivityForResult();
        ActivityResultCallback<ActivityResult> callback =
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent retData = result.getData(); //se obtienen los datos de resultado
                        if (retData != null) {
                            String username = retData.getExtras().getString("username");
                            String password = retData.getExtras().getString("password");
                            int admin = retData.getExtras().getInt("admin");
                            if (result.getResultCode() == Activity.RESULT_OK) { // creando usuario
                                if (ListaUsuarios.this.gestorDB.insertaItem(username, password, admin)) {
                                    Toast.makeText(ListaUsuarios.this, "Usuario creado correctamente.", Toast.LENGTH_SHORT).show();
                                    actualizaUsuario();
                                } else {
                                    Toast.makeText(ListaUsuarios.this, "Error, el usuario ya existe.", Toast.LENGTH_SHORT).show();
                                }
                            } else if (result.getResultCode() == 1) { // modificando usuario
                                if (ListaUsuarios.this.gestorDB.modificaItem(username, password, admin)) {
                                    Toast.makeText(ListaUsuarios.this, "Usuario modificado correctamente.", Toast.LENGTH_SHORT).show();
                                    actualizaUsuario();
                                } else {
                                    Toast.makeText(ListaUsuarios.this, "Error al modificar el usuario.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ListaUsuarios.this, "ResultCode inválido.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                };
        this.activityResultLauncherEdit = this.registerForActivityResult(contract, callback);

        this.registerForContextMenu( lvLista );
        this.gestorDB = new DBManager( this.getApplicationContext() );
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Configurar lista
        final ListView lvLista = this.findViewById( R.id.lvLista );

        this.adaptadorDB = new SimpleCursorAdapter(
                this,
                R.layout.lvlista_usuario,
                null,
                new String[] { DBManager.COL_NOM_USUARIO, DBManager.COL_ADMIN },
                new int[] { R.id.lvLista_Usuario_Nombre, R.id.lvLista_Usuario_Admin },
                0
        );

        lvLista.setAdapter( this.adaptadorDB );
        this.actualizaUsuario();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        this.gestorDB.close();
        this.adaptadorDB.getCursor().close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu( menu );
        this.getMenuInflater().inflate( R.menu.main_menu_usuarios, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        boolean toret = false;
        switch( menuItem.getItemId() ) {
            case R.id.opUsuarios:
                this.actualizaUsuario();
                toret = true;
                break;
            case R.id.opAlimentos:
                ListaUsuarios.this.startActivity(new Intent(ListaUsuarios.this, ListaAlimentos.class));
                ListaUsuarios.this.finish();
                toret = true;
                break;
            case R.id.opAdmins:
                this.actualizaAdmins();
                toret = true;
                break;
            case R.id.opUsuariosNormales:
                this.actualizaUsuariosNormales();
                toret = true;
                break;
        }
        return toret;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu( menu, v, menuInfo );

        if ( v.getId() == R.id.lvLista ) {
            this.getMenuInflater().inflate( R.menu.lista_menu_contextual, menu );
        }

        return;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        boolean toret = super.onContextItemSelected(item);
        int pos = ( (AdapterView.AdapterContextMenuInfo) item.getMenuInfo() ).position;
        Cursor cursor = this.adaptadorDB.getCursor();

        switch ( item.getItemId() ) {
            case R.id.item_contextual_elimina:
                if ( cursor.moveToPosition( pos ) ) {
                    final String username = cursor.getString( 0 );
                    this.gestorDB.eliminaItem( username );
                    this.actualizaUsuario();
                    toret = true;
                } else {
                    String msg = this.getString( R.string.msgNoPos ) + ": " + pos;
                    Log.e( "context_elimina", msg );
                    Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
                }

                break;
            case R.id.item_contextual_modifica:
                if ( cursor.moveToPosition( pos ) ) {
                    final String username = cursor.getString( 0 );
                    final int psw = cursor.getInt( 1 );

                    lanzaEditor( username, true );
                    toret = true;
                } else {
                    String msg = this.getString( R.string.msgNoPos ) + ": " + pos;
                    Log.e( "context_modifica", msg );
                    Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
                }

                break;
        }

        return toret;
    }

    /** Actualiza el num. de elementos existentes en la vista. */
    private void actualizaUsuario()
    {
        final TextView lblNum = this.findViewById( R.id.lblNum );

        this.adaptadorDB.changeCursor( this.gestorDB.getUsuarios() );
        lblNum.setText( String.format( Locale.getDefault(),"%d", this.adaptadorDB.getCount() ) );
    }

    private void actualizaUsuariosNormales()
    {
        final TextView lblNum = this.findViewById( R.id.lblNum );

        this.adaptadorDB.changeCursor( this.gestorDB.getUsuariosNormales() );
        lblNum.setText( String.format( Locale.getDefault(),"%d", this.adaptadorDB.getCount() ) );
    }

    private void actualizaAdmins()
    {
        final TextView lblNum = this.findViewById( R.id.lblNum );

        this.adaptadorDB.changeCursor( this.gestorDB.getAdmins() );
        lblNum.setText( String.format( Locale.getDefault(),"%d", this.adaptadorDB.getCount() ) );
    }

    private void lanzaEditor(String username, boolean admin)
    {
        Intent subActividad = new Intent( ListaUsuarios.this, FormUsuario.class );

        subActividad.putExtra( "username", username );
        subActividad.putExtra( "admin", admin );

        activityResultLauncherEdit.launch(subActividad);
    }

    private DBManager gestorDB;
    private SimpleCursorAdapter adaptadorDB;
}