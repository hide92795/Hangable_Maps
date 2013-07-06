package hide92795.mods.hangablemaps.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.ModLoader;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.IEventListener;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {
	public static ArrayList<Integer> maps = new ArrayList<Integer>();
	private int counter;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (this.counter > 100) {
			maps.clear();
			RenderHangableMaps.trace = true;
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
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
					Packet250CustomPayload packet = new Packet250CustomPayload();
					packet.channel = "HM_ReqMapData";
					packet.data = bytes.toByteArray();
					packet.length = packet.data.length;
					ModLoader.sendPacket(packet);
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER, TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return null;
	}
}
