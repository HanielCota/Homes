package com.github.hanielcota.homes.utils;

import com.github.hanielcota.homes.menu.factory.MenuItemFactory;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class SimplixMenu implements InventoryHolder {

    private final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();

    private final Inventory inventory;

    @Setter
    private Predicate<Player> closeFilter;

    public SimplixMenu(int size) {
        this(owner -> createInventory(owner, size));
    }

    public SimplixMenu(int size, String title) {
        this(owner -> createInventory(owner, size, title));
    }

    public SimplixMenu(InventoryType type) {
        this(owner -> createInventory(owner, type));
    }

    public SimplixMenu(InventoryType type, String title) {
        this(owner -> createInventory(owner, type, title));
    }

    private static Inventory createInventory(InventoryHolder owner, int size) {
        return Bukkit.createInventory(owner, size);
    }

    private static Inventory createInventory(InventoryHolder owner, int size, String title) {
        return Bukkit.createInventory(owner, size, title);
    }

    private static Inventory createInventory(InventoryHolder owner, InventoryType type) {
        return Bukkit.createInventory(owner, type);
    }

    private static Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
        return Bukkit.createInventory(owner, type, title);
    }

    public SimplixMenu(Function<InventoryHolder, Inventory> inventoryFunction) {
        if (inventoryFunction == null) {
            throw new IllegalArgumentException("inventoryFunction cannot be null");
        }
        Inventory inv = inventoryFunction.apply(this);

        if (inv == null || inv.getHolder() != this) {
            throw new IllegalStateException(
                    "Inventory holder is not FastInv, found: " + (inv != null ? inv.getHolder() : "null"));
        }

        inventory = inv;
    }

    protected void onOpen(InventoryOpenEvent event) {}

    protected void onClick(InventoryClickEvent event) {}

    protected void onClose(InventoryCloseEvent event) {}

    public void addItem(ItemStack item) {
        addItem(item, null);
    }

    public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        int slot = inventory.firstEmpty();
        if (slot < 0) {
            return;
        }
        setItem(slot, item, handler);
    }

    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        inventory.setItem(slot, item);

        if (handler != null) {
            itemHandlers.put(slot, handler);
            return;
        }

        itemHandlers.remove(slot);
    }

    public void setItems(int slotFrom, int slotTo, ItemStack item) {
        setItems(slotFrom, slotTo, item, null);
    }

    public void setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i <= slotTo; i++) {
            setItem(i, item, handler);
        }
    }

    public void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    public void setItems(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    public void removeItem(int slot) {
        inventory.clear(slot);
        itemHandlers.remove(slot);
    }

    public void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    public void addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        if (openHandler == null) {
            return;
        }
        openHandlers.add(openHandler);
    }

    public void addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        if (closeHandler == null) {
            return;
        }
        closeHandlers.add(closeHandler);
    }

    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        if (clickHandler == null) {
            return;
        }
        clickHandlers.add(clickHandler);
    }

    public void open(Player player) {
        if (player == null) {
            return;
        }
        player.openInventory(inventory);
    }

    public int[] getBorders() {
        int size = inventory.getSize();
        return IntStream.range(0, size)
                .filter(i -> size < 27 || i < 9 || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9)
                .toArray();
    }

    public int[] getCorners() {
        int size = inventory.getSize();
        return IntStream.range(0, size)
                .filter(i -> i < 2
                        || (i > 6 && i < 10)
                        || i == 17
                        || i == size - 18
                        || (i > size - 11 && i < size - 7)
                        || i > size - 3)
                .toArray();
    }

    public void fillEmptySlotsWithBarrier(int startSlot, int endSlot) {
        for (int emptySlot = startSlot; emptySlot <= endSlot; emptySlot++) {
            if (getInventory().getItem(emptySlot) == null) {
                setItem(emptySlot, MenuItemFactory.createBarrierItem());
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    void handleOpen(InventoryOpenEvent e) {
        onOpen(e);
        openHandlers.forEach(c -> c.accept(e));
    }

    boolean handleClose(InventoryCloseEvent e) {
        onClose(e);
        closeHandlers.forEach(c -> c.accept(e));

        return closeFilter != null && closeFilter.test((Player) e.getPlayer());
    }

    void handleClick(InventoryClickEvent e) {
        onClick(e);
        clickHandlers.forEach(c -> c.accept(e));

        Consumer<InventoryClickEvent> clickConsumer = itemHandlers.get(e.getRawSlot());

        if (clickConsumer != null) {
            clickConsumer.accept(e);
        }
    }
}
