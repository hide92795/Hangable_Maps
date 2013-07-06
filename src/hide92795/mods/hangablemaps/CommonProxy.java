package hide92795.mods.hangablemaps;

import hide92795.mods.hangablemaps.client.ClientTickHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.src.ModLoader;

import cpw.mods.fml.common.INetworkHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.TickRegistry;

public class CommonProxy {
	public void registerRenderInformation() {
		// Client side only!
	}

	public void registerRecipies() {
		List var1 = CraftingManager.getInstance().getRecipeList();
		ItemStack var4;
		for (int var2 = 0; var2 < var1.size(); ++var2) {
			IRecipe var3 = (IRecipe) var1.get(var2);
			var4 = var3.getRecipeOutput();
			if (var4 != null) {
				if (var4.itemID == Item.emptyMap.itemID
						|| var4.itemID == Item.map.itemID) {
					var1.remove(var2);
				}
			}

		}
		ModLoader.addRecipe(new ItemStack(Item.emptyMap, 1, 0), new Object[] {
				"###", "#X#", "###", '#', Item.paper, 'X', Item.compass });
		CraftingManager.getInstance().getRecipeList()
				.add(new RecipeEmptyMapChangeScale());
	}

	public void registerEntity() {
		EntityRegistry.registerGlobalEntityID(EntityHangableMap.class,
				"hangablemap", HangableMaps.entityID);
		EntityRegistry.registerModEntity(EntityHangableMap.class,
				"hangablemap", HangableMaps.entityID, HangableMaps.instance,
				160, 5, false);
	}

	public void registerItem() {
		Item.emptyMap = (ItemEmptyHangableMap) new ItemEmptyHangableMap(139)
				.setUnlocalizedName("emptyMap");
		Item.map = (ItemHangableMap) new ItemHangableMap(102)
				.setUnlocalizedName("map");
	}
}
