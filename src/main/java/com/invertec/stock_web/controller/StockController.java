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
                              @RequestParam(required = false) String centro,
                              @RequestParam(required = false) String almacen,
                              @RequestParam(required = false) String area,
                              @RequestParam(defaultValue = "0") int pagina,
                              Model model) {

        Map<String, Object> data = service.obtenerPaginado(buscar, centro, almacen, area, pagina, 50);
        model.addAllAttributes(data);

        model.addAttribute("totalMateriales", service.buscarTodos(buscar, centro, almacen, area).size());
        model.addAttribute("totalAlertas", service.contarTotalAlertas(centro, almacen));
        model.addAttribute("valorTotalInventario", service.obtenerValorTotalInventario(centro, almacen));
        model.addAttribute("modo", "normal");

        model.addAttribute("areasDisponibles", service.obtenerAreasDisponibles(centro, almacen));
        model.addAttribute("areaSeleccionada", area);
        model.addAttribute("centroSeleccionado", centro);
        model.addAttribute("almacenSeleccionado", almacen);
        model.addAttribute("buscar", buscar);

        return "stock";
    }

    @GetMapping("/tabla")
    public String obtenerTablaParcial(@RequestParam(required = false) String buscar,
                                      @RequestParam(required = false) String centro,
                                      @RequestParam(required = false) String almacen,
                                      @RequestParam(required = false) String area,
                                      @RequestParam(defaultValue = "0") int pagina,
                                      Model model) {

        Map<String, Object> data = service.obtenerPaginado(buscar, centro, almacen, area, pagina, 50);
        model.addAllAttributes(data);
        return "fragments/tabla :: tablaFragment";
    }

    @GetMapping("/stock/autocomplete")
    @ResponseBody
    public List<Map<String, Object>> autocomplete(@RequestParam String buscar,
                                                  @RequestParam(required = false) String centro,
                                                  @RequestParam(required = false) String almacen,
                                                  @RequestParam(required = false) String area) {
        return service.buscarCoincidenciasRapidas(buscar, centro, almacen, area);
    }

    @PostMapping("/stock/minimo")
    @ResponseBody
    public ResponseEntity<?> actualizarStockMinimo(@RequestParam String material,
                                                   @RequestParam BigDecimal stockMinimo,
                                                   @RequestParam(required = false) String centro,
                                                   @RequestParam(required = false) String almacen,
                                                   Principal principal) {
        String usuario = principal != null ? principal.getName() : "sistema";
        service.actualizarStockMinimo(material, stockMinimo, usuario, centro, almacen);
        return ResponseEntity.ok().build();
    }
}