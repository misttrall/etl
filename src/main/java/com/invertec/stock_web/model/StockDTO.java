package com.invertec.stock_web.model;

import java.math.BigDecimal;

public class StockDTO {

    private String matnr;
    private String descripcion;
    private String centro;
    private String almacen;
    private BigDecimal stockLibre;
    private BigDecimal precio;
    private BigDecimal valorTotal;
    private String area;
    private BigDecimal stockMinimo;

    // Getters y Setters
    public String getMatnr() { return matnr; }
    public void setMatnr(String matnr) { this.matnr = matnr; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCentro() { return centro; }
    public void setCentro(String centro) { this.centro = centro; }

    public String getAlmacen() { return almacen; }
    public void setAlmacen(String almacen) { this.almacen = almacen; }

    public BigDecimal getStockLibre() { return stockLibre; }
    public void setStockLibre(BigDecimal stockLibre) { this.stockLibre = stockLibre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public BigDecimal getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(BigDecimal stockMinimo) { this.stockMinimo = stockMinimo; }
}