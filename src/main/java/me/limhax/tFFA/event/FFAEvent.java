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

package me.limhax.tFFA.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.limhax.tFFA.TFFA;
import me.limhax.tFFA.manager.ConfigManager;
import me.limhax.tFFA.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
public class FFAEvent {

  private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
  private volatile boolean running, started, stopping;
  private BukkitTask startTask;

  public void start() {
    if (running) return;

    running = true;
    stopping = false;

    resetWorldBorder();

    ConfigManager config = config();
    long delay = config.getInt("start-delay");

    broadcast(config.getMessage("event-starting").replace("%seconds%", String.valueOf(delay)));

    startTask = Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), () -> {
      started = true;
      broadcast(config.getMessage("event-started"));

      TFFA.getInstance().getEffectManager().scheduleEffects();
      TFFA.getInstance().getBorderManager().scheduleBorderShrink(getWorld());
    }, delay * 20L);
  }

  public void addPlayer(Player p) {
    if (p == null || stopping || players.containsKey(p.getName())) return;

    players.put(p.getName(), p);
    TFFA.getInstance().getInventoryManager().applyInventory(p);
    broadcast(config().getMessage("player-joined-announce").replace("%player%", p.getName()));
  }

  public void removePlayer(Player p) {
    if (p == null || players.remove(p.getName()) == null) return;

    cleanupPlayer(p);
    executeCommands("settings.elimination-commands", p);

    if (players.size() == 1 && !stopping) {
      handleWinner();
    }
  }

  public void stop() {
    if (!running && !stopping) return;

    stopping = true;

    if (startTask != null && !startTask.isCancelled()) {
      startTask.cancel();
    }

    players.values().forEach(this::cleanupPlayer);
    resetWorldBorder();

    players.clear();
    running = started = stopping = false;
  }

  private void handleWinner() {
    stopping = true;
    Player winner = players.values().iterator().next();

    broadcast(config().getMessage("event-win-announce").replace("%player%", winner.getName()));
    executeCommands("settings.elimination-commands", winner);

    Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), () -> {
      executeCommands("settings.reward-commands", winner);
      Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), this::stop, 20L);
    }, 20L);
  }

  private void cleanupPlayer(Player p) {
    if (p == null || !p.isOnline()) return;

    broadcast(config().getMessage("player-left-announce").replace("%player%", p.getName()));

    p.setHealth(20);
    p.setFoodLevel(20);
    p.setSaturation(20);
    for (PotionEffect effect : p.getActivePotionEffects()) {
      p.removePotionEffect(effect.getType());
    }
  }

  private void executeCommands(String path, Player p) {
    List<String> commands = TFFA.getInstance().getConfig().getStringList(path);
    commands.forEach(cmd -> {
      if (cmd != null && !cmd.trim().isEmpty()) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", p.getName()));
      }
    });
  }

  private void resetWorldBorder() {
    World world = getWorld();
    if (world != null) {
      world.getWorldBorder().setSize(config().getInt("border-original-size"));
    }
  }

  private void broadcast(String message) {
    if (message != null && !message.trim().isEmpty()) {
      Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }
  }

  private ConfigManager config() {
    return TFFA.getInstance().getConfigManager();
  }

  private World getWorld() {
    return WorldUtil.getWorld(config().getSetting("world-name"));
  }
}