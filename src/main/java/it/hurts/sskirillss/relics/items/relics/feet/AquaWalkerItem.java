package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.api.events.common.FluidCollisionEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

public class AquaWalkerItem extends RelicItem {
    public static final String TAG_DRENCH = "drench";

    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("walking", RelicAbilityEntry.builder()
                                .stat("time", RelicAbilityStat.builder()
                                        .initialValue(30D, 60D)
                                        .upgradeModifier("add", 5D)
                                        .formatValue(value -> String.valueOf((int) (MathUtils.round(value, 0))))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#ff6900", "#ff2e00")
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        int drench = NBTUtils.getInt(stack, TAG_DRENCH, 0);

        if (!(slotContext.getWearer() instanceof Player player) || player.tickCount % 20 != 0)
            return;

        if (drench > 0 && !player.isInWater() && !player.level.getFluidState(player.blockPosition().below()).is(FluidTags.WATER))
            NBTUtils.setInt(stack, TAG_DRENCH, --drench);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onFluidCollide(FluidCollisionEvent event) {
            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.AQUA_WALKER.get());
            int drench = NBTUtils.getInt(stack, TAG_DRENCH, 0);

            if (!(event.getEntityLiving() instanceof Player player) || stack.isEmpty() || drench > getAbilityValue(stack, "walking", "time")
                    || !event.getFluid().is(FluidTags.WATER) || player.isShiftKeyDown())
                return;

            if (player.tickCount % 20 == 0) {
                NBTUtils.setInt(stack, TAG_DRENCH, ++drench);

                if (drench % 5 == 0)
                    addExperience(player, stack, 1);
            }

            event.setCanceled(true);
        }
    }
}