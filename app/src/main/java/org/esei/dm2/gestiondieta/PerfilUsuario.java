package org.esei.dm2.gestiondieta;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilUsuario extends AppCompatActivity {
    private DBManager dbman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        this.dbman = new DBManager( this.getApplicationContext() );

        final TextView lblnombre = this.findViewById(R.id.textViewNombre);
        final EditText lblaltura = this.findViewById(R.id.editTextAltura);
        final EditText lblpeso = this.findViewById(R.id.editTextPeso);
        final TextView lblsexo = this.findViewById(R.id.seleccionSexo);
        final EditText lbledad = this.findViewById(R.id.editTextEdad);
        final TextView lblobjetivo = this.findViewById(R.id.textViewMostrarObjetivo);
        final TextView lblmetabolismo = this.findViewById(R.id.textViewMostrarMetabolismo);
        final Button btModifUsuario = this.findViewById(R.id.buttonModifPerfil);
        final Button btBalanceEnergia = this.findViewById(R.id.buttonBalanceEnergia);
        final Button btHistorial = this.findViewById(R.id.buttonConsultarHistorico);

        btBalanceEnergia.setEnabled(false);
        btHistorial.setEnabled(false);

        final Intent retData = getIntent(); //se obtienen los datos de resultado
        final String username = retData.getStringExtra( "username" );

        final Cursor cur= dbman.getUsuario(username);


        if(cur!=null && cur.getCount()>0) {

            if (cur.moveToFirst()) {
                btModifUsuario.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!lblaltura.getText().toString().equals("") && !lblpeso.getText().toString().equals("") && !lblsexo.getText().toString().equals("") && !lbledad.getText().toString().equals("") && !lblobjetivo.getText().toString().equals("")) {
                            if (Integer.parseInt(lblaltura.getText().toString()) < 50 || Integer.parseInt(lblaltura.getText().toString()) > 250) {
                                Toast.makeText(PerfilUsuario.this, "La altura debe ser entre 50 y 250 cm", Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(lblpeso.getText().toString()) < 20 || Integer.parseInt(lblpeso.getText().toString()) > 300){
                                Toast.makeText(PerfilUsuario.this, "El peso debe ser entre 20 y 300 kg", Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(lbledad.getText().toString()) < 10 || Integer.parseInt(lbledad.getText().toString()) > 120){
                                Toast.makeText(PerfilUsuario.this, "La edad debe ser entre 10 y 120 años", Toast.LENGTH_SHORT).show();
                            } else {
                                dbman.updateUser(username, Integer.parseInt(lblaltura.getText().toString()), Integer.parseInt(lblpeso.getText().toString()), lblsexo.getText().toString(), Integer.parseInt(lbledad.getText().toString()),lblobjetivo.getText().toString());
                                lblmetabolismo.setText(metabolismo(lblsexo.getText().toString(), Integer.parseInt(lblpeso.getText().toString()), Integer.parseInt(lblaltura.getText().toString()), Integer.parseInt(lbledad.getText().toString())) + " calorias");
                                btBalanceEnergia.setEnabled(true);
                                btHistorial.setEnabled(true);
                                Toast.makeText(PerfilUsuario.this, "Perfil guardado correctamente.", Toast.LENGTH_SHORT).show();
                            }
                        } else Toast.makeText(PerfilUsuario.this, "Por favor, completa antes tu perfil", Toast.LENGTH_SHORT).show();
                    }
                });

                lblsexo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder( PerfilUsuario.this );
                        builder.setTitle( "Elige opción" );
                        builder.setItems( new String[]{ "Hombre", "Mujer" }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dlg, int opc) {
                                if ( opc == 0 ) {
                                    lblsexo.setText("Hombre");
                                }
                                else lblsexo.setText("Mujer");
                            }
                        });
                        builder.create().show();
                    }
                });

                lblobjetivo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder( PerfilUsuario.this );
                        builder.setTitle( "Elige opción" );
                        builder.setItems( new String[]{ "Subir de peso", "Mantener peso" , "Perder peso" }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dlg, int opc) {
                                if ( opc == 0 ) {
                                    lblobjetivo.setText("Subir de peso");
                                }
                                else if(opc == 1) {
                                    lblobjetivo.setText("Mantener peso");
                                }
                                else lblobjetivo.setText("Perder peso");

                            }
                        });
                        builder.create().show();
                    }
                });

                lblnombre.setText(cur.getString(0));
                lblaltura.setText(cur.getString(1));
                lblpeso.setText(cur.getString(2));
                lblsexo.setText(cur.getString(3));
                lbledad.setText(cur.getString(4));
                lblobjetivo.setText(cur.getString(5));

                if(cur.getString(3) != null && cur.getString(2) != null && cur.getString(4) != null) {
                    lblmetabolismo.setText(metabolismo(cur.getString(3), cur.getInt(2), cur.getInt(1), cur.getInt(4)) + " calorias");
                    btBalanceEnergia.setEnabled(true);
                    btHistorial.setEnabled(true);
                }
            } else {
                lblnombre.setText("No se encontraron datos");
            }
        }

        btBalanceEnergia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lblaltura.getText().toString().equals("") && !lblpeso.getText().toString().equals("") && !lblsexo.getText().toString().equals("") && !lbledad.getText().toString().equals("") && !lblobjetivo.getText().toString().equals("")) {
                    lanzaBalanceEnergia(metabolismo(lblsexo.getText().toString(), Integer.parseInt(lblpeso.getText().toString()), Integer.parseInt(lblaltura.getText().toString()), Integer.parseInt(lbledad.getText().toString())), username, lblobjetivo.getText().toString());
                }
                else Toast.makeText(PerfilUsuario.this, "Por favor, completa antes tu perfil", Toast.LENGTH_SHORT).show();
            }
        });

        btHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzaHistorial(username);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private Double metabolismo(String sexo, int peso, int altura, int edad){
        if(sexo.equals("Hombre"))
            return 10*peso+6.25*altura-5*edad+5;
        else return 10*peso+6.25*altura-5*edad-161;
    }

    private void lanzaBalanceEnergia(Double metabolismo, String username, String objetivo)
    {
        Intent calcularCalorias=new Intent(new Intent(PerfilUsuario.this, CalcularCalorias.class));

        calcularCalorias.putExtra("metabolismo", metabolismo);
        calcularCalorias.putExtra("username", username);
        calcularCalorias.putExtra("objetivo", objetivo);

        PerfilUsuario.this.startActivity(calcularCalorias);
    }

    private void lanzaHistorial(String username){
        PerfilUsuario.this.startActivity(new Intent(PerfilUsuario.this, ListaHistorial.class).putExtra("username", username));
    }
}