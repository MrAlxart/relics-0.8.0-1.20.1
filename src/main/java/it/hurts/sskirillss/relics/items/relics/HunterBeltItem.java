package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.api.integration.curios.ISlotModifier;
import it.hurts.sskirillss.relics.api.integration.curios.SlotModifierData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.HunterBeltModel;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

public class HunterBeltItem extends RelicItem<HunterBeltItem.Stats> implements ISlotModifier {
    public static HunterBeltItem INSTANCE;

    public HunterBeltItem() {
        super(RelicData.builder()
                .rarity(Rarity.UNCOMMON)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(LootTables.VILLAGE_BUTCHER.toString())
                        .chance(0.2F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .ability(AbilityTooltip.builder()
                        .arg(config.additionalLooting)
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (config.petDamageMultiplier * 100 - 100) + "%")
                        .build())
                .build();
    }

    @Override
    public SlotModifierData getSlotModifiers() {
        return SlotModifierData.builder()
                .entry(Pair.of("talisman", 1))
                .build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new HunterBeltModel();
    }

    @Override
    public int getLootingBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return config.additionalLooting;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class HunterBeltEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            Entity entity = event.getSource().getEntity();

            if (!(entity instanceof TameableEntity))
                return;

            TameableEntity pet = (TameableEntity) entity;

            if (!(pet.getOwner() instanceof PlayerEntity)
                    && EntityUtils.findEquippedCurio(pet.getOwner(), ItemRegistry.HUNTER_BELT.get()).isEmpty())
                return;

            event.setAmount(event.getAmount() * config.petDamageMultiplier);
        }
    }

    public static class Stats extends RelicStats {
        public int additionalLooting = 1;
        public float petDamageMultiplier = 3.0F;
    }
}