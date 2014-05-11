package rancraftPenguins;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;
import rancraftPenguins.EntityPenguinCloud;

public class EntityAIFlyAway extends EntityAIBase
{
    private EntityCreature entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private float speed;
	int timeAirborne;
    Random rand = new Random();

    public EntityAIFlyAway(EntityCreature par1EntityCreature, float par2)
    {
        this.entity = par1EntityCreature;
        this.speed = par2;
        this.setMutexBits(1);
        //par1EntityCreature.posY += 0.4F;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    //@Override
    //@SideOnly(Side.CLIENT)
    public boolean shouldExecute()
    {
    	boolean retVal = false;
    	float scareDistance = 10.0F; // how close a player can get before entity flies away
        EntityPlayer entityplayer = this.entity.worldObj.getClosestPlayerToEntity(this.entity, -1.0D);
    	EntityPenguinCloud thisCloudPeng = (EntityPenguinCloud)this.entity;

        if (entityplayer != null){
        	/* flee if the player isn't wearing flame penguin armor */
	        if(!isDisguised(entityplayer) || thisCloudPeng.isAngry()){ // anger could be from shearing or hitting
	        	thisCloudPeng.setAngry(false);
	            //System.out.printf("  Nearest player is %f blocks away\n", this.entity.getDistanceToEntity(entityplayer));
	        	if(this.entity.getDistanceToEntity(entityplayer) < scareDistance){ // there's a nearby player (later: with no penguin armor)
	                Vec3 playerLoc = entityplayer.worldObj.getWorldVec3Pool().getVecFromPool(entityplayer.posX, entityplayer.posY, entityplayer.posZ);
		        	//Vec3 playerLoc = entityplayer.getPlayerCoordinates();
		        	//Vec3 playerLoc = entityplayer.getPosition(1.0F);
	        		//System.out.printf("  Nearest player is at (%f,%f,%f)\n", playerLoc.xCoord, playerLoc.yCoord, playerLoc.zCoord);
	                //System.out.printf("  Nearest player is %f blocks away\n", this.entity.getDistanceToEntity(entityplayer));
		            Vec3 proposedDest = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 64, 1, playerLoc);
	        		//System.out.printf("  Proposed destination vector: (%f,%f,%f)\n", proposedDest.xCoord, proposedDest.yCoord, proposedDest.zCoord);
	            	//thisCloudPeng.currentFlightTarget = new ChunkCoordinates((int)thisCloudPeng.posX + rand.nextInt(7) - rand.nextInt(7), (int)thisCloudPeng.posY + rand.nextInt(6) - 2, (int)thisCloudPeng.posZ + rand.nextInt(7) - rand.nextInt(7));
		            if(thisCloudPeng != null && proposedDest != null){
		            	//thisCloudPeng.currentFlightTarget = new ChunkCoordinates((int)thisCloudPeng.posX + (int)proposedDest.xCoord, (int)thisCloudPeng.posY + (int)proposedDest.yCoord, (int)thisCloudPeng.posZ + (int)proposedDest.zCoord);
		            	thisCloudPeng.currentFlightTarget = new ChunkCoordinates((int)proposedDest.xCoord, (int)proposedDest.yCoord + 50, (int)proposedDest.zCoord);
		            }
		
		            if (proposedDest != null && proposedDest.lengthVector() > 3.0D) // found a valid path
		            {
		                this.xPosition = thisCloudPeng.currentFlightTarget.posX;
		                this.yPosition = thisCloudPeng.currentFlightTarget.posY;
		                this.zPosition = thisCloudPeng.currentFlightTarget.posZ;
		        		//System.out.printf("  Should execute: Going from (%f,%f,%f) to (%f,%f,%f)\n", this.entity.posX, this.entity.posY, this.entity.posZ, this.xPosition, this.yPosition, this.zPosition);
		                retVal = true;
		            }
		        }
	        }
	    }
        return retVal;
    }
    
    /* like EntityPenguinFlam.isDisguised, but only applies to flame penguin armor */
    protected boolean isDisguised(EntityPlayer entityplayer1)
    {
        InventoryPlayer inventoryplayer1 = entityplayer1.inventory;
        boolean retval = false; // not disguised

        if(entityplayer1.capabilities.isCreativeMode ||
        	((inventoryplayer1.armorItemInSlot(3) != null && 
					inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinFlameMask))
			 && (inventoryplayer1.armorItemInSlot(2) != null && 
			        inventoryplayer1.armorItemInSlot(2).getItem().equals(RanCraftPenguins.PenguinFlameChestPlate))
			 && (inventoryplayer1.armorItemInSlot(1) != null && 
			        inventoryplayer1.armorItemInSlot(1).getItem().equals(RanCraftPenguins.PenguinFlameLeggings))
			 && (inventoryplayer1.armorItemInSlot(0) != null && 
			        ((inventoryplayer1.armorItemInSlot(0).getItem().equals(RanCraftPenguins.PenguinBootsCloud))
        			|| inventoryplayer1.armorItemInSlot(0).getItem().equals(RanCraftPenguins.PenguinFlameFlippers)) ) ) )
        {
        	retval = true;
        }
    	return retval;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
    	boolean continueExec = false;
    	Vec3 proposedDest = null;
    	EntityPenguinCloud thisCloudPeng = (EntityPenguinCloud)this.entity;
    	thisCloudPeng.timeAirborne++;

    	EntityPlayer entityplayer = this.entity.worldObj.getClosestPlayerToEntity(this.entity, -1.0D);
    	//Vec3 playerLoc = entityplayer.getPosition(1.0F);
    	if(entityplayer != null){
	        Vec3 playerLoc = entityplayer.worldObj.getWorldVec3Pool().getVecFromPool(entityplayer.posX, entityplayer.posY, entityplayer.posZ);
	        proposedDest = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 64, 1, playerLoc);
	    	continueExec = (proposedDest != null && (thisCloudPeng.timeAirborne < thisCloudPeng.timeFlyingLimit || !thisCloudPeng.isCollidedVertically));
    	} else {
    		continueExec = false;
    	}

    	if(continueExec){
    		/* adapted from entityBat */
            //super.updateAITasks();

            if (thisCloudPeng.currentFlightTarget != null && (!entity.worldObj.isAirBlock((int)thisCloudPeng.posX, (int)thisCloudPeng.posY, (int)thisCloudPeng.posZ) || (int)thisCloudPeng.posY < 1))
            {
            	thisCloudPeng.currentFlightTarget = null;
            }

    		if(thisCloudPeng.timeAirborne < thisCloudPeng.timeClimbingLimit){
	            if (thisCloudPeng.currentFlightTarget == null || rand.nextInt(30) == 0 || thisCloudPeng.currentFlightTarget.getDistanceSquared((int)thisCloudPeng.posX, (int)thisCloudPeng.posY, (int)thisCloudPeng.posZ) < 4.0F)
	            {
	            	thisCloudPeng.currentFlightTarget = new ChunkCoordinates((int)proposedDest.xCoord, (int)proposedDest.yCoord + 50, (int)proposedDest.zCoord);
	            }
			} else { // timeAirborne = 100 - 200 or still aloft, so set a new target and descend
	        	thisCloudPeng.currentFlightTarget = new ChunkCoordinates((int)proposedDest.xCoord, (int)proposedDest.yCoord - 20, (int)proposedDest.zCoord);
			}
	
            double d0 = (double)thisCloudPeng.currentFlightTarget.posX + 0.5D - thisCloudPeng.posX;
            double d1 = (double)thisCloudPeng.currentFlightTarget.posY + 0.1D - thisCloudPeng.posY;
            double d2 = (double)thisCloudPeng.currentFlightTarget.posZ + 0.5D - thisCloudPeng.posZ;
            thisCloudPeng.motionX += (Math.signum(d0) * 0.5D - thisCloudPeng.motionX) * 0.10000000149011612D;
            thisCloudPeng.motionY += (Math.signum(d1) * 0.699999988079071D - thisCloudPeng.motionY) * 0.10000000149011612D;
            thisCloudPeng.motionZ += (Math.signum(d2) * 0.5D - thisCloudPeng.motionZ) * 0.10000000149011612D;
            float f = (float)(Math.atan2(thisCloudPeng.motionZ, thisCloudPeng.motionX) * 180.0D / Math.PI) - 90.0F;
            float f1 = MathHelper.wrapAngleTo180_float(f - thisCloudPeng.rotationYaw);
            thisCloudPeng.doMoveForward(0.5F);
            thisCloudPeng.rotationYaw += f1;
    	} else {
            //System.out.printf("  Don't continue executing: timeAirborne is %d\n", thisCloudPeng.timeAirborne);
            thisCloudPeng.timeAirborne = 0;
    	}

		return continueExec;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	boolean success = false;
    	int moveAttempts;
    	
		//System.out.printf("  startExecuting -- trying to move from %f,%f,%f to %f,%f,%f.\n", this.entity.lastTickPosX,this.entity.lastTickPosY,this.entity.lastTickPosZ,this.xPosition,this.yPosition,this.zPosition);

		/*moveAttempts = 0;
		while(!success && moveAttempts < 20){
			moveAttempts++;
			if(this.shouldExecute()){
				success = this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
				//System.out.printf("  Successful moving? %b.\n", success);
			}
		} */
        this.entity.isAirBorne = true;
    }
}
