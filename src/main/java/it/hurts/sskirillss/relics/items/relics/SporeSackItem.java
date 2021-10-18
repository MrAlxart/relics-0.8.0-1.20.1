package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.SporeSackModel;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SporeSackItem extends RelicItem<SporeSackItem.Stats> implements ICurioItem {
    public static SporeSackItem INSTANCE;

    public SporeSackItem() {
        super(RelicData.builder()
                .rarity(Rarity.UNCOMMON)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(LootTables.JUNGLE_TEMPLE.toString())
                        .chance(0.2F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .arg((int) (config.chance * 100) + "%")
                        .arg(config.radius)
                        .build())
                .build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new SporeSackModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SporeSackEvents {
        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            Stats config = INSTANCE.config;

            if (!(event.getEntity() instanceof ProjectileEntity))
                return;

            ProjectileEntity projectile = (ProjectileEntity) event.getEntity();

            if (projectile.getOwner() == null || !(projectile.getOwner() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) projectile.getOwner();
            World world = projectile.getCommandSenderWorld();

            if (world.isClientSide())
                return;

            if (player.getCooldowns().isOnCooldown(ItemRegistry.SPORE_SACK.get())
                    || world.getRandom().nextFloat() > config.chance)
                return;

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SPORE_SACK.get(), player).ifPresent(triple -> {
                if (isBroken(triple.getRight()))
                    return;

                ((ServerWorld) world).sendParticles(new RedstoneParticleData(0, 255, 0, 1),
                        projectile.getX(), projectile.getY(), projectile.getZ(), 100, 1, 1, 1, 0.5);
                world.playSound(null, projectile.blockPosition(), SoundEvents.FIRE_EXTINGUISH,
                        SoundCategory.PLAYERS, 1.0F, 0.5F);
                player.getCooldowns().addCooldown(ItemRegistry.SPORE_SACK.get(), config.cooldown * 20);

                for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(config.radius))) {
                    if (entity == player)
                        continue;

                    entity.addEffect(new EffectInstance(Effects.POISON, config.poisonDuration * 20, config.poisonAmplifier));
                    entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, config.slownessDuration * 20, config.slownessAmplifier));
                }
            });
        }
    }

    public static class Stats extends RelicStats {
        public float chance = 0.3F;
        public int radius = 3;
        public int cooldown = 5;
        public int poisonAmplifier = 2;
        public int poisonDuration = 5;
        public int slownessAmplifier = 0;
        public int slownessDuration = 5;
    }
}