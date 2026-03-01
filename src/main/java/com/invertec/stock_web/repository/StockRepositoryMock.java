package com.invertec.stock_web.repository;

import com.invertec.stock_web.model.StockDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("local")
public class StockRepositoryMock implements IStockRepository {

    @Override
    public List<StockDTO> obtenerFiltradoPaginado(
            String centro, String almacen, String buscar, int pagina, int size) {

        List<StockDTO> lista = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            StockDTO s = new StockDTO();
            s.setMatnr("MAT00" + i);
            s.setDescripcion("Producto Demo " + i);
            s.setCentro("IN01");
            s.setAlmacen("1014");
            s.setStockLibre(new BigDecimal("10"));
            s.setPrecio(new BigDecimal("500"));
            s.setValorTotal(new BigDecimal("50000"));
            s.setStockMinimo(new BigDecimal("50"));
            lista.add(s);
        }

        return lista;
    }

    @Override public int contar(String c,String a,String b){ return 20; }

    @Override
    public List<StockDTO> obtenerStockBajoFiltrado(String c,String a){
        return obtenerFiltradoPaginado(c,a,null,0,20);
    }

    @Override public int contarStockBajo(String c,String a){ return 5; }

    @Override
    public BigDecimal sumarValorTotalAlertas(String c,String a){
        return new BigDecimal("100000");
    }

    @Override
    public BigDecimal sumarValorTotal(String c,String a){
        return new BigDecimal("500000");
    }

    @Override
    public void guardarStockMinimo(
            String m,String c,String a,BigDecimal s,String u){}
}