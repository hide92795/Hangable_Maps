package org.hide92795.mod.hangable_maps;

import lyonlancer5.mapdata.ServerEventHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

	public void registerRenderers() {}
	
	public void registerTickHandlers(){
		FMLCommonHandler.instance().bus().register(new ServerEventHandler());
	}
	
	@SuppressWarnings("unchecked")
	public void registerRecipes() {
		GameRegistry.addRecipe(new ItemStack(HangableMaps.emptyMap, 1, 0), new Object[]{
			"#Y#", "#X#", "###", '#', Items.paper, 'X', Items.compass, 'Y', Items.item_frame 
		});
		
		CraftingManager.getInstance().getRecipeList().add(new RecipeEmptyMapChangeScale());
	}

	public void registerEntity() {
		EntityRegistry.registerGlobalEntityID(EntityHangableMap.class,
				"hangablemap", HangableMaps.entityID);
		EntityRegistry.registerModEntity(EntityHangableMap.class,
				"hangablemap", HangableMaps.entityID, HangableMaps.instance,
				160, 5, false);
	}
}
