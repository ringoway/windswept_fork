package com.rosemods.windswept.common.block.blueprint;

import com.google.common.collect.Maps;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * An {@link Item} extension that fills itself after a defined {@link #followItem}.
 */
public class InjectedItem extends Item {
	private static final Map<Item, TargetedItemCategoryFiller> FILLER_MAP = Maps.newHashMap();
	private final Item followItem;

	public InjectedItem(Item followItem, Properties properties) {
		super(properties);
		this.followItem = followItem;
		FILLER_MAP.put(followItem, new TargetedItemCategoryFiller(() -> followItem));
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		FILLER_MAP.get(this.followItem).fillItem(this.asItem(), group, items);
	}
}