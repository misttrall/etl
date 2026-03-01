package com.invertec.stock_web.repository;

import com.invertec.stock_web.model.StockDTO;
import java.math.BigDecimal;
import java.util.List;

public interface IStockRepository {

    List<StockDTO> obtenerFiltradoPaginado(
            String centro,
            String almacen,
            String buscar,
            int pagina,
            int size);

    int contar(String centro, String almacen, String buscar);

    List<StockDTO> obtenerStockBajoFiltrado(
            String centro,
            String almacen);

    int contarStockBajo(String centro, String almacen);

    BigDecimal sumarValorTotalAlertas(String centro, String almacen);

    BigDecimal sumarValorTotal(String centro, String almacen);

    void guardarStockMinimo(
            String material,
            String centro,
            String almacen,
            BigDecimal stockMinimo,
            String usuario);
}