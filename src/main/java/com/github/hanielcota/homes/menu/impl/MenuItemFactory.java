package com.github.hanielcota.homes.menu.impl;

import com.github.hanielcota.homes.domain.Home;
import com.github.hanielcota.homes.utils.ItemBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuItemFactory {

    public static ItemStack createHomeItem(Home home) {
        return new ItemBuilder(Material.BLACK_BED)
                .setName("§a" + home.getHomeName())
                .setLore("§7Clique para se teleportar para esta home.")
                .build();
    }

    public static ItemStack createBarrierItem() {
        return new ItemBuilder(Material.BARRIER).setName("§cVázio.").build();
    }

    public static ItemStack createOrderItem() {
        return new ItemBuilder(Material.HOPPER)
                .setName("§aOrganizar")
                .setLore(
                        "§7Clique para organizar alfabeticamente",
                        "§7os nomes das homes de A a Z.")
                .build();
    }

    public static ItemStack createBackItem() {
        return new ItemBuilder(Material.SPECTRAL_ARROW)
                .setName("§cFechar")
                .setLore("§7Clique para fechar o menu.")
                .build();
    }
}
