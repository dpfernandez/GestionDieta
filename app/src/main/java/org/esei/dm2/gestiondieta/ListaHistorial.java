package org.esei.dm2.gestiondieta;

import android.app.Activity;
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
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class ListaHistorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_lista_historico);

        ListView lvLista = this.findViewById (R.id.lvListaHistorico);

        this.registerForContextMenu( lvLista );
        this.gestorDB = new DBManager( this.getApplicationContext() );

    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Configurar lista
        final ListView lvLista = this.findViewById( R.id.lvListaHistorico);

        final Intent retData = getIntent(); //se obtienen los datos de resultado
        final String nombre = retData.getStringExtra( "username" );

        this.adaptadorDB = new SimpleCursorAdapter(
                this,
                R.layout.lvlista_historico,
                null,
                new String[] { DBManager.FECHA, DBManager.CALORIAS_QUEMADAS, DBManager.ALIMENTOS_CONSUMIDOS },
                new int[] { R.id.lvLista_Historico_Fecha, R.id.lvLista_Historico_CaloriasQuemadas, R.id.lvLista_Historico_Alimentos },
                0
        );

        lvLista.setAdapter( this.adaptadorDB );
        this.actualizaHistorico(nombre);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        this.gestorDB.close();
        this.adaptadorDB.getCursor().close();
    }

    private void actualizaHistorico(String nombre)
    {

        this.adaptadorDB.changeCursor( this.gestorDB.getHistorialUsuario(nombre) );
    }

    private DBManager gestorDB;
    private SimpleCursorAdapter adaptadorDB;

}
