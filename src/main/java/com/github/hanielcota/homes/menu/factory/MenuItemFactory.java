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
        boolean isPublic = home.isPublic();

        String publicStatus = isPublic ? "§aPública" : "§cPrivada";

        return new ItemBuilder(Material.BLACK_BED)
                .setName("§a" + homeName)
                .setLore(
                        "",
                        "§7Visibilidade: " + publicStatus,
                        "§7Clique para se teleportar para esta home.",
                        "",
                        "§7Clique com Shift e botão esquerdo para deletar a home.",
                        "",
                        "§7Caso clique com botão direito sem segurar Shift,",
                        "§7a home será tornada pública.")
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

    public static ItemStack createPublicHomeItem(Home home) {
        return new ItemBuilder(Material.OAK_DOOR)
                .setName("§aHome Pública: " + home.getHomeName())
                .setLore("§7Proprietário: " + home.getPlayerName(), "§7Clique para se teleportar para esta home.")
                .build();
    }

    public static ItemStack createPublicHomesItem() {
        return new ItemBuilder(Material.ENDER_EYE)
                .setName("§aHomes Públicas")
                .setLore("§7Clique para visualizar", "§7as homes públicas.")
                .build();
    }

    public static ItemStack createInformationPublicItem() {
        return new ItemBuilder(Material.BOOK)
                .setName("§aDefinir Home Pública")
                .setLore(
                        "§7Clique para obter informações sobre como",
                        "§7definir uma home como pública.",
                        "",
                        "§7Para tornar uma home pública,",
                        "§7clique com o botão direito.",
                        "§7Isso a tornará visível para outros jogadores.")
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
