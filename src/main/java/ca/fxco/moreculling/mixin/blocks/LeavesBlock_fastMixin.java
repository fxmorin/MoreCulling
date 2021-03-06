package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

import static ca.fxco.moreculling.config.option.LeavesCullingMode.FAST;
import static ca.fxco.moreculling.config.option.LeavesCullingMode.VERTICAL;

@Mixin(value = LeavesBlock.class, priority = 1200)
public class LeavesBlock_fastMixin extends Block {

    public LeavesBlock_fastMixin(Settings settings) {
        super(settings);
    }


    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (MoreCulling.CONFIG.leavesCullingMode == FAST || CullingUtils.areLeavesOpaque() ||
                (MoreCulling.CONFIG.leavesCullingMode == VERTICAL && direction.getAxis() == Direction.Axis.Y))
            return stateFrom.getBlock() instanceof LeavesBlock || super.isSideInvisible(state, stateFrom, direction);
        return super.isSideInvisible(state, stateFrom, direction);
    }
}
