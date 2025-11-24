// This file is part of TFFA
// Copyright (C) 2025 Limhax
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package me.limhax.tffa.manager;

import lombok.Getter;
import lombok.Setter;
import me.limhax.tffa.TFFA;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class BorderManager {

  private static final double EXPAND = 0.3;
  private final Map<UUID, BukkitRunnable> borderShrinkTasks = new HashMap<>();
  private long lastVelocity;
  private long velocityCooldown = 250;
  private boolean borderShrinkingEnabled;
  private int startAfterSeconds;
  private int shrinkDuration;
  private double finalSize;
  private int shrinkInterval;

  public void tick(Player player, Location to, Location from) {
    if (!TFFA.getInstance().getEvent().getPlayers().contains(player)) {
      return;
    }
    this.velocityCooldown = TFFA.getInstance().getConfigManager().getInt("border-velocity-cooldown");

    WorldBorder border = player.getWorld().getWorldBorder();

    if (isInsideBorder(to, border)) {
      return;
    }

    long now = System.currentTimeMillis();
    if (now - lastVelocity < velocityCooldown) {
      return;
    }
    this.lastVelocity = now;

    Vector knockback = getKnockbackVector(to, border);
    player.setVelocity(knockback);
  }

  public boolean isInsideBorder(Location location, WorldBorder border) {
    Location center = border.getCenter();
    double safeZoneHalfSize = (border.getSize() / 2.0) - EXPAND;

    double dx = Math.abs(location.getX() - center.getX());
    double dz = Math.abs(location.getZ() - center.getZ());

    return dx <= safeZoneHalfSize && dz <= safeZoneHalfSize;
  }

  private Vector getKnockbackVector(Location playerLocation, WorldBorder border) {
    Location center = border.getCenter();
    double safeZoneHalfSize = (border.getSize() / 2.0) - EXPAND;

    Vector offset = playerLocation.toVector().subtract(center.toVector());

    double dx = Math.abs(offset.getX()) - safeZoneHalfSize;
    double dz = Math.abs(offset.getZ()) - safeZoneHalfSize;

    Vector inward;

    if (dx > dz) {
      inward = new Vector(Math.signum(-offset.getX()), 0, 0);
    } else {
      inward = new Vector(0, 0, Math.signum(-offset.getZ()));
    }

    final double minYVel = TFFA.getInstance().getConfigManager().getDouble("border-velocity-y", 0.05);
    final double strength = TFFA.getInstance().getConfigManager().getDouble("border-velocity-strength", 0.2);

    inward.normalize();
    inward.setY(minYVel);


    return inward.multiply(strength);
  }

  public void scheduleBorderShrink(World world) {
    if (world == null) return;
    WorldBorder border = world.getWorldBorder();
    int delay = TFFA.getInstance().getConfigManager().getInt("border-shrink-delay");
    int duration = TFFA.getInstance().getConfigManager().getInt("border-shrink-time");
    int finalSize = TFFA.getInstance().getConfigManager().getInt("border-shrink-size");

    Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), () -> {
      border.setSize(finalSize, duration);
    }, delay * 20L);
  }
}