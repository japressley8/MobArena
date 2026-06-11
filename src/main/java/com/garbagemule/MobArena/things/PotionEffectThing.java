package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PotionEffectThing implements Thing {

    private final PotionEffect effect;

    public PotionEffectThing(PotionEffect effect) {
        this.effect = effect;
    }

    public PotionEffect getEffect() {
        return effect;
    }

    @Override
    public boolean giveTo(Player player) {
        return player.addPotionEffect(effect, true);
    }

    @Override
    public boolean takeFrom(Player player) {
        player.removePotionEffect(effect.getType());
        return true;
    }

    @Override
    public boolean heldBy(Player player) {
        return player.hasPotionEffect(effect.getType());
    }

}
