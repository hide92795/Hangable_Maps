package org.hide92795.mod.hangable_maps;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import lyonlancer5.mapdata.NetworkWrapper;
import lyonlancer5.mapdata.PacketMapData;
import lyonlancer5.mapdata.PacketMapData.PacketChannel;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "HangableMaps", name = "Hangable Maps", version = "1.7.10_1")
public class HangableMaps {
	
	public LinkedList<PacketMapData.MapDataQueue> queue = new LinkedList<PacketMapData.MapDataQueue>();
	public HashMap<Short, HangableMapData> mapinfos = new HashMap<Short, HangableMapData>();
	
	public static final Logger logger = LogManager.getLogger("Hangable Maps");
	
	@Instance("HangableMaps")
	public static HangableMaps instance;

	@SidedProxy(clientSide = "org.hide92795.mod.hangable_maps.client.ClientProxy", serverSide = "org.hide92795.mod.hangable_maps.CommonProxy")
	public static CommonProxy proxy;
	
	public static boolean drawIconName;
	public static boolean createMapCenterOnPlayer;
	public static boolean showMapName;
	public static int entityID;
	
	public static final ItemEmptyMap emptyMap = new ItemEmptyHangableMap();
	public static final ItemMap filledMap = new ItemHangableMap();
	
	private static Configuration config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger.info("Loading Hangable Maps - version 1.7.10_1");
		
		NetworkWrapper.registerNetworkWrappers();
		GameRegistry.registerItem(emptyMap, emptyMap.getUnlocalizedName());
		GameRegistry.registerItem(filledMap, filledMap.getUnlocalizedName());
		
		config = new Configuration(event.getSuggestedConfigurationFile());
		loadConfiguration(config);
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		proxy.registerRenderers();
		proxy.registerEntity();
		proxy.registerRecipes();
	}

	private void loadConfiguration(Configuration configuration) {
		try {
			configuration.load();
			showMapName = configuration.get("General", "Show Map Name", true).getBoolean(true);
			entityID = configuration.get("General", "Hangable Map Entity ID", 190).getInt(190);
			drawIconName = configuration.get("General", "Draw Icon Name", false).getBoolean(false);
		} catch (Exception e) {
			
		} finally {
			configuration.save();
		}
	}

	/**
	 * Sends the map data from the server to the client
	 */
	public void sendMapData(MapData map, short id, int row, EntityPlayerMP player) {
		byte[] data = getMapData(map, id, row);
		if (data == null) {
			return;
		}
		
		PacketMapData packet = new PacketMapData(PacketChannel.MAP_DATA, data);
		NetworkWrapper.getServerPacketHandler().sendTo(packet, player);
	}

	
	/**
	 * Transforms the {@link MapData} and its relevant information to 
	 * a byte array.
	 * @param map The MapData
	 * @param id The ID of the MapData
	 * @param row The row of the map data
	 * @return A byte array containing the map data
	 */
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

	/**
	 * Executes the update of the filled map
	 */
	public void executeQueue() {
		int size = queue.size();
		for (int i = 0; i < size; i++) {
			PacketMapData.MapDataQueue q = queue.pollFirst();
			ItemStack stack = new ItemStack(filledMap, 1, q.id);
			MapData data = filledMap.getMapData(stack, q.world);
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

	/**
	 * Sends a request to the server for map information
	 * @param mapId The map ID to query the server for
	 * @return The {@link HangableMapData} of the requested map
	 */
	private HangableMapData requestMapInfo(short mapId) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try {
			data.writeShort(mapId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PacketMapData packet = new PacketMapData(PacketChannel.REQ_MAP_INFO, bytes.toByteArray());
		NetworkWrapper.getClientPacketHandler().sendToServer(packet);
		HangableMapData mapdata = new HangableMapData();
		mapinfos.put(mapId, mapdata);
		return mapdata;
	}

	/**
	 * Processes the given map information
	 * @param mapId The map ID associated with the map data
	 */
	public void receiveMapInformation(short mapId, int xCenter, int zCenter, int dimension, byte scale) {
		HangableMapData data = getMapInformation(mapId);
		data.xCenter = xCenter;
		data.zCenter = zCenter;
		data.dimension = dimension;
		data.scale = scale;
		data.received = true;
		mapinfos.put(mapId, data);
	}

	/**
	 *Retrieves the map data for the given map ID
	 * @param mapId The map ID to get
	 * @return The {@link HangableMapData} of the requested map
	 */
	public HangableMapData getMapInformation(short mapId) {
		HangableMapData data = mapinfos.get(mapId);
		if (data == null) {
			data = requestMapInfo(mapId);
		}
		return data;
	}
}
