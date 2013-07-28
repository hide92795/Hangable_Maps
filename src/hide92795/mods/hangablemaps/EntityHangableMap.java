package hide92795.mods.hangablemaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class EntityHangableMap extends Entity {
	public int facingYaw;
	public int direction;
	public int xPosition;
	public int yPosition;
	public int zPosition;

	public int mapId;

	public ItemStack mapStack;
	private int tickCounter1;

	public EntityHangableMap(World par1World) {
		super(par1World);
		this.mapId = -1;
		this.direction = 0;
		this.yOffset = 0.0F;
		this.setSize(0.5F, 0.5F);
	}

	public EntityHangableMap(World world, int x, int y, int z, float var2, float var3, float var4, int facingYaw,
			int mapId, boolean onTop) {
		this(world);
		this.facingYaw = facingYaw;
		this.mapId = mapId;
		this.mapStack = new ItemStack(Item.map, 1, mapId);
		this.xPosition = x;
		this.yPosition = y;
		this.zPosition = z;
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
		setPosition(onTop, facingYaw, var2, var3, var4);
		this.dataWatcher.updateObject(19, new Integer(mapId));
	}

	public void setPosition(boolean top, int facingYaw, float var2, float var3, float var4) {
		if (!top) {
			this.setDirection(facingYaw);
		} else {
			this.direction = 4;
			switch (facingYaw) {
			case 0:
				this.facingYaw = 0;
				break;

			case 1:
				this.facingYaw = 270;
				break;

			case 2:
				this.facingYaw = 180;
				break;

			case 3:
				this.facingYaw = 90;
			}

			this.setPositionAndRotation((double) var2, (double) var3, (double) var4, (float) this.facingYaw, 90.0F);
		}
	}

	/**
	 * Sets the position and rotation. Only difference from the other one is no
	 * bounding on the rotation. Args: posX, posY, posZ, yaw, pitch
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9) {
		this.setPositionAndRotation(var1, this.posY, var5, var7, var8);
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(19, new Integer(0));
	}

	public int getMapIdFromDataWatcher() {
		return this.dataWatcher.getWatchableObjectInt(19);
	}

	/**
	 * Sets the direction the painting faces.
	 */
	public void setDirection(int par1) {
		this.direction = par1;
		this.prevRotationYaw = this.rotationYaw = (float) (par1 * 90);
		float var2 = 16f;
		float var3 = 16f;
		float var4 = 16f;

		if (par1 != 0 && par1 != 2) {
			var2 = 0.5F;
		} else {
			var4 = 0.5F;
		}

		var2 /= 32.0F;
		var3 /= 32.0F;
		var4 /= 32.0F;
		float var5 = (float) this.xPosition + 0.5F;
		float var6 = (float) this.yPosition + 0.5F;
		float var7 = (float) this.zPosition + 0.5F;
		float var8 = 0.5625F;

		if (par1 == 0) {
			var7 -= var8;
			var5 -= 0.0f;
		}

		if (par1 == 1) {
			var5 -= var8;
			var7 += 0.0f;
		}

		if (par1 == 2) {
			var7 += var8;
			var5 += 0.0f;
		}

		if (par1 == 3) {
			var5 += var8;
			var7 -= 0.0f;
		}
		var6 += 0.0f;
		this.setPosition((double) var5, (double) var6, (double) var7);
		float var9 = -0.00625F;
		// this.boundingBox.setBounds((double) (var5 - var2 - var9), (double) (var6 - var3 - var9),
		// (double) (var7 - var4 - var9), (double) (var5 + var2 + var9), (double) (var6 + var3 + var9),
		// (double) (var7 + var4 + var9));
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		if (this.tickCounter1++ == 100 && !this.worldObj.isRemote) {
			this.tickCounter1 = 0;
			if (!this.isDead && !this.onValidSurface()) {
				System.out.println(this.onValidSurface());
				System.out.println(isDead);
				this.setDead();
				this.entityDropItem(mapStack, 0.0F);
			}
		}
	}

	/**
	 * checks to make sure painting can be placed there
	 */
	public boolean onValidSurface() {
		// if (!this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()) {
		// System.out.println("EntityHangableMap.onValidSurface()-0");
		// return true;
		// } else {
		int var1 = 1;
		int var2 = 1;
		int var3 = this.xPosition;
		int var4 = this.yPosition;
		int var5 = this.zPosition;

		if (this.direction == 0) {
			var3 = MathHelper.floor_double(this.posX - (double) 0.5d);
		}

		if (this.direction == 1) {
			var5 = MathHelper.floor_double(this.posZ - (double) 0.5d);
		}

		if (this.direction == 2) {
			var3 = MathHelper.floor_double(this.posX - (double) 0.5d);
		}

		if (this.direction == 3) {
			var5 = MathHelper.floor_double(this.posZ - (double) 0.5d);
		}

		if (this.direction == 4) {
			var4 = MathHelper.floor_double(this.posY - (double) 0.5d);
		}

		var4 = MathHelper.floor_double(this.posY - (double) 0.5d);

		for (int var6 = 0; var6 < var1; ++var6) {
			for (int var7 = 0; var7 < var2; ++var7) {
				Material var8;

				if (this.direction != 0 && this.direction != 2) {
					var8 = this.worldObj.getBlockMaterial(this.xPosition, var4 + var7, var5 + var6);
				} else {
					var8 = this.worldObj.getBlockMaterial(var3 + var6, var4 + var7, this.zPosition);
				}

				if (!var8.isSolid()) {
					return false;
				}
			}
		}
		return true;
		// }
	}

	/**
	 * Returns true if other Entities should be prevented from moving through
	 * this Entity.
	 */
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
		System.out.println("EntityHangableMap.attackEntityFrom()");
		if (this.isEntityInvulnerable()) {
			return false;
		}
		if (!this.isDead && !this.worldObj.isRemote) {
			this.setDead();
			this.setBeenAttacked();
			EntityPlayer var3 = null;

			if (par1DamageSource.getEntity() instanceof EntityPlayer) {
				var3 = (EntityPlayer) par1DamageSource.getEntity();
			}

			if (var3 != null && var3.capabilities.isCreativeMode) {
				return true;
			}

			this.entityDropItem(mapStack, 0.0F);
		}

		return true;
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setByte("Dir", (byte) this.direction);
		par1NBTTagCompound.setInteger("FYaw", this.facingYaw);
		par1NBTTagCompound.setInteger("TileX", this.xPosition);
		par1NBTTagCompound.setInteger("TileY", this.yPosition);
		par1NBTTagCompound.setInteger("TileZ", this.zPosition);
		par1NBTTagCompound.setInteger("MapID", this.mapId);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		this.direction = par1NBTTagCompound.getByte("Dir");
		this.facingYaw = par1NBTTagCompound.getInteger("FYaw");
		this.xPosition = par1NBTTagCompound.getInteger("TileX");
		this.yPosition = par1NBTTagCompound.getInteger("TileY");
		this.zPosition = par1NBTTagCompound.getInteger("TileZ");
		this.mapId = par1NBTTagCompound.getInteger("MapID");
		this.mapStack = new ItemStack(Item.map, 1, this.mapId);
		this.dataWatcher.updateObject(19, new Integer(mapId));

		float var9 = (float) xPosition;
		float var10 = (float) yPosition;
		float var11 = (float) zPosition;
		float var12 = 0.59F;
		boolean top = this.direction == 4;

		if (direction == 0) {
			var11 -= var12;
		}
		if (direction == 1) {
			var9 -= var12;
		}
		if (direction == 2) {
			var11 += var12;
		}
		if (direction == 3) {
			var9 += var12;
		}
		if (direction == 4) {
			var10 += var12;
		}

		this.setPosition(top, facingYaw, var9 + 0.5F, var10 + 0.5F, var11 + 0.5F);
	}

	/**
	 * Tries to moves the entity by the passed in displacement. Args: x, y, z
	 */
	@Override
	public void moveEntity(double par1, double par3, double par5) {
		if (!this.worldObj.isRemote && !this.isDead && par1 * par1 + par3 * par3 + par5 * par5 > 0.0D) {
			this.setDead();
			this.entityDropItem(mapStack, 0.0F);
		}
	}

	/**
	 * Adds to the current velocity of the entity. Args: x, y, z
	 */
	@Override
	public void addVelocity(double par1, double par3, double par5) {
		if (!this.worldObj.isRemote && !this.isDead && par1 * par1 + par3 * par3 + par5 * par5 > 0.0D) {
			this.setDead();
			this.entityDropItem(mapStack, 0.0F);
		}
	}

	public MapData getMapData() {
		int id = getMapIdFromDataWatcher();
		if (id == -1) {
			return null;
		} else {
			MapData var1 = null;
			if (this.worldObj.isRemote) {
				var1 = ItemMap.getMPMapData((short) id, this.worldObj);
			} else {
				var1 = Item.map.getMapData(new ItemStack(Item.map, 1, id), this.worldObj);
			}
			return var1;
		}
	}
}
