package com.rosemods.windswept.core.data.client;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.rosemods.windswept.core.Windswept;
import com.rosemods.windswept.core.other.WindsweptDamageSources;
import com.rosemods.windswept.core.registry.WindsweptAttributes;
import com.rosemods.windswept.core.registry.WindsweptEffects;
import com.rosemods.windswept.core.registry.WindsweptEnchantments;
import com.rosemods.windswept.core.registry.WindsweptEntityTypes;
import com.rosemods.windswept.integration.jei.WindsweptPlugin;
import com.teamabnormals.blueprint.common.block.sign.BlueprintStandingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallSignBlock;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.codehaus.plexus.util.StringUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.rosemods.windswept.core.registry.WindsweptBlocks.*;
import static com.rosemods.windswept.core.registry.WindsweptItems.*;

public class WindsweptLangProvider extends LanguageProvider {
    private final List<String> keys = Lists.newArrayList();

    public WindsweptLangProvider(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), Windswept.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Items //
        this.add(HOLLY_BOATS.getSecond().get(), "Holly Boat with Chest");
        this.add(CHESTNUT_BOATS.getSecond().get(), "Chestnut Boat with Chest");
        this.add(PINE_BOATS.getSecond().get(), "Pine Boat with Chest");
        this.add(CHRISTMAS_PUDDING_SLICE.get(), "Slice of Christmas Pudding");
        this.add(SWEET_BERRY_BOWL.get(), "Bowl of Sweet Berries");
        this.add(WILD_BERRY_BOWL.get(), "Bowl of Wild Berries");
        this.add(GOAT.get(), "Raw Chevon");
        this.add(COOKED_GOAT.get(), "Cooked Chevon");
        this.add(GOAT_SHANKS.get(), "Raw Chevon Shanks");
        this.add(COOKED_GOAT_SHANKS.get(), "Cooked Chevon Shanks");
        this.add(FOUL_BERRY_BOWL.get(), "Bowl of Foul Berries");
        this.add(CUPIDS_ARROW.get(), "Cupid's Arrow");
        this.add(HOLLY_BERRIES_ON_A_STICK.get(), "Holly Berries on a Stick");
        this.translateBannerPattern(SNOW_CHARGE_BANNER_PATTERN, "snow_charge");
        this.translateBannerPattern(SNOW_GOLEM_BANNER_PATTERN, "snow_golem");
        this.translateBannerPattern(ROSE_FLOWER_BANNER_PATTERN, "rose_flower");
        this.translateMusicDisc(MUSIC_DISC_RAIN, "rose - rain");
        this.translateMusicDisc(MUSIC_DISC_SNOW, "rose - snow");
        this.translateMusicDisc(MUSIC_DISC_BUMBLEBEE, "rose - bumblebee");
        this.add(HOLLY_FURNACE_BOAT.get(), "Holly Boat with Furnace");
        this.add(CHESTNUT_FURNACE_BOAT.get(), "Chestnut Boat with Furnace");
        this.add(PINE_FURNACE_BOAT.get(), "Pine Boat with Furnace");

        // Blocks //
        this.translateSign(HOLLY_SIGNS, "holly");
        this.add(HOLLY_HEDGE.get(), "Holly Leaf Hedge");
        this.add(HOLLY_LEAF_PILE.get(), "Pile of Holly Leaves");
        this.add(HOLLY_BERRY_BASKET.get(), "Basket of Holly Berries");
        this.translateSign(CHESTNUT_SIGNS, "chestnut");
        this.add(CHESTNUT_HEDGE.get(), "Chestnut Leaf Hedge");
        this.add(CHESTNUT_LEAF_PILE.get(), "Pile of Chestnut Leaves");
        this.add(CHESTNUT_CRATE.get(), "Crate of Chestnuts");
        this.add(ROASTED_CHESTNUT_CRATE.get(), "Crate of Roasted Chestnuts");
        this.translateSign(PINE_SIGNS, "pine");
        this.add(PINE_HEDGE.get(), "Pine Leaf Hedge");
        this.add(PINE_LEAF_PILE.get(), "Pile of Pine Leaves");
        this.add(WILL_O_THE_WISP.get(), "Will o' the Wisp");
        this.add("block.windswept.snow_carpet", "Snow Carpet");
        this.add(GINGER_ROOT_CRATE.get(), "Crate of Ginger Roots");
        this.add(WILD_BERRY_BASKET.get(), "Basket of Wild Berries");
        this.add(RED_MUSHROOM_BASKET.get(), "Basket of Red Mushrooms");
        this.add(BROWN_MUSHROOM_BASKET.get(), "Basket of Brown Mushrooms");
        this.add(GLOW_SHROOM_BASKET.get(), "Basket of Glow Shrooms");

        // Entities //
        this.add(WindsweptEntityTypes.CUPIDS_ARROW.get(), "Cupid's Arrow");

        // Effects //
        this.translateEffect(WindsweptEffects.THORNS, "Causes damage to enemies when they attack you.");
        this.translateEffect(WindsweptEffects.FROST_RESISTANCE, "Grants immunity to frost. Allows for walking on Powder Snow. Stops Entity Conversion in Powder Snow.");
        this.translateEffect(WindsweptEffects.PLENTY, "idk yet.");

        this.translatePotion(WindsweptEffects.THORNS_POTION, "Prickling");
        this.translatePotion(WindsweptEffects.LONG_THORNS_POTION, "Prickling");
        this.translatePotion(WindsweptEffects.STRONG_THORNS_POTION, "Prickling");

        this.translatePotion(WindsweptEffects.FROST_RESISTANCE_POTION, "Frost Resistance");
        this.translatePotion(WindsweptEffects.LONG_FROST_RESISTANCE_POTION, "Frost Resistance");

        // Attributes
        this.translateAttribute(WindsweptAttributes.SNOW_SPEED);
        this.translateAttribute(WindsweptAttributes.FRAGRANCE);
        this.add(WindsweptAttributes.SPRINT_DAMAGE.get().getDescriptionId(), "Attack Damage while Sprinting");

        // Enchantments //
        this.translateEnchantment(WindsweptEnchantments.SLIPPING_CURSE, "Curse of Slipping", "Causes the wearer to slip on any block as if it was ice whilst damaging the boots.");

        // Damage Sources //
        this.translateDamageSource(WindsweptDamageSources.HOLLY_LEAVES,
                player -> player + " was ripped to death by holly leaves",
                (player, entity) -> player + " was ripped to death by holly leaves whilst trying to escape " + entity);

        this.translateDamageSource(WindsweptDamageSources.ICICLE,
                player -> player + " was impaled on an icicle",
                (player, entity) -> player + " was impaled on an icicle whilst trying to escape " + entity);

        // Slabfish //
        this.translateSlabfish("chestnut");
        this.translateSlabfish("grove");
        this.translateSlabfish("pine");

        // JEI Info //
        this.jeiInfo(MUSIC_DISC_RAIN, "Dropped by a Drowned if killed by a Skeleton.");
        this.jeiInfo(MUSIC_DISC_SNOW, "Dropped by a Chilled if killed by a Skeleton.");
        this.jeiInfo(MUSIC_DISC_BUMBLEBEE, "Sometimes dropped when shearing a Beehive for Honeycombs.");

        this.jeiInfo(WILD_BERRIES, "Wild berries are the fruit of the wild berry bush. The plant is made unique by their ability to grow in snowy conditions. The berry can be cultivated for a juice that can regenerate health, and can be frozen to a popsicle that grants resistance to frost.");
        this.jeiInfo(NIGHTSHADE, "Nightshades are mystical flowers found rarely growing in patches from the roots of trees. The flower emits a subtle white light; similar to the light of the moon.");

        this.jeiInfo(CARVED_PINECONE_BLOCK, "The face of the Carved Pinecone creates a single note randomly as wind passes through them, the note produced is deeper depending on how high they are stacked.");
        //this.jeiInfo(WILL_O_THE_WISP, "The face of the Carved Pinecone ");

        this.jeiInfo(ICE_LANTERN, "The Ice Lantern emits a light incapable of melting Ice and Snow.");
        this.jeiInfo(WOODEN_BUCKET, "Wooden buckets are easily craft-able; but it comes at a cost to their strength. Durability is taken when fluids exit the bucket.");
        this.jeiInfo(SNOW_BOOTS, "Snow boots allow for faster traversal through snow, and grants the wearer the ability to walk on Powder Snow. The leather can be dyed.");

        // Auto Translation //
        this.translateRegistry(ForgeRegistries.BLOCKS, Block::getDescriptionId);
        this.translateRegistry(ForgeRegistries.ITEMS, Item::getDescriptionId);
        this.translateRegistry(ForgeRegistries.ENTITY_TYPES, EntityType::getDescriptionId);
        this.translateRegistry(ForgeRegistries.BIOMES, b -> "biome." + Windswept.MOD_ID + "." + ForgeRegistries.BIOMES.getKey(b).getPath());
    }


    private <T> void translateRegistry(IForgeRegistry<T> registry, Function<T, String> toString) {
        for (RegistryObject<T> object : Windswept.REGISTRY_HELPER.getSubHelper(registry).getDeferredRegister().getEntries())
            this.add(toString.apply(object.get()), toUpper(registry, object));
    }

    @Override
    public void add(String key, String value) {
        if (!this.keys.contains(key)) {
            super.add(key, value);
            this.keys.add(key);
        }
    }

    private void translateBlock(RegistryObject<? extends Block> block) {
        this.add(block.get(), toUpper(ForgeRegistries.BLOCKS, block));
    }

    private void translateEnchantment(RegistryObject<? extends Enchantment> enchantment, String name, String desc) {
        String descId = enchantment.get().getDescriptionId();
        this.add(descId, name);
        this.add(descId + ".desc", desc);
    }

    private void translateMusicDisc(RegistryObject<? extends Item> item, String desc) {
        this.add(item.get(), "Music Disc");
        this.addDescription(item, desc);
    }

    private void translateSign(Pair<RegistryObject<BlueprintStandingSignBlock>, RegistryObject<BlueprintWallSignBlock>> sign, String name) {
        this.translateBlock(sign.getFirst());
        this.add(sign.getFirst().get().getDescriptionId().replace(name, name + "_wall"), toUpper(ForgeRegistries.BLOCKS, sign.getSecond()));
    }

    private void translateAttribute(RegistryObject<? extends Attribute> attribute) {
        this.add(attribute.get().getDescriptionId(), toUpper(ForgeRegistries.ATTRIBUTES, attribute));
    }

    private void translateEffect(RegistryObject<? extends MobEffect> effect, String desc) {
        this.add(effect.get(), toUpper(ForgeRegistries.MOB_EFFECTS, effect));
        this.add(effect.get().getDescriptionId() + ".description", desc);
    }

    private void translateSlabfish(String type) {
        this.add("entity.environmental.slabfish.type." + type, toUpper(type));
    }

    private void translateBannerPattern(RegistryObject<? extends Item> item, String name) {
        String desc = toUpper(name);
        this.add(item.get(), "Banner Pattern");
        this.addDescription(item, desc);

        for (DyeColor dye : DyeColor.values())
            this.add("block.minecraft.banner." + Windswept.MOD_ID + "." + name + "." + dye.getName(), toUpper(dye.getName()) + " " + desc);
    }

    private void jeiInfo(Supplier<? extends ItemLike> item, String desc) {
        this.add(WindsweptPlugin.getDesc(item), desc);
    }

    private void translateDamageSource(DamageSource source, Function<String, String> death, BiFunction<String, String, String> killed) {
        this.add("death.attack." + source.getMsgId(), death.apply("%1$s"));
        this.add("death.attack." + source.getMsgId() + ".player", killed.apply("%1$s", "%2$s"));
    }

    private void translatePotion(RegistryObject<? extends Potion> potion, String effect) {
        String name = ForgeRegistries.POTIONS.getKey(potion.get()).getPath();

        this.add("item.minecraft.potion.effect." + name, "Potion of " + effect);
        this.add("item.minecraft.splash_potion.effect." + name, "Splash Potion of " + effect);
        this.add("item.minecraft.tipped_arrow.effect." + name, "Arrow of " + effect);
        this.add("item.minecraft.lingering_potion.effect." + name, "Lingering Potion of " + effect);
        this.add("item.caverns_and_chasms.tether_potion.effect." + name, "Tether Potion of " + effect);
    }

    private void addDescription(RegistryObject<? extends Item> item, String desc) {
        this.add(item.get().getDescriptionId() + ".desc", desc);
    }

    private static <T> String toUpper(IForgeRegistry<T> registry, RegistryObject<? extends T> object) {
        return toUpper(registry.getKey(object.get()).getPath());
    }

    private static String toUpper(String string) {
        return StringUtils.capitaliseAllWords(string.replace('_', ' '));
    }

}
