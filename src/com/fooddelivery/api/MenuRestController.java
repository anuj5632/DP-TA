package com.fooddelivery.api;

import com.fooddelivery.dto.MenuItemResponse;
import com.fooddelivery.models.MenuItemDefinition;
import com.fooddelivery.services.MenuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/menu")
public class MenuRestController {
    private final MenuService menuService;

    public MenuRestController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<MenuItemResponse> getMenu() {
        return menuService.getMenu().stream()
                .map(MenuRestController::toDto)
                .toList();
    }

    private static MenuItemResponse toDto(MenuItemDefinition def) {
        return new MenuItemResponse(
                def.getCode(),
                def.getType().name(),
                def.getDisplayName(),
                def.getBasePrice());
    }
}
