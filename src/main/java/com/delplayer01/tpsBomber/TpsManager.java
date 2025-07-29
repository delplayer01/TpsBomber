package com.delplayer01.tpsBomber;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TpsManager {

    private static final List<Integer> usedIndexes = new ArrayList<>();

    public static TpInfo getTpInfo(int mapNum, int tpNum) {
        File mapFolder = new File(TpsBomber.getInstance().getDataFolder(), "Map" + mapNum);
        File tpFile = new File(mapFolder, "tp.yml");

        if (!tpFile.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(tpFile);
        String raw = config.getString("tp" + tpNum);
        if (raw == null) return null;

        String[] parts = raw.split(" - tag:");
        String[] coords = parts[0].split(",");
        String tag = parts.length > 1 ? parts[1].trim() : null;

        World world = Bukkit.getWorld("world");
        if (world == null) return null;

        try {
            double x = Double.parseDouble(coords[0].trim());
            double y = Double.parseDouble(coords[1].trim());
            double z = Double.parseDouble(coords[2].trim());
            Location loc = new Location(world, x, y, z);
            return new TpInfo(loc, tag);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Integer> getShuffledIndexes() {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);
        return indexes;
    }

    public static void resetUsedIndexes() {
        usedIndexes.clear();
    }

    public static boolean isUsed(int index) {
        return usedIndexes.contains(index);
    }

    public static void markUsed(int index) {
        usedIndexes.add(index);
    }
}
