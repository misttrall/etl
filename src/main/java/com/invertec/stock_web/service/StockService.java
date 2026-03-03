package com.invertec.stock_web.service;

import com.invertec.stock_web.model.StockDTO;
import com.invertec.stock_web.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockService {

    private final StockRepository repository;

    public StockService(StockRepository repository) {
        this.repository = repository;
    }

    // ------------------- PAGINADO -------------------
    public Map<String, Object> obtenerPaginado(String buscar, String centro, String almacen, String area, int pagina, int size) {
        buscar = limpiar(buscar);

        List<StockDTO> lista = repository.obtenerFiltradoPaginadoDashboard(buscar, centro, almacen, area, pagina, size);
        int total = repository.contarDashboard(buscar, centro, almacen, area);
        int totalPaginas = (int) Math.ceil((double) total / size);

        Map<String, Object> data = new HashMap<>();
        data.put("stocks", lista);
        data.put("paginaActual", pagina);
        data.put("totalPaginas", totalPaginas);
        data.put("buscar", buscar);
        data.put("centroSeleccionado", centro);
        data.put("almacenSeleccionado", almacen);
        data.put("areaSeleccionada", area);

        return data;
    }

    public List<StockDTO> buscarTodos(String buscar, String centro, String almacen, String area) {
        buscar = limpiar(buscar);
        return repository.obtenerFiltradoDashboard(buscar, centro, almacen, area);
    }

    // ------------------- ALERTAS -------------------
    public List<StockDTO> obtenerStockBajo(String centro, String almacen) {
        return repository.obtenerStockBajoDashboard(centro, almacen);
    }

    public int contarTotalAlertas(String centro, String almacen) {
        return repository.contarStockBajoDashboard(centro, almacen);
    }

    public BigDecimal obtenerValorTotalAlertas(String centro, String almacen) {
        return repository.sumarValorTotalAlertasDashboard(centro, almacen);
    }

    public BigDecimal obtenerValorTotalInventario(String centro, String almacen) {
        return repository.sumarValorTotalDashboard(centro, almacen);
    }

    // ------------------- STOCK MINIMO -------------------
    public void actualizarStockMinimo(String material, BigDecimal stockMinimo, String usuario, String centro, String almacen) {
        if (stockMinimo == null || stockMinimo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Stock mínimo inválido");
        }
        repository.guardarStockMinimo(material, centro, almacen, stockMinimo, usuario);
    }

    // ------------------- AUTOCOMPLETE -------------------
    public List<Map<String, Object>> buscarCoincidenciasRapidas(String buscar, String centro, String almacen, String area) {
        buscar = limpiar(buscar);
        if (buscar == null || buscar.isBlank()) return List.of();
        return repository.buscarCoincidenciasRapidas(buscar, centro, almacen, area, 10);
    }

    // ------------------- ÁREAS -------------------
    public List<String> obtenerAreasDisponibles(String centro, String almacen) {
        return repository.listarAreasDisponibles(centro, almacen);
    }

    // ------------------- UTIL -------------------
    private String limpiar(String valor) {
        if (valor == null || valor.isBlank()) return null;
        return valor.trim();
    }
}