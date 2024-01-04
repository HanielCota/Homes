package com.github.hanielcota.homes.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicBoolean;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimplixManager {

    private static final AtomicBoolean isRegistered = new AtomicBoolean(false);

    public static void registerPlugin(@NonNull Plugin plugin) {
        if (isRegistered.getAndSet(true)) {
            throw new IllegalStateException("SimplixManager is already registered by plugin: " + plugin.getName());
        }

        Bukkit.getPluginManager().registerEvents(new SimplixInventoryListener(plugin), plugin);
    }

    public static void closeAllInventories() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getOpenInventory().getTopInventory().getHolder() instanceof SimplixMenu)
                .forEach(Player::closeInventory);
    }

    @AllArgsConstructor
    public static final class SimplixInventoryListener implements Listener {

        private final Plugin plugin;

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getClickedInventory() == null
                    || !(event.getClickedInventory().getHolder() instanceof SimplixMenu inventory)) {
                return;
            }

            boolean wasCancelled = event.isCancelled();
            event.setCancelled(true);

            inventory.handleClick(event);

            if (!wasCancelled && !event.isCancelled()) {
                event.setCancelled(false);
            }
        }

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent e) {
            if (!(e.getInventory().getHolder() instanceof SimplixMenu inv)) {
                return;
            }

            inv.handleOpen(e);
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            if (!(e.getInventory().getHolder() instanceof SimplixMenu inv)) {
                return;
            }

            if (inv.handleClose(e)) {
                Bukkit.getScheduler().runTask(this.plugin, () -> inv.open((Player) e.getPlayer()));
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent e) {
            if (e.getPlugin() != this.plugin) {
                return;
            }

            closeAllInventories();
            isRegistered.set(false);
        }
    }
}
