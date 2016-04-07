/* Copyright (c) 2016 Lance Selga <lyonecro55@gmail.com>
 * 
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */
package lyonlancer5.mapdata;

import org.hide92795.mod.hangable_maps.HangableMaps;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Type;

public class ServerEventHandler {
	
	private int nextMapUpdate;
	
	/**
	 * Handles the original "ServerTickHandler" class
	 * Updated to fit the EventHandler instead of ITickHandler
	 */
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event){
		if(event.type == Type.SERVER){
			switch(event.phase){
			case START:
				break;
			case END:
				++this.nextMapUpdate;
				if (this.nextMapUpdate > 3) {
					this.nextMapUpdate = 0;
					HangableMaps.instance.executeQueue();
				}
				break;
			}
		}
	}
}
