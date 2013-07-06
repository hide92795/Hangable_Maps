package hide92795.mods.hangablemaps;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class RecipeEmptyMapChangeScale extends ShapedRecipes {

	public RecipeEmptyMapChangeScale() {
		super(3, 3, new ItemStack[] { new ItemStack(Item.paper),
				new ItemStack(Item.paper), new ItemStack(Item.paper),
				new ItemStack(Item.paper), new ItemStack(Item.emptyMap),
				new ItemStack(Item.paper), new ItemStack(Item.paper),
				new ItemStack(Item.paper), new ItemStack(Item.paper) },
				new ItemStack(Item.emptyMap));
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(InventoryCrafting par1InventoryCrafting,
			World par2World) {
		ItemStack emptymap = null;

		for (int slot = 0; slot < par1InventoryCrafting.getSizeInventory(); slot++) {
			ItemStack itemStack = par1InventoryCrafting.getStackInSlot(slot);
			if (itemStack == null) {
				return false;
			}
			switch (slot) {
			case 4:
				if (itemStack.itemID == Item.emptyMap.itemID) {
					emptymap = itemStack;
				} else {
					return false;
				}
				break;
			default:
				if (itemStack.itemID != Item.paper.itemID) {
					return false;
				}
				break;
			}

		}

		if (emptymap == null) {
			return false;
		} else {
			return emptymap == null ? false : emptymap.getItemDamage() < 4;
		}
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting) {
		ItemStack emptymap = par1InventoryCrafting.getStackInSlot(4);
		emptymap = emptymap.copy();
		emptymap.stackSize = 1;
		emptymap.setItemDamage(emptymap.getItemDamage() + 1);
		return emptymap;
	}
}
