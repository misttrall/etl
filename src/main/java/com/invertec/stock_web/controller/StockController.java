package com.invertec.stock_web.controller;

import com.invertec.stock_web.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StockController {

    private final StockService service;

    public StockController(StockService service) {
        this.service = service;
    }

    // 🔹 Vista principal con paginación
    @GetMapping("/")
    public String listarStock(
            @RequestParam(required = false) String centro,
            @RequestParam(required = false) String almacen,
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {

        var data = service.obtenerPaginado(
                centro,
                almacen,
                buscar,
                pagina,
                50);

        model.addAllAttributes(data);

        // Totales reales (no los de la página)
        model.addAttribute("totalMateriales", service.contarTotal(centro, almacen, buscar));
        model.addAttribute("totalAlertas", service.contarTotalAlertas(centro, almacen));
        model.addAttribute("valorTotalInventario", service.obtenerValorTotalInventario(centro, almacen));

        model.addAttribute("modo", "normal");

        return "stock";
    }

    // 🔹 Vista alertas SIN paginación
    @GetMapping("/alertas")
    public String listarStockBajo(
            @RequestParam(required = false) String centro,
            @RequestParam(required = false) String almacen,
            Model model) {

        var alertas = service.obtenerStockBajoFiltrado(centro, almacen);

        model.addAttribute("stocks", alertas);
        model.addAttribute("paginaActual", 1);
        model.addAttribute("totalPaginas", 1);
        // Totales del dashboard
        model.addAttribute("totalMateriales", service.contarTotal(null, null, null));
        model.addAttribute("totalAlertas", alertas.size());
        model.addAttribute("valorTotalInventario", service.obtenerValorTotalInventario(null, null));

        model.addAttribute("modo", "alertas");

        return "stock";
    }
}