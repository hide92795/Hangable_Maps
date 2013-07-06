package hide92795.mods.hangablemaps.client;

import hide92795.mods.hangablemaps.HangableMaps;
import hide92795.mods.hangablemaps.HangableMaps.MapDataQueue;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.item.ItemMap;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		if (packet.channel.equals("HM_MapData")) {
			try {
				DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
				World var10 = ModLoader.getMinecraftInstance().theWorld;
				MapData var12 = ItemMap.getMPMapData(dataStream.readShort(), var10);
				int var6 = dataStream.readInt();

				for (int var8 = var6 * 128; var8 < var6 * 128 + 128; ++var8) {
					var12.colors[var8] = dataStream.readByte();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (packet.channel.equals("HM_MapInfo")) {
			try {
				DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
				short mapId = dataStream.readShort();
				int x = dataStream.readInt();
				int z = dataStream.readInt();
				int dim = dataStream.readInt();
				byte scale = dataStream.readByte();
				HangableMaps.instance.receiveMapInfo(mapId, x, z, dim, scale);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
