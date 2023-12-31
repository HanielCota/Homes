package com.github.hanielcota.homes;

import co.aikar.commands.PaperCommandManager;
import com.github.hanielcota.homes.commands.HomeCommand;
import com.github.hanielcota.homes.commands.HomesCommand;
import com.github.hanielcota.homes.commands.SetHomeCommand;
import com.github.hanielcota.homes.controller.HomeController;
import com.github.hanielcota.homes.infra.CreateHomeTable;
import com.github.hanielcota.homes.infra.HikariCPDataSource;
import com.github.hanielcota.homes.repository.HomeRepository;
import com.github.hanielcota.homes.repository.cache.HomeCacheManager;
import com.github.hanielcota.homes.repository.impl.HomeRepositoryImpl;
import com.github.hanielcota.homes.service.HomeService;
import com.github.hanielcota.homes.utils.SimplixManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class HomesPlugin extends JavaPlugin {

    private HomeController homeController;
    private HomeService homeService;
    private HomeRepository homeRepository;
    private HomeCacheManager homeCacheManager;

    @Override
    public void onEnable() {
        setupDependencies();
        setupCommands();

        CreateHomeTable.createHomeTable();
    }

    @Override
    public void onDisable() {
        HikariCPDataSource.closeDataSource();
    }

    private void setupDependencies() {
        homeCacheManager = new HomeCacheManager();
        homeRepository = new HomeRepositoryImpl(homeCacheManager);
        homeService = new HomeService(homeRepository);
        homeController = new HomeController(homeService);

        SimplixManager.registerPlugin(this);
    }

    private void setupCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new HomeCommand(homeController));
        commandManager.registerCommand(new SetHomeCommand(homeController, this));
        commandManager.registerCommand(new HomesCommand(homeController, this));
    }
}
