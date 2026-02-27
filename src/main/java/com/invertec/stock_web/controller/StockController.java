package com.invertec.stock_web.controller;

import com.invertec.stock_web.service.StockService;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        NumberFormat usdFormat = NumberFormat.getCurrencyInstance(Locale.US);
        // Totales reales (no los de la página)
        model.addAttribute("totalMateriales", service.contarTotal(centro, almacen, buscar));
        model.addAttribute("totalAlertas", service.contarTotalAlertas(centro, almacen));
        model.addAttribute("valorTotalInventario", usdFormat.format(service.obtenerValorTotalInventario(centro, almacen)));

        model.addAttribute("modo", "normal");

        return "stock";
    }
    
    @PostMapping("/stock/minimo")
public String actualizarStockMinimo(
        @RequestParam String material,
        @RequestParam String centro,
        @RequestParam String almacen,
        @RequestParam BigDecimal stockMinimo,
        Principal principal,
        RedirectAttributes redirectAttributes) {

    try {

        String usuario = principal != null
                ? principal.getName()
                : "sistema";

        service.actualizarStockMinimo(
                material,
                centro,
                almacen,
                stockMinimo,
                usuario);

        redirectAttributes.addFlashAttribute(
                "success",
                "Stock mínimo actualizado correctamente.");

    } catch (Exception e) {

        redirectAttributes.addFlashAttribute(
                "error",
                "Error al actualizar el stock mínimo.");
    }

    return "redirect:/";
}
}