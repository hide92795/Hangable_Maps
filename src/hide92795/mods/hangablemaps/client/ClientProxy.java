package hide92795.mods.hangablemaps.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.ModLoader;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import hide92795.mods.hangablemaps.EntityHangableMap;
import hide92795.mods.hangablemaps.CommonProxy;
import hide92795.mods.hangablemaps.ServerTickHandler;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderInformation() {
		RenderingRegistry.registerEntityRenderingHandler(
				EntityHangableMap.class, new RenderHangableMaps());
	}
}
