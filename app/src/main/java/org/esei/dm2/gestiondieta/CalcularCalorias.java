package org.esei.dm2.gestiondieta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CalcularCalorias extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncherEdit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcular_calorias);

        this.items = new ArrayList<Alimento>();

        datos = ( (App) this.getApplication() ).getDatos();

        ImageButton btInserta = this.findViewById(R.id.btInserta);
        Button btCalcularBalance = this.findViewById(R.id.btCalculo);
        EditText ejercicio= this.findViewById(R.id.editTextEjercicio);
        TextView mostrarBalance= this.findViewById(R.id.textViewMostrarBalance);
        ListView lvItems = this.findViewById( R.id.lvLista );
        ListView lvItems2 = this.findViewById(R.id.lvLista2);
        TextView date = this.findViewById(R.id.textViewMostrarFecha);

        /**Para transformar la fecha se definen dos DateFormat, uno con el formato inicial y otro con el
         formato resultado, se parsea mediante el primer DateFormat la cadena con la fecha original a Date,
         posteriormente con el segundo DateFormat y format(Date) se pasa esa Date a cadena con el formato deseado**/

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = LocalDate.now().toString();
        Date dateStr= null;
        try {
            dateStr = inputFormat.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(dateStr);
        date.setText(outputDateStr);

        final Intent retData = getIntent(); //se obtienen los datos de resultado
        final Double metabolismo = retData.getExtras().getDouble( "metabolismo" );
        final String objetivo = retData.getExtras().getString( "objetivo" );

        this.itemsAdapter = new ArrayAdapter<Alimento>(
                this.getApplicationContext(),android.R.layout.simple_selectable_list_item,
                this.items
        );
        lvItems.setAdapter(this.itemsAdapter);

        lvItems2.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Cursor cursor = (Cursor) adaptadorDB.getItem(pos);
                Alimento alimento = new Alimento (cursor.getString(0), cursor.getInt(1), cursor.getInt(2));
                if ( pos >= 0 ){
                    CalcularCalorias.this.itemsAdapter.add(alimento);
                    int lastIndex = itemsAdapter.getCount() - 1;
                    lvItems.smoothScrollToPosition(lastIndex);
                }
            }
        });

        btInserta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalcularCalorias.this.onAdd();
            }
        });

        ActivityResultContract<Intent, ActivityResult> contract =
                new ActivityResultContracts.StartActivityForResult();
        ActivityResultCallback<ActivityResult> callback =
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) { //se comprueba el resultado
                            Intent retData = result.getData(); //se obtienen los datos de resultado
                            String nombre = retData.getExtras().getString( "nombre" );
                            int cantidad = retData.getExtras().getInt( "cantidad" );
                            int calorias = retData.getExtras().getInt( "calorias" );
                            Alimento alimento=new Alimento(nombre, cantidad, calorias);


                            CalcularCalorias.this.itemsAdapter.add(alimento);
                            int lastIndex = itemsAdapter.getCount() - 1;
                            lvItems.smoothScrollToPosition(lastIndex);
                            CalcularCalorias.this.dbman.insertaAlimento(nombre, cantidad, calorias, false);

                            /** al añadir alimentos con nombres que aun no se encuentran registrados
                             en la BD, son insertados automaticamente, de esta manera la
                             BD va creciendo automaticamente a medida que los usuarios hacen uso
                             de la aplicación. Los alimentos con nombres que ya figuran en la BD NO SON INSERTADOS*/

                            Toast.makeText( CalcularCalorias.this, "Alimento agregado correctamente.", Toast.LENGTH_SHORT ).show();

                        } else {
                            Toast.makeText( CalcularCalorias.this, "Error al añadir alimento.", Toast.LENGTH_SHORT ).show();
                        }
                    }
                };

        btCalcularBalance.setOnClickListener(new View.OnClickListener() { /**Hace la suma de todas las calorias aportadas por los alimentos
         que el usuario ha consumido en el dia y le resta su metabolismo basal y las calorias quemadas realizando ejercicio*/
        @Override
        public void onClick(View view) {
            int calConsumidas=0;
            String nombresAlimentos ="";
            for(int i=0; i<itemsAdapter.getCount(); i++){
                calConsumidas+=itemsAdapter.getItem(i).getCalorias();
                nombresAlimentos= nombresAlimentos+itemsAdapter.getItem(i).getNombre()+"\n";
            }

            int ejer=0;

            if(!ejercicio.getText().toString().equals(""))
                ejer=Integer.parseInt(ejercicio.getText().toString());

            mostrarBalance.setText(calculoBalance(metabolismo, calConsumidas, ejer)+" calorias");
            actualizaHistorico(datos.getUsername(),fecha,calculoBalance(metabolismo, calConsumidas, ejer),nombresAlimentos);
            avisarBalance(objetivo,calculoBalance(metabolismo, calConsumidas, ejer));

        }
        });

        this.activityResultLauncherEdit = this.registerForActivityResult(contract, callback);

        this.registerForContextMenu( lvItems );

        this.dbman = DBManager.getManager(this.getApplicationContext());

        SharedPreferences prefs = this.getPreferences( Context.MODE_PRIVATE );

        String username = datos.getUsername();
        for (int i = 0; i < prefs.getInt(username,0); i++) {
            Alimento alimento = new Alimento(prefs.getString(username+"_"+i,""), prefs.getInt(username+"_"+i+"_cantidad", 1 ),prefs.getInt(username+"_"+i+"_calorias", 1 ));
            this.itemsAdapter.add(alimento);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Configurar lista
        final ListView lvLista = this.findViewById( R.id.lvLista2);

        this.adaptadorDB = new SimpleCursorAdapter(
                this,
                R.layout.lvlista_alimentos_2,
                null,
                new String[] { DBManager.NOMBRE_ALIMENTO, DBManager.CANTIDAD_ALIMENTO, DBManager.CALORIAS_ALIMENTO },
                new int[] { R.id.lvLista_Alimento_Nombre_2, R.id.lvLista_Alimento_Cantidad_2, R.id.lvLista_Alimento_Calorias_2 },
                0
        );

        lvLista.setAdapter( this.adaptadorDB );
        this.actualizaAlimentosVisibles();
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

        switch ( item.getItemId() ) {
            case R.id.item_contextual_elimina:
                if ( pos>=0) {
                    CalcularCalorias.this.items.remove(pos);
                    CalcularCalorias.this.itemsAdapter.notifyDataSetChanged();
                    toret = true;
                } else {
                    String msg = this.getString( R.string.msgNoPos ) + ": " + pos;
                    Log.e( "context_elimina", msg );
                    Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
                }

                break;
            case R.id.item_contextual_modifica:
                if ( pos>=0) {
                    modificarCantidad(pos);
                }

                break;
        }

        return toret;
    }

    @Override
    public void onPause()
    {
        super.onPause();

        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String username = datos.getUsername();
        if(!itemsAdapter.isEmpty()) {
            editor.putInt(username,itemsAdapter.getCount());
            for (int i = 0; i < itemsAdapter.getCount(); i++) {
                editor.putString(username+"_"+i,itemsAdapter.getItem(i).getNombre());
                editor.putInt(username+"_"+i+"_cantidad", itemsAdapter.getItem(i).getCantidad());
                editor.putInt(username+"_"+i+"_calorias", itemsAdapter.getItem(i).getCalorias());
            }
        }
        editor.apply();

        this.dbman.close();
        this.adaptadorDB.getCursor().close();
    }

    private void onAdd() {
        Intent subActividad = new Intent( CalcularCalorias.this, FormAlimento.class );

        subActividad.putExtra( "nombre", "" );
        subActividad.putExtra( "cantidad", "" );
        subActividad.putExtra( "calorias", "" );

        activityResultLauncherEdit.launch(subActividad);
    }

    private void modificarCantidad(int position){ /**Permite modificar la cantidad de un alimento concreto, modificando de esta manera el aporte calorico */
        final EditText edText = new EditText( CalcularCalorias.this );

        AlertDialog.Builder builder = new AlertDialog.Builder(CalcularCalorias.this);
        builder.setTitle("Modificar cantidad en gramos de "+CalcularCalorias.this.itemsAdapter.getItem(position).getNombre());
        edText.setText(String.valueOf(CalcularCalorias.this.itemsAdapter.getItem(position).getCantidad()));
        builder.setView(edText);
        builder.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final int cantidad = Integer.parseInt(edText.getText().toString());

                int cantidadAnterior = CalcularCalorias.this.itemsAdapter.getItem(position).getCantidad();
                int caloriasAnterior = CalcularCalorias.this.itemsAdapter.getItem(position).getCalorias();
                CalcularCalorias.this.itemsAdapter.getItem(position).setCantidad(cantidad);
                CalcularCalorias.this.itemsAdapter.getItem(position).setCalorias((cantidad*caloriasAnterior)/cantidadAnterior);
                CalcularCalorias.this.itemsAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private double calculoBalance(Double metabolismo, int calConsumidas, int ejercicio) {
        return calConsumidas-(metabolismo+ejercicio);
    }

    private void actualizaAlimentosVisibles()
    {
        this.adaptadorDB.changeCursor( this.dbman.getAlimentosVisibles() );
    }

    private void actualizaHistorico(String nombre, String fecha, double calorias, String alimentos){
        if(dbman.existeHistorico(nombre, fecha)){
            dbman.modificaHistorico(nombre,fecha,calorias,alimentos);
        }
        else{
            dbman.insertaHistorico(nombre,fecha,calorias,alimentos);
        }
    }

    private void avisarBalance(String objetivo, double balance){
        if(objetivo.equals("Subir de peso")){
            if(balance < 500){
                Toast.makeText( CalcularCalorias.this, "Estas comiendo muy poco, aumenta tu ingesta calorica", Toast.LENGTH_SHORT ).show();
            }
            else if(balance <=1500){
                Toast.makeText( CalcularCalorias.this, "Estas en el buen camino, sigue con este superhabit calorico", Toast.LENGTH_SHORT ).show();
            }
            else Toast.makeText( CalcularCalorias.this, "Estás con un superavit demasiado elevado, tu salud podría resentirse", Toast.LENGTH_SHORT ).show();
        }
        else if(objetivo.equals("Mantener peso")){
            if(balance < 500 &&
                    balance > -500){
                Toast.makeText( CalcularCalorias.this, "Vas bien, sigue con una ingesta calórica similar", Toast.LENGTH_SHORT ).show();
            }
            else Toast.makeText( CalcularCalorias.this, "Debes ajustar tu balance a valores proximos a 0 para mantener el peso", Toast.LENGTH_SHORT ).show();
        }
        else{
            if(balance > -500){
                Toast.makeText( CalcularCalorias.this, "Estas comiendo demasiado, disminuye tu ingesta calorica", Toast.LENGTH_SHORT ).show();
            }
            else if(balance >= -1500){
                Toast.makeText( CalcularCalorias.this, "Estas en el buen camino, sigue con este déficit calorico", Toast.LENGTH_SHORT ).show();
            }
            else Toast.makeText( CalcularCalorias.this, "Estás con un déficit demasiado elevado, tu salud podría resentirse", Toast.LENGTH_SHORT ).show();
        }
    }


    private ArrayAdapter<Alimento> itemsAdapter;
    private ArrayList<Alimento> items;
    private DBManager dbman;
    private SimpleCursorAdapter adaptadorDB;
    private DatosUsuario datos;
}
