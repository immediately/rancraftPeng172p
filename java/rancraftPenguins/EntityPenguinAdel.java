package rancraftPenguins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityPenguinAdel extends EntityPenguin
{
    final float MAXHEALTHWILD = 6;
    final float MAXHEALTHTAME = 14;
    float healthQuartile1 = (float)(MAXHEALTHTAME * 0.25) + 1;
    float healthQuartile2 = (float)(MAXHEALTHTAME * 0.50) + 1;
    float healthQuartile3 = (float)(MAXHEALTHTAME * 0.75) + 1;
	private double moveSpeed;

    public EntityPenguinAdel(World par1World)
    {
        super(par1World);
        //texture = texturePath + "pengAdel.png";
        setSize(0.4F, 1.3F); // width, height?
        moveSpeed = 0.28D; // (was 0.2);
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
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        if (isAngry())
        {
            return RanCraftPenguins.modID+":penguinAdel.angry";
        }

        if (rand.nextInt(RanCraftPenguins.pengQuietInt) == 0) {
	        if(dataWatcher.getWatchableObjectFloat(18) < healthQuartile1) {
	    		return RanCraftPenguins.modID+":penguinAdel.healthd";
	    	} else if(dataWatcher.getWatchableObjectFloat(18) < healthQuartile2) {
	    		return RanCraftPenguins.modID+":penguinAdel.healthc";
	    	} else if(dataWatcher.getWatchableObjectFloat(18) < healthQuartile3) {
	    		return RanCraftPenguins.modID+":penguinAdel.healthb";
	    	} else {
	    		return RanCraftPenguins.modID+":penguinAdel.healtha";
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
        return RanCraftPenguins.modID+":penguinAdel.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return RanCraftPenguins.modID+":penguinAdel.death";
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
		return RanCraftPenguins.PengSkinBlack;
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        int i = this.isTamed() ? 3 : 2;
        return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float)i);
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
		int i = rand.nextInt(3);
		if(i > 0){ // 66% black, 33% white
			dropped = RanCraftPenguins.PengFeatherBlack;
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
    public EntityPenguinAdel spawnBabyAnimal(EntityAgeable par1EntityAgeable)
    {
        EntityPenguinAdel var2 = new EntityPenguinAdel(this.worldObj);
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
        else if (!(par1EntityAnimal instanceof EntityPenguinAdel))
        {
            return false;
        }
        else
        {
            EntityPenguinAdel var2 = (EntityPenguinAdel)par1EntityAnimal;
            return !var2.isTamed() ? false : ((this.isSitting() || var2.isSitting()) ? false : this.isInLove() && var2.isInLove());
        }
    }

	public double getMoveSpeed() {
		return moveSpeed;
	}
}
