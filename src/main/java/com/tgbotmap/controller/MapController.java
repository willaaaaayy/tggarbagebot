package com.tgbotmap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @GetMapping("/public/map")
    public String showMap() {
        return "map";
    }
}
