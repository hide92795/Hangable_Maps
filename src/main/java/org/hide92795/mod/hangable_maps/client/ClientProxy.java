package org.hide92795.mod.hangable_maps.client;

import org.hide92795.mod.hangable_maps.CommonProxy;
import org.hide92795.mod.hangable_maps.EntityHangableMap;

import lyonlancer5.mapdata.ClientEventHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {
	
	public void registerTickHandlers(){
		FMLCommonHandler.instance().bus().register(new ClientEventHandler());
		super.registerTickHandlers();
	}
	
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(
				EntityHangableMap.class, new RenderHangableMaps());
	}
	
	public void registerRecipes() {
		super.registerRecipes();
	}

	public void registerEntity() {
		super.registerEntity();
	}
}
