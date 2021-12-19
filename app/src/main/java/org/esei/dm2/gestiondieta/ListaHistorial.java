package org.esei.dm2.gestiondieta;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ListaHistorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_lista_historico);

        ListView lvLista = this.findViewById (R.id.lvListaHistorico);

        this.registerForContextMenu( lvLista );
        this.gestorDB = DBManager.getManager(this.getApplicationContext());

    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Configurar lista
        final ListView lvLista = this.findViewById( R.id.lvListaHistorico);

        final String nombre =  ( (App) this.getApplication() ).getDatos().getUsername();

        this.adaptadorDB = new SimpleCursorAdapter(
                this,
                R.layout.lvlista_historico,
                null,
                new String[] { DBManager.FECHA, DBManager.CALORIAS_QUEMADAS, DBManager.ALIMENTOS_CONSUMIDOS },
                new int[] { R.id.lvLista_Historico_Fecha, R.id.lvLista_Historico_CaloriasQuemadas, R.id.lvLista_Historico_Alimentos },
                0
        );

        this.adaptadorDB.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int column) {
                if( column == 2 ){ //la columna del campo fecha en el cursor
                    TextView tv = (TextView) view;
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String dateStr = cursor.getString(2);//recogemos la fecha en si
                    Date date = null;
                    try {
                        date = inputFormat.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(date);
                    tv.setText(outputDateStr);
                    return true;
                }
                return false;
            }
        });

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
