package com.github.hanielcota.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.github.hanielcota.homes.HomesPlugin;
import com.github.hanielcota.homes.domain.Home;
import com.github.hanielcota.homes.menu.impl.PublicHomesMenuImpl;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("publicas")
@AllArgsConstructor
public class PublicHomesCommand extends BaseCommand {

    private final HomesPlugin plugin;

    @Default
    public void onCommand(Player player) {
        List<Home> publicHomes = plugin.getHomeController().getPublicHomes(player.getName());
        new PublicHomesMenuImpl(player, publicHomes, plugin);
    }

    @Subcommand("set")
    public void onSetCommand(Player player, String homeName) {
        if (homeName == null || homeName.isEmpty()) {
            player.sendMessage("§cNome da home inválido. Uso: /publicas set <nome da home>");
            return;
        }

        if (!plugin.getHomeController().isHomeNameTaken(player.getName(), homeName)) {
            player.sendMessage("§cHome não encontrada: " + homeName);
            return;
        }

        plugin.getHomeController().setHomeVisibility(player.getName(), homeName, true);

        player.sendMessage("§aHome '" + homeName + "' agora é pública!");
    }

    @Subcommand("ver")
    public void onViewCommand(Player player, String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            player.sendMessage("§cNome de jogador inválido. Uso: /publicas ver <nick>");
            return;
        }

        if (player.getName().equalsIgnoreCase(playerName)) {
            player.sendMessage("§cVocê já está visualizando suas próprias homes públicas.");
            return;
        }

        List<Home> publicHomes = plugin.getHomeController().getPublicHomes(playerName);

        if (publicHomes.isEmpty()) {
            player.sendMessage("§cO jogador '" + playerName + "' não possui homes públicas.");
            return;
        }

        new PublicHomesMenuImpl(player, publicHomes, plugin);
    }
}
