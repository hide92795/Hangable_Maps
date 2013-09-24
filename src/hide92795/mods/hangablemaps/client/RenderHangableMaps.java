package hide92795.mods.hangablemaps.client;

import hide92795.mods.hangablemaps.EntityHangableMap;
import hide92795.mods.hangablemaps.HangableMaps;
import hide92795.mods.hangablemaps.HangableMaps.HangableMapData;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.Renderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapCoord;
import net.minecraft.world.storage.MapData;

@SideOnly(Side.CLIENT)
public class RenderHangableMaps extends Render {
	private static final ResourceLocation texture_map_icon = new ResourceLocation("textures/map/map_icons.png");
	private static final ResourceLocation texture_map_background = new ResourceLocation("textures/map/map_background.png");
	public static boolean trace;
	private TextureManager textureManager;
	private Random rand = new Random();
	private int[] intArray = new int[16384];
	private final DynamicTexture bufferedImage;
	private GameSettings gameSettings;
	private FontRenderer fontRenderer;
	private ResourceLocation texture_mapdata;

	public RenderHangableMaps() {
		Minecraft minecraft = ModLoader.getMinecraftInstance();
		this.fontRenderer = minecraft.fontRenderer;
		this.gameSettings = minecraft.gameSettings;
		this.bufferedImage = new DynamicTexture(128, 128);
		this.textureManager = minecraft.renderEngine;
		this.texture_mapdata = textureManager.getDynamicTextureLocation("map", this.bufferedImage);
		this.intArray = this.bufferedImage.getTextureData();
		for (int var4 = 0; var4 < 16384; ++var4) {
			this.intArray[var4] = 0;
		}
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.renderHangableMap((EntityHangableMap) var1, var2, var4, var6, var8, var9);
	}

	public void renderHangableMap(EntityHangableMap entity, double var2, double var4, double var6, float var8,
			float var9) {
		this.rand.setSeed(187L);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) var2, (float) var4, (float) var6);
		GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw), 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * var9, 1.0F, 0.0F,
				0.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_LIGHTING);
		textureManager.bindTexture(texture_map_background);
		GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(8.0D, -8.0D, 1.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(-8.0D, -8.0D, 0.9D, 0.0D, 0.0D);
		tessellator.addVertexWithUV(-8.0D, 8.0D, 0.9D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(8.0D, 8.0D, 0.9D, 1.0D, 1.0D);
		tessellator.draw();
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glTranslatef(-8.0F, -8.0F, 0.9F);
		GL11.glScalef(0.125F, 0.125F, 0.125F);
		Minecraft minecraft = ModLoader.getMinecraftInstance();
		MapData mapdata = entity.getMapData();
		if (trace) {
			ClientTickHandler.maps.add(entity.getMapIdFromDataWatcher());
		}
		if (mapdata != null) {
			renderMap(entity, minecraft.thePlayer, mapdata);
		}
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	public void renderMap(EntityHangableMap entity, EntityPlayer player, MapData mapdata) {
		for (int i = 0; i < 16384; ++i) {
			byte b0 = mapdata.colors[i];

			if (b0 / 4 == 0) {
				this.intArray[i] = (i + i / 128 & 1) * 8 + 16 << 24;
			} else {
				int j = MapColor.mapColorArray[b0 / 4].colorValue;
				int k = b0 & 3;
				short short1 = 220;

				if (k == 2) {
					short1 = 255;
				}

				if (k == 0) {
					short1 = 180;
				}

				int l = (j >> 16 & 255) * short1 / 255;
				int i1 = (j >> 8 & 255) * short1 / 255;
				int j1 = (j & 255) * short1 / 255;
				this.intArray[i] = -16777216 | l << 16 | i1 << 8 | j1;
			}
		}

		this.bufferedImage.updateDynamicTexture();
		byte b1 = 0;
		byte b2 = 0;

		Tessellator tessellator = Tessellator.instance;
		textureManager.bindTexture(texture_mapdata);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(0.0d, 128.0d, -0.009999999776482582D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(128.0d, 128.0d, -0.009999999776482582D, 1.0D, 1.0D);
		tessellator.addVertexWithUV(128.0d, 0.0d, -0.009999999776482582D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(0.0d, 0.0d, -0.009999999776482582D, 0.0D, 0.0D);
		tessellator.draw();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);

		// render player icon
		HangableMapData mapinfo = HangableMaps.instance.getMapinfos((short) entity.getMapIdFromDataWatcher());
		textureManager.bindTexture(this.texture_map_icon);
		if (mapinfo.received) {
			Iterator<EntityPlayer> iterator = player.worldObj.playerEntities.iterator();
			while (iterator.hasNext()) {
				EntityPlayer entityPlayer = iterator.next();
				drawPlayer(entityPlayer, mapdata, mapinfo);
			}
		}

		// render map name
		if (HangableMaps.showMapName) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, -0.04F);
			GL11.glScalef(1.0F, 1.0F, 1.0F);
			this.fontRenderer.drawString(mapdata.mapName, 0, 0, -16777216);
			GL11.glPopMatrix();
		}
	}

	private void drawPlayer(EntityPlayer player, MapData mapdata, HangableMapData mapinfo) {
		if (player.dimension == mapinfo.dimension) {
			int scale = 1 << mapinfo.scale;
			float diffX = (float) (player.posX - (double) mapinfo.xCenter);
			float diffZ = (float) (player.posZ - (double) mapinfo.zCenter);

			int mapSize = scale * 64;
			if (Math.abs(diffX) > mapSize || Math.abs(diffZ) > mapSize) {
				return;
			}

			diffX /= (float) scale;
			diffZ /= (float) scale;

			byte centerX = (byte) ((int) ((double) (diffX * 2.0F) + 0.5D));
			byte centerZ = (byte) ((int) ((double) (diffZ * 2.0F) + 0.5D));
			byte var16 = 63;
			byte iconRotation;

			double par8 = player.rotationYaw;

			if (diffX >= (float) (-var16) && diffZ >= (float) (-var16) && diffX <= (float) var16
					&& diffZ <= (float) var16) {
				par8 += par8 < 0.0D ? -8.0D : 8.0D;
				iconRotation = (byte) ((int) (par8 * 16.0D / 360.0D));

				if (mapinfo.dimension < 0) {
					int var17 = rand.nextInt(100000);
					iconRotation = (byte) (var17 * var17 * 34187121 + var17 * 121 >> 15 & 15);
				}
			} else {
				iconRotation = 0;

				if (diffX <= (float) (-var16)) {
					centerX = (byte) ((int) ((double) (var16 * 2) + 2.5D));
				}

				if (diffZ <= (float) (-var16)) {
					centerZ = (byte) ((int) ((double) (var16 * 2) + 2.5D));
				}

				if (diffX >= (float) var16) {
					centerX = (byte) (var16 * 2 + 1);
				}

				if (diffZ >= (float) var16) {
					centerZ = (byte) (var16 * 2 + 1);
				}
			}
			if (HangableMaps.drawIconName) {
				// render name
				GL11.glPushMatrix();
				GL11.glTranslatef((float) centerX / 2.0F + 64.0F, (float) centerZ / 2.0F + 64.0F, -0.04F);
				GL11.glScalef(0.7F, 0.7F, 1.0F);
				int width = fontRenderer.getStringWidth(player.getEntityName());
				this.fontRenderer.drawString(player.getEntityName(), -width / 2, 3, 0x000000);
				GL11.glPopMatrix();
			}
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			byte iconSize = (byte) 0;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) centerX / 2.0F + 64.0F, (float) centerZ / 2.0F + 64.0F, -0.02F);
			GL11.glRotatef((float) (iconRotation * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
			GL11.glScalef(4.0F, 4.0F, 3.0F);
			GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
			float var21 = (float) (iconSize % 4 + 0) / 4.0F;
			float var23 = (float) (iconSize / 4 + 0) / 4.0F;
			float var22 = (float) (iconSize % 4 + 1) / 4.0F;
			float var24 = (float) (iconSize / 4 + 1) / 4.0F;
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-1.0D, 1.0D, 0.0D, (double) var21, (double) var23);
			tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, (double) var22, (double) var23);
			tessellator.addVertexWithUV(1.0D, -1.0D, 0.0D, (double) var22, (double) var24);
			tessellator.addVertexWithUV(-1.0D, -1.0D, 0.0D, (double) var21, (double) var24);
			tessellator.draw();
			GL11.glPopMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture_mapdata;
	}
}