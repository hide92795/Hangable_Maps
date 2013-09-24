package hide92795.mods.hangablemaps;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyHangableMap extends ItemEmptyMap {

	protected ItemEmptyHangableMap(int par1) {
		super(par1);
		setHasSubtypes(true);
		setTextureName("map_empty");
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer entityPlayer) {
		ItemStack var4 = new ItemStack(Item.map, 1, par2World.getUniqueDataId("map"));
		String var5 = "map_" + var4.getItemDamage();
		MapData mapData = new MapData(var5);
		par2World.setItemData(var5, mapData);
		mapData.scale = (byte) par1ItemStack.getItemDamage();
		int var7 = 128 * (1 << mapData.scale);
		mapData.xCenter = (int) (Math.round(entityPlayer.posX / (double) var7) * (long) var7);
		mapData.zCenter = (int) (Math.round(entityPlayer.posZ / (double) var7) * (long) var7);
		mapData.dimension = (byte) par2World.provider.dimensionId;
		mapData.markDirty();
		--par1ItemStack.stackSize;

		if (par1ItemStack.stackSize <= 0) {
			return var4;
		} else {
			if (!entityPlayer.inventory.addItemStackToInventory(var4.copy())) {
				entityPlayer.dropPlayerItem(var4);
			}

			return par1ItemStack;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("Scaling at 1:" + (1 << itemStack.getItemDamage()));
		par3List.add("(Level " + itemStack.getItemDamage() + "/" + 4 + ")");
	}
}
