package rancraftPenguins.client;

import java.util.EnumMap;

import rancraftPenguins.RCPacket;
//import rancraftPenguins.PacketAL;
import rancraftPenguins.RanCraftPenguins;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.util.AttributeKey;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;

/*
One other thing to mention here is that since we are on the client side, we might be using Minecraft.getMinecraft() 
quite a lot to access things like the world, player, etc. Since it would be very wasteful to have to do this each 
and every tick, a better way is to store the instance of Minecraft when you construct your tick handler:
*/
@SideOnly(Side.CLIENT)
public class ClientTickHandler {
    static int tickCount = 0; // static to preserve their values between calls
    static int tick2Count = 0; // this one only ticks once each time the data are checked
    static int prevAir = 0;

	/** Stores an instance of Minecraft grabbed once for easy access in packets */
	private Minecraft mcc;
	private MinecraftServer mcs;
	
	// create a constructor that takes a Minecraft argument; now we have it whenever we need it
/*	public ClientTickHandler() {
		this.mcc = Minecraft.getMinecraft();
		this.mcs = MinecraftServer.getServer();
	}*/
	
	// create a constructor that takes a Minecraft argument; now we have it whenever we need it
	public ClientTickHandler(Minecraft mcClient, MinecraftServer mcServer) {
		this.mcc = mcClient;
		this.mcs = mcServer;
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
    	Minecraft theMinecraft = Minecraft.getMinecraft();
        int maxAir = 300; /* in case this changes in later versions of MC */
        int depleteFreq, curAir;
        float fallDist;
        int tickFreq = 5; /* increase this to avoid using too many cycles */
        EntityPlayerSP entityplayersp;
        InventoryPlayer inventoryplayer;

		if(mcc.inGameHasFocus){
	        entityplayersp = theMinecraft.thePlayer;
	        inventoryplayer = entityplayersp.inventory;
	
	        /* for various infrequent things */
	        tickCount++;
	        if(tickCount > 199){
	        	tickCount = 0; /* to avoid letting tickCount get too high */
	        }
	    	if(tickCount%tickFreq == 0){ // to avoid using too many cycles
	    		tick2Count++; // so this increments every tickFreq ticks
	            if(tick2Count > 199){
	            	tick2Count = 0; /* to avoid letting tick2Count get too high */
	            }
	
            	//System.out.printf("Client: Checking if we're holding a penguin fishing rod: %s.\n", inventoryplayer.getCurrentItem().getDisplayName());
		        if(inventoryplayer.getCurrentItem() != null && inventoryplayer.getCurrentItem().getItem().equals(RanCraftPenguins.PenguinFishingRod)){
	            	//System.out.printf("Client: holding fishing rod. Requesting state from server.\n");
	            	sendFishingRodStateRequest(entityplayersp);
		        }
	
		        if(inventoryplayer.armorItemInSlot(0) != null && inventoryplayer.armorItemInSlot(0).getItem().equals(RanCraftPenguins.PenguinBootsCloud)){
		        	if(!entityplayersp.isCollidedVertically){ // not touching ground
		        		//if(!entityplayersp.isCollidedVertically){
		        		fallDist = entityplayersp.fallDistance / 3.0F;
			        	sendFallDistanceInfo(entityplayersp, fallDist); /* sending info to server */
		        	}
		        }
	
		        if(inventoryplayer.armorItemInSlot(0) != null && inventoryplayer.armorItemInSlot(0).getItem().equals(RanCraftPenguins.PenguinFlippers))
		        {
		            if(entityplayersp.isInWater()){ // faster in water
		            	//System.out.printf("Current velocity total=%f\n", Math.abs(entityplayersp.motionX) + Math.abs(entityplayersp.motionZ));
		            	if(Math.abs(entityplayersp.motionX) + Math.abs(entityplayersp.motionY) + Math.abs(entityplayersp.motionZ) < 1.5F){
				            entityplayersp.motionX *= 1.3F;
				            entityplayersp.motionY *= 1.1F;
				            entityplayersp.motionZ *= 1.3F;
		            	}
		            } else if(entityplayersp.onGround){ // much slower or slightly slower on land
			            if(inventoryplayer.armorItemInSlot(1) != null && inventoryplayer.armorItemInSlot(1).getItem().equals(RanCraftPenguins.PenguinPants)){
			            	// slightly slower if also wearing PenguinPants
				            entityplayersp.motionX *= 0.8F;
				            entityplayersp.motionZ *= 0.8F;
			            } else if(inventoryplayer.armorItemInSlot(1) == null || !(inventoryplayer.armorItemInSlot(1).getItem().equals(RanCraftPenguins.PenguinFlameLeggings))) {
			            	// much slower and can't jump if wearing no pants or something other than PenguinPants or PenguinFlameLeggings
				            entityplayersp.motionX *= 0.5F;
				            entityplayersp.motionZ *= 0.5F;
			            }
		            } else if(entityplayersp.movementInput.jump){ // can't jump with flippers unless either Penguin pants or leggings
		            	if(inventoryplayer.armorItemInSlot(1) == null || (!inventoryplayer.armorItemInSlot(1).getItem().equals(RanCraftPenguins.PenguinPants) && !inventoryplayer.armorItemInSlot(1).getItem().equals(RanCraftPenguins.PenguinFlameLeggings))) {
			            	entityplayersp.motionY *= 0.4D;
		            	} // else is wearing penguin pants or leggings, so can jump normally
		            }
		        }
		
		        /* Flame flippers increases swimming speed to double the speed of normal penguin flippers */
		        /* Also, less bouncing in the water, but harder to steer. Good for ocean crossings. */
		        /* FlamePenguin leggings eliminate the slowing effects of both types of flippers */
		        if(inventoryplayer.armorItemInSlot(0) != null && inventoryplayer.armorItemInSlot(0).getItem().equals(RanCraftPenguins.PenguinFlameFlippers))
		        {
		            if(entityplayersp.isInWater()){ // faster in water
			        	if(Math.abs(entityplayersp.motionX) + Math.abs(entityplayersp.motionZ) < 1.5F){
				            entityplayersp.motionX *= 1.7F;
				            entityplayersp.motionZ *= 1.7F;
			        	}
		            } else if(entityplayersp.onGround){ // slower on land unless PenguinFlameLeggings
			            if(inventoryplayer.armorItemInSlot(1) == null || !inventoryplayer.armorItemInSlot(1).getItem().equals(RanCraftPenguins.PenguinFlameLeggings)) {
				            entityplayersp.motionX *= 0.5F;
				            entityplayersp.motionZ *= 0.5F;
			            }
		            } else if(entityplayersp.movementInput.jump){ // can't jump unless PenguinFlameLeggings
			            if(inventoryplayer.armorItemInSlot(1) == null || !inventoryplayer.armorItemInSlot(1).getItem().equals(RanCraftPenguins.PenguinFlameLeggings)) {
			            	entityplayersp.motionY *= 0.5D;
			            }
		            }
		        }
		
		        /* Flame Penguin chest plate extinguishes the player once they're not in lava */
		        if(inventoryplayer.armorItemInSlot(2) != null && inventoryplayer.armorItemInSlot(2).getItem().equals(RanCraftPenguins.PenguinFlameChestPlate)){
		        	if(entityplayersp.isBurning()){
		        		entityplayersp.extinguish();
			        	//System.out.printf("Client: Player isBurning and wearing chestplate, so sending extinguish.\n");
		        		sendExtinguishMessage(entityplayersp);
		        	}
		        }
		        
		        /* Any penguin hat (quadruples air supply) */
		    	if(entityplayersp.isInsideOfMaterial(Material.water)){ // we're underwater
			        if(inventoryplayer.armorItemInSlot(3) != null && 
			        		((inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatEmp))
			        		  || (inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatKing))
			        		  || (inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatYE))
			        		  || (inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatMag))
			        		  || (inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatGal))
			        		  || (inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatWF))
			        		  || (inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatAdel))
			        		  || (inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatGent))
			        		  || (inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatHum))
			        		  || (inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatAfr))
			        		  || (inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatCS))
			        		  || (inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatLB)))) {
			        	// Usually quadruples time underwater, but sometimes much more -- air use stops for awhile
			        	curAir = entityplayersp.getAir();
	    	        	//Side side = FMLCommonHandler.instance().getEffectiveSide();
			        	//depleteFreq = theMinecraft.theWorld.isRemote ? 23 : 4; // b/c air depletes faster in smp
	    	        	//System.out.printf("isRemote = %b\n", theMinecraft.theWorld.isRemote); // always true
	    	        	//System.out.printf("isClientSide = %b\n", (side == Side.CLIENT)); // always true
			        	depleteFreq = 5; // 4
			        	if(tick2Count%depleteFreq == 0 || curAir < 1){ // check air and allow it to drop one in n ticks
		    	        	//System.out.printf("Client: Should I allow air to drop? curAir = %d, prevAir = %d\n", curAir, prevAir);
			        		if(prevAir > curAir){
			        			curAir--; // decrement air (already being done at a higher level?)
			        			prevAir = curAir;
			    	        	//System.out.printf("Client: ALLOWING air to drop: Airlevel = %d\n", curAir);
			        		}
			        	}
		        		entityplayersp.setAir(prevAir); // set air for this user to prevAir, which might be lower now
		        		sendAirLevelInfo(entityplayersp, prevAir); /* sending info to server */
			        	//System.out.printf("client: JUST SET air to prevAir: %d: Deplete freq = %d\n", prevAir, depleteFreq);
			        /* Flame penguin mask (no air loss) */
			        } else if(inventoryplayer.armorItemInSlot(3) != null && inventoryplayer.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinFlameMask)) {
				    	curAir = entityplayersp.getAir();
				    	if(curAir < maxAir){ // fix it every time it goes below maximum
				    		//System.out.printf("From mod_RanCraft: Value for air before setAir: %d\n", entityplayersp.getAir());
			        		entityplayersp.setAir(maxAir);
				        	//System.out.printf("From mod_RanCraft: New value for air: %d\n", entityplayersp.getAir());
			        		sendAirLevelInfo(entityplayersp, maxAir); /* sending info to server */
		    	    	}
		        	} else {  // not wearing penguin mask or hat, so make sure prevAir is ready for hat-on calculations
		        		prevAir = maxAir;
			        	//System.out.printf("Client: Max air for prevAir for when hat goes back on: prevAir = %d\n", prevAir);
		        	}
		        } else { // not underwater
		    		prevAir = maxAir;
		        	curAir = maxAir;
		        	//System.out.printf("Client: Max air b/c not underwater! Airlevel = %d\n", prevAir);
		        }
	    	} // tickCount
        }
	    return;
	}

	public void sendExtinguishMessage(EntityPlayerSP entityPlayerSp)
    {
    	try {
    		// Create Extinguish packet
    		//RCPacket packet = new RCPacket("RC_Ext", mcc.thePlayer.getDisplayName(), mcs);
    		RCPacket packet = new RCPacket("RC_Ext", mcc.thePlayer.getDisplayName());
    		// Send Extinguish packet
        	//System.out.printf("Client: Sending extinguish message...\n");
    		rancraftPenguins.RanCraftPenguins.instance.nh.sendPacketToServer(packet);
    	} catch (Exception ex) {
	        ex.printStackTrace();
            System.out.println("Client: Error creating packet");
    	}
    }

	public void sendAirLevelInfo(EntityPlayerSP entityPlayerSp, int airLevel)
    {
    	try {
    		// Create packet with Air Level info
    		//RCPacket packet = new RCPacket("RC_AL", mcc.thePlayer.getDisplayName(), mcs, airLevel);
    		RCPacket packet = new RCPacket("RC_AL", mcc.thePlayer.getDisplayName(), airLevel);
    		//PacketAL packet = new PacketAL("RC_AL", mcc.thePlayer.getDisplayName(), mcc, mcs, airLevel);
    		// Send Air Level packet
        	//System.out.printf("Client: Sending airlevel=%d message\n", airLevel);
    		rancraftPenguins.RanCraftPenguins.instance.nh.sendPacketToServer(packet);
    	} catch (Exception ex) {
	        ex.printStackTrace();
            System.out.println("Client: Error creating packet");
    	}
    }

	public void sendFallDistanceInfo(EntityPlayerSP entityPlayerSp, float fallDistance)
    {
    	try {
    		// Create packet with Fall Distance info
    		//RCPacket packet = new RCPacket("RC_FD", mcc.thePlayer.getDisplayName(), mcs, fallDistance);
    		RCPacket packet = new RCPacket("RC_FD", mcc.thePlayer.getDisplayName(), fallDistance);
    		// Send Fall Distance packet
        	//System.out.printf("Client: Sending falldistance=%f message\n", fallDistance);
    		rancraftPenguins.RanCraftPenguins.instance.nh.sendPacketToServer(packet);
    	} catch (Exception ex) {
	        ex.printStackTrace();
            System.out.println("Client: Error creating packet");
    	}
    }

	public void sendFishingRodStateRequest(EntityPlayerSP entityPlayerSp)
    {
    	try {
    		// Create packet with Fishing Rod State Request
    		//RCPacket packet = new RCPacket("RC_FRSR", mcc.thePlayer.getDisplayName(), mcs);
    		RCPacket packet = new RCPacket("RC_FRSR", mcc.thePlayer.getDisplayName());
    		// Send Fishing Rod State Request packet
        	//System.out.printf("Client: Sending FishingRodStateRequest.\n");
    		rancraftPenguins.RanCraftPenguins.instance.nh.sendPacketToServer(packet);
    	} catch (Exception ex) {
	        ex.printStackTrace();
            System.out.println("Client: Error creating packet");
    	}
    }

/*  Later try using this event instead of ticking?
 * 5. LivingUpdateEvent
Variables: EntityLivingBase entity
Called every tick at the beginning of the entity's onUpdate method.
Uses: This is probably the most useful Event. You can allow player's to fly if holding an item or wearing your armor
set, you can modify a player's fall speed here, add potion effects or anything else you can imagine. It's really
really handy.
 */
}

