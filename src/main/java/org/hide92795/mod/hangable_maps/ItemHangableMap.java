package org.hide92795.mod.hangable_maps;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemHangableMap extends ItemMap {
	protected ItemHangableMap() {
		super();
		this.setMaxStackSize(64);
		setTextureName("map_filled");
		setUnlocalizedName("map");
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer,
			World world, int x, int y, int z, int direction, float par8,
			float par9, float par10) {
		int facingYaw = 0;
		float var9 = (float) x;
		float var10 = (float) y;
		float var11 = (float) z;
		float var12 = 0.59F;
		boolean onTop = false;

		if (direction == 1) {
			var10 += var12;
			facingYaw = MathHelper
					.floor_double((double) (entityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			onTop = true;
		}

		if (direction == 0) {
			return false;
		} else {
			if (direction == 2) {
				var11 -= var12;
				facingYaw = 0;
			}

			if (direction == 3) {
				var11 += var12;
				facingYaw = 2;
			}

			if (direction == 4) {
				var9 -= var12;
				facingYaw = 1;
			}

			if (direction == 5) {
				var9 += var12;
				facingYaw = 3;
			}

			EntityHangableMap entity = new EntityHangableMap(world, x, y, z,
					var9 + 0.5F, var10 + 0.5F, var11 + 0.5F, facingYaw,
					itemStack.getItemDamage(), onTop);
			if (entity.onValidSurface()) {
				if (!world.isRemote) {
					world.spawnEntityInWorld(entity);
				}

				--itemStack.stackSize;
			}
			return true;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		MapData var5 = this
				.getMapData(par1ItemStack, par2EntityPlayer.worldObj);
		if (var5 == null) {
			par3List.add("Fetching map data...");
		} else {
			par3List.add(var5.mapName);
			par3List.add("Scaling at 1:" + (1 << var5.scale));
			par3List.add("(Level " + var5.scale + "/" + 4 + ")");
		}
	}
}
