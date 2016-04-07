package org.hide92795.mod.hangable_maps;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEmptyHangableMap extends ItemEmptyMap {

	protected ItemEmptyHangableMap() {
		super();
		setHasSubtypes(true);
		setTextureName("map_empty");
		setUnlocalizedName("emptyMap");
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer entityPlayer) {
		ItemStack mapStack = new ItemStack(HangableMaps.filledMap, 1, par2World.getUniqueDataId("map"));
		String mapIdent = "map_" + mapStack.getItemDamage();
		MapData mapData = new MapData(mapIdent);
		par2World.setItemData(mapIdent, mapData);
		mapData.scale = (byte) par1ItemStack.getItemDamage();
		int var7 = 128 * (1 << mapData.scale);
		mapData.xCenter = (int) (Math.round(entityPlayer.posX / (double) var7) * (long) var7);
		mapData.zCenter = (int) (Math.round(entityPlayer.posZ / (double) var7) * (long) var7);
		mapData.dimension = (byte) par2World.provider.dimensionId;
		mapData.markDirty();
		--par1ItemStack.stackSize;

		if (par1ItemStack.stackSize <= 0) {
			return mapStack;
		} else {
			if (!entityPlayer.inventory.addItemStackToInventory(mapStack.copy())) {
				entityPlayer.entityDropItem(mapStack, 0);
			}

			return par1ItemStack;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("Scaling at 1:" + (1 << itemStack.getItemDamage()));
		par3List.add("(Level " + itemStack.getItemDamage() + "/" + 4 + ")");
	}
}
