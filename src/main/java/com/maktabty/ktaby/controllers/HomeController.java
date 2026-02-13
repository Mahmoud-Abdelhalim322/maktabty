package com.maktabty.ktaby.controllers;

import com.maktabty.ktaby.services.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final SearchService searchService;

    public HomeController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/")
    public String home() {
        return "homepage/index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String query, Model model) {
        if (query != null && !query.trim().isEmpty()) {
            model.addAttribute("results", searchService.search(query.trim()));
            model.addAttribute("query", query);
        }
        return "search/results";
    }
}
