/* Copyright (c) 2016 Lance Selga <lyonecro55@gmail.com>
 * 
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */
package lyonlancer5.mapdata;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.hide92795.mod.hangable_maps.HangableMaps;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class PacketMapData implements IMessage {

	public PacketChannel channel;
	public byte[] data;
	private int length;
	
	public PacketMapData(){}
	
	public PacketMapData(PacketChannel channel, byte[] data){
		this.channel = channel;
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		channel = PacketChannel.fromName(ByteBufUtils.readUTF8String(buf));
		length = buf.readInt();
		data = buf.readBytes(length).array();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, channel.name);
		buf.writeInt(data.length);
		buf.writeBytes(data);
	}
	
	/**
	 * Creates a queue to update the map, called on the server side
	 * @param world The world object that the map is contained in
	 * @param id The map ID
	 * @param player The player currently holding the map
	 * @return An instance of the {@link MapDataQueue} to execute later
	 */
	public static MapDataQueue createQueue(World world, int id, EntityPlayerMP player) {
		MapDataQueue q = new MapDataQueue();
		q.world = world;
		q.id = id;
		q.row = 0;
		q.playerMP = player;
		return q;
	}



	public static enum PacketChannel {
		//CLIENT SIDE
		MAP_DATA("HM_MapData"),
		MAP_INFO("HM_MapInfo"),
		
		//SERVER SIDE
		REQ_MAP_DATA("HM_ReqMapData"),
		REQ_MAP_INFO("HM_ReqMapInfo");
		
		public final String name;
		
		private PacketChannel(String x){
			name = x;
		}
		
		public static PacketChannel fromName(String x){
			for(PacketChannel e : PacketChannel.values()){
				if(e.name.equalsIgnoreCase(x)) return e;
			}
			return null;
		}
	}

	public static class HM_ServerHandler implements IMessageHandler<PacketMapData, PacketMapData>{

		@Override
		public PacketMapData onMessage(PacketMapData message, MessageContext ctx) {
			if(ctx.side == Side.CLIENT){
				if (message.channel == PacketChannel.REQ_MAP_DATA) {
					try {
						EntityPlayerMP playerMP = ctx.getServerHandler().playerEntity;
						DataInputStream dataStream = new DataInputStream(
								new ByteArrayInputStream(message.data));
						int size = dataStream.readInt();
						for (int i = 0; i < size; i++) {
							int mapId = dataStream.readInt();
							PacketMapData.MapDataQueue dataQueue = PacketMapData.createQueue(playerMP.worldObj,
									mapId, playerMP);
							boolean flag = HangableMaps.instance.queue.contains(dataQueue);
							if (!flag) {
								HangableMaps.instance.queue.add(dataQueue);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (message.channel == PacketChannel.REQ_MAP_INFO) {
					try {
						EntityPlayerMP playerMP = ctx.getServerHandler().playerEntity;
						DataInputStream dataStream = new DataInputStream(
								new ByteArrayInputStream(message.data));
						short mapId = dataStream.readShort();
						MapData mapdata = HangableMaps.filledMap.getMapData(new ItemStack(HangableMaps.filledMap, 1, mapId), playerMP.worldObj);
						sendMapInfo(playerMP, mapId, mapdata);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					
				}
			}
			return null;
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
			PacketMapData packet = new PacketMapData(PacketChannel.MAP_INFO, bytes.toByteArray());
			NetworkWrapper.getClientPacketHandler().sendTo(packet, ePlayer);
			
		}
	}
	
	public static class HM_ClientHandler implements IMessageHandler<PacketMapData, PacketMapData>{

		@Override
		public PacketMapData onMessage(PacketMapData message, MessageContext ctx) {
			if(ctx.side == Side.SERVER){
				if (message.channel == PacketChannel.MAP_DATA){
					try {
						DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(message.data));
						World worldObj = Minecraft.getMinecraft().theWorld;
						MapData mapData = ItemMap.func_150912_a(dataStream.readShort(), worldObj);
						int var6 = dataStream.readInt();

						for (int var8 = var6 * 128; var8 < var6 * 128 + 128; ++var8) {
							mapData.colors[var8] = dataStream.readByte();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (message.channel == PacketChannel.MAP_INFO){
					try {
						DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(message.data));
						short mapId = dataStream.readShort();
						int x = dataStream.readInt();
						int z = dataStream.readInt();
						int dim = dataStream.readInt();
						byte scale = dataStream.readByte();
						HangableMaps.instance.receiveMapInformation(mapId, x, z, dim, scale);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}
	}

	public static class MapDataQueue {
		public World world;
		public EntityPlayerMP playerMP;
		public int id;
		public int row;
	
		@Override
		public int hashCode() {
			return (playerMP.getCommandSenderName() + "-" + id).hashCode();
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
}
