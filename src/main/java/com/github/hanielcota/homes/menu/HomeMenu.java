package com.github.hanielcota.homes.menu;


import com.github.hanielcota.homes.domain.Home;
import org.bukkit.entity.Player;

import java.util.List;

public interface HomeMenu {
    void showHomesMenu(Player player, List<Home> homes);
}
