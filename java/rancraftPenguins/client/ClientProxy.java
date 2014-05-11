package rancraftPenguins.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
//import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import rancraftPenguins.CommonProxy;
import rancraftPenguins.EntityPenguinAfr;
import rancraftPenguins.EntityPenguinCS;
import rancraftPenguins.EntityPenguinCloud;
import rancraftPenguins.EntityPenguinGent;
import rancraftPenguins.EntityPenguinHum;
import rancraftPenguins.EntityPenguinShuriken;
//import rancraftPenguins.client.RenderPenguinShuriken;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

import rancraftPenguins.EntityPenguinEmp;
//import rancraftPenguins.client.ModelPenguinEmp;
import rancraftPenguins.EntityPenguinKing;
//import rancraftPenguins.client.ModelPenguinKing;
import rancraftPenguins.EntityPenguinGal;
//import rancraftPenguins.client.ModelPenguinGal;
import rancraftPenguins.EntityPenguinMag;
//import rancraftPenguins.client.ModelPenguinMag;
import rancraftPenguins.EntityPenguinAdel;
//import rancraftPenguins.client.ModelPenguinAdel;
import rancraftPenguins.EntityPenguinYE;
//import rancraftPenguins.client.ModelPenguinYE;
import rancraftPenguins.EntityPenguinWF;
//import rancraftPenguins.client.ModelPenguinWF;
import rancraftPenguins.EntityPenguinLB;
//import rancraftPenguins.client.ModelPenguinLB;
import rancraftPenguins.EntityPenguinFlam;
//import rancraftPenguins.client.ModelPenguinFlam;
import rancraftPenguins.EntityPenguinNinj;
//import rancraftPenguins.client.ModelPenguinNinj;

public class ClientProxy extends CommonProxy {

	@Override
	public void load(){
		//rancraftPenguins.RanCraftPenguins.Channel.register(new ClientPacketHandler());
	}

	@Override
	public void initialize() {
		Minecraft mc = Minecraft.getMinecraft();
		MinecraftServer ms = MinecraftServer.getServer();
		// calling super will register any 2-sided tick handlers you have that are registered in the CommonProxy
		// this is important since the CommonProxy will only register it on the server side, and you will need it
		// registered on the client as well; however, we do not have any at this point
		super.initialize();
		
		// here we register our RenderTickHandler - be sure to pass in the instance of Minecraft!
		FMLCommonHandler.instance().bus().register(new ClientTickHandler(mc, ms));
		//FMLCommonHandler.instance().bus().register(new ClientTickHandler());

		// this is also an ideal place to register things like KeyBindings
	}

	@Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinShuriken.class, new RenderPenguinShuriken());
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinEmp.class, new RenderPenguin(new ModelPenguinEmp(),new ModelPenguinEmp(), 0.6F, "Emp"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinKing.class, new RenderPenguin(new ModelPenguinKing(),new ModelPenguinKing(), 0.5F, "King"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinYE.class, new RenderPenguin(new ModelPenguinYE(),new ModelPenguinYE(), 0.4F, "YE"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinMag.class, new RenderPenguin(new ModelPenguinMag(),new ModelPenguinMag(), 0.4F, "Mag"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinGal.class, new RenderPenguin(new ModelPenguinGal(),new ModelPenguinGal(), 0.3F, "Gal"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinAdel.class, new RenderPenguin(new ModelPenguinAdel(),new ModelPenguinAdel(), 0.3F, "Adel"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinWF.class, new RenderPenguin(new ModelPenguinWF(),new ModelPenguinWF(), 0.2F, "WF"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinLB.class, new RenderPenguin(new ModelPenguinLB(),new ModelPenguinLB(), 0.2F, "LB"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinGent.class, new RenderPenguin(new ModelPenguinGent(),new ModelPenguinGent(), 0.4F, "Gent"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinCS.class, new RenderPenguin(new ModelPenguinCS(),new ModelPenguinCS(), 0.4F, "CS"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinAfr.class, new RenderPenguin(new ModelPenguinAfr(),new ModelPenguinAfr(), 0.4F, "Afr"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinHum.class, new RenderPenguin(new ModelPenguinHum(),new ModelPenguinHum(), 0.4F, "Hum"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinFlam.class, new RenderPenguin(new ModelPenguinFlam(),new ModelPenguinFlam(), 0.7F, "Flam"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinNinj.class, new RenderPenguinNinj(new ModelPenguinNinj(),0.5F));
        //RenderingRegistry.registerEntityRenderingHandler(EntityPenguinNinj.class, new RenderPenguin(new ModelPenguinNinj(),new ModelPenguinNinj(), 0.5F, "Ninj"));
        RenderingRegistry.registerEntityRenderingHandler(EntityPenguinCloud.class, new RenderPenguin(new ModelPenguinCloud(),new ModelPenguinCloud(), 0.3F, "Cloud"));
	}

        /* not registering a renderer, but these are one-time events, too. *
        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
    } */
    @Override
    public int addArmor(String armorName){
    	return RenderingRegistry.addNewArmourRendererPrefix(armorName);
    }
}