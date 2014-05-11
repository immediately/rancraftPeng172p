package rancraftPenguins;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;

import java.io.File;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import rancraftPenguins.client.ClientTickHandler;

import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.common.BiomeDictionary;
import static io.netty.buffer.Unpooled.buffer;
import static net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

//@Mod(modid = "rancraftpenguins", name = "Rancraft Penguins", version = "1.7.2p")
//@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {
//		"RanCraftExt", "RanCraftAL", "RanCraftFD", "RanCraftFRS", "RanCraftFRSR" }, packetHandler = RCPacketHandler.class)
@Mod(modid = RanCraftPenguins.MODID, name = "Rancraft Penguins", version = RanCraftPenguins.VERSION)
public class RanCraftPenguins {

	public static final String modID = "rancraftpenguins"; // must match modid above!

	// The instance of your mod that Forge uses.
	@Instance("RanCraftPenguins")
	public static RanCraftPenguins instance;

    public static final String MODID = "rancraftpenguins";
    public static final String VERSION = "1.7.2p";

	public static FMLEventChannel Channel;
	
	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "rancraftPenguins.client.ClientProxy", serverSide = "rancraftPenguins.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        // create our mod's channel. (ChannelHandler doesn't work:"cannot instantiate the type")
        //NetworkRegistry.INSTANCE.newChannel("NetworkExample", new ChannelHandler());
    }

/*    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onEntityUpdated(LivingEvent.LivingUpdateEvent updateEvent) {
        ByteBuf data = buffer(4);
        data.writeInt(42);
        C17PacketCustomPayload packet = new C17PacketCustomPayload("NetworkExample", data);
        EntityClientPlayerMP player = (EntityClientPlayerMP)updateEvent.entityLiving;
        player.sendQueue.addToSendQueue(packet);
    }
*/
/*    @EventHandler
    public void init(FMLInitializationEvent event){
    	Channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("RCExt");
    	//proxy.load();

    	//Channel.sendToServer(FMLProxyPacket);
    	//Channel.sendTo(FMLProxyPacket, EntityPlayerMP);
    }
*/
    static ToolMaterial WEAPONPENGUINITE0 = EnumHelperClient.addToolMaterial(
			"Penguinite", 0, 60, 2.0F, -3, 15);
	static ToolMaterial WEAPONPENGUINITE1 = EnumHelperClient.addToolMaterial(
			"Penguinite", 0, 60, 2.0F, 2, 15);

	// (name, harvestLevel, maxUses, efficiency, damage, enchantability)
	/*
	name, durability, protection, enchantability
	CLOTH(5, new int[]{1, 3, 2, 1}, 15),
    CHAIN(15, new int[]{2, 5, 4, 1}, 12),
    IRON(15, new int[]{2, 6, 5, 2}, 9),
    GOLD(7, new int[]{2, 5, 3, 1}, 25),
    DIAMOND(33, new int[]{3, 8, 6, 3}, 10);
	 */

	public static ArmorMaterial ARMORPENGUINITE1 = EnumHelperClient.addArmorMaterial(
			"ARMORPENGUINITE1", 7, new int[] { 2, 4, 3, 2 }, 13); // name, dur,
																	// reduction[],
																	// enchant
	static ArmorMaterial ARMORPENGUINITE2 = EnumHelperClient.addArmorMaterial(
			"ARMORPENGUINITE2", 18, new int[] { 3, 6, 5, 3 }, 15); // name, dur,
																	// reduction[],
																	// enchant
	static ArmorMaterial ARMORPENGUINITE3 = EnumHelperClient.addArmorMaterial(
			"ARMORPENGUINITE3", 26, new int[] { 3, 8, 6, 3 }, 17); // name, dur,
																	// reduction[],
																	// enchant

	//customCraftingMaterial(PengScaleRed);

	public static Item PengScaleRed, PengDownCloud, PengFeatherBlack,
			PengFeatherBlue, PengFeatherBrown;
	public static Item PengFeatherBrownStriped, PengFeatherStriped,
			PengFeatherWhite, PengFeatherYellow;
	public static Item PengSkinBlack, PengSkinBlue, PengSkinLightBlue,
			PengSkinBrown, PengSkinRed, PenguinCrown, FishMagma, PenguinSauce;
	public static Item PenguinHatEmp, PenguinHatKing, PenguinHatAdel,
			PenguinHatYE, PenguinHatGal, PenguinHatAfr, PenguinHatHum;
	public static Item PenguinHatMag, PenguinHatWF, PenguinHatLB,
			PenguinHatGent, PenguinHatCS;
	public static Item PenguinTunic, PenguinPants, PenguinFlippers, PenguinBootsCloud;
	public static Item PenguinFlameMask, PenguinFlameChestPlate,
			PenguinFlameLeggings, PenguinFlameFlippers;
	public static Item PenguinBow, PenguinFlameRepeater, PenguinKatanaD, 
			PenguinKatanaS, PenguinShuriken, PenguinFishingRod, PenguinShears;

/*	public static int PengScaleRedID, PengDownCloudID, PengFeatherBlackID,
			PengFeatherBlueID, PengFeatherBrownID;
	public static int PengSkinBlackID, PengSkinBlueID, PengSkinLightBlueID,
			PengSkinBrownID, PengSkinRedID, PenguinCrownID, FishMagmaID, PenguinSauceID;
	public static int PengFeatherBrownStripedID, PengFeatherStripedID,
			PengFeatherWhiteID, PengFeatherYellowID;
	public static int PenguinHatEmpID, PenguinHatKingID, PenguinHatAdelID,
			PenguinHatYEID, PenguinHatGalID, PenguinHatAfrID, PenguinHatHumID;
	public static int PenguinHatMagID, PenguinHatWFID, PenguinHatLBID,
			PenguinHatGentID, PenguinHatCSID;
	public static int PenguinTunicID, PenguinPantsID, PenguinFlippersID, PenguinBootsCloudID;
	public static int PenguinFlameMaskID, PenguinFlameChestPlateID,
			PenguinFlameLeggingsID, PenguinFlameFlippersID;
	
	public static int PenguinBowID, PenguinFlameRepeaterID, PenguinKatanaDID,
			PenguinKatanaSID, PenguinShurikenID, PenguinFishingRodID, PenguinShearsID;
*/
	public static int PengEmpSpawnFreq, PengKingSpawnFreq, PengAdelSpawnFreq,
			PengYESpawnFreq, PengAfrSpawnFreq, PengHumSpawnFreq;
	public static int PengGalSpawnFreq, PengMagSpawnFreq, PengWFSpawnFreq,
			PengLBSpawnFreq;
	public static int PengGentSpawnFreq, PengCSSpawnFreq;
	public static int PengFlamSpawnFreq, PengNinjSpawnFreq, PengCloudSpawnFreq;

	public static int pengQuietInt; // chance that a calm penguin will remain
									// quiet
	private static int pengCallFreqInt; // how often healthy penguins call

	// register the packet handler (from http://www.minecraftforge.net/wiki/Netty_Packet_Handling)
	//public static final PacketPipeline packetPipeline = new PacketPipeline();

	//EnumMap<Side, FMLEmbeddedChannel> channels = NetworkRegistry.INSTANCE.newChannel(channel, new ChannelHandler());
	//EnumMap<Side, FMLEmbeddedChannel> channels = NetworkRegistry.INSTANCE.newChannel("RCChannel", new ChannelHandler());
	//EnumMap<Side, FMLEmbeddedChannel> channels = NetworkRegistry.INSTANCE.newChannel("RCChannel", new ChannelHandlerRC());
	//newChannel("RCChannel", FMLIndexedMessageToMessageCodec);
	public static NetworkHelper nh;
	
/*	@EventHandler
	public void initialise(FMLInitializationEvent evt) {
	    packetPipeline.initialise();
	}

	@EventHandler
	public void postInitialise(FMLPostInitializationEvent evt) {
	    packetPipeline.postInitialise();
	}
*/
	//@PreInit
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {

        //nh = new NetworkHelper("RC_P", RCPacketC.class, RCPacketS.class); // if you use multiple packet classes, register all like this
        nh = new NetworkHelper("RC_P", RCPacket.class);
        //World world = Minecraft.getMinecraft().theWorld;


		// New for 1.7:
		// IMPORTANT: Be sure to register your handler on the correct bus!!! (see below)
        //@SideOnly(Side.CLIENT)

		// the majority of events use the MinecraftForge event bus:
		//MinecraftForge.EVENT_BUS.register(new ClientTickHandler());

		// but some are on the FML bus:
		//System.out.printf("\n *** SERVER or CLIENT? ****\n\n");
        //@SideOnly(Side.CLIENT)
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()){
    		Minecraft mcc = Minecraft.getMinecraft();
    		MinecraftServer mcs = MinecraftServer.getServer();
    		//System.out.printf("It's client, apparently.\n");
        	FMLCommonHandler.instance().bus().register(new ClientTickHandler(mcc, mcs));
        }

		
		
		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());
		// System.out.printf("Config file is %s.\n",
		// event.getSuggestedConfigurationFile().getName());
		config.load();

		/*PengScaleRedID = config.get("Penguin Scale Red", "PSCR", 4200).getInt();
		PengFeatherBlackID = config.getItem("Penguin Feather Black ID", 4201)
				.getInt();
		PengFeatherBlueID = config.getItem("Penguin Feather Blue ID", 4202)
				.getInt();
		PengFeatherBrownID = config.getItem("Penguin Feather Brown ID", 4203)
				.getInt();
		PengFeatherBrownStripedID = config.getItem(
				"Penguin Feather Brown Striped ID", 4207).getInt();
		PengFeatherStripedID = config.getItem("Penguin Feather Striped ID",
				4204).getInt();
		PengFeatherWhiteID = config.getItem("Penguin Feather White ID", 4205)
				.getInt();
		PengFeatherYellowID = config.getItem("Penguin Feather Yellow ID", 4206)
				.getInt();
		PengDownCloudID = config.getItem("Penguin Down Cloud ID", 4208)
				.getInt();

		PengSkinBlackID = config.getItem("Penguin Skin Black ID", 4251)
				.getInt();
		PengSkinBlueID = config.getItem("Penguin Skin Blue ID", 4252).getInt();
		PengSkinRedID = config.getItem("Penguin Skin Red ID", 4253).getInt();
		PengSkinBrownID = config.getItem("Penguin Skin Brown ID", 4254)
				.getInt();
		PengSkinLightBlueID = config
				.getItem("Penguin Skin Light Blue ID", 4255).getInt();
		FishMagmaID = config.getItem("Magma Fish ID", 4231).getInt();
		PenguinSauceID = config.getItem("Penguin Sauce ID", 4232).getInt();
		PenguinShearsID = config.getItem("Penguin Shears ID", 4232).getInt();
		PenguinCrownID = config.getItem("Penguin Crown ID", 4280).getInt();

		PenguinHatEmpID = config.getItem("Penguin Hat Emperor ID", 4300)
				.getInt();
		PenguinHatKingID = config.getItem("Penguin Hat King ID", 4301).getInt();
		PenguinHatAdelID = config.getItem("Penguin Hat Adélie ID", 4305)
				.getInt();
		PenguinHatYEID = config.getItem("Penguin Hat Yellow-Eyed ID", 4304)
				.getInt();
		PenguinHatMagID = config.getItem("Penguin Hat Magellanic ID", 4303)
				.getInt()ITickHandler;
		PenguinHatGalID = config.getItem("Penguin Hat Galápagos ID", 4306)
				.getInt();
		PenguinHatWFID = config.getItem("Penguin Hat White Flippered ID", 4307)
				.getInt();
		PenguinHatLBID = config.getItem("Penguin Hat Little Blue ID", 4302)
				.getInt();
		PenguinHatGentID = config.getItem("Penguin Hat Gentoo ID", 4308)
				.getInt();
		PenguinHatCSID = config.getItem("Penguin Hat Chinstrap ID", 4309)
				.getInt();
		PenguinHatAfrID = config.getItem("Penguin Hat African ID", 4310)
				.getInt();
		PenguinHatHumID = config.getItem("Penguin Hat Humboldt ID", 4311)
				.getInt();

		PenguinTunicID = config.getItem("Penguin Tunic ID", 4350).getInt();
		PenguinPantsID = config.getItem("Penguin Pants ID", 4351).getInt();
		PenguinFlippersID = config.getItem("Penguin Flippers ID", 4352)
				.getInt();
		PenguinBootsCloudID = config.getItem("Penguin Boots Cloud ID", 4353)
				.getInt();
		PenguinFlameMaskID = config.getItem("Penguin Hat Flame ID", 4400)
				.getInt();
		PenguinFlameChestPlateID = config.getItem(
				"Penguin Chestplate Flame ID", 4401).getInt();
		PenguinFlameLeggingsID = config.getItem("Penguin Leggings Flame ID",
				4402).getInt();
		PenguinFlameFlippersID = config.getItem("Penguin Flippers Flame ID",
				4403).getInt();

		PenguinBowID = config.getItem("Penguin Bow ID", 4501).getInt();
		PenguinFlameRepeaterID = config.getItem("Penguin Flame Repeater ID",
				4511).getInt();
		PenguinKatanaDID = config.getItem("Penguin's Katana ID", 4512).getInt();
		PenguinKatanaSID = config.getItem("Penguin's Katana Sheathed ID", 4513)
				.getInt();
		PenguinShurikenID = config.getItem("Penguin's Shuriken ID", 4514)
				.getInt();
		PenguinFishingRodID = config.getItem("Penguin Fishing Rod ID",
				4515).getInt();
				*/

		//PenguinBowID = config.get("cat", "key", 4501, "duh").getInt(); /* experiment */

		PengEmpSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Emperor", 20).getInt(20);
		PengKingSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency King", 16).getInt(16);
		PengAdelSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Adélie", 30).getInt(30);
		PengYESpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Yellow-Eyed", 20).getInt(20);
		PengMagSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Magellanic", 20).getInt(20);
		PengGalSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Galápagos", 20).getInt(20);
		PengWFSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency White-Flippered", 20).getInt(20);
		PengLBSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Little Blue", 30).getInt(30);
		PengGentSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Gentoo", 20).getInt(20);
		PengCSSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Chinstrap", 20).getInt(20);
		PengAfrSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency African", 20).getInt(20);
		PengHumSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Humboldt", 20).getInt(20);
		PengFlamSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Flame", 40).getInt(40);
		PengNinjSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Ninja", 20).getInt(20);
		PengCloudSpawnFreq = config.get(config.CATEGORY_GENERAL,
				"Spawn Frequency Cloud Penguin", 20).getInt(20);

		pengCallFreqInt = config.get(config.CATEGORY_GENERAL,
				"How often healthy penguins call (0-9)", 9).getInt(9);
		switch (pengCallFreqInt) {
		case 0:
			pengQuietInt = 255; // 1 in 255 chance of a call
			break;
		case 1 - 9:
			pengQuietInt = 11 - pengCallFreqInt; // 9 is default pengCallFreqInt
													// so 2 is default
													// pengQuietInt
			break;
		default:
			pengQuietInt = 2; // out of range values default to pengQuietInt of
								// 2
			break;
		}
		// System.out.printf("pengQuietInt is now %d\n", pengQuietInt);

		config.save();

		proxy.registerSounds();

		// For better looking roll-over labels in English AND localization!
		//LocalizePenguins.localize();

		// second arg of ItemPenguinStuff constructor is actual filename of the
		// texture png
		PengScaleRed = new ItemRCSimple("pengScaleFlam")
				.setUnlocalizedName("PSCR").setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengScaleRed, "PSCR");
		PengFeatherBlack = new ItemRCSimple("pengFeatherBlack").setUnlocalizedName("PFBLK").setCreativeTab(
				CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengFeatherBlack, "PFBLK");
		PengFeatherBlue = new ItemRCSimple("pengFeatherBlue").setUnlocalizedName("PFBLU").setCreativeTab(
						CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengFeatherBlue, "PFBLU");
		PengFeatherBrown = new ItemRCSimple("pengFeatherBrown").setUnlocalizedName("PFBR").setCreativeTab(
				CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengFeatherBrown, "PFBR");
		PengFeatherBrownStriped = new ItemRCSimple("pengFeatherBrownStriped").setUnlocalizedName("PFBRST")
				.setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengFeatherBrownStriped, "PFBRST");
		PengFeatherStriped = new ItemRCSimple("pengFeatherStriped").setUnlocalizedName("PFST")
				.setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengFeatherStriped, "PFST");
		PengFeatherWhite = new ItemRCSimple("pengFeatherWhite").setUnlocalizedName("PFW").setCreativeTab(
				CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengFeatherWhite, "PFW");
		PengFeatherYellow = new ItemRCSimple("pengFeatherYellow").setUnlocalizedName("PFY").setCreativeTab(
				CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengFeatherYellow, "PFY");
		PengDownCloud = new ItemRCSimple("pengDownCloud")
				.setUnlocalizedName("PDCL").setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengDownCloud, "PDCL");

		PengSkinBlack = new ItemRCSimple("pengSkinBlack")
				.setUnlocalizedName("PSBLK").setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengSkinBlack, "PSBLK");
		PengSkinBlue = new ItemRCSimple("pengSkinBlue")
				.setUnlocalizedName("PSBLU").setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengSkinBlue, "PSBLU");
		PengSkinLightBlue = new ItemRCSimple("pengSkinLightBlue").setUnlocalizedName("PSLBLU")
				.setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengSkinLightBlue, "PSLBLU");
		PengSkinRed = new ItemRCSimple("pengSkinFlam")
				.setUnlocalizedName("PSKR").setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengSkinRed, "PSKR");
		PengSkinBrown = new ItemRCSimple("pengSkinBrown")
				.setUnlocalizedName("PSBR").setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PengSkinBrown, "PSBR");
		PenguinCrown = new ItemRCSimple("pengCrown")
				.setUnlocalizedName("PCR").setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PenguinCrown, "PCR");
		FishMagma = new ItemRCSimple("magmaFish")
				.setUnlocalizedName("MF").setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(FishMagma, "MF");
		PenguinSauce = new ItemRCSimple("pengSauce")
				.setUnlocalizedName("PSC").setCreativeTab(CreativeTabs.tabMaterials);
		GameRegistry.registerItem(PenguinSauce, "PSC");

		//PenguinHatEmp = new PenguinArmor(PenguinHatEmpID, ARMORPENGUINITE1,
			//	"pengHatEmp", 3, 0).setUnlocalizedName("EPH");
		PenguinHatEmp = new PenguinArmor(ARMORPENGUINITE1, "pengHatEmp", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("EPH");
		GameRegistry.registerItem(PenguinHatEmp, "EPH");
		PenguinHatKing = new PenguinArmor(ARMORPENGUINITE1, "pengHatKing", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("KPH");
		GameRegistry.registerItem(PenguinHatKing, "KPH");
		PenguinHatAdel = new PenguinArmor(ARMORPENGUINITE1, "pengHatAdel", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("APH");
		GameRegistry.registerItem(PenguinHatAdel, "APH");
		PenguinHatYE = new PenguinArmor(ARMORPENGUINITE1, "pengHatYE", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("YEPH");
		GameRegistry.registerItem(PenguinHatYE, "YEPH");
		PenguinHatLB = new PenguinArmor(ARMORPENGUINITE1, "pengHatLB", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("LBPH");
		GameRegistry.registerItem(PenguinHatLB, "LBPH");
		PenguinHatMag = new PenguinArmor(ARMORPENGUINITE1, "pengHatMag", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("MPH");
		GameRegistry.registerItem(PenguinHatMag, "MPH");
		PenguinHatGal = new PenguinArmor(ARMORPENGUINITE1, "pengHatGal", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("GPH");
		GameRegistry.registerItem(PenguinHatGal, "GPH");
		PenguinHatWF = new PenguinArmor(ARMORPENGUINITE1, "pengHatWF", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("WFPH");
		GameRegistry.registerItem(PenguinHatWF, "WFPH");
		PenguinHatGent = new PenguinArmor(ARMORPENGUINITE1, "pengHatGent", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("GTPH");
		GameRegistry.registerItem(PenguinHatGent, "GTPH");
		PenguinHatCS = new PenguinArmor(ARMORPENGUINITE1, "pengHatCS", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("CSPH");
		GameRegistry.registerItem(PenguinHatCS, "CSPH");
		PenguinHatAfr = new PenguinArmor(ARMORPENGUINITE1, "pengHatAfr", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("AFPH");
		GameRegistry.registerItem(PenguinHatAfr, "AFPH");
		PenguinHatHum = new PenguinArmor(ARMORPENGUINITE1, "pengHatHum", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("HPH");
		GameRegistry.registerItem(PenguinHatHum, "HPH");

		PenguinTunic = new PenguinArmor(ARMORPENGUINITE1, "pengTunic", proxy.addArmor("PenguinArmor"), 1).setUnlocalizedName("PT");
		GameRegistry.registerItem(PenguinTunic, "PT");
		PenguinPants = new PenguinArmor(ARMORPENGUINITE1, "pengPants", proxy.addArmor("PenguinArmor"), 2).setUnlocalizedName("PP");
		GameRegistry.registerItem(PenguinPants, "PP");
		PenguinFlippers = new PenguinArmor(ARMORPENGUINITE1, "pengFlippers", proxy.addArmor("PenguinArmor"), 3).setUnlocalizedName("PF");
		GameRegistry.registerItem(PenguinFlippers, "PF");
		PenguinBootsCloud = new PenguinArmor(ARMORPENGUINITE1, "pengBootsCloud", proxy.addArmor("PenguinArmor"), 3).setUnlocalizedName("CPB");
		GameRegistry.registerItem(PenguinBootsCloud, "CPB");

		PenguinFlameMask = new PenguinArmor(ARMORPENGUINITE2, "pengMask", proxy.addArmor("PenguinArmor"), 0).setUnlocalizedName("FPM");
		GameRegistry.registerItem(PenguinFlameMask, "FPM");
		PenguinFlameChestPlate = new PenguinArmor(ARMORPENGUINITE2, "pengChestPlate", proxy.addArmor("PenguinArmor"), 1).setUnlocalizedName("FPCP");
		GameRegistry.registerItem(PenguinFlameChestPlate, "FPCP");
		PenguinFlameLeggings = new PenguinArmor(ARMORPENGUINITE2, "pengLeggings", proxy.addArmor("PenguinArmor"), 2).setUnlocalizedName("FPL");
		GameRegistry.registerItem(PenguinFlameLeggings, "FPL");
		PenguinFlameFlippers = new PenguinArmor(ARMORPENGUINITE2, "pengFlamFlippers", proxy.addArmor("PenguinArmor"), 3).setUnlocalizedName("FPF");
		GameRegistry.registerItem(PenguinFlameFlippers, "FPF");

		PenguinBow = new ItemPenguinBow().setUnlocalizedName("PB").setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(PenguinBow, "PB");
		PenguinFlameRepeater = new ItemPenguinFlameRepeater().setUnlocalizedName("PFR").setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(PenguinFlameRepeater, "PFR");
		PenguinKatanaD =       new ItemPenguinKatana(WEAPONPENGUINITE1, 1)
				.setMaxStackSize(1).setUnlocalizedName("PKD").setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(PenguinKatanaD, "PKD");
		PenguinKatanaS = new ItemPenguinKatana(WEAPONPENGUINITE0, 0)
				.setMaxStackSize(1).setUnlocalizedName("PKS").setCreativeTab(null);
		GameRegistry.registerItem(PenguinKatanaS, "PKS");
		PenguinShuriken = new ItemPenguinShuriken()
				.setMaxStackSize(16).setUnlocalizedName("PS").setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(PenguinShuriken, "PS");
		PenguinFishingRod = new ItemPenguinFishingRod().setUnlocalizedName("PFRD").setCreativeTab(CreativeTabs.tabTools);
		GameRegistry.registerItem(PenguinFishingRod, "PFRD");
		PenguinShears = new ItemPenguinShears().setUnlocalizedName("PSS").setCreativeTab(CreativeTabs.tabTools);
		GameRegistry.registerItem(PenguinShears, "PSS");
	}

	//@Init
	@Mod.EventHandler
	public void load(FMLInitializationEvent event) {

		proxy.registerRenderers();

		//item.NividiumIngot.name=Nividium Ingot;
		//tile.NividiumOre.name=Nividium Ore;
		// old version -- items didn't show up on item list without it
//		LanguageRegistry.addName(PenguinFlippers, "Penguin Flippers");
//		LanguageRegistry.addName(PengSkinBlack, "Black Penguin Skin");
//		LanguageRegistry.addName(PengFeatherBlack, "Black Penguin Feather");
		// now this just replaces the unlocalized name (e.g. item.PFBLK.name => item.Black Penguin Feather.name 
		//PenguinKatanaD.setUnlocalizedName("Penguin's Katana");
		//PenguinKatanaS.setUnlocalizedName("Penguin's Katana Sheath");
		//PenguinShuriken.setUnlocalizedName("Penguin's Shuriken");
		//PenguinFlameRepeater.setUnlocalizedName("Flame Penguin Repeater");
		//PenguinBow.setUnlocalizedName("Penguin Bow");
		//PenguinFishingRod.setUnlocalizedName("Penguin Fishing Rod");
		//PengScaleRed.setUnlocalizedName("Flame Penguin Scale");
		//PengFeatherBlack.setUnlocalizedName("Black Penguin Feather");
		//PengFeatherBlue.setUnlocalizedName("Blue Penguin Feather");
		//PengDownCloud.setUnlocalizedName("Cloud Penguin Down");
		//PengFeatherBrown.setUnlocalizedName("Brown Penguin Feather");
		//PengFeatherBrownStriped.setUnlocalizedName("Brown Striped Penguin Feather");
		//PengFeatherStriped.setUnlocalizedName("Striped Penguin Feather");
		//PengFeatherWhite.setUnlocalizedName("White Penguin Feather");
		//PengFeatherYellow.setUnlocalizedName("Yellow Penguin Feather");
		//PengSkinBlack.setUnlocalizedName("Black Penguin Skin");
		//PengSkinBlue.setUnlocalizedName("Blue Penguin Skin");
		//PengSkinLightBlue.setUnlocalizedName("Light Blue Penguin Skin");
		//PengSkinRed.setUnlocalizedName("Red Penguin Skin");
		//PengSkinBrown.setUnlocalizedName("Brown Penguin Skin");
		//PenguinCrown.setUnlocalizedName("Penguin Crown");
		//FishMagma.setUnlocalizedName("Magma Fish");
		//PenguinShears.setUnlocalizedName("Penguin Shears");
		//PenguinSauce.setUnlocalizedName("Penguin Sauce");
		//PenguinHatEmp.setUnlocalizedName("Emperor Penguin Hat");
		//PenguinHatKing.setUnlocalizedName("King Penguin Hat");
		//PenguinHatLB.setUnlocalizedName("Little Blue Penguin Hat");
		//PenguinHatMag.setUnlocalizedName("Magellanic Penguin Hat");
		//PenguinHatGal.setUnlocalizedName("Galápagos Penguin Hat");
		//PenguinHatWF.setUnlocalizedName("White-Flippered Penguin Hat");
		//PenguinHatYE.setUnlocalizedName("Yellow-Eyed Penguin Hat");
		//PenguinHatAdel.setUnlocalizedName("Adélie Penguin Hat");
		//PenguinHatGent.setUnlocalizedName("Gentoo Penguin Hat");
		//PenguinHatCS.setUnlocalizedName("Chinstrap Penguin Hat");
		//PenguinHatAfr.setUnlocalizedName("African Penguin Hat");
		//PenguinHatHum.setUnlocalizedName("Humboldt Penguin Hat");
		//PenguinPants.setUnlocalizedName("Penguin Pants");
		//PenguinTunic.setUnlocalizedName("Penguin Tunic");
		//PenguinFlippers.setUnlocalizedName("Penguin Flippers");
		//PenguinBootsCloud.setUnlocalizedName("Cloud Penguin Boots");
		//PenguinFlameMask.setUnlocalizedName("Flame Penguin Mask");
		//PenguinFlameChestPlate.setUnlocalizedName("Flame Penguin Chestplate");
		//PenguinFlameLeggings.setUnlocalizedName("Flame Penguin Leggings");
		//PenguinFlameFlippers.setUnlocalizedName("Flame Penguin Flippers");

		/* Handle Biome Types for spawning */
		BiomeGenBase[] biomesMountain = BiomeDictionary
				.getBiomesForType(Type.MOUNTAIN);
		BiomeGenBase[] biomesCoastal = BiomeDictionary
				.getBiomesForType(Type.BEACH);
		BiomeGenBase[] biomesWater = BiomeDictionary
				.getBiomesForType(Type.WATER);
		BiomeGenBase[] biomesFrozen = BiomeDictionary
				.getBiomesForType(Type.FROZEN);
		BiomeGenBase[] biomesNether = BiomeDictionary
				.getBiomesForType(Type.NETHER);

		SpawnListEntry spawnlistEmp = new SpawnListEntry(
				EntityPenguinEmp.class, PengEmpSpawnFreq, 3, 5);
		SpawnListEntry spawnlistKing = new SpawnListEntry(
				EntityPenguinKing.class, PengKingSpawnFreq, 3, 5);
		SpawnListEntry spawnlistCS = new SpawnListEntry(EntityPenguinCS.class,
				PengCSSpawnFreq, 3, 5);
		SpawnListEntry spawnlistAdel = new SpawnListEntry(
				EntityPenguinAdel.class, PengAdelSpawnFreq, 3, 5);
		SpawnListEntry spawnlistGent = new SpawnListEntry(
				EntityPenguinGent.class, PengGentSpawnFreq, 3, 5);
		SpawnListEntry spawnlistLB = new SpawnListEntry(EntityPenguinLB.class,
				PengLBSpawnFreq, 3, 5);
		SpawnListEntry spawnlistWF = new SpawnListEntry(EntityPenguinWF.class,
				PengWFSpawnFreq, 3, 5);
		SpawnListEntry spawnlistYE = new SpawnListEntry(EntityPenguinYE.class,
				PengYESpawnFreq, 3, 5);
		SpawnListEntry spawnlistMag = new SpawnListEntry(
				EntityPenguinMag.class, PengMagSpawnFreq, 3, 5);
		SpawnListEntry spawnlistGal = new SpawnListEntry(
				EntityPenguinGal.class, PengGalSpawnFreq, 3, 5);
		SpawnListEntry spawnlistFlam = new SpawnListEntry(
				EntityPenguinFlam.class, PengFlamSpawnFreq, 3, 5);
		SpawnListEntry spawnlistAfr = new SpawnListEntry(
				EntityPenguinAfr.class, PengAfrSpawnFreq, 3, 5);
		SpawnListEntry spawnlistHum = new SpawnListEntry(
				EntityPenguinHum.class, PengHumSpawnFreq, 3, 5);
		SpawnListEntry spawnlistNinj = new SpawnListEntry(
				EntityPenguinNinj.class, PengNinjSpawnFreq, 1, 3);
		SpawnListEntry spawnlistCloud = new SpawnListEntry(EntityPenguinCloud.class, PengCloudSpawnFreq, 3, 5);

		int i = 0;
		for (i = 0; i < biomesCoastal.length; i++) {
			biomesCoastal[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistLB);
			biomesCoastal[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistWF);
			biomesCoastal[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistYE);
			biomesCoastal[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistMag);
			biomesCoastal[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistGal);
			biomesCoastal[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistAfr);
			biomesCoastal[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistHum);
		}

		for (i = 0; i < biomesWater.length; i++) {
			biomesWater[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistLB);
			biomesWater[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistWF);
			biomesWater[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistYE);
			biomesWater[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistMag);
			biomesWater[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistGal);
			biomesWater[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistAfr);
			biomesWater[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistHum);
		}

		for (i = 0; i < biomesNether.length; i++) {
			biomesNether[i].getSpawnableList(EnumCreatureType.monster).add(
					spawnlistFlam);
		}

		for (i = 0; i < biomesMountain.length; i++) {
			biomesMountain[i].getSpawnableList(EnumCreatureType.creature).add(
					spawnlistCloud);
		}

		for (i = 0; i < biomesFrozen.length; i++) {
			if (BiomeDictionary.isBiomeOfType(biomesFrozen[i], Type.FOREST)) {
				biomesFrozen[i].getSpawnableList(EnumCreatureType.creature)
						.add(spawnlistAdel);
				biomesFrozen[i].getSpawnableList(EnumCreatureType.creature)
						.add(spawnlistGent);
			} else {
				biomesFrozen[i].getSpawnableList(EnumCreatureType.creature)
						.add(spawnlistEmp);
				biomesFrozen[i].getSpawnableList(EnumCreatureType.creature)
						.add(spawnlistKing);
				biomesFrozen[i].getSpawnableList(EnumCreatureType.creature)
						.add(spawnlistCS);
				biomesFrozen[i].getSpawnableList(EnumCreatureType.monster).add(
						spawnlistNinj);
			}
		}

		// Registers the entities
		int shurikenID = 3; // ModLoader.getUniqueEntityId();
		/* registerModEntity gives entity an ID specific to this mod */
		EntityRegistry.registerModEntity(EntityPenguinShuriken.class,"PenguinShuriken", shurikenID, this, 48, 3, true);

		// little blue
		int pengLBID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengLBID = 4; // ModLoader.getUniqueEntityId(); //Just local now, so
							// keep it low
		//EntityRegistry.registerModEntity(EntityPenguinLB.class,"LittleBluePenguin", pengLBID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengLBID, EntityPenguinLB.class);
		//EntityList.entityEggs.put(pengLBID, new EntityEggInfo(pengLBID,0x223b4d, 0xf7f7f7));
		EntityRegistry.registerGlobalEntityID(EntityPenguinLB.class, "LittleBluePenguin", pengLBID, 0x223b4d, 0xf7f7f7);

		// white-flippered
		int pengWFID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengWFID = 5; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinWF.class,"White-FlipperedPenguin", pengWFID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengWFID, EntityPenguinWF.class);
		//EntityList.entityEggs.put(pengWFID, new EntityEggInfo(pengWFID,0x999999, 0x663333));
		EntityRegistry.registerGlobalEntityID(EntityPenguinWF.class, "White-FlipperedPenguin", pengWFID, 0x999999, 0x663333);

		// adélie
		int pengAdelID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengAdelID = 6; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinAdel.class,"AdéliePenguin", pengAdelID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengAdelID, EntityPenguinAdel.class);
		//EntityList.entityEggs.put(pengAdelID, new EntityEggInfo(pengAdelID, 0,0xf7f7f7));
		EntityRegistry.registerGlobalEntityID(EntityPenguinAdel.class, "AdéliePenguin", pengAdelID, 0,0xf7f7f7);

		// yellow-eyed
		int pengYEID = EntityRegistry.findGlobalUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinYE.class,
		//		"Yellow-EyedPenguin", pengYEID, this, 80, 3, false);
		EntityRegistry.registerGlobalEntityID(EntityPenguinYE.class, "Yellow-EyedPenguin", pengYEID, 0x663333, 0xe7e7a7);
		//EntityList.IDtoClassMapping.put(pengYEID, EntityPenguinYE.class);
		//EntityList.entityEggs.put(pengYEID, new EntityEggInfo(pengYEID,0x663333, 0xe7e7a7));
		//EntityList.addMapping(EntityPenguinYE.class, "Yellow-EyedPenguin", pengYEID, 0x663333, 0xe7e7a7);

		// magellanic
		int pengMagID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengMagID = 8; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinMag.class,"MagellanicPenguin", pengMagID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengMagID, EntityPenguinMag.class);
		//EntityList.entityEggs.put(pengMagID, new EntityEggInfo(pengMagID,0xf7f7f7, 0));
		EntityRegistry.registerGlobalEntityID(EntityPenguinMag.class, "MagellanicPenguin", pengMagID, 0xf7f7f7, 0);

		// galapagos
		int pengGalID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengGalID = 9; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinGal.class,"GalápagosPenguin", pengGalID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengGalID, EntityPenguinGal.class);
		//EntityList.entityEggs.put(pengGalID, new EntityEggInfo(pengGalID,0xf7f7f7, 0x878787));
		EntityRegistry.registerGlobalEntityID(EntityPenguinGal.class, "GalápagosPenguin", pengGalID, 0xf7f7f7, 0x878787);

		// king
		int pengKingID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengKingID = 10; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinKing.class,"KingPenguin", pengKingID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengKingID, EntityPenguinKing.class);
		//EntityList.entityEggs.put(pengKingID, new EntityEggInfo(pengKingID, 0,0xf79900));
		EntityRegistry.registerGlobalEntityID(EntityPenguinKing.class, "KingPenguin", pengKingID, 0, 0xf79900);

		// emperor
		int pengEmpID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengEmpID = 11; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinEmp.class,"EmperorPenguin", pengEmpID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengEmpID, EntityPenguinEmp.class);
		//EntityList.entityEggs.put(pengEmpID, new EntityEggInfo(pengEmpID, 0,0xf7f7b7));
		EntityRegistry.registerGlobalEntityID(EntityPenguinEmp.class, "EmperorPenguin", pengEmpID, 0, 0xf7f7b7);

		// flame
		int pengFlamID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengFlamID = 12; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinFlam.class,"FlamePenguin", pengFlamID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengFlamID, EntityPenguinFlam.class);
		//EntityList.entityEggs.put(pengFlamID, new EntityEggInfo(pengFlamID,0xa00f10, 0x878787));
		EntityRegistry.registerGlobalEntityID(EntityPenguinFlam.class, "FlamePenguin", pengFlamID, 0xa00f10, 0x878787);

		// ninja
		int pengNinjID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengNinjID = 13; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinNinj.class,"NinjaPenguin", pengNinjID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengNinjID, EntityPenguinNinj.class);
		//EntityList.entityEggs.put(pengNinjID, new EntityEggInfo(pengNinjID, 0,0));
		EntityRegistry.registerGlobalEntityID(EntityPenguinNinj.class, "NinjaPenguin", pengNinjID, 0, 0);

		// gentoo
		int pengGentID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengGentID = 14; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinGent.class,"GentooPenguin", pengGentID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengGentID, EntityPenguinGent.class);
		//EntityList.entityEggs.put(pengGentID, new EntityEggInfo(pengGentID,0x151515, 0xa7a7a7));
		EntityRegistry.registerGlobalEntityID(EntityPenguinGent.class, "GentooPenguin", pengGentID, 0x151515, 0xa7a7a7);

		// chinstrap
		int pengCSID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengCSID = 15; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinCS.class, "CSPenguin",pengCSID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengCSID, EntityPenguinCS.class);
		//EntityList.entityEggs.put(pengCSID, new EntityEggInfo(pengCSID,0x050505, 0x777777));
		EntityRegistry.registerGlobalEntityID(EntityPenguinCS.class, "CSPenguin", pengCSID, 0x050505, 0x777777);

		// cloud
		int pengCloudID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengCloudID = 16; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinCloud.class,"CloudPenguin", pengCloudID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengCloudID, EntityPenguinCloud.class);
		//EntityList.entityEggs.put(pengCloudID, new EntityEggInfo(pengCloudID,0x60c0e0, 0xffffff));
		EntityRegistry.registerGlobalEntityID(EntityPenguinCloud.class, "CloudPenguin", pengCloudID, 0x60c0e0, 0xffffff);

		// african
		int pengAfrID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengAfrID = 17; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinAfr.class,"AfricanPenguin", pengAfrID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengAfrID, EntityPenguinAfr.class);
		//EntityList.entityEggs.put(pengAfrID, new EntityEggInfo(pengAfrID,0x999999, 0x000000));
		EntityRegistry.registerGlobalEntityID(EntityPenguinAfr.class, "AfricanPenguin", pengAfrID, 0x999999, 0x000000);

		// humboldt
		int pengHumID = EntityRegistry.findGlobalUniqueEntityId();
		//int pengHumID = 18; // ModLoader.getUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityPenguinHum.class,"HumboldtPenguin", pengHumID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(pengHumID, EntityPenguinHum.class);
		//EntityList.entityEggs.put(pengHumID, new EntityEggInfo(pengHumID, 0xffffff, 0x663333));
		EntityRegistry.registerGlobalEntityID(EntityPenguinHum.class, "HumboldtPenguin", pengHumID, 0xffffff, 0x663333);

		// Crafting Recipes
		GameRegistry.addRecipe(new ItemStack(PenguinFishingRod), "  X",
				" XY", "XZY", Character.valueOf('X'), PengSkinBlack,
				Character.valueOf('Y'), PengFeatherBlack, Character.valueOf('Z'),
				Items.fishing_rod);
		GameRegistry.addRecipe(new ItemStack(PenguinFlameRepeater), "VWV",
				"XYZ", "VWV", Character.valueOf('V'), PengScaleRed,
				Character.valueOf('W'), PengSkinRed, Character.valueOf('X'),
				Items.iron_ingot, Character.valueOf('Y'), Items.repeater,
				Character.valueOf('Z'), PenguinBow);
		GameRegistry.addRecipe(new ItemStack(PenguinBow), " XY", "XVY", " XY",
				Character.valueOf('X'), PengSkinBlack, Character.valueOf('Y'),
				PengFeatherBlack, Character.valueOf('V'), Items.bow);
		GameRegistry.addRecipe(new ItemStack(PenguinHatEmp), "XYX", "Z Z",
				Character.valueOf('X'), PengFeatherBlack,
				Character.valueOf('Y'), PengSkinBlack, Character.valueOf('Z'),
				PengFeatherWhite);
		GameRegistry.addRecipe(new ItemStack(PenguinHatKing), " W ", "XYX",
				"Z Z", Character.valueOf('W'), PenguinCrown,
				Character.valueOf('X'), PengFeatherBlack,
				Character.valueOf('Y'), PengSkinBlack, Character.valueOf('Z'),
				PengFeatherWhite);
		GameRegistry.addShapelessRecipe(new ItemStack(PenguinHatKing),
				new ItemStack(PenguinCrown), new ItemStack(PenguinHatEmp));
		GameRegistry.addRecipe(new ItemStack(PenguinHatLB), "XYX", "X X",
				Character.valueOf('X'), PengFeatherBlue,
				Character.valueOf('Y'), PengSkinBlue);
		GameRegistry.addRecipe(new ItemStack(PenguinHatYE), "XYX", "Z Z",
				Character.valueOf('X'), PengFeatherBrown,
				Character.valueOf('Y'), PengSkinBrown, Character.valueOf('Z'),
				PengFeatherYellow);
		GameRegistry.addRecipe(new ItemStack(PenguinHatMag), "XYX", "X X",
				Character.valueOf('X'), PengFeatherStriped,
				Character.valueOf('Y'), PengSkinBlack);
		GameRegistry.addRecipe(new ItemStack(PenguinHatGal), "XYX", "Z Z",
				Character.valueOf('X'), PengFeatherBlack,
				Character.valueOf('Y'), PengSkinBlack, Character.valueOf('Z'),
				PengFeatherBrownStriped);
		GameRegistry.addRecipe(new ItemStack(PenguinHatWF), "XYX", "Z Z",
				Character.valueOf('X'), PengFeatherBrown,
				Character.valueOf('Y'), PengSkinBrown, Character.valueOf('Z'),
				PengFeatherWhite);
		GameRegistry.addRecipe(new ItemStack(PenguinHatAdel), "XYX", "X X",
				Character.valueOf('X'), PengFeatherBlack,
				Character.valueOf('Y'), PengSkinBlack);
		GameRegistry.addRecipe(new ItemStack(PenguinHatGent), "XYX", "Z Z",
				Character.valueOf('X'), PengFeatherWhite,
				Character.valueOf('Y'), PengSkinBlack, Character.valueOf('Z'),
				PengFeatherBlack);
		GameRegistry.addRecipe(new ItemStack(PenguinHatAfr), "XYZ", "Z X",
				Character.valueOf('X'), PengFeatherStriped,
				Character.valueOf('Y'), PengSkinBlack, Character.valueOf('Z'),
				PengFeatherWhite);
		GameRegistry.addRecipe(new ItemStack(PenguinHatHum), "XYZ", "Z X",
				Character.valueOf('X'), PengFeatherWhite,
				Character.valueOf('Y'), PengSkinBrown, Character.valueOf('Z'),
				PengFeatherBrownStriped);
		GameRegistry.addRecipe(new ItemStack(PenguinHatCS), "XYX", "Z Z",
				Character.valueOf('X'), PengFeatherStriped,
				Character.valueOf('Y'), PengSkinBlack, Character.valueOf('Z'),
				PengFeatherBlack);

		GameRegistry.addRecipe(new ItemStack(PenguinFlameMask), "XYX", "XZX",
				Character.valueOf('X'), PengScaleRed, Character.valueOf('Y'),
				PengSkinRed, Character.valueOf('Z'), Blocks.glass_pane);

		GameRegistry.addRecipe(new ItemStack(PenguinTunic), "X X", "XYX",
				"XYX", Character.valueOf('X'), PengFeatherBlack,
				Character.valueOf('Y'), Blocks.wool);
		GameRegistry.addRecipe(new ItemStack(PenguinFlameChestPlate), "X X",
				"XYX", "XYX", Character.valueOf('X'), PengScaleRed,
				Character.valueOf('Y'), Blocks.wool);
		GameRegistry.addRecipe(new ItemStack(PenguinPants), "ZYZ", "X X",
				"W W", Character.valueOf('W'), PengFeatherBlack,
				Character.valueOf('X'), PengFeatherBlue,
				Character.valueOf('Y'), PengSkinBlue, Character.valueOf('Z'),
				PengSkinBlack);
		GameRegistry.addRecipe(new ItemStack(PenguinFlameLeggings), "XXX",
				"Y Y", "Y Y", Character.valueOf('X'), PengSkinRed,
				Character.valueOf('Y'), PengScaleRed);
		GameRegistry.addRecipe(new ItemStack(PenguinFlippers), "X X", "Y Y",
				"Y Y", Character.valueOf('X'), PengSkinBlack,
				Character.valueOf('Y'), PengFeatherBlack);
		GameRegistry.addRecipe(new ItemStack(PenguinBootsCloud), "X X", "Y Y",
				"Y Y", Character.valueOf('X'), PengSkinLightBlue,
				Character.valueOf('Y'), PengDownCloud);
		GameRegistry.addRecipe(new ItemStack(PenguinFlameFlippers), "X X",
				"Y Y", "Y Y", Character.valueOf('X'), PengSkinRed,
				Character.valueOf('Y'), PengScaleRed);
		
		GameRegistry.addRecipe(new ItemStack(Items.arrow, 4), "X",
				"Y", "Z", Character.valueOf('X'), Items.flint,
				Character.valueOf('Y'), Items.stick, Character.valueOf('Z'), PengFeatherWhite);
		GameRegistry.addRecipe(new ItemStack(Items.arrow, 4), "X",
				"Y", "Z", Character.valueOf('X'), Items.flint,
				Character.valueOf('Y'), Items.stick, Character.valueOf('Z'), PengFeatherBlue);
		GameRegistry.addRecipe(new ItemStack(Items.arrow, 4), "X",
				"Y", "Z", Character.valueOf('X'), Items.flint,
				Character.valueOf('Y'), Items.stick, Character.valueOf('Z'), PengFeatherStriped);
		GameRegistry.addRecipe(new ItemStack(Items.arrow, 4), "X",
				"Y", "Z", Character.valueOf('X'), Items.flint,
				Character.valueOf('Y'), Items.stick, Character.valueOf('Z'), PengFeatherBrownStriped);
		GameRegistry.addRecipe(new ItemStack(Items.arrow, 4), "X",
				"Y", "Z", Character.valueOf('X'), Items.flint,
				Character.valueOf('Y'), Items.stick, Character.valueOf('Z'), PengFeatherBlack);
		GameRegistry.addRecipe(new ItemStack(Items.arrow, 4), "X",
				"Y", "Z", Character.valueOf('X'), Items.flint,
				Character.valueOf('Y'), Items.stick, Character.valueOf('Z'), PengFeatherBrown);
		GameRegistry.addRecipe(new ItemStack(Items.arrow, 4), "X",
				"Y", "Z", Character.valueOf('X'), Items.flint,
				Character.valueOf('Y'), Items.stick, Character.valueOf('Z'), PengFeatherYellow);
		
		GameRegistry.addRecipe(new ItemStack(Items.item_frame, 1), "XXX",
				"XYX", "XXX", Character.valueOf('X'), Items.stick,
				Character.valueOf('Y'), PengSkinBlack);
		GameRegistry.addRecipe(new ItemStack(Items.item_frame, 1), "XXX",
				"XYX", "XXX", Character.valueOf('X'), Items.stick,
				Character.valueOf('Y'), PengSkinBlue);
		GameRegistry.addRecipe(new ItemStack(Items.item_frame, 1), "XXX",
				"XYX", "XXX", Character.valueOf('X'), Items.stick,
				Character.valueOf('Y'), PengSkinLightBlue);
		GameRegistry.addRecipe(new ItemStack(Items.item_frame, 1), "XXX",
				"XYX", "XXX", Character.valueOf('X'), Items.stick,
				Character.valueOf('Y'), PengSkinRed);
		GameRegistry.addRecipe(new ItemStack(Items.item_frame, 1), "XXX",
				"XYX", "XXX", Character.valueOf('X'), Items.stick,
				Character.valueOf('Y'), PengSkinBrown);
		
		GameRegistry.addRecipe(new ItemStack(PenguinShears, 1), " X ", "YZX", " Y ",
				Character.valueOf('X'), PengFeatherBlack, Character.valueOf('Y'), PengSkinBlack,
				Character.valueOf('Z'), Items.shears);

		GameRegistry.addShapelessRecipe(new ItemStack(FishMagma, 4),
				new ItemStack(Items.fish, 1, 0), new ItemStack(Items.fish,
						1, 0), new ItemStack(Items.fish, 1, 0),
				new ItemStack(Items.fish, 1, 0), new ItemStack(
						Items.magma_cream, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(FishMagma, 1),
				new ItemStack(Items.fish, 1, 0), new ItemStack(
						Items.magma_cream, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(PenguinSauce, 1),
				new ItemStack(Items.fire_charge, 1, 0), new ItemStack(
						Items.bowl, 1, 0), new ItemStack(Items.sugar, 1, 0), new ItemStack(
						Blocks.brown_mushroom_block, 1, 0), new ItemStack(PengScaleRed, 1, 0));

		GameRegistry.addShapelessRecipe(new ItemStack(Items.book, 1),
				new ItemStack(PengSkinBlack, 1, 0), new ItemStack(Items.paper,
						1, 0), new ItemStack(Items.paper, 1, 0), new ItemStack(
								Items.paper, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.book, 1),
				new ItemStack(PengSkinBlue, 1, 0), new ItemStack(Items.paper, 1,
						0), new ItemStack(Items.paper, 1, 0), new ItemStack(
								Items.paper, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.book, 1),
				new ItemStack(PengSkinLightBlue, 1, 0), new ItemStack(Items.paper, 1,
						0), new ItemStack(Items.paper, 1, 0), new ItemStack(
								Items.paper, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.book, 1),
				new ItemStack(PengSkinBrown, 1, 0), new ItemStack(Items.paper,
						1, 0), new ItemStack(Items.paper, 1, 0), new ItemStack(
								Items.paper, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.book, 1),
				new ItemStack(PengSkinRed, 1, 0), new ItemStack(Items.paper, 1,
						0), new ItemStack(Items.paper, 1, 0), new ItemStack(
								Items.paper, 1, 0));
		

		//From 1.6: TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
	}

	//@PostInit
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// This runs after all mods are loaded
		// Useful for having penguins spawn in custom biomes? YES.
		BiomeDictionary.registerAllBiomes();
		//System.out.printf("postInit is done.\n");
	}
}
