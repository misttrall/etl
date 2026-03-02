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
    public Map<String, Object> obtenerPaginado(String buscar, int pagina, int size) {
        buscar = limpiar(buscar);

        List<StockDTO> lista = repository.obtenerFiltradoPaginadoDashboard(buscar, pagina, size);
        int total = repository.contarDashboard(buscar);
        int totalPaginas = (int) Math.ceil((double) total / size);

        Map<String, Object> data = new HashMap<>();
        data.put("stocks", lista);
        data.put("paginaActual", pagina);
        data.put("totalPaginas", totalPaginas);
        data.put("buscar", buscar);

        return data;
    }

    public List<StockDTO> buscarTodos(String buscar) {
        buscar = limpiar(buscar);
        return repository.obtenerFiltradoDashboard(buscar);
    }

    // ------------------- ALERTAS -------------------
    public List<StockDTO> obtenerStockBajo() {
        return repository.obtenerStockBajoDashboard();
    }

    public List<StockDTO> obtenerStockBajoPaginado(int pagina, int size) {
        return repository.obtenerStockBajoPaginadoDashboard(pagina, size);
    }

    public int contarTotalAlertas() {
        return repository.contarStockBajoDashboard();
    }

    public BigDecimal obtenerValorTotalAlertas() {
        BigDecimal total = repository.sumarValorTotalAlertasDashboard();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal obtenerValorTotalInventario() {
        BigDecimal total = repository.sumarValorTotalDashboard();
        return total != null ? total : BigDecimal.ZERO;
    }

    // ------------------- STOCK MINIMO -------------------
    public void actualizarStockMinimo(String material, BigDecimal stockMinimo, String usuario) {
        if (stockMinimo == null || stockMinimo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Stock mínimo inválido");
        }
        repository.guardarStockMinimo(material, stockMinimo, usuario);
    }

    // ------------------- AUTOCOMPLETE -------------------
    public List<Map<String, Object>> buscarCoincidenciasRapidas(String buscar) {
        buscar = limpiar(buscar);
        if (buscar == null || buscar.isBlank()) return List.of();
        return repository.buscarCoincidenciasRapidas("IN01", "1014", buscar, 10);
    }

    // ------------------- UTIL -------------------
    private String limpiar(String valor) {
        if (valor == null || valor.isBlank()) return null;
        return valor.trim();
    }
}