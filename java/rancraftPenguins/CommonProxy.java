package rancraftPenguins;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {
    //public static String RANCRAFTPENGITEMS_PNG = "/rancraftPenguins/client/textures/items.png";
    //public static String BLOCK_PNG = "/tutorial/generic/block.png";
   
	public void initialize() {
		// since we are not registering a tick handler that ticks on the server, we will not put anything here for now
		// but if you had a WorldTickEvent or PlayerTickEvent, for example, this is where you should register it
		// if you try to register the RenderTickHandler here, your game WILL crash
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

	public void load(){
		//rancraftPenguins.RanCraftPenguins.Channel.register(new ServerPacketHandler());
	}

	//FMLCommonHandler;
	@EventHandler
	/*public void preInit(FMLPreInitializationEvent event)  {
		
        	//FMLCommonHandler.instance().bus().register(new SkcEvent());
        	FMLCommonHandler.instance().bus().register(null);
        
        	System.out.println("Event Handler Initialized");
		
	}*/
	
	// Client stuff
    public void registerRenderers() {
            // Nothing here as the server doesn't render graphics!
    }

	public int addArmor(String armorName) {
		return 0;
	}

	public void registerSounds() {
		// server doesn't do sounds
	}
}