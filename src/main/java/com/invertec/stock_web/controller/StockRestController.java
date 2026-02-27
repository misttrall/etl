package com.invertec.stock_web.controller;

import com.invertec.stock_web.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stock")
public class StockRestController {

    private final StockService service;

    public StockRestController(StockService service) {
        this.service = service;
    }

    @GetMapping("/pagina")
    public Map<String, Object> obtenerPagina(
            @RequestParam(required = false) String centro,
            @RequestParam(required = false) String almacen,
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int pagina) {

        return service.obtenerPaginado(centro, almacen, buscar, pagina, 50);
    }
}