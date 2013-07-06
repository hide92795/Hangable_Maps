package hide92795.mods.hangablemaps;

import hide92795.mods.hangablemaps.client.ClientTickHandler;

import java.io.ByteArrayOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.BaseMod;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "HangableMaps", name = "Hangable Maps", version = "1.6.1_1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {
		"HM_MapData", "HM_MapInfo" }, packetHandler = hide92795.mods.hangablemaps.client.ClientPacketHandler.class), serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {
		"HM_ReqMapData", "HM_ReqMapInfo" }, packetHandler = ServerPacketHandler.class))
public class HangableMaps {
	public LinkedList<MapDataQueue> queue = new LinkedList<MapDataQueue>();
	public HashMap<Short, HangableMapData> mapinfos = new HashMap<Short, HangableMapData>();
	@Instance("HangableMaps")
	public static HangableMaps instance;
	public static boolean drawIconName;
	public static boolean createMapCenterOnPlayer;
	public static int entityID;
	public static boolean showMapName;
	private static Configuration config;

	@SidedProxy(clientSide = "hide92795.mods.hangablemaps.client.ClientProxy", serverSide = "hide92795.mods.hangablemaps.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.registerItem();
		config = new Configuration(event.getSuggestedConfigurationFile());
		loadConfiguration(config);
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		proxy.registerRenderInformation();
		proxy.registerEntity();
		proxy.registerRecipies();
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}

	private void loadConfiguration(Configuration configuration) {
		configuration.load();
		this.showMapName = configuration.get("general", "showMapName", true).getBoolean(true);
		this.entityID = configuration.get("general", "entityID", 190).getInt(190);
		this.drawIconName = configuration.get("general", "drawIconName", false).getBoolean(false);
		configuration.save();
	}

	public void sendMapData(MapData map, short id, int row, EntityPlayerMP player) {
		try {

			byte[] data = getMapData(map, id, row);
			if (data == null) {
				return;
			}
			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = "HM_MapData";
			packet.data = data;
			packet.length = packet.data.length;
			player.playerNetServerHandler.sendPacketToPlayer(packet);
		} catch (Exception e) {
		}
	}

	private byte[] getMapData(MapData map, short id, int row) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try {
			data.writeShort(id);
			data.writeInt(row);
			for (int var6 = row * 128; var6 < row * 128 + 128; ++var6) {
				data.writeByte(map.colors[var6]);
			}
			return bytes.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MapDataQueue createQueue(World world, int id, EntityPlayerMP player) {
		MapDataQueue q = new MapDataQueue();
		q.world = world;
		q.id = id;
		q.row = 0;
		q.playerMP = player;
		return q;
	}

	public static class MapDataQueue {
		public World world;
		public EntityPlayerMP playerMP;
		public int id;
		public int row;

		@Override
		public int hashCode() {
			return (playerMP.username + "-" + id).hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MapDataQueue) {
				MapDataQueue q = (MapDataQueue) obj;
				if (q.id == this.id) {
					return true;
				}
			}
			return false;
		}
	}

	public void executeQueue() {
		int size = queue.size();
		for (int i = 0; i < size; i++) {
			MapDataQueue q = queue.pollFirst();
			ItemStack stack = new ItemStack(Item.map, 1, q.id);
			MapData data = Item.map.getMapData(stack, q.world);
			if (data != null) {
				sendMapData(data, (short) q.id, q.row, q.playerMP);
			} else {
				q.row = 128;
			}
			q.row++;
			if (q.row < 128) {
				queue.addLast(q);
			}
		}
	}

	public class HangableMapData {
		public boolean received;
		public int xCenter;
		public int zCenter;
		public int randomEffect;
		public int dimension;
		public byte scale;
	}

	private HangableMapData requestMapInfo(short mapId) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try {
			data.writeShort(mapId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "HM_ReqMapInfo";
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;
		ModLoader.sendPacket(packet);
		HangableMapData mapdata = new HangableMapData();
		mapinfos.put(mapId, mapdata);
		return mapdata;
	}

	public void receiveMapInfo(short mapId, int xCenter, int zCenter, int dimension, byte scale) {
		HangableMapData data = getMapinfos(mapId);
		data.xCenter = xCenter;
		data.zCenter = zCenter;
		data.dimension = dimension;
		data.scale = scale;
		data.received = true;
		mapinfos.put(mapId, data);
	}

	public HangableMapData getMapinfos(short mapId) {
		HangableMapData data = mapinfos.get(mapId);
		if (data == null) {
			data = requestMapInfo(mapId);
		}
		return data;
	}
}
