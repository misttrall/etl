package com.invertec.stock_web.controller;

import com.invertec.stock_web.model.StockDTO;
import com.invertec.stock_web.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class StockController {

    private final StockService service;

    public StockController(StockService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String listarStock(@RequestParam(required = false) String buscar,
                              @RequestParam(defaultValue = "0") int pagina,
                              Model model) {

        Map<String, Object> data = service.obtenerPaginado(buscar, pagina, 50);
        model.addAllAttributes(data);

        model.addAttribute("totalMateriales", service.buscarTodos(buscar).size());
        model.addAttribute("totalAlertas", service.contarTotalAlertas());
        model.addAttribute("valorTotalInventario", service.obtenerValorTotalInventario());
        model.addAttribute("modo", "normal");

        return "stock";
    }

    @GetMapping("/stock/buscar")
    public String buscarStockAjax(@RequestParam(required = false) String buscar,
                                  Model model) {
        List<StockDTO> lista = service.buscarTodos(buscar);
        model.addAttribute("stocks", lista);
        model.addAttribute("totalPaginas", 1);
        model.addAttribute("paginaActual", 0);
        return "fragments/tabla :: tablaFragment";
    }

    @PostMapping("/stock/minimo")
    @ResponseBody
    public ResponseEntity<?> actualizarStockMinimo(@RequestParam String material,
                                                   @RequestParam BigDecimal stockMinimo,
                                                   Principal principal) {
        String usuario = principal != null ? principal.getName() : "sistema";
        service.actualizarStockMinimo(material, stockMinimo, usuario);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tabla")
    public String obtenerTablaParcial(@RequestParam(required = false) String buscar,
                                      @RequestParam(defaultValue = "0") int pagina,
                                      Model model) {
        Map<String, Object> data = service.obtenerPaginado(buscar, pagina, 50);
        model.addAllAttributes(data);
        return "fragments/tabla :: tablaFragment";
    }

    @GetMapping("/stock/autocomplete")
    @ResponseBody
    public List<Map<String, Object>> autocomplete(@RequestParam String buscar) {
        return service.buscarCoincidenciasRapidas(buscar);
    }
}