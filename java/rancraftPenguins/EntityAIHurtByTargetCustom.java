package rancraftPenguins;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIHurtByTargetCustom extends EntityAITarget
{
    boolean field_75312_a;

    /** The PathNavigate of our entity. */
    EntityLivingBase entityPathNavigate;

    public EntityAIHurtByTargetCustom(EntityCreature par1EntityCreature, boolean par2)
    {
        super(par1EntityCreature, false);
        this.field_75312_a = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	EntityLivingBase par1EntityLiving = this.taskOwner.getAttackTarget();

    	if (par1EntityLiving instanceof EntityPlayer)
        {
    		//System.out.printf("Targeting a PLAYER.\n");
	    	if (this.taskOwner instanceof EntityTameable && ((EntityTameable)this.taskOwner).isTamed())
	        {
	    		//System.out.printf("I'm tame. Deciding whether to attack.\n");
	    		//System.out.printf("I am a %s.\n", this.taskOwner.getEntityName());
	    		//System.out.printf("My target's username is %s.\n", ((EntityPlayer) par1EntityLiving).username);
	            if (par1EntityLiving instanceof EntityTameable && ((EntityTameable)par1EntityLiving).isTamed())
	            {
	        		//System.out.printf("Not attacking, b/c it's tame.\n");
	                return false;
	            }
	
	            if (par1EntityLiving == ((EntityTameable)this.taskOwner).getOwner())
	            {
	        		//System.out.printf("Not attacking, and going to stop being mad, b/c it's my owner. For my honor.\n");
	        		this.taskOwner.setAttackTarget(null); // seems to be the key
	        		((EntityPenguin) this.taskOwner).setAngry(false); // if he doesn't look angry he shouldn't sound angry!
	                return false; // THIS doesn't stop the attack by itself. Previous line is also necessary.
	            }
	        }
	        else if (par1EntityLiving instanceof EntityPlayer && ((EntityPlayer)par1EntityLiving).capabilities.disableDamage)
	        {
	            return false;
	        }
	
	        else if (this.shouldCheckSight && !this.taskOwner.getEntitySenses().canSee(par1EntityLiving))
	        {
	            return false;
	        }
        }

        return this.isSuitableTarget(this.taskOwner.getAITarget(), true);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     * This seems to only affect the 
     */
    public boolean continueExecuting()
    {
    	EntityLivingBase par1EntityLiving = this.taskOwner.getAttackTarget();

    	if((par1EntityLiving == null) || (par1EntityLiving.isDead)){
    		//System.out.printf("From Client: ATTACK TARGET -- dead? %s\n", this.taskOwner.getAttackTarget().getEntityName().toString());
    		//resetTask();
        	return false;
        } else {
    		//System.out.printf("From Client: ATTACK TARGET STILL ALIVE... STILL ALIVE...\n");
        	return true;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
        this.entityPathNavigate = this.taskOwner.getAITarget();

        if (this.field_75312_a)
        {
           double d0 = this.getTargetDistance(); // d0 is target distance
           List list = this.taskOwner.worldObj.getEntitiesWithinAABB(this.taskOwner.getClass(), AxisAlignedBB.getAABBPool().getAABB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D).expand(d0, 10.0D, d0));
           Iterator var2 = list.iterator();

            while (var2.hasNext())
            {
                EntityLiving var3 = (EntityLiving)var2.next();

                if (this.taskOwner != var3 && var3.getAttackTarget() == null)
                {
                    var3.setAttackTarget(this.taskOwner.getAITarget());
                }
            }
        }

        super.startExecuting();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        if (this.taskOwner.getAttackTarget() != null && this.taskOwner.getAttackTarget() instanceof EntityPlayer && ((EntityPlayer)this.taskOwner.getAttackTarget()).capabilities.disableDamage)
        {
            super.resetTask();
        }
    }
}
