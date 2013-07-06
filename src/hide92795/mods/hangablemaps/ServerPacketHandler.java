package hide92795.mods.hangablemaps;

import hide92795.mods.hangablemaps.HangableMaps.MapDataQueue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.storage.MapData;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ServerPacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager network,
			Packet250CustomPayload packet, Player player) {
		if (packet.channel.equals("HM_ReqMapData")) {
			try {
				EntityPlayerMP ePlayer = (EntityPlayerMP) player;
				DataInputStream dataStream = new DataInputStream(
						new ByteArrayInputStream(packet.data));
				int size = dataStream.readInt();
				for (int i = 0; i < size; i++) {
					int mapId = dataStream.readInt();
					MapDataQueue q = HangableMaps.createQueue(ePlayer.worldObj,
							mapId, ePlayer);
					boolean b = HangableMaps.instance.queue.contains(q);
					if (!b) {
						HangableMaps.instance.queue.add(q);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (packet.channel.equals("HM_ReqMapInfo")) {
			try {
				EntityPlayerMP ePlayer = (EntityPlayerMP) player;
				DataInputStream dataStream = new DataInputStream(
						new ByteArrayInputStream(packet.data));
				short mapId = dataStream.readShort();
				MapData mapdata = Item.map.getMapData(new ItemStack(Item.map,
						1, mapId), ePlayer.worldObj);
				sendMapInfo(ePlayer, mapId, mapdata);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sendMapInfo(EntityPlayerMP ePlayer, short mapId,
			MapData mapdata) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		data.writeShort(mapId);
		data.writeInt(mapdata.xCenter);
		data.writeInt(mapdata.zCenter);
		data.writeInt(mapdata.dimension);
		data.writeByte(mapdata.scale);
		Packet250CustomPayload packet = new Packet250CustomPayload(
				"HM_MapInfo", bytes.toByteArray());
		ePlayer.playerNetServerHandler.sendPacketToPlayer(packet);
	}
}
