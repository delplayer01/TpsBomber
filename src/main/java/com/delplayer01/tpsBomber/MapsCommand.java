package com.delplayer01.tpsBomber;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class MapsCommand implements CommandExecutor, TabCompleter {

    private final Map<Integer, YamlConfiguration> mapConfigs = new HashMap<>();
    private final Map<Integer, List<BukkitRunnable>> activeTasks = new HashMap<>();

    public MapsCommand() {
        reloadAllMaps();
    }

    private void reloadAllMaps() {
        mapConfigs.clear();
        for (int i = 1; i <= 5; i++) {
            File tpFile = new File(TpsBomber.getInstance().getDataFolder(), "Map" + i + "/tp.yml");
            if (tpFile.exists()) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(tpFile);
                mapConfigs.put(i, config);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadAllMaps();
            sender.sendMessage(ChatColor.GREEN + "Se recargaron los archivos tp.yml de todos los mapas.");
            return true;
        }

        if (args.length != 2 || (!args[1].equalsIgnoreCase("start") && !args[1].equalsIgnoreCase("stop"))) {
            sender.sendMessage(ChatColor.RED + "Uso correcto: /maps <map1-map5> <start|stop>  o /maps reload");
            return true;
        }

        String mapName = args[0].toLowerCase();
        int mapNumber;
        try {
            if (!mapName.startsWith("map")) throw new NumberFormatException();
            mapNumber = Integer.parseInt(mapName.substring(3));
            if (mapNumber < 1 || mapNumber > 5) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Mapa inválido. Usa map1 a map5.");
            return true;
        }

        if (args[1].equalsIgnoreCase("stop")) {
            List<BukkitRunnable> tasks = activeTasks.remove(mapNumber);
            if (tasks != null) {
                for (BukkitRunnable task : tasks) {
                    task.cancel();
                }
                sender.sendMessage(ChatColor.YELLOW + "Se han cancelado los teleports en curso del " + mapName);
            } else {
                sender.sendMessage(ChatColor.RED + "No hay tareas activas para cancelar en " + mapName);
            }
            return true;
        }

        YamlConfiguration config = mapConfigs.get(mapNumber);
        if (config == null) {
            sender.sendMessage(ChatColor.RED + "No se encontró la configuración para Map" + mapNumber + ". Usa /maps reload para recargar.");
            return true;
        }

        List<Player> taggedPlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getScoreboardTags().contains("tps")) {
                taggedPlayers.add(player);
            }
        }

        if (taggedPlayers.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No hay jugadores con la tag 'tps'.");
            return true;
        }

        if (taggedPlayers.size() > 50) {
            sender.sendMessage(ChatColor.RED + "¡Hay más jugadores con la tag 'tps' que posiciones disponibles (50)!");
            sender.sendMessage(ChatColor.RED + "lol error 934 avisame a mi osea a delplayer pos");
        }

        World world = Bukkit.getWorld("world");
        if (world == null) {
            List<World> worlds = Bukkit.getWorlds();
            if (worlds.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No se encontró ningún mundo cargado.");
                return true;
            }
            world = worlds.get(0);
        }

        int delayTicks = 0;
        final int delayBetweenTeleports = 40;
        List<BukkitRunnable> scheduledTasks = new ArrayList<>();

        for (int i = 0; i < taggedPlayers.size() && i < 50; i++) {
            final Player player = taggedPlayers.get(i);
            final String tpKey = "tp" + (i + 1);
            final YamlConfiguration configFinal = config;
            final World worldFinal = world;

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    String locationStr = configFinal.getString(tpKey);
                    if (locationStr == null) return;
                    try {
                        String[] parts = locationStr.split(",");
                        if (parts.length < 3) return;
                        double x = Double.parseDouble(parts[0]);
                        double y = Double.parseDouble(parts[1]);
                        double z = Double.parseDouble(parts[2]);
                        Location loc = new Location(worldFinal, x, y, z);
                        player.teleport(loc);
                        player.removeScoreboardTag("tps");
                    } catch (Exception ignored) {}
                }
            };

            task.runTaskLater(TpsBomber.getInstance(), delayTicks);
            scheduledTasks.add(task);
            delayTicks += delayBetweenTeleports;
        }

        activeTasks.put(mapNumber, scheduledTasks);
        sender.sendMessage(ChatColor.GREEN + "Se está teletransportando a " + Math.min(taggedPlayers.size(), 50) + " jugadores del " + mapName);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("map1", "map2", "map3", "map4", "map5", "reload");
        } else if (args.length == 2) {
            if ("reload".equalsIgnoreCase(args[0])) return Collections.emptyList();
            return Arrays.asList("start", "stop");
        }
        return Collections.emptyList();
    }
}