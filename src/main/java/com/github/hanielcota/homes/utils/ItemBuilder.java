package com.github.hanielcota.homes.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public class ItemBuilder {

    private final ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(@NotNull Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder setDisplayName(@NotNull Component displayName) {
        getItemMeta().displayName(displayName.decoration(TextDecoration.ITALIC, false));
        return this;
    }

    public ItemBuilder setLore(@NotNull String... lore) {
        List<TextComponent> loreComponents = Arrays.stream(lore)
                .map(line -> Component.text(line).decoration(TextDecoration.ITALIC, false))
                .toList();

        getItemMeta().lore(loreComponents);
        return this;
    }

    public ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
        getItemStack().addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        getItemStack().setAmount(amount);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        getItemMeta().setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder addItemFlags(@NotNull ItemFlag... flags) {
        getItemMeta().addItemFlags(flags);
        return this;
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        getItemMeta().setCustomModelData(customModelData);
        return this;
    }

    private ItemMeta getItemMeta() {
        if (itemMeta == null) {
            itemMeta = itemStack.getItemMeta();
        }
        return itemMeta;
    }

    public ItemStack build() {
        getItemStack().setItemMeta(getItemMeta());
        return getItemStack();
    }
}
