package org.esei.dm2.gestiondieta;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        this.datos = new DatosUsuario();
    }
    public DatosUsuario getDatos() {
        return this.datos;
    }
    public void setDatos(DatosUsuario datos) {
        this.datos = datos;
    }
    private DatosUsuario datos;

    public void logout() {
        this.datos = null;
    }
}