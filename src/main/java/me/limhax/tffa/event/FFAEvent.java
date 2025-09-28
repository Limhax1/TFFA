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

package me.limhax.tffa.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.limhax.tffa.TFFA;
import me.limhax.tffa.manager.ConfigManager;
import me.limhax.tffa.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

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

    long delay = config().getInt("start-delay");
    broadcast(config().getMessage("event-starting").replace("%seconds%", String.valueOf(delay)));

    BukkitTask countdownTask = Bukkit.getScheduler().runTaskTimer(TFFA.getInstance(), new Runnable() {
      long timeLeft = delay;

      @Override
      public void run() {
        if (timeLeft <= 0 || !running || stopping) {
          Bukkit.getScheduler().cancelTask(this.hashCode());
          return;
        }

        if (--timeLeft <= 10 || timeLeft % 30 == 0) {
          broadcast(config().getMessage("event-starting").replace("%seconds%", String.valueOf(timeLeft)));
        }
      }
    }, 20L, 20L);

    startTask = Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), () -> {
      started = true;
      broadcast(config().getMessage("event-started"));
      TFFA.getInstance().getEffectManager().scheduleEffects();
      TFFA.getInstance().getBorderManager().scheduleBorderShrink(getWorld());
      countdownTask.cancel();
    }, delay * 20L);
  }

  public void addPlayer(Player p) {
    if (p == null || stopping || players.containsKey(p.getName())) return;

    players.put(p.getName(), p);
    Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), () -> {
      p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
      TFFA.getInstance().getInventoryManager().applyInventory(p);
      broadcast(config().getMessage("player-joined-announce").replace("%player%", p.getName()));
    }, 20);
  }

  public void removePlayer(Player p) {
    if (p == null || players.remove(p.getName()) == null) return;

    cleanupPlayer(p, true);
    executeCommands("settings.elimination-commands", p);

    if (players.size() == 1 && !stopping) {
      handleWinner();
    } else if (players.isEmpty() && !stopping) {
      Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), this::stop, 20L);
    }
  }

  public void stop() {
    if (!running && !stopping) return;

    stopping = true;

    if (startTask != null && !startTask.isCancelled()) startTask.cancel();

    players.values().forEach(p -> cleanupPlayer(p, true));
    resetWorldBorder();
    players.clear();
    running = started = stopping = false;
  }

  private void handleWinner() {
    stopping = true;
    Player winner = players.values().iterator().next();

    players.remove(winner.getName());

    cleanupPlayer(winner, false);

    broadcast(config().getMessage("event-win-announce").replace("%player%", winner.getName()));

    executeCommands("settings.elimination-commands", winner);

    Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), () -> {
      executeCommands("settings.reward-commands", winner);
      Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), this::stop, 20L);
    }, 20L);
  }

  private void cleanupPlayer(Player p, boolean announceLeave) {
    if (p == null || !p.isOnline()) return;

    if (announceLeave) {
      broadcast(config().getMessage("player-left-announce").replace("%player%", p.getName()));
    }

    p.setHealth(20);
    p.setFoodLevel(20);
    p.setSaturation(20);
    p.getInventory().clear();
    p.getInventory().setArmorContents(null);
    p.updateInventory();
    p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
  }

  private void executeCommands(String path, Player p) {
    config().getConfig().getStringList(path).stream()
        .filter(cmd -> cmd != null && !cmd.trim().isEmpty())
        .forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", p.getName())));
  }

  private void resetWorldBorder() {
    World world = getWorld();
    if (world != null) world.getWorldBorder().setSize(config().getInt("border-original-size"));
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
