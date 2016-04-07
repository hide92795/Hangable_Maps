/* Copyright (c) 2016 Lance Selga <lyonecro55@gmail.com>
 * 
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */
package lyonlancer5.mapdata;

import org.hide92795.mod.hangable_maps.HangableMaps;

import lyonlancer5.mapdata.PacketMapData.HM_ClientHandler;
import lyonlancer5.mapdata.PacketMapData.HM_ServerHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkWrapper {
	
	private static SimpleNetworkWrapper server, client;
	
	public static void registerNetworkWrappers(){
		server = NetworkRegistry.INSTANCE.newSimpleChannel("HM_Server");
		client = NetworkRegistry.INSTANCE.newSimpleChannel("HM_Client");
		
		server.registerMessage(HM_ServerHandler.class, PacketMapData.class, 0, Side.SERVER);
		client.registerMessage(HM_ClientHandler.class, PacketMapData.class, 1, Side.CLIENT);
		
		HangableMaps.logger.info("Registered network wrappers");
	}
	
	public static SimpleNetworkWrapper getServerPacketHandler(){
		return server;
	}
	
	public static SimpleNetworkWrapper getClientPacketHandler(){
		return client;
	}

}
