package com.delplayer01.tpsBomber;

import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class TpsBomber extends JavaPlugin {

    private static TpsBomber instance;

    public static TpsBomber getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        getCommand("maps").setExecutor(new MapsCommand());
        getCommand("maps").setTabCompleter((TabCompleter) new MapsCommand());

        for (int i = 1; i <= 5; i++) {
            File mapFolder = new File(getDataFolder(), "Map" + i);
            if (!mapFolder.exists()) {
                mapFolder.mkdirs();
                getLogger().info("Creada carpeta: " + mapFolder.getPath());
            }

            File tpFile = new File(mapFolder, "tp.yml");
            if (!tpFile.exists()) {
                try {
                    tpFile.createNewFile();


                    YamlConfiguration config = new YamlConfiguration();
                    for (int tp = 1; tp <= 50; tp++) {
                        config.set("tp" + tp, "0,0,0");
                    }
                    config.save(tpFile);

                    getLogger().info("Archivo tp.yml creado y rellenado en " + tpFile.getPath());
                } catch (IOException e) {
                    getLogger().warning("No se pudo crear o guardar tp.yml en " + mapFolder.getName() + ": " + e.getMessage());
                }
            }
        }
    }
}

