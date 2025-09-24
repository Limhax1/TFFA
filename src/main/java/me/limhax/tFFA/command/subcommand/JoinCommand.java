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

package me.limhax.tFFA.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.limhax.tFFA.TFFA;
import me.limhax.tFFA.event.FFAEvent;
import me.limhax.tFFA.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("ffa|tffa")
@CommandPermission("tffa.join")
public class JoinCommand extends BaseCommand {

  @Subcommand("join")
  public void execute(Player sender) {
    ConfigManager config = TFFA.getInstance().getConfigManager();
    FFAEvent event = TFFA.getInstance().getEvent();
    if (sender.getPlayer() == null) return;
    if (event.isStarted()) {
      sender.sendMessage(config.getMessage("join-already-started"));
      return;
    }

    if (!event.isRunning()) {
      sender.sendMessage(config.getMessage("join-not-running"));
      return;
    }

    if (event.getPlayers().contains(sender)) {
      sender.sendMessage(config.getMessage("join-already-in-event"));
      return;
    }

    if (!sender.getInventory().isEmpty() && TFFA.getInstance().getConfigManager().getBoolean("require-empty-inv-to-join")) {
      sender.sendMessage(config.getMessage("join-inventory-not-empty"));
      return;
    }

    List<String> joinCommands = TFFA.getInstance().getConfig().getStringList("settings.join-commands");
    for (String command : joinCommands) {
      String cmd = command.replace("%player%", sender.getPlayer().getName());
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), () -> {
      if (sender.getPlayer() != null) {
        event.addPlayer(sender.getPlayer());
      }
    }, 20);

    sender.sendMessage(config.getMessage("joined-event"));
  }
}
