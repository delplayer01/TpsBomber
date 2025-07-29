package com.delplayer01.tpsBomber;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class MapsCommand implements CommandExecutor {

    private final Map<Integer, List<Integer>> mapIndexes = new HashMap<>();
    private final Map<UUID, Integer> playerIndexes = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() && !sender.getName().equalsIgnoreCase("CONSOLE")) {
            sender.sendMessage("§cNo tienes permiso.");
            return true;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("start")) {
            try {
                int mapNum = Integer.parseInt(args[0].replace("map", "").trim());
                List<Player> playersToTp = new ArrayList<>();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getScoreboardTags().contains("tps")) {
                        playersToTp.add(player);
                    }
                }

                if (playersToTp.isEmpty()) {
                    sender.sendMessage("§cNo hay jugadores con la tag 'tps'.");
                    return true;
                }

                if (!mapIndexes.containsKey(mapNum)) {
                    mapIndexes.put(mapNum, TpsManager.getShuffledIndexes());
                }

                List<Integer> indexes = mapIndexes.get(mapNum);
                int assigned = 0;

                for (Player player : playersToTp) {
                    if (assigned >= 50 || indexes.isEmpty()) break;

                    int currentIndex = indexes.remove(0);
                    TpsManager.markUsed(currentIndex);

                    TpInfo tpInfo = TpsManager.getTpInfo(mapNum, currentIndex);
                    if (tpInfo == null) continue;

                    Location tpLocation = tpInfo.getLocation();
                    String tag = tpInfo.getTag();

                    player.teleport(tpLocation);

                    if (tag != null && !tag.isEmpty()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tag " + player.getName() + " add " + tag);
                    }

                    player.removeScoreboardTag("tps");
                    assigned++;
                }

            } catch (NumberFormatException e) {
                sender.sendMessage("§cEl número del mapa no es válido.");
            }

            return true;
        }

        sender.sendMessage("§cUso: /maps <map> start");
        return true;
    }
}
