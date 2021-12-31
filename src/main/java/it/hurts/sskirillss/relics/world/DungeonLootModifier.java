package it.hurts.sskirillss.relics.world;

import com.google.gson.JsonObject;
import it.hurts.sskirillss.relics.configs.data.relics.RelicLootData;
import it.hurts.sskirillss.relics.configs.data.runes.RuneLootData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RuneItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class DungeonLootModifier extends LootModifier {
    public DungeonLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        Random random = context.getRandom();

        List<RelicItem<?>> relics = ItemRegistry.getRegisteredRelics();

        for (int i = 0; i < relics.size(); i++) {
            RelicItem<?> relic = relics.get(random.nextInt(relics.size()));
            int generated = 0;

            for (RelicLootData loot : relic.getLoot()) {
                if (loot.getTable().contains(context.getQueriedLootTableId().toString())
                        && random.nextFloat() <= loot.getChance()) {
                    generatedLoot.add(new ItemStack(relic));

                    generated++;
                }
            }

            if (generated >= 1)
                break;
        }

        for (RuneItem rune : ItemRegistry.getRegisteredRunes()) {
            RuneLootData loot = rune.getLoot();
            ItemStack stack = new ItemStack(rune);

            stack.setCount(random.nextInt(5) + 1);

            if (loot.getTable().contains(context.getQueriedLootTableId().toString())
                    && random.nextFloat() <= loot.getChance())
                generatedLoot.add(stack);
        }

        return generatedLoot;
    }

    private static class Serializer extends GlobalLootModifierSerializer<DungeonLootModifier> {
        @Override
        public DungeonLootModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            return new DungeonLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(DungeonLootModifier instance) {
            return this.makeConditions(instance.conditions);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EventHandler {
        @SubscribeEvent
        public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
            event.getRegistry().register(new DungeonLootModifier.Serializer().setRegistryName(
                    new ResourceLocation(Reference.MODID, "dungeon_loot_modifier")));
        }
    }
}