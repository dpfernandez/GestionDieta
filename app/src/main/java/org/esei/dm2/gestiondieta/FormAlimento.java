package org.esei.dm2.gestiondieta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;

public class FormAlimento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_form_alimento);

        final TextView text = this.findViewById( R.id.anadirAlimento );
        final ImageButton btGuardar = this.findViewById( R.id.btGuardarAlimento );
        final ImageButton btCancelar = this.findViewById( R.id.btCancelarAlimento );
        final EditText edNombre = this.findViewById( R.id.edNombre);
        final EditText edCantidad = this.findViewById( R.id.edCantidad);
        final EditText edCalorias = this.findViewById( R.id.edCalorias);


        final Intent datosEnviados = this.getIntent();
        // La actividad siempre entra con extras ya sean cadenas vacias a la hora de insertar o los campos del usuario a modificar


        final String nombre = datosEnviados.getExtras().getString("nombre");
        final String cantidad = datosEnviados.getExtras().getString("cantidad");
        final String calorias = datosEnviados.getExtras().getString("calorias");

        if(!nombre.equals("") && !cantidad.equals("") && !calorias.equals("")) {
           text.setText("Modificar alimento");
           edNombre.setEnabled(false);
        }

        edNombre.setText(nombre);
        edCantidad.setText(cantidad);
        edCalorias.setText(calorias);


        btGuardar.setEnabled( false );

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String n = edNombre.getText().toString();
                final int cant = Integer.parseInt(edCantidad.getText().toString());
                final int cal = Integer.parseInt(edCalorias.getText().toString());
                final Intent retData = new Intent();

                retData.putExtra( "nombre", n );
                retData.putExtra( "cantidad", cant );
                retData.putExtra( "calorias", cal );

                if(!nombre.equals("") && !cantidad.equals("") && !calorias.equals("")) {

                    FormAlimento.this.setResult(1, retData);// si entra por aqui es para modificar
                }
                else{
                    FormAlimento.this.setResult(RESULT_OK, retData); // si entra por aqui es para insertar
                }
                FormAlimento.this.finish();
            }
        });



        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormAlimento.this.setResult( Activity.RESULT_CANCELED );
                FormAlimento.this.finish();
            }
        });



        edNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btGuardar.setEnabled( edNombre.getText().toString().trim().length() > 0 && edCantidad.getText().toString().trim().length() > 0 && edCalorias.getText().toString().trim().length() > 0);
            }
        });

        edCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btGuardar.setEnabled( edNombre.getText().toString().trim().length() > 0 && edCantidad.getText().toString().trim().length() > 0 && edCalorias.getText().toString().trim().length() > 0);
            }
        });

        edCalorias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btGuardar.setEnabled( edNombre.getText().toString().trim().length() > 0 && edCantidad.getText().toString().trim().length() > 0 && edCalorias.getText().toString().trim().length() > 0);
            }
        });
    }

}
