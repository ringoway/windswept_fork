package com.rosemods.windswept.common.block.blueprint;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class ItemStackUtil {
	private static final String[] M_NUMERALS = {"", "M", "MM", "MMM"};
	private static final String[] C_NUMERALS = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
	private static final String[] X_NUMERALS = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
	private static final String[] I_NUMERALS = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

	/**
	 * Searches for a specific item in a {@link NonNullList} of {@link ItemStack} and returns its index.
	 *
	 * @param item  The item to search for.
	 * @param items The list of {@link ItemStack}s.
	 * @return The index of the specified item in the list, or -1 if it was not in the list.
	 */
	public static int findIndexOfItem(Item item, NonNullList<ItemStack> items) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getItem() == item) {
				return i;
			}
		}
		return -1;
	}

	public static void fillAfterItemForCategory(Item item, Item targetItem, CreativeModeTab tab, NonNullList<ItemStack> items) {
		if (isAllowedInTab(item, tab)) {
			int targetIndex = findIndexOfItem(targetItem, items);
			if (targetIndex != -1) {
				items.add(targetIndex + 1, new ItemStack(item));
			} else {
				items.add(new ItemStack(item));
			}
		}
	}

	public static String intToRomanNumerals(int number) {
		String thousands = M_NUMERALS[number / 1000];
		String hundreds = C_NUMERALS[(number % 1000) / 100];
		String tens = X_NUMERALS[(number % 100) / 10];
		String ones = I_NUMERALS[number % 10];
		return thousands + hundreds + tens + ones;
	}

	/**
	 * Checks if an {@link Item} is in an {@link CreativeModeTab}.
	 *
	 * @param item The {@link Item} to check.
	 * @param tab  The {@link CreativeModeTab} to check.
	 * @return Whether the item is in the {@link CreativeModeTab}.
	 */
	public static boolean isAllowedInTab(Item item, @Nonnull CreativeModeTab tab) {
		return ((ItemInvokerMixin) item).callAllowedIn(tab);
	}
}