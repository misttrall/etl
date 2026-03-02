package com.invertec.stock_web.controller;

import com.invertec.stock_web.model.StockDTO;
import com.invertec.stock_web.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Controller
public class AlertasController {

    private final StockService service;

    public AlertasController(StockService service) {
        this.service = service;
    }

    @GetMapping("/alertas")
    public String alertas(Model model) {

        List<StockDTO> stocksBajo = service.obtenerStockBajo();

        NumberFormat usdFormat = NumberFormat.getCurrencyInstance(Locale.US);

        model.addAttribute("stocks", stocksBajo);
        model.addAttribute("totalAlertas", service.contarTotalAlertas());
        model.addAttribute("valorTotalAlertas", usdFormat.format(service.obtenerValorTotalAlertas()));
        model.addAttribute("modo", "alertas");

        return "alertas/index";
    }
}