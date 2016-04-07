/* Copyright (c) 2016 Lance Selga <lyonecro55@gmail.com>
 * 
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */
package lyonlancer5.mapdata;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import org.hide92795.mod.hangable_maps.client.RenderHangableMaps;

import lyonlancer5.mapdata.PacketMapData.PacketChannel;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientEventHandler {
	
	public static ArrayList<Integer> maps = Lists.newArrayList();
	private int counter;
	
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event){
		if(event.type == Type.CLIENT){
			switch(event.phase){
			case START:
				if (this.counter > 100) {
					maps.clear();
					RenderHangableMaps.trace = true;
				}
				break;
				
			case END:
				try {
					if (this.counter++ > 100) {
						this.counter = 0;
						RenderHangableMaps.trace = false;
						if (maps.size() != 0) {
							ByteArrayOutputStream bytes = new ByteArrayOutputStream();
							DataOutputStream data = new DataOutputStream(bytes);
							data.writeInt(maps.size());
							for (Integer i : maps) {
								data.writeInt(i);
							}
							PacketMapData packet = new PacketMapData(PacketChannel.REQ_MAP_DATA, bytes.toByteArray());
							NetworkWrapper.getServerPacketHandler().sendToServer(packet);
						}
					}
				} catch (Exception e) {
				}
				break;
			}
		}
	}
	
	

}
