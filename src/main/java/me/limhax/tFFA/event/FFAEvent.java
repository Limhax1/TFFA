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
import lombok.Setter;
import me.limhax.tFFA.TFFA;
import me.limhax.tFFA.manager.ConfigManager;
import me.limhax.tFFA.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FFAEvent {

  private boolean running;
  private boolean stated;
  private boolean stopping;
  private ArrayList<Player> players;

  public FFAEvent() {
    running = false;
    players = new ArrayList<>();
  }

  public void start() {
    running = true;
    this.stopping = false;
    ConfigManager config = TFFA.getInstance().getConfigManager();
    long delay = config.getInt("start-delay") * 1000L;

    String message = config.getMessage("event-starting")
        .replace("%seconds%", String.valueOf(delay / 1000L));
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.sendMessage(message);
    }

    Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), () -> {
      stated = true;
      String message1 = config.getMessage("event-started");
      for (Player player : Bukkit.getOnlinePlayers()) {
        player.sendMessage(message1);
      }

      TFFA.getInstance().getEffectManager().scheduleEffects();
      TFFA.getInstance().getBorderManager().scheduleBorderShrink(WorldUtil.getWorld(TFFA.getInstance().getConfigManager().getSetting("world-name")));

    }, delay / 1000 * 20L);
  }

  public void addPlayer(Player p) {
    if (players != null && !players.contains(p)) {
      players.add(p);
    }

    TFFA.getInstance().getInventoryManager().applyInventory(p);

    ConfigManager config = TFFA.getInstance().getConfigManager();
    String message = config.getMessage("player-joined-announce")
        .replace("%player%", p.getName());

    for (Player player : Bukkit.getOnlinePlayers()) {
      player.sendMessage(message);
    }
  }

  public void removePlayer(Player p) {
    if (players != null) {
      players.remove(p);
    }

    cleanupPlayer(p);

    if (players.size() == 1) {
      if (!stopping) {
        Player winner = players.get(0);
        ConfigManager config = TFFA.getInstance().getConfigManager();
        String winMessage = config.getMessage("event-win-announce")
            .replace("%player%", winner.getName());
        for (Player player : Bukkit.getOnlinePlayers()) {
          player.sendMessage(winMessage);
        }

        Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), () -> {
          List<String> effectStrings = TFFA.getInstance().getConfig().getStringList("settings.reward-commands");
          for (String command : effectStrings) {
            String cmd = command.replace("%player%", winner.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
          }
        }, 20L);
      }
      stop();
    }
  }

  public void stop() {
    stopping = true;


    for (Player player : new ArrayList<>(players)) {
      cleanupPlayer(player);
    }

    World world = WorldUtil.getWorld(TFFA.getInstance().getConfigManager().getSetting("world-name"));
    final int original = TFFA.getInstance().getConfigManager().getInt("border-original-size");
    world.getWorldBorder().setSize(original);

    players.clear();
    running = false;
    stated = false;
  }

  private void cleanupPlayer(Player p) {
    ConfigManager config = TFFA.getInstance().getConfigManager();
    String message = config.getMessage("player-left-announce")
        .replace("%player%", p.getName());

    for (Player player : Bukkit.getOnlinePlayers()) {
      player.sendMessage(message);
    }

    p.setHealth(20);
    p.clearActivePotionEffects();
    if (p.getInventory() != null) {
      p.getInventory().clear();
    }
  }
}
