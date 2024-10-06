package com.rosemods.windswept.common.block.blueprint;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Item.class)
public interface ItemInvokerMixin {
	@Invoker
	boolean callAllowedIn(CreativeModeTab group);
}
