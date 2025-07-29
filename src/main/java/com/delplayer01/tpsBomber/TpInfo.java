package com.delplayer01.tpsBomber;

import org.bukkit.Location;

public class TpInfo {
    private final Location location;
    private final String tag;

    public TpInfo(Location location, String tag) {
        this.location = location;
        this.tag = tag;
    }

    public Location getLocation() {
        return location;
    }

    public String getTag() {
        return tag;
    }
}
