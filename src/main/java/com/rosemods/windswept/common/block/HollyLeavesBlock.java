package com.rosemods.windswept.common.block;

import com.rosemods.windswept.core.other.WindsweptDamageSources;
import com.rosemods.windswept.core.other.tags.WindsweptEntityTypeTags;
import com.rosemods.windswept.core.registry.WindsweptBlocks;
import com.teamabnormals.blueprint.common.block.wood.LogBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class HollyLeavesBlock extends LogBlock {
    private static final VoxelShape AABB = box(1f, 1f, 1f, 15f, 15f, 15f);

    public HollyLeavesBlock(properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entityInside(1.2f, entity, level);
    }

    @Override
    public @Nullable BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return BlockPathTypes.DAMAGE_CAUTIOUS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
        return .2f;
    }

    public static void entityInside(float damage, Entity entity, Level level) {
        if (!level.isClientSide && (entity.xOld != entity.getX() || entity.zOld != entity.getZ()) && entity instanceof LivingEntity && !entity.getType().is(WindsweptEntityTypeTags.HOLLY_IMMUNE) && !(entity instanceof Player player && player.isCrouching())) {
            double d0 = Math.abs(entity.getX() - entity.xOld);
            double d1 = Math.abs(entity.getZ() - entity.zOld);
            if (d0 >= .003d || d1 >= .003d)
                entity.hurt(WindsweptDamageSources.HOLLY_LEAVES, damage);
        }
    }

}
