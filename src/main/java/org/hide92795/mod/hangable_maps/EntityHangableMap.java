package org.hide92795.mod.hangable_maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class EntityHangableMap extends Entity {
	//Entity Position
	public int facingYaw;
	public int direction;
	public int xPosition;
	public int yPosition;
	public int zPosition;
	
	//Map Information
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
		this.mapStack = new ItemStack(HangableMaps.filledMap, 1, mapId);
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
	public void setDirection(int direction) {
		this.direction = direction;
		this.prevRotationYaw = this.rotationYaw = (float) (direction * 90);
		float var5 = (float) this.xPosition + 0.5F;
		float var6 = (float) this.yPosition + 0.5F;
		float var7 = (float) this.zPosition + 0.5F;
		float var8 = 0.5625F;

		if (direction == 0) {
			var7 -= var8;
			var5 -= 0.0f;
		}

		if (direction == 1) {
			var5 -= var8;
			var7 += 0.0f;
		}

		if (direction == 2) {
			var7 += var8;
			var5 += 0.0f;
		}

		if (direction == 3) {
			var5 += var8;
			var7 -= 0.0f;
		}
		var6 += 0.0f;
		this.setPosition((double) var5, (double) var6, (double) var7);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		if (this.tickCounter1++ == 100 && !this.worldObj.isRemote) {
			this.tickCounter1 = 0;
			if (!this.isDead && !this.onValidSurface()) {
				this.setDead();
				this.entityDropItem(mapStack, 0.0F);
			}
		}
	}

	/**
	 * checks to make sure painting can be placed there
	 */
	public boolean onValidSurface() {
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
					var8 = this.worldObj.getBlock(this.xPosition, var4 + var7, var5 + var6).getMaterial();
				} else {
					var8 = this.worldObj.getBlock(var3 + var6, var4 + var7, this.zPosition).getMaterial();
				}

				if (!var8.isSolid()) {
					return false;
				}
			}
		}
		return true;
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
	public boolean attackEntityFrom(DamageSource damageSource, float damageValue) {
		if (this.isEntityInvulnerable()) {
			return false;
		}
		if (!this.isDead && !this.worldObj.isRemote) {
			this.setDead();
			this.setBeenAttacked();
			EntityPlayer var3 = null;

			if (damageSource.getEntity() instanceof EntityPlayer) {
				var3 = (EntityPlayer) damageSource.getEntity();
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
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setByte("Dir", (byte) this.direction);
		nbt.setInteger("FYaw", this.facingYaw);
		nbt.setInteger("TileX", this.xPosition);
		nbt.setInteger("TileY", this.yPosition);
		nbt.setInteger("TileZ", this.zPosition);
		nbt.setInteger("MapID", this.mapId);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		this.direction = nbt.getByte("Dir");
		this.facingYaw = nbt.getInteger("FYaw");
		this.xPosition = nbt.getInteger("TileX");
		this.yPosition = nbt.getInteger("TileY");
		this.zPosition = nbt.getInteger("TileZ");
		this.mapId = nbt.getInteger("MapID");
		this.mapStack = new ItemStack(HangableMaps.filledMap, 1, this.mapId);
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
	public void moveEntity(double x, double y, double z) {
		if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0D) {
			this.setDead();
			this.entityDropItem(mapStack, 0.0F);
		}
	}

	/**
	 * Adds to the current velocity of the entity. Args: x, y, z
	 */
	@Override
	public void addVelocity(double x, double y, double z) {
		if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0D) {
			this.setDead();
			this.entityDropItem(mapStack, 0.0F);
		}
	}

	public MapData getMapData() {
		int id = getMapIdFromDataWatcher();
		if (id == -1) {
			return null;
		} else {
			MapData mapData = null;
			if (this.worldObj.isRemote) {
				mapData = ItemMap.func_150912_a(id, this.worldObj);
			} else {
				mapData = Items.filled_map.getMapData(new ItemStack(HangableMaps.filledMap, 1, id), this.worldObj);
			}
			return mapData;
		}
	}
}
