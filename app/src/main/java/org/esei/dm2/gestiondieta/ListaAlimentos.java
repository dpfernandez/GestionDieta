package org.esei.dm2.gestiondieta;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class ListaAlimentos extends AppCompatActivity {
    private ActivityResultLauncher<Intent> activityResultLauncherEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_lista_alimentos);

        ListView lvLista = this.findViewById (R.id.lvListaAlimentos);
        ImageButton btInserta = this.findViewById( R.id.btInsertaAlimento );

        // Inserta
        btInserta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lanzaEditorAlimento("","","");
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
                            String nombre = retData.getExtras().getString("nombre");
                            int cantidad = retData.getExtras().getInt("cantidad");
                            int calorias = retData.getExtras().getInt("calorias");

                            if (result.getResultCode() == Activity.RESULT_OK) { // creando alimento
                                if (ListaAlimentos.this.gestorDB.insertaAlimento(nombre, cantidad, calorias, true)) {
                                    Toast.makeText(ListaAlimentos.this, "Alimento añadido correctamente.", Toast.LENGTH_SHORT).show();
                                    actualizaAlimentos();
                                } else {
                                    Toast.makeText(ListaAlimentos.this, "Error, el alimento ya existe.", Toast.LENGTH_SHORT).show();
                                }

                            } else if (result.getResultCode() == 1) { // modificando alimento
                                if (ListaAlimentos.this.gestorDB.modificaAlimento(nombre, cantidad, calorias)) {
                                    Toast.makeText(ListaAlimentos.this, "Alimento modificado correctamente.", Toast.LENGTH_SHORT).show();
                                    actualizaAlimentos();
                                } else {
                                    Toast.makeText(ListaAlimentos.this, "Error al modificar el alimento.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ListaAlimentos.this, "ResultCode inválido.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                };
        this.activityResultLauncherEdit = this.registerForActivityResult(contract, callback);

        this.registerForContextMenu( lvLista );
        this.gestorDB = DBManager.getManager(this.getApplicationContext());

    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Configurar lista
        final ListView lvLista = this.findViewById( R.id.lvListaAlimentos);

        this.adaptadorDB = new SimpleCursorAdapter(
                this,
                R.layout.lvlista_alimentos,
                null,
                new String[] { DBManager.NOMBRE_ALIMENTO, DBManager.CANTIDAD_ALIMENTO, DBManager.CALORIAS_ALIMENTO, DBManager.VISIBILIDAD_ALIMENTO },
                new int[] { R.id.lvLista_Alimento_Nombre, R.id.lvLista_Alimento_Cantidad, R.id.lvLista_Alimento_Calorias, R.id.lvLista_Alimento_Visibilidad },
                0
        );

        lvLista.setAdapter( this.adaptadorDB );
        this.actualizaAlimentos();
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
        this.getMenuInflater().inflate( R.menu.main_menu_alimentos, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        boolean toret = false;
        switch( menuItem.getItemId() ) {
            case R.id.opUsuarios:
                ListaAlimentos.this.startActivity(new Intent(ListaAlimentos.this, ListaUsuarios.class));
                ListaAlimentos.this.finish();
                toret = true;
                break;
            case R.id.opAlimentos:
                this.actualizaAlimentos();
                toret = true;
                break;
            case R.id.opVisibles:
                this.actualizaAlimentosVisibles();
                toret = true;
                break;
            case R.id.opOcultos:
                this.actualizaAlimentosOcultos();
                toret = true;
                break;
        }
        return toret;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu( menu, v, menuInfo );

        if ( v.getId() == R.id.lvListaAlimentos ) {
            this.getMenuInflater().inflate( R.menu.lista_menu_contextual_alimentos, menu );
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
            case R.id.alimento_contextual_elimina:
                if ( cursor.moveToPosition( pos ) ) {
                    final String nombre = cursor.getString( 0 );
                    AlertDialog.Builder builder = new AlertDialog.Builder( ListaAlimentos.this );
                    builder.setTitle( "Estas seguro?" );
                    builder.setItems( new String[]{ "Si", "No" }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dlg, int opc) {
                            if ( opc == 0 ) {
                                ListaAlimentos.this.gestorDB.eliminaAlimento( nombre );
                                ListaAlimentos.this.actualizaAlimentos();
                            }
                            else builder.create().closeOptionsMenu();
                        }
                    });
                    builder.create().show();
                    toret = true;
                } else {
                    String msg = this.getString( R.string.msgNoPos ) + ": " + pos;
                    Log.e( "context_elimina", msg );
                    Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
                }

                break;
            case R.id.alimento_contextual_modifica:
                if ( cursor.moveToPosition( pos ) ) {
                    final String nombre = cursor.getString( 0 );
                    final String cantidad = String.valueOf(cursor.getInt( 1 ));
                    final String calorias = String.valueOf(cursor.getInt( 2 ));

                    lanzaEditorAlimento(nombre, cantidad, calorias);
                    toret = true;
                } else {
                    String msg = this.getString( R.string.msgNoPos ) + ": " + pos;
                    Log.e( "context_modifica", msg );
                    Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
                }

                break;
            case R.id.alimento_contextual_ocultaDesoculta:
                if ( cursor.moveToPosition( pos ) ) {
                    final String nombre = cursor.getString( 0 );
                    final int visibilidad = cursor.getInt( 3 );

                    this.gestorDB.ocultaDesoculta( nombre, visibilidad );
                    this.actualizaAlimentos();
                    toret = true;
                } else {
                    String msg = this.getString( R.string.msgNoPos ) + ": " + pos;
                    Log.e( "context_ocultaDesoculta", msg );
                    Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
                }

                break;
        }

        return toret;
    }

    private void actualizaAlimentos()
    {
        final TextView lblNum = this.findViewById( R.id.lblNumAlimentos );

        this.adaptadorDB.changeCursor( this.gestorDB.getAlimentos() );
        lblNum.setText( String.format( Locale.getDefault(),"%d", this.adaptadorDB.getCount() ) );
    }

    private void actualizaAlimentosVisibles()
    {
        final TextView lblNum = this.findViewById( R.id.lblNumAlimentos );

        this.adaptadorDB.changeCursor( this.gestorDB.getAlimentosVisibles() );
        lblNum.setText( String.format( Locale.getDefault(),"%d", this.adaptadorDB.getCount() ) );
    }

    private void actualizaAlimentosOcultos()
    {
        final TextView lblNum = this.findViewById( R.id.lblNumAlimentos );

        this.adaptadorDB.changeCursor( this.gestorDB.getAlimentosOcultos() );
        lblNum.setText( String.format( Locale.getDefault(),"%d", this.adaptadorDB.getCount() ) );
    }

    private void lanzaEditorAlimento(String nombre, String cantidad, String calorias)
    {
        Intent subActividad = new Intent( ListaAlimentos.this, FormAlimento.class );

        subActividad.putExtra( "nombre", nombre );
        subActividad.putExtra( "cantidad", cantidad );
        subActividad.putExtra( "calorias", calorias );

        activityResultLauncherEdit.launch(subActividad);
    }

    private DBManager gestorDB;
    private SimpleCursorAdapter adaptadorDB;

}
