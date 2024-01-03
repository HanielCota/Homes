package com.github.hanielcota.homes.menu.factory;

import com.github.hanielcota.homes.domain.Home;
import com.github.hanielcota.homes.utils.ItemBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuItemFactory {

    public static ItemStack createHomeItem(Home home) {
        String homeName = home.getHomeName();

        return new ItemBuilder(Material.BLACK_BED)
                .setName("§a" + homeName)
                .setLore("§7Clique para se teleportar até esta home.",
                        "",
                        "§7Segure 'Shift' e clique com o botão esquerdo",
                        "§7para excluir esta home.")
                .build();
    }

    public static ItemStack createBarrierItem() {
        return new ItemBuilder(Material.BARRIER).setName("§cVazio.").build();
    }

    public static ItemStack createOrderItem() {
        return new ItemBuilder(Material.HOPPER)
                .setName("§aOrganizar")
                .setLore("§7Clique para organizar alfabeticamente", "§7os nomes das homes de A a Z.")
                .build();
    }

    public static ItemStack createBackItem(boolean redirect) {
        String itemName = redirect ? "§aVoltar" : "§cFechar";
        String loreText = redirect ? "§7Clique para voltar ao menu anterior." : "§7Clique para fechar o menu.";

        return new ItemBuilder(Material.SPECTRAL_ARROW)
                .setName(itemName)
                .setLore(loreText)
                .build();
    }
}
