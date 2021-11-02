package it.hurts.sskirillss.relics.items.relics.boots;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttribute;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.AmphibianBootModel;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

public class AmphibianBootItem extends RelicItem<AmphibianBootItem.Stats> {

    public static AmphibianBootItem INSTANCE;

    public AmphibianBootItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.AQUATIC)
                        .chance(0.1F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (config.swimSpeedModifier * 100 - 100) + "%")
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg(config.airSupplyModifier)
                        .build())
                .build();
    }

    @Override
    public RelicAttribute getAttributes(ItemStack stack) {
        return RelicAttribute.builder()
                .attribute(new RelicAttribute.Modifier(ForgeMod.SWIM_SPEED.get(), config.swimSpeedModifier))
                .build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new AmphibianBootModel();
    }

    public static class Stats extends RelicStats {
        public float swimSpeedModifier = 1.25F;
        public int airSupplyModifier = 2;
    }
}