package com.invertec.stock_web.controller;

import com.invertec.stock_web.model.StockDTO;
import com.invertec.stock_web.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;

@Controller
public class AlertasController {

    private final StockService service;

    // Constructor inyecta el servicio
    public AlertasController(StockService service) {
        this.service = service;
    }

    /**
     * Vista de Alertas de Stock.
     * Muestra los materiales cuyo stock está por debajo del mínimo,
     * junto con el total de alertas y el valor monetario total de esos materiales.
     *
     * @param centro  filtro por centro (opcional)
     * @param almacen filtro por almacén (opcional)
     * @param model   modelo para pasar atributos a la vista
     * @return nombre de la vista "alertas"
     */
    @GetMapping("/alertas")
    public String alertas(
            @RequestParam(required = false) String centro,
            @RequestParam(required = false) String almacen,
            Model model) {

        // Obtener la lista de productos bajo stock
        List<StockDTO> stocksBajo = service.obtenerStockBajoFiltrado(centro, almacen);
        NumberFormat usdFormat = NumberFormat.getCurrencyInstance(Locale.US);
        
        // Pasar datos a la vista
        model.addAttribute("stocks", stocksBajo);
        model.addAttribute("totalAlertas", stocksBajo.size()); // cantidad de alertas
        model.addAttribute("valorTotalAlertas", usdFormat.format(service.obtenerValorTotalAlertas(centro, almacen))); // suma monetaria
        model.addAttribute("centroSeleccionado", centro);
        model.addAttribute("almacenSeleccionado", almacen);

        // Para usar en la paginación y rutas condicionales
        model.addAttribute("modo", "alertas");

        return "alertas/index.html";
    }
}