package com.invertec.stock_web.service;
import com.invertec.stock_web.repository.IStockRepository;
import com.invertec.stock_web.model.StockDTO;
import com.invertec.stock_web.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockService {

    private final IStockRepository repository;

    public StockService(IStockRepository repository) {
        this.repository = repository;
    }


    public Map<String, Object> obtenerPaginado(
            String centro,
            String almacen,
            String buscar,
            int pagina,
            int size) {

        centro = limpiar(centro);
        almacen = limpiar(almacen);
        buscar = limpiar(buscar);

        List<StockDTO> lista =
                repository.obtenerFiltradoPaginado(centro, almacen, buscar, pagina, size);

        int total = repository.contar(centro, almacen, buscar);
        int totalPaginas = (int) Math.ceil((double) total / size);

        Map<String, Object> data = new HashMap<>();
        data.put("stocks", lista);
        data.put("paginaActual", pagina);
        data.put("totalPaginas", totalPaginas);
        data.put("buscar", buscar);
        data.put("centroSeleccionado", centro);
        data.put("almacenSeleccionado", almacen);

        return data;
    }

    public List<StockDTO> obtenerStockBajoFiltrado(
            String centro,
            String almacen) {

        centro = limpiar(centro);
        almacen = limpiar(almacen);

        return repository.obtenerStockBajoFiltrado(centro, almacen);
    }

    public int contarTotal(
            String centro,
            String almacen,
            String buscar) {

        centro = limpiar(centro);
        almacen = limpiar(almacen);
        buscar = limpiar(buscar);

        return repository.contar(centro, almacen, buscar);
    }

    public int contarTotalAlertas(
            String centro,
            String almacen) {

        centro = limpiar(centro);
        almacen = limpiar(almacen);

        return repository.contarStockBajo(centro, almacen);
    }

    public BigDecimal obtenerValorTotalAlertas(String centro, String almacen) {
        BigDecimal total = repository.sumarValorTotalAlertas(centro, almacen);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal obtenerValorTotalInventario(
            String centro,
            String almacen) {

        centro = limpiar(centro);
        almacen = limpiar(almacen);

        BigDecimal total = repository.sumarValorTotal(centro, almacen);

        return total != null ? total : BigDecimal.ZERO;
    }

    private String limpiar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
    public void actualizarStockMinimo(String material,
                                   String centro,
                                   String almacen,
                                   BigDecimal stockMinimo,
                                   String usuario) {

    if (stockMinimo == null || stockMinimo.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("Stock mínimo inválido");
    }

    repository.guardarStockMinimo(
            material,
            centro,
            almacen,
            stockMinimo,
            usuario);
}
}