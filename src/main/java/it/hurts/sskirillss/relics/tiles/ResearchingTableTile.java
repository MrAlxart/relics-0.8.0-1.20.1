package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.init.TileRegistry;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ResearchingTableTile extends BlockEntity {
    @Getter
    @Setter
    private ItemStack stack = ItemStack.EMPTY;

    public int ticksExisted;

    public ResearchingTableTile(BlockPos pos, BlockState state) {
        super(TileRegistry.RESEARCHING_TABLE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ResearchingTableTile tile) {
        if (level == null)
            return;

        tile.ticksExisted++;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        stack = ItemStack.of((CompoundTag) compound.get("stack"));
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        if (stack != null) {
            CompoundTag itemStack = new CompoundTag();

            stack.save(itemStack);

            compound.put("stack", itemStack);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();

        this.saveAdditional(tag);

        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);

        this.load(pkt.getTag());
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}