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

package me.limhax.tFFA;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.limhax.tFFA.command.MainCommand;
import me.limhax.tFFA.command.subcommand.*;
import me.limhax.tFFA.event.FFAEvent;
import me.limhax.tFFA.listener.BukkitListener;
import me.limhax.tFFA.manager.*;
import me.limhax.tFFA.util.ColorUtil;
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

    this.getServer().getPluginManager().registerEvents(new BukkitListener(), this);
    this.getLogger().info(ColorUtil.translate("Bukkit listener registered."));
    this.configManager = new ConfigManager();
    this.getLogger().info(ColorUtil.translate("Config manager instance created."));
    this.kitManager = new KitManager();
    this.getLogger().info(ColorUtil.translate("Kit manager instance created."));
    this.event = new FFAEvent();
    this.getLogger().info(ColorUtil.translate("Event instance created."));
    this.inventoryManager = new InventoryManager();
    this.getLogger().info(ColorUtil.translate("Inventory manager instance created."));
    this.commandManager = new PaperCommandManager(this);
    this.getLogger().info(ColorUtil.translate("Command manager instance created."));
    this.borderManager = new BorderManager();
    this.getLogger().info(ColorUtil.translate("Border manager instance created."));
    this.effectManager = new EffectManager();
    this.getLogger().info(ColorUtil.translate("Effect manager instance created."));

    this.commandManager.registerCommand(new MainCommand());
    this.getLogger().info(ColorUtil.translate("Main command registered."));
    this.commandManager.registerCommand(new StartCommand());
    this.getLogger().info(ColorUtil.translate("Start command registered."));
    this.commandManager.registerCommand(new StopCommand());
    this.getLogger().info(ColorUtil.translate("Stop command registered."));
    this.commandManager.registerCommand(new JoinCommand());
    this.getLogger().info(ColorUtil.translate("Join command registered."));
    this.commandManager.registerCommand(new LeaveCommand());
    this.getLogger().info(ColorUtil.translate("Leave command registered."));
    this.commandManager.registerCommand(new KitCommand());
    this.getLogger().info(ColorUtil.translate("Kit command registered."));
    this.commandManager.registerCommand(new ReloadCommand());
    this.getLogger().info(ColorUtil.translate("Reload command registered."));

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
