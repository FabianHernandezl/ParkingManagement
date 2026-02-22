/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author fabia
 */
public class TipoVehiculoReportRow {

    private String tipoVehiculo;
    private int cantidad;
    private double porcentaje;
    private int totalVehiculos;
    private String fechaInicio;
    private String fechaFin;

    public TipoVehiculoReportRow() {
    }

    public TipoVehiculoReportRow(String tipoVehiculo,
            int cantidad,
            int totalVehiculos,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {
        this.tipoVehiculo = tipoVehiculo;
        this.cantidad = cantidad;
        this.totalVehiculos = totalVehiculos;
        this.porcentaje = totalVehiculos > 0 ? (cantidad * 100.0) / totalVehiculos : 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.fechaInicio = fechaInicio != null ? fechaInicio.format(formatter) : "N/A";
        this.fechaFin = fechaFin != null ? fechaFin.format(formatter) : "N/A";
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.porcentaje = totalVehiculos > 0 ? (cantidad * 100.0) / totalVehiculos : 0;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public int getTotalVehiculos() {
        return totalVehiculos;
    }

    public void setTotalVehiculos(int totalVehiculos) {
        this.totalVehiculos = totalVehiculos;
        this.porcentaje = totalVehiculos > 0 ? (cantidad * 100.0) / totalVehiculos : 0;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }
}
