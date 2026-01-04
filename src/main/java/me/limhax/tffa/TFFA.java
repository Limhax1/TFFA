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

package me.limhax.tffa;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.limhax.tffa.command.MainCommand;
import me.limhax.tffa.command.subcommand.*;
import me.limhax.tffa.event.FFAEvent;
import me.limhax.tffa.listener.BukkitListener;
import me.limhax.tffa.manager.*;
import me.limhax.tffa.util.ColorUtil;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class TFFA extends JavaPlugin {

  @Getter
  public static TFFA instance;
  @Getter
  public FFAEvent event;
  @Getter
  public InventoryManager inventoryManager;
  @Getter
  public ConfigManager configManager;
  @Getter
  public KitManager kitManager;
  @Getter
  public BorderManager borderManager;
  @Getter
  public EffectManager effectManager;
  private PaperCommandManager commandManager;

  @Override
  public void onEnable() {
    instance = this;

    this.getLogger().info("Registering listeners & commands...");
    this.getServer().getPluginManager().registerEvents(new BukkitListener(), this);
    this.configManager = new ConfigManager();
    this.kitManager = new KitManager();
    this.event = new FFAEvent();
    this.inventoryManager = new InventoryManager();
    this.commandManager = new PaperCommandManager(this);
    this.borderManager = new BorderManager();
    this.effectManager = new EffectManager();

    this.commandManager.registerCommand(new MainCommand());
    this.commandManager.registerCommand(new StartCommand());
    this.commandManager.registerCommand(new StopCommand());
    this.commandManager.registerCommand(new JoinCommand());
    this.commandManager.registerCommand(new LeaveCommand());
    this.commandManager.registerCommand(new KitCommand());
    this.commandManager.registerCommand(new ReloadCommand());

    this.getLogger().info(ColorUtil.translate("TFFA has been enabled."));
  }

  @Override
  public void onDisable() {
    this.event.stop();
    this.getLogger().info(ColorUtil.translate("Event stopped."));
    this.event = null;
    this.inventoryManager = null;
    this.commandManager = null;
    this.getLogger().info(ColorUtil.translate("TFFA has been disabled."));
  }
}
