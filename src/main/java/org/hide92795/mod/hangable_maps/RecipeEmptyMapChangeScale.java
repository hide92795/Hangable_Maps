package org.hide92795.mod.hangable_maps;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

public class RecipeEmptyMapChangeScale extends ShapedRecipes {

	public RecipeEmptyMapChangeScale() {
		super(3, 3, 
				new ItemStack[] { new ItemStack(Items.paper),
				new ItemStack(Items.paper), new ItemStack(Items.paper),
				new ItemStack(Items.paper), new ItemStack(HangableMaps.emptyMap),
				new ItemStack(Items.paper), new ItemStack(Items.paper),
				new ItemStack(Items.paper), new ItemStack(Items.paper) },
				
				new ItemStack(HangableMaps.emptyMap)
		);
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(InventoryCrafting inventoryCrafting, World world) {
		ItemStack emptymap = null;

		for (int slot = 0; slot < inventoryCrafting.getSizeInventory(); slot++) {
			
			ItemStack itemStack = inventoryCrafting.getStackInSlot(slot);
			
			if (itemStack == null) {
				return false;
			}
			
			switch (slot) {
			case 4:
				if(itemStack.getItem() == HangableMaps.emptyMap){
					emptymap = itemStack;
				} else {
					return false;
				}
				break;
				
			default:
				if(itemStack.getItem() != Items.paper){
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
