package rancraftPenguins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class EntityPenguinFlam extends EntityPenguin
{
    final float MAXHEALTHWILD = 30;
    /* automatically compute the four Quartiles */
    float healthQuartile1 = (float)(MAXHEALTHWILD * 0.25) + 1;
    float healthQuartile2 = (float)(MAXHEALTHWILD * 0.50) + 1;
    float healthQuartile3 = (float)(MAXHEALTHWILD * 0.75) + 1;
	private float moveSpeed;
	
    public EntityPenguinFlam(World par1World)
    {
        super(par1World);
        //texture = texturePath + "pengFlam.png";
        setSize(0.6F, 1.6F); // width, height?
        moveSpeed = 0.4F; // (was 0.2);
        isImmuneToFire = true;
        tasks.addTask(1, new EntityAISwimming(this));
        //tasks.addTask(2, new EntityAISwimmingPeng(this, 2 * getMoveSpeed()));
        tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        tasks.addTask(4, new EntityAIAttackOnCollide(this, moveSpeed, true));
        tasks.addTask(7, new EntityAIWander(this, moveSpeed));
        tasks.addTask(9, new EntityAIWatchClosest(this, net.minecraft.entity.player.EntityPlayer.class, 8F));
        tasks.addTask(9, new EntityAILookIdle(this));
        targetTasks.addTask(3, new EntityAIHurtByTargetCustom(this, true));
        //targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, net.minecraft.entity.monster.EntityPigZombie.class, 16F, 200, false));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, net.minecraft.entity.monster.EntityPigZombie.class, 200, false));
        //targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, net.minecraft.entity.player.EntityPlayer.class, 16F, 0, true));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, net.minecraft.entity.player.EntityPlayer.class, 0, true));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        if (isAngry())
        {
            return RanCraftPenguins.modID+":penguinFlam.angry";
        }

        if (rand.nextInt(RanCraftPenguins.pengQuietInt) == 0) {
	        if(dataWatcher.getWatchableObjectFloat(18) < healthQuartile1) {
	    		return RanCraftPenguins.modID+":penguinFlam.healthd";
	    	} else if(dataWatcher.getWatchableObjectFloat(18) < healthQuartile2) {
	    		return RanCraftPenguins.modID+":penguinFlam.healthc";
	    	} else if(dataWatcher.getWatchableObjectFloat(18) < healthQuartile3) {
	    		return RanCraftPenguins.modID+":penguinFlam.healthb";
	    	} else {
	    		return RanCraftPenguins.modID+":penguinFlam.healtha";
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
        return RanCraftPenguins.modID+":penguinFlam.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return RanCraftPenguins.modID+":penguinFlam.death";
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
		return RanCraftPenguins.PengSkinRed;
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        byte byte0 = ((byte)6);
        return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), byte0);
    }

    /**
     * Sets the active target the Task system uses for tracking
     * gets triggered when flame penguin is attacked
     */
    public void setAttackTarget(EntityLivingBase par1EntityLiving)
    {
    	if(par1EntityLiving instanceof EntityPlayer){ // target is a player
    		if(!isDisguised((EntityPlayer)par1EntityLiving) || (attackingPlayer == par1EntityLiving) ){ // player isn't disguised or else player attacked
    			//System.out.printf("Not disguised!!!\n");
    			super.setAttackTarget(par1EntityLiving);
        		setAngry(true);
                this.handleHealthUpdate((byte)6);
    		}
    	} else { // target isn't a player (probably zombiepigman, so don't change to angry)
			super.setAttackTarget(par1EntityLiving);
    	}
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     * Overrides parent version because there are fewer requirements in the Nether.
     */
    public boolean getCanSpawnHere()
    {
    	return (worldObj.difficultySetting.getDifficultyId() > 0 && this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox));
    }

    public boolean interactShearing(EntityPlayer par1EntityPlayer)
    {
    	if (!worldObj.isRemote)
        {
	        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();
			//System.out.printf("From Client: Interacting...\n");
	
	        if (itemstack != null && itemstack.getItem().equals(Items.shears))
	        {
	        	// shear
	        	if(!isAngry()) // only chance to work is if penguin isn't hunting
	        	{
        			int k = rand.nextInt(3); // 0-2 drops
        			if(k > 0){
		                this.worldObj.playSoundAtEntity(this, "mob.sheep.shear", 1.0F, 1.0F);
        			}
        			for(int j = 0; j < k; j++) {
	                    EntityItem entityitem = entityDropItem(new ItemStack(RanCraftPenguins.PengScaleRed), 1.0F);
	                    entityitem.motionY += rand.nextFloat() * 0.05F;
	                    entityitem.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
	                    entityitem.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
            		}
	        		setAngry(true);
                    //worldObj.setEntityState(this, (byte)6); // broadcasts that flam is angry
                    this.handleHealthUpdate((byte)6);
	        		super.setAttackTarget(par1EntityPlayer); // flame penguins see through your disguise when you shear them
	        		itemstack.damageItem(2, par1EntityPlayer); // shears wear out faster on a flame penguin
	        	}
	        }
	        if (itemstack != null && itemstack.getItem().equals(RanCraftPenguins.PenguinShears))
	        {
	        	// shear using EPIC PENGUIN SHEARS!?!!
	        	if(!isAngry()) // only chance to work is if penguin isn't hunting
	        	{
        			int k = rand.nextInt(3); // 0-2 drops
        			if(k > 0){
		                this.worldObj.playSoundAtEntity(this, "mob.sheep.shear", 1.0F, 1.0F);
        			}
        			for(int j = 0; j < k; j++) {
	                    EntityItem entityitem = entityDropItem(new ItemStack(RanCraftPenguins.PengScaleRed), 1.0F);
	                    entityitem.motionY += rand.nextFloat() * 0.05F;
	                    entityitem.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
	                    entityitem.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
            		}
        			int k1 = rand.nextInt(2); // only gets mad half of the time with peng shears
        			if (k1 > 0){
        				setAngry(true);
        				//worldObj.setEntityState(this, (byte)6); // broadcasts that flam is angry
        				this.handleHealthUpdate((byte)6);
        				super.setAttackTarget(par1EntityPlayer); // flame penguins see through your disguise when you shear them
        			}
	        		itemstack.damageItem(1, par1EntityPlayer); // shears wear out normally with peng shears
	        	}
	        }
	        
	        // Can pacify them temporarily with magma fish (but can't tame them)
	        if (itemstack != null && itemstack.getItem().equals(RanCraftPenguins.FishMagma))
	        {
	            if(isAngry() && isDisguised(par1EntityPlayer)) // May only succeed if peng is hunting and player is disguised. Otherwise it doesn't use fish
	            {
                    if (!par1EntityPlayer.capabilities.isCreativeMode) 
                    {
                    	itemstack.stackSize--;
	        		}
		            if(itemstack.stackSize <= 0)
		            {
		            	par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, null);
		            }
	                if(rand.nextInt(3) == 0) // 33% chance of working
	                {
	                    this.setAngry(false);
	                    this.setPathToEntity(null);
	                	this.setAttackTarget(null);
	                    //this.handleHealthUpdate((byte)7);
	                    //worldObj.setEntityState(this, (byte)7); // broadcasts that flam is pacified
                        this.playTameEffect(false); // show smoke for success. nothing if it fails.
	                	this.setTarget(null);
	                	this.setRevengeTarget(null);
	                    this.worldObj.setEntityState(this, (byte)7);
	                    this.setJumping(false);
	                    //this.setLastAttackingEntity(null);
	                    if(this.isAngry() == false){
	                    	//System.out.printf("From Client EntityPenguinFlam: flam has NO target.\n");
	                    } else {
	                    	//System.out.printf("From Client EntityPenguinFlam: flam has a target.\n");
	                    }
	                }
	            }
        	}
        }
    	return false; //default value does nothing
    }
    
    /* make sure the version in EntityTameable isn't triggered */
    public void handleHealthUpdate(byte par1)
    {
        if (par1 == 7){ // to tame
            this.playTameEffect(false);
        } else if (par1 == 6) { // to not tame
        	;
        } else {
            super.handleHealthUpdate(par1);
        }
    }

    protected boolean isDisguised(EntityPlayer entityplayer1)
    {
        InventoryPlayer inventoryplayer1 = entityplayer1.inventory;
        boolean retval = false; // not disguised

        if ((inventoryplayer1.armorItemInSlot(3) != null && 
					(inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatEmp) 
					|| inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatKing)
					|| inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatYE)
					|| inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatMag)
					|| inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatLB)
					|| inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatWF)
					|| inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatGal)
					|| inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinHatAdel)
					|| inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinFlameMask)))
			 && (inventoryplayer1.armorItemInSlot(2) != null && 
			        (inventoryplayer1.armorItemInSlot(2).getItem().equals(RanCraftPenguins.PenguinTunic)
			        || inventoryplayer1.armorItemInSlot(2).getItem().equals(RanCraftPenguins.PenguinFlameChestPlate)))
			 && (inventoryplayer1.armorItemInSlot(1) != null && 
			        (inventoryplayer1.armorItemInSlot(1).getItem().equals(RanCraftPenguins.PenguinPants)
			        || inventoryplayer1.armorItemInSlot(1).getItem().equals(RanCraftPenguins.PenguinFlameLeggings)))
			 && (inventoryplayer1.armorItemInSlot(0) != null && 
			        (inventoryplayer1.armorItemInSlot(0).getItem().equals(RanCraftPenguins.PenguinFlippers)
			        || inventoryplayer1.armorItemInSlot(0).getItem().equals(RanCraftPenguins.PenguinFlameFlippers)
			        || inventoryplayer1.armorItemInSlot(0).getItem().equals(RanCraftPenguins.PenguinBootsCloud))) )
        {
			//System.out.printf("IS disguised!!!\n");
        	retval = true;
        }
    	return retval;
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 4;
    }

    /**
     * [This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.]
     */
	@Override
	public EntityAnimal spawnBabyAnimal(EntityAgeable par1EntityAgeable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canMateWith(EntityAnimal par1EntityAnimal) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable var1) {
		// TODO Auto-generated method stub
		return null;
	}

	public float getMoveSpeed() {
		return moveSpeed;
	}
}