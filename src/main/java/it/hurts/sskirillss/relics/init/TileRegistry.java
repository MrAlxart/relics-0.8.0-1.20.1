package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.tiles.ResearchingTableTile;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class TileRegistry {
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Reference.MODID);

    public static final RegistryObject<BlockEntityType<ResearchingTableTile>> RESEARCHING_TABLE = TILES.register("researching_table", () ->
            BlockEntityType.Builder.of(ResearchingTableTile::new, BlockRegistry.RESEARCHING_TABLE.get()).build(null));

    public static void register() {
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}