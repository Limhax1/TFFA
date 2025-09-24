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

package me.limhax.tFFA.manager;

import me.limhax.tFFA.TFFA;
import me.limhax.tFFA.event.FFAEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class EffectManager {

  public EffectManager() {
  }

  public void scheduleEffects() {
    List<DelayedPotionEffect> effects = getPotionEffects();

    for (DelayedPotionEffect effect : effects) {
      scheduleEffect(effect.type(), effect.duration(), effect.amplifier(), effect.delay());
    }
  }

  private List<DelayedPotionEffect> getPotionEffects() {
    ConfigManager configManager = TFFA.getInstance().getConfigManager();
    List<String> effectStrings = configManager.getConfig().getStringList("settings.potion-effects");
    List<DelayedPotionEffect> effects = new ArrayList<>();

    for (String effectString : effectStrings) {
      DelayedPotionEffect effect = parseEffectString(effectString);
      if (effect != null) {
        effects.add(effect);
      }
    }

    return effects;
  }

  private DelayedPotionEffect parseEffectString(String effectString) {
    try {
      String[] parts = effectString.split(":");
      if (parts.length != 4) {
        TFFA.getInstance().getLogger().warning("Invalid potion effect format: " + effectString);
        return null;
      }

      PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
      if (type == null) {
        TFFA.getInstance().getLogger().warning("Unknown potion effect type: " + parts[0]);
        return null;
      }

      int duration = Integer.parseInt(parts[1]);
      int amplifier = Integer.parseInt(parts[2]);
      int delay = Integer.parseInt(parts[3]);

      return new DelayedPotionEffect(type, duration, amplifier, delay);

    } catch (NumberFormatException e) {
      TFFA.getInstance().getLogger().warning("Invalid number format in potion effect: " + effectString);
      return null;
    }
  }

  public void scheduleEffect(PotionEffectType type, int duration, int amplifier, int delay) {
    FFAEvent event = TFFA.getInstance().getEvent();

    Bukkit.getScheduler().runTaskLater(TFFA.getInstance(), () -> {
      if (event.isRunning() && event.isStarted() && !event.isStopping()) {
        for (Player player : event.getPlayers().values()) {
          player.addPotionEffect(new PotionEffect(type, duration, amplifier - 1));
        }
      }
    }, delay * 20L);
  }

  public record DelayedPotionEffect(PotionEffectType type, int duration, int amplifier, int delay) {
  }
}