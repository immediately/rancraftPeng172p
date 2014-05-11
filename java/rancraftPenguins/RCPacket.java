package rancraftPenguins;

//The Rancraft Penguins "everything" packet, modified from atomicstryker's ChestAttackedPacket.java
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import rancraftPenguins.NetworkHelper.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class RCPacket implements IPacket
{
    private EntityPlayer thePlayer;
    private String playerName;
    private String eventType;
    private Minecraft mc;
    private MinecraftServer ms;
    private int iPayload;
    private float fPayload;
    private String sPayload;
   
    // if there is a constructor with >0 args, we MUST supply another with no args
    public RCPacket()
    {
        thePlayer = null;
        playerName = "";
        eventType = "";
        mc = null;
        ms = null;
        iPayload = 0;
        fPayload = 0.0F;
    }
   
    // This is the only server-to-client packet
    public RCPacket(String event, EntityPlayer player, int intP)
    {
    	this.thePlayer = player;
        playerName = player.getDisplayName();
        eventType = event;
        iPayload = intP;
		//System.out.printf("RCPacket: Constructed a RCPacket object for playerName: %s\n", player);
    }
   
    @SideOnly(Side.CLIENT) // restrict side to client for every client-to-server packet c'tor to work with dedicated server
    public RCPacket(String event, String player)
    {
        playerName = player;
        eventType = event;
        iPayload = 0;
        fPayload = 0.0F;
    }

    @SideOnly(Side.CLIENT) // restrict side to client for every client-to-server packet c'tor to work with dedicated server
    public RCPacket(String event, String player, int intP)
    {
        playerName = player;
        eventType = event;
        iPayload = intP;
        fPayload = 0.0F;
    }
   
    @SideOnly(Side.CLIENT) // restrict side to client for every client-to-server packet c'tor to work with dedicated server
    public RCPacket(String event, String player, float floatP)
    {
        playerName = player;
        eventType = event;
        iPayload = 0;
        fPayload = floatP;
    }
   
    @Override
    public void writeBytes(ChannelHandlerContext ctx, ByteBuf bytes)
    {
		String s = new String(this.playerName);
		String e = new String(this.eventType);

		//System.out.printf("RCPacket: First write the length of the username, which is %d\n", s.length());
        bytes.writeShort(s.length());
        for (char c : s.toCharArray()){
    		//System.out.printf("RCPacket: Writing %c\n", c);
        	bytes.writeChar(c);
        }

        //System.out.printf("RCPacket: Now write the length of the eventname, which is %d\n", e.length());
        bytes.writeShort(e.length());
        for (char c : e.toCharArray()){
    		//System.out.printf("RCPacket: Writing %c\n", c);
        	bytes.writeChar(c);
        }

        /* That's all we need for RC_Ext or RC_FRSR because it's enough to know the event type */

        /* for RC_Air we need to encode an int -- either MAXAIR or current air level */
        if(this.eventType.compareTo("RC_AL") == 0){
            bytes.writeInt(iPayload);
    	}
        /* for RC_FD we need to encode a float */
        if(this.eventType.compareTo("RC_FD") == 0){
            bytes.writeFloat(fPayload);
    	}
        /* for RC_PFRI we need to encode an int: 1 for cast state and 0 for not cast */
        if(this.eventType.compareTo("RC_PFRI") == 0){
    		//System.out.printf("RCPacket: About to send fishing rod icon = %d to playerName: %s\n", iPayload, playerName);
            bytes.writeInt(iPayload);
    	}
    }

    @Override
    public void readBytes(ChannelHandlerContext ctx, ByteBuf bytes)
    {
        short len = bytes.readShort();
        char[] pChars1 = new char[len];
        for (int i = 0; i < len; i++)
        	pChars1[i] = bytes.readChar();
        playerName = String.valueOf(pChars1);
		//System.out.printf("RCPacket: Just read this string for playerName: %s\n", playerName);
       
		/* if this is a server, get the player whose name is on the packet Remove entirely from RCPacket.java? */
        EntityPlayer playerMP = null;
        if(MinecraftServer.getServer() != null){
        	playerMP = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
        }

		len = bytes.readShort();
        char[] pChars2 = new char[len];
        for (int i = 0; i < len; i++)
        	pChars2[i] = bytes.readChar();
        eventType = String.valueOf(pChars2);
		//System.out.printf("RCPacket: Just read this string for eventType: %s\n", eventType);

        if(MinecraftServer.getServer() != null){
        	playerMP = (EntityPlayerMP)playerMP;
        }

        int extinguish = 451;
        int maxAir = 300; /* in case this changes in later versions of MC */
        int incomingI = 0;
        float incomingF = 0.0F;

        //These first four are all server functions receiving packets from clients
        //System.out.printf("Server, PacketHandler: Got SOMETHING from some channel...\n");
        if (eventType.compareTo("RC_FRSR") == 0 && playerMP.getCurrentEquippedItem() != null) {
        	try{
            	//System.out.printf("Server: Got FishingRodStateRequest event. Sending response to %s.\n", playerMP.getDisplayName());
                //System.out.println("Server: got request RC_FRSR. Now send packet with fishing rod state to the client...");
            	sendFishingRodState(playerMP, playerMP.getCurrentEquippedItem().getItem());
        	} catch(Exception ex) {
    	        ex.printStackTrace();
                System.out.println("RCPacket: Error reading packet");
        		return;
        	}
        }
        if (eventType.compareTo("RC_Ext") == 0) {
        	try{
            	//System.out.printf("Server: Got an RC_Ext event type. Trying to extinguish player.\n");
        		playerMP.extinguish();
        	} catch(Exception ex) {
    	        ex.printStackTrace();
                System.out.println("RCPacket: Error reading packet");
        		return;
        	}
        }
        if (eventType.compareTo("RC_AL") == 0) {
        	try{
        		incomingI = bytes.readInt();
	        	//System.out.printf("RCPacket: Got %d from an RC_AL event\n", incomingI);
	        	//System.out.printf("RCPacket: playerMP is %s\n", playerMP.getDisplayName());
	        	//System.out.printf("RCPacket: RC_AL: Are we on server side now? %b\n", MinecraftServer.getServer() != null);
	        	if(incomingI > -1 && incomingI < maxAir + 1){
	        		playerMP.setAir(incomingI);
	        	}
        	} catch(Exception ex) {
    	        ex.printStackTrace();
                System.out.println("RCPacket: Error reading packet");
        		return;
        	}
        }
        if (eventType.compareTo("RC_FD") == 0) {
        	try{
        		incomingF = bytes.readFloat();
	        	//System.out.printf("Server: Got %f from RC_FD event\n", incomingF);
        		playerMP.fallDistance = incomingF;
        	} catch(Exception ex) {
    	        ex.printStackTrace();
                System.out.println("RCPacket: Error reading packet");
        		return;
        	}
        }
		// Penguin Fishing Rod Icon is the only packet that goes from server to client
        // This one is the only client function. Sets the fishing rod icon according to the info received from the server
		if(eventType.compareTo("RC_PFRI") == 0 && FMLCommonHandler.instance().getEffectiveSide().isClient()){ // Maybe don't need to check isClient.
        	// It's extremely wasteful to getMinecraft every 5 ticks while we're holding a penguin fishing rod, but I'm not sure how to avoid it.
			ItemStack currItemStack = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();
			if(currItemStack != null && currItemStack.getItem() == RanCraftPenguins.PenguinFishingRod){
		        incomingI = 0;
		    	//System.out.printf("RCPacket: Got a penguin fishing rod icon...\n");
	        	try{
	        		incomingI = bytes.readInt();
		        	//System.out.printf("RCPacket: Got %d from an RC_PFRI event so set fishing rod icon to that.\n", incomingI);
		        	//System.out.printf("RCPacket: Got playerMP is %b.\n", playerMP != null); // this one is false if haven't run in SP mode once
		        	//System.out.printf("RCPacket: Got playerSP is %b.\n", this.thePlayer != null); // this one is the one we want, though
		        	//System.out.printf("RCPacket: playername is %s.\n", playerName);
		        	//System.out.printf("RCPacket: RC_PFRI: Are we on server side now? %b\n", MinecraftServer.getServer() != null);
	            	((ItemPenguinFishingRod)currItemStack.getItem()).setIconTo(incomingI);
	        	} catch(Exception ex) {
	    	        ex.printStackTrace();
	                System.out.println("RCPacket RC_PFRI: Error reading packet or setting the item icon");
	        		return;
	        	}
			}
        }
    }
    
    //This is called when we recognize a RC_FRSR from a client
    public void sendFishingRodState(EntityPlayer player, Item currentItem){

    	if(currentItem != null && currentItem.equals(RanCraftPenguins.PenguinFishingRod)){
    		rancraftPenguins.ItemPenguinFishingRod pfr = (rancraftPenguins.ItemPenguinFishingRod)currentItem;
        	//System.out.printf("RCPacket, PacketHandler: About to reply to client with fishing rod state = %d\n", pfr.rodIcon);

	    	try {
	    		// Create Penguin Fishing Rod Icon packet
	        	//System.out.printf("RCPacket: Sending Penguin FishingRodIcon=%d message back to the client using RCPacket\n", pfr.rodIcon);
	    		RCPacket packet = new RCPacket("RC_PFRI", player, pfr.rodIcon);
	    		rancraftPenguins.RanCraftPenguins.instance.nh.sendPacketToPlayer(packet, player);
	    	} catch (Exception ex) {
    	        ex.printStackTrace();
                System.out.println("Client: Error creating packet");
	    	}
    	}
    }
}
