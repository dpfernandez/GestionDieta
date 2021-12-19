package org.esei.dm2.gestiondieta;

public class DatosUsuario {
    private String username;
    private boolean admin;

    public DatosUsuario() {
    }
    public DatosUsuario(String username, boolean admin) {
        this.username = username;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return admin;
    }
}
