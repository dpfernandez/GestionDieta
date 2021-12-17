package org.esei.dm2.gestiondieta;

public class Alimento {
    private String nombre;
    private int cantidad;
    private int calorias;

    //Clase auxiliar para añadir alimentos en la actividad CalcularCalorias
    public Alimento(String n, int g, int cal){
        nombre=n;
        cantidad=g;
        calorias=cal;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCalorias() {
        return calorias;
    }

    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }

    @Override
    public String toString(){
        return nombre +"\n"+cantidad+ " gramos\n"+calorias+" kcal";
    }
}
