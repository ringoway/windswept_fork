package com.rosemods.windswept.common.block;

import com.rosemods.windswept.common.block.blueprint.BlueprintFlowerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class SnowdropBlock extends BlueprintFlowerBlock {
    public SnowdropBlock(Supplier<MobEffect> stewEffect, int stewEffectDuration, Properties properties) {
        super(stewEffect, stewEffectDuration, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter getter, BlockPos pos) {
        return super.mayPlaceOn(state, getter, pos) || state.is(Blocks.SNOW_BLOCK);
    }

}
