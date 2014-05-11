package rancraftPenguins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityPenguinWF extends EntityPenguin
{
    final float MAXHEALTHWILD = 5;
    final float MAXHEALTHTAME = 12;
    /* automatically compute the four Quartiles */
    float healthQuartile1 = (float)(MAXHEALTHTAME * 0.25) + 1;
    float healthQuartile2 = (float)(MAXHEALTHTAME * 0.50) + 1;
    float healthQuartile3 = (float)(MAXHEALTHTAME * 0.75) + 1;
	private float moveSpeed;

    public EntityPenguinWF(World par1World)
    {
        super(par1World);
        //texture = texturePath + "pengWF.png";
        setSize(0.3F, 0.7F); // width, height?
        moveSpeed = 0.28F; // (was 0.2);
        //looksWithInterest = false;
        //tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAISwimmingPeng(this, getMoveSpeed()));
        tasks.addTask(2, aiSit);
        tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        tasks.addTask(4, new EntityAIAttackOnCollide(this, moveSpeed, true));
        tasks.addTask(5, new EntityAIFollowOwner(this, moveSpeed, 10F, 2.0F));
        tasks.addTask(6, new EntityAIMate(this, moveSpeed));
        tasks.addTask(7, new EntityAIWander(this, moveSpeed));
        tasks.addTask(9, new EntityAIWatchClosest(this, net.minecraft.entity.player.EntityPlayer.class, 8F));
        tasks.addTask(9, new EntityAILookIdle(this));
        targetTasks.addTask(3, new EntityAIHurtByTargetCustom(this, true));
    }

    /**
     * returns the directory and filename as a String
     */
    /*public String getTexture()
    {
        if (isChild()) {
	    	if (isTamed())
	            return texturePath + "pengWFCh_tame.png";
	        else
	        	return texturePath + "pengWFCh.png";
        }
    	if (isAngry()) {
	       	return texturePath + "pengWF_angry.png";
	    } 
	   	if (isTamed()) {
	        return texturePath + "pengWF_tame.png";
		}
	    return super.getTexture();
    }*/

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        if (isAngry())
        {
            return RanCraftPenguins.modID+":penguinWF.angry";
        }

        if (rand.nextInt(RanCraftPenguins.pengQuietInt) == 0) {
	        if(dataWatcher.getWatchableObjectFloat(18) < healthQuartile1) {
	    		return RanCraftPenguins.modID+":penguinWF.healthd";
	    	} else if(dataWatcher.getWatchableObjectFloat(18) < healthQuartile2) {
	    		return RanCraftPenguins.modID+":penguinWF.healthc";
	    	} else if(dataWatcher.getWatchableObjectFloat(18) < healthQuartile3) {
	    		return RanCraftPenguins.modID+":penguinWF.healthb";
	    	} else {
	    		return RanCraftPenguins.modID+":penguinWF.healtha";
	    	}
        } else {
        	return null;
        }
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return RanCraftPenguins.modID+":penguinWF.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return RanCraftPenguins.modID+":penguinWF.death";
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 0.4F;
    }

/*  Not needed here. This function in EntityPenguin.java will set moveSpeed from this file correctly
    public void applyEntityAttributes()
    {
            super.applyEntityAttributes((float) moveSpeed, MAXHEALTHTAME, MAXHEALTHWILD);
    }
*/

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected Item getDropItem()
    {
    	return RanCraftPenguins.PengSkinBrown;
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        byte byte0 = ((byte)(isTamed() ? 3 : 1));
        return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), byte0);
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
    	int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);
        return worldObj.getFullBlockLightValue(i, j, k) > 8 && super.getCanSpawnHere();
    }
    
    public Item droppedFeather()
    {
    	Item dropped;
    	int i = rand.nextInt(10);
		if(i > 7){ // 70% brown, 30% white
			dropped = RanCraftPenguins.PengFeatherBrown;
		} else {
			dropped = RanCraftPenguins.PengFeatherWhite;
		}
		return dropped;
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 4;
    }

    /**
     * This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.
     */
    public EntityPenguinWF spawnBabyAnimal(EntityAgeable par1EntityAgeable)
    {
        EntityPenguinWF var2 = new EntityPenguinWF(this.worldObj);
        String var3 = this.getOwnerName();

        if (var3 != null && var3.trim().length() > 0)
        {
            var2.setOwner(var3);
            var2.setTamed(true);
        }

        return var2;
    }

    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
	@Override
    public boolean canMateWith(EntityAnimal par1EntityAnimal)
    {
        if (par1EntityAnimal == this)
        {
            return false;
        }
        else if (!this.isTamed() || this.isAngry())
        {
            return false;
        }
        else if (!(par1EntityAnimal instanceof EntityPenguinWF))
        {
            return false;
        }
        else
        {
            EntityPenguinWF var2 = (EntityPenguinWF)par1EntityAnimal;
            return !var2.isTamed() ? false : ((this.isSitting() || var2.isSitting()) ? false : this.isInLove() && var2.isInLove());
        }
    }

	public float getMoveSpeed() {
		return moveSpeed;
	}
}
