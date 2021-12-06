package org.esei.dm2.gestiondieta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FormUsuario extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView( R.layout.activity_form_usuario);

        final TextView text = this.findViewById( R.id.textView );
        final ImageButton btGuardar = this.findViewById( R.id.btGuardar );
        final ImageButton btCancelar = this.findViewById( R.id.btCancelar );
        final EditText edPassword = this.findViewById( R.id.edPassword);
        final EditText edUsername = this.findViewById( R.id.edUsername);
        final CheckBox cbAdmin = this.findViewById( R.id.cbAdmin );

        final Intent datosEnviados = this.getIntent();
        // La actividad siempre entra con extras ya sean cadenas vacias a la hora de insertar o los campos del usuario a modificar

        final String username = datosEnviados.getExtras().getString( "username", "ERROR" );
        final boolean admin = datosEnviados.getExtras().getBoolean( "admin", false );

        btGuardar.setEnabled( false );

        edUsername.setText( username );
        if(username.equals("")) {
            if(admin) { // crear usuario
                text.setText(getString(R.string.crearUsuario));
            } else { // registrarse
                text.setText(getString(R.string.registrarseHeader));
                cbAdmin.setEnabled(false);
            }
        } else { // modificar
            text.setText(getString(R.string.modifUsuarioHeader));
            edUsername.setEnabled(false);
        }

        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormUsuario.this.setResult( Activity.RESULT_CANCELED );
                FormUsuario.this.finish();
            }
        });

        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user = edUsername.getText().toString();
                final String pass = edPassword.getText().toString();
                int admin;
                boolean checkAdmin = cbAdmin.isChecked();
                if (checkAdmin){
                    admin = 1;
                } else {
                    admin = 0;
                }
                final Intent retData = new Intent();

                retData.putExtra( "username", user );
                retData.putExtra( "password", pass );
                retData.putExtra( "admin", admin );

                if(!username.equals(""))
                    FormUsuario.this.setResult(1, retData); // si entra por aqui es para modificar
                else{
                    FormUsuario.this.setResult(RESULT_OK, retData); // si entra por aqui es para insertar
                }
                FormUsuario.this.finish();
            }
        });

        edUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btGuardar.setEnabled( edUsername.getText().toString().trim().length() > 0 && edPassword.getText().toString().trim().length() > 0);
            }
        });

        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btGuardar.setEnabled( edUsername.getText().toString().trim().length() > 0 && edPassword.getText().toString().trim().length() > 0);
            }
        });
    }
}