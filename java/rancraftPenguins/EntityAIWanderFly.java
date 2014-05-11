package rancraftPenguins;

import java.util.Random;

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

public class EntityAIWanderFly extends EntityAIBase
{
    private EntityCreature entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private float speed;
	int timeAirborne;
    Random rand = new Random();

    public EntityAIWanderFly(EntityCreature par1EntityCreature, float par2)
    {
        this.entity = par1EntityCreature;
        this.speed = par2;
        this.setMutexBits(1);
        //par1EntityCreature.posY += 0.4F;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	boolean retVal = false;
    	
        if (this.entity.getAge() >= 100)
        {
        	retVal = false;
        }
        else if (this.entity.getRNG().nextInt(480) != 0) // EntityAIWander is 120
        {
        	retVal = false;
        }
        else
        {
            Vec3 vec3 = RandomPositionGenerator.findRandomTarget(this.entity, 64, 1);

            if (vec3 == null)
            {
            	retVal = false;
            }
            else
            {
                this.xPosition = vec3.xCoord;
                this.yPosition = vec3.yCoord;
                this.zPosition = vec3.zCoord;
                retVal = true;
            }
        }
        return retVal;
    }
    
    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
    	//boolean continueExec = !this.entity.getNavigator().noPath();
    	EntityPenguinCloud thisCloudPeng = (EntityPenguinCloud)this.entity;
    	thisCloudPeng.timeAirborne++;

        Vec3 proposedDest = RandomPositionGenerator.findRandomTarget(this.entity, 64, 1);
    	boolean continueExec = (proposedDest != null && (thisCloudPeng.timeAirborne < thisCloudPeng.timeFlyingLimit || !thisCloudPeng.isCollidedVertically));

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
    	
        this.entity.isAirBorne = true;
    }
}
