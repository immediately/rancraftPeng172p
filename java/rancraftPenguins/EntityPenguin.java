package rancraftPenguins;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//import net.minecraft.block.BlockCloth;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityPenguin extends EntityTameable
{
    protected boolean looksWithInterest;
    protected float field_25048_b;
    protected float field_25054_c;
    private float field_70926_e;
    private float field_70924_f;
    private boolean field_70928_h;

    /** true is the penguin is wet else false */
    protected boolean isShaking;
    
    protected int GenericShearing = 1;
    
    /**
     * This time increases while penguin is shaking and emitting water particles.
     */
    protected float timePenguinIsShaking;
    protected float prevTimePenguinIsShaking;
	protected int timeDry = 0;  // how long since the penguin has left the water
    protected EntityAISitCust aiSitCust = new EntityAISitCust(this);

    public EntityPenguin(World par1World)
    {
        super(par1World);
        looksWithInterest = false;
        this.getNavigator().setAvoidsWater(true); // change this later?
    }

    // The above version without args isn't needed for penguins
    public void applyEntityAttributes(float moveSpeed, float maxHealthTame, float maxHealthWild)
    {
        super.applyEntityAttributes();
        //this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(moveSpeed);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(moveSpeed);

        if (this.isTamed())
        {
            //this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(maxHealthTame);
            this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealthTame);
        }
        else
        {
            //this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(maxHealthWild);
            this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealthWild);
        }
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    /**
     * Returns true if penguins can be tamed with stuffID
     */
    protected boolean isPenguinFood(ItemStack par1ItemStack)
    {
        return (par1ItemStack.getItem().equals(Items.fish)); // this includes pufferfish, salmon, and clownfish
        		/*|| stuff == 23272 /* aquaculture mod raw fish fillet and whale steak -- disabled b/c of item ID change
        		|| stuff == 23273);*/
    }

    /* overrides the fn for EntityLiving */
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    /**
     * Checks if the parameter is an wheat item.
     * The one in EntityAnimal sometimes causes a crash because it tries to evaluate without checking for null first
     */
    public boolean isWheat(ItemStack par1ItemStack)
    {
        if (par1ItemStack == null)
        {
            return false;
        } else {
            return (par1ItemStack.getItem().equals(Items.wheat));
        }
    }

    /**
     * Sets the active target the Task system uses for tracking
     */
    public void setAttackTarget(EntityLivingBase par1EntityLivingBase)
    {
        super.setAttackTarget(par1EntityLivingBase);

        if (par1EntityLivingBase == null)
        {
            if (!this.isAngry())
            {
                return;
            }

            this.setAngry(false);
            List list = this.worldObj.getEntitiesWithinAABB(this.getClass(), AxisAlignedBB.getAABBPool().getAABB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(16.0D, 10.0D, 16.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityPenguin entitypenguin = (EntityPenguin)iterator.next();

                if (this != entitypenguin)
                {
                    entitypenguin.setAngry(false);
                }
            }
        }
        else
        {
            this.setAngry(true);
        }
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    protected void updateAITick()
    {
        this.dataWatcher.updateObject(18, Float.valueOf(this.getHealth()));
    }


    //abstract public int getMaxHealth();
    
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(18, new Float(this.getHealth()));
        this.dataWatcher.addObject(19, new Byte((byte)0));
        //this.dataWatcher.addObject(20, new Byte((byte)BlockColored.getBlockFromDye(1)));
    }


    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void playStepSound(int par1, int par2, int par3, int par4)
    {
        this.playSound("mob.wolf.step", 0.15F, 1.0F);
    }

    /**
     * Sets the size of this mob (useful for adjusting penguin's dimensions when it swims)
     */
    protected void setSize2(float width, float height)
    {
    	super.setSize(width, height);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setBoolean("Angry", this.isAngry());
    }

    /**
     * Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        this.setAngry(par1NBTTagCompound.getBoolean("Angry"));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    abstract protected String getLivingSound();

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    abstract protected String getHurtSound();

    /**
     * Returns the sound this mob makes on death.
     */
    abstract protected String getDeathSound();

    /**
     * Returns the volume for the sounds this mob makes.
     */
    abstract protected float getSoundVolume();

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    abstract protected Item getDropItem();

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (!worldObj.isRemote && isShaking && !field_70928_h && !hasPath() && onGround)
        {
            field_70928_h = true;
            timePenguinIsShaking = 0.0F;
            prevTimePenguinIsShaking = 0.0F;
            worldObj.setEntityState(this, (byte)8);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
        this.field_70924_f = this.field_70926_e;

        if (this.func_70922_bv())
        {
            this.field_70926_e += (1.0F - this.field_70926_e) * 0.4F;
        }
        else
        {
            this.field_70926_e += (0.0F - this.field_70926_e) * 0.4F;
        }

        if (this.func_70922_bv())
        {
            this.numTicksToChaseTarget = 10;
        }

        if (this.isWet())
        {
            this.isShaking = true;
            this.field_70928_h = false;
            this.timePenguinIsShaking = 0.0F;
            this.prevTimePenguinIsShaking = 0.0F;
        }
        else if ((this.isShaking || this.field_70928_h) && this.field_70928_h)
        {
            if (this.timePenguinIsShaking == 0.0F)
            {
                this.playSound("mob.wolf.shake", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.prevTimePenguinIsShaking = this.timePenguinIsShaking;
            this.timePenguinIsShaking += 0.05F;

            if (this.prevTimePenguinIsShaking >= 2.0F)
            {
                this.isShaking = false;
                this.field_70928_h = false;
                this.prevTimePenguinIsShaking = 0.0F;
                this.timePenguinIsShaking = 0.0F;
            }

            if (this.timePenguinIsShaking > 0.4F)
            {
                float f = (float)this.boundingBox.minY;
                int i = (int)(MathHelper.sin((this.timePenguinIsShaking - 0.4F) * (float)Math.PI) * 7.0F);

                for (int j = 0; j < i; ++j)
                {
                    float f1 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    this.worldObj.spawnParticle("splash", this.posX + (double)f1, (double)(f + 0.8F), this.posZ + (double)f2, this.motionX, this.motionY, this.motionZ);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean getPenguinShaking()
    {
        return this.isShaking;
    }

    @SideOnly(Side.CLIENT)
    /**
     * Used when calculating the amount of shading to apply while the penguin is shaking.
     */
    public float getShadingWhileShaking(float par1)
    {
        return 0.75F + ((prevTimePenguinIsShaking + (timePenguinIsShaking - prevTimePenguinIsShaking) * par1) / 2.0F) * 0.25F;
    }

    @SideOnly(Side.CLIENT)
    public float getShakeAngle(float par1, float par2)
    {
        float f = (prevTimePenguinIsShaking + (timePenguinIsShaking - prevTimePenguinIsShaking) * par1 + par2) / 1.8F;

        if (f < 0.0F)
        {
            f = 0.0F;
        }
        else if (f > 1.0F)
        {
            f = 1.0F;
        }

        return MathHelper.sin(f * (float)Math.PI) * MathHelper.sin(f * (float)Math.PI * 11F) * 0.15F * (float)Math.PI;
    }

    @SideOnly(Side.CLIENT)
    public float getInterestedAngle(float par1)
    {
        return (this.field_70924_f + (this.field_70926_e - this.field_70924_f) * par1) * 0.15F * (float)Math.PI;
    }

    public float getEyeHeight()
    {
        return height * 0.8F;
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves AND penguins.
     */
    public int getVerticalFaceSpeed()
    {
        return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        Entity entity = par1DamageSource.getEntity();
        aiSitCust.setSitting(false);

        if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
        {
            par2 = (par2 + 1) / 2;
        }

        return super.attackEntityFrom(par1DamageSource, par2);
    }

    /**
     * Called when this class wants to know what to drop on shearing
     */
    public Item droppedFeather()
    {
    	Item dropped;
    	dropped = Items.potato;
    	return dropped;
    }
    
    /**
     * Called when a penguin gets sheared
     */
    public boolean interactShearing(EntityPlayer par1EntityPlayer)
    {
    	if (!worldObj.isRemote)
        {
	        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();

	        //checks if there is something in the player's inventory to prevent crash
	        //checks if it is supposed to run this shearing code or not
	        if(itemstack != null){
	        
		        if (!isChild() && (itemstack.getItem().equals(Items.shears) || itemstack.getItem().equals(RanCraftPenguins.PenguinShears))) // can't shear chicks
		        {
		        	// shear
		        	if(!isTamed())
		        	{
		        		setAngry(true);
	        			setAttackTarget(par1EntityPlayer);
		        	} else {
		        		int shearDurabilityInverse;
		        		int angerChanceInverse;
		        		
				        if (itemstack.getItem().equals(RanCraftPenguins.PenguinShears)){
				        	angerChanceInverse = 5; // 20% chance penguin may go wild and attack
				        	shearDurabilityInverse = 3; // 33% chance shears won't get worn
				        } else { // ordinary shears
				        	angerChanceInverse = 3; // 33% chance penguin may go wild and attack
				        	shearDurabilityInverse = 1; // 0% chance shears won't get worn
				        }

				        int i = rand.nextInt(angerChanceInverse);
		        		if(i == 0){ // 33% chance for normal; 20% chance for penguin shears
		                    aiSitCust.setSitting(false); // stand up
		        			setTamed(false);
		        			setAngry(true);
		        			setAttackTarget(par1EntityPlayer);
		        		} else {
		        			int k = rand.nextInt(3); // 0-2 drops
		        			if(k > 0){
				                this.worldObj.playSoundAtEntity(this, "mob.sheep.shear", 1.0F, 1.0F);
		        			}
		        			for(int j = 0; j < k; j++) {
		        				Item dropped;
		        				dropped = this.droppedFeather();
		        				EntityItem entityitem = entityDropItem(new ItemStack(dropped), 1.0F); 
			                    entityitem.motionY += rand.nextFloat() * 0.05F;
			                    entityitem.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
			                    entityitem.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
		            		}
		        		}
		        		int k = rand.nextInt(shearDurabilityInverse);
		        		if (k < 3){
		        			itemstack.damageItem(1, par1EntityPlayer); // 2/3 of the time if variable is 3; always if variable is 1
		        		}
		        	}
		        }
		        // holding fish or other penguin food
		        else if (isPenguinFood(itemstack))
		        {
                    ItemFood itemfood = (ItemFood)itemstack.getItem();

                    // penguin is not tame (could be angry) so try to tame it
		        	if(!isTamed())
		        	{
	                    if (!par1EntityPlayer.capabilities.isCreativeMode) 
	                    {
	                    	itemstack.stackSize--;
		        		}
		
		                if (itemstack.stackSize <= 0)
		                {
		                    par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, null);
		                }
		                if (rand.nextInt(3) == 0)
		                {
		        			//System.out.printf("Sitting is currently: %b\n", this.isSitting());
		        			setAngry(false);
		                    setTamed(true);
		                    setPathToEntity(null);
		                    setAttackTarget(null);
		                    //this.aiSitCust.setSitting(true);  // not sure why tamed penguin sits with this commented out
		                    setHealth(20.0F);
		                    //setOwner(par1EntityPlayer.PERSISTED_NBT_TAG);
	  	                    setOwner(par1EntityPlayer.getCommandSenderName());
		                    this.playTameEffect(true);
		                    this.worldObj.setEntityState(this, (byte)7);
		        			//System.out.printf("Now Sitting is: %b\n", this.isSitting());
		        			//System.out.printf("Got Sitting from cust: %b\n", this.aiSitCust.getSitting());
		                }
		                else
		                {
		                    this.playTameEffect(false);
		                    worldObj.setEntityState(this, (byte)6);
		                }
		                return true;
		        	} else { // penguin is tame (and may be a child). so feed it if it's hungry
                        this.heal((float)itemfood.func_150905_g(itemstack));
	
		                if (this.dataWatcher.getWatchableObjectFloat(18) < 20)
		                {
	                        if (!par1EntityPlayer.capabilities.isCreativeMode)
	                        {
	                            --itemstack.stackSize;
	                        }

	                        this.heal((float)itemfood.func_150905_g(itemstack));

	                        if (itemstack.stackSize <= 0)
	                        {
	                            par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
	                        }

		                    return true;
		                }
		        	}
		        }
	        }
	        
	        // sit toggle
			//System.out.printf("Player name: %s\n", par1EntityPlayer.PERSISTED_NBT_TAG);
			//System.out.printf("Owner: %s\n", getOwnerName());

	        //if (par1EntityPlayer.PERSISTED_NBT_TAG.equalsIgnoreCase(getOwnerName()) &&
	        if (par1EntityPlayer.getCommandSenderName().equalsIgnoreCase(getOwnerName()) &&
            	(itemstack == null || !(isPenguinFood(itemstack) || isWheat(itemstack) || itemstack.getItem().equals(Items.shears) || 
            	itemstack.getItem().equals(RanCraftPenguins.PenguinShears))))
	        {
				//System.out.printf("Toggling sitting from: %b\n", this.isSitting());
                this.aiSit.setSitting(!this.isSitting());
                //this.aiSitCust.setSitting(!this.isSitting());
				//System.out.printf("Toggled sitting: %b\n", this.isSitting());
    			setAngry(false);
                setTamed(true);
                setPathToEntity(null);
                setAttackTarget(null);
	            isJumping = false;
	        }
        }
    	return false; //default value does nothing
    }
    
	/**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        boolean shearingValue = this.interactShearing(par1EntityPlayer);
        if (shearingValue)
        {
        	return true;
        }
        return super.interact(par1EntityPlayer);
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte par1)
    {
        if (par1 == 8)
        {
            field_70928_h = true;
            timePenguinIsShaking = 0.0F;
            prevTimePenguinIsShaking = 0.0F;
        }
        else
        {
            super.handleHealthUpdate(par1);
        }
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    abstract public int getMaxSpawnedInChunk();

    /**
     * gets this penguin's angry state
     */
    public boolean isAngry()
    {
        return (dataWatcher.getWatchableObjectByte(16) & 2) != 0;
    }

    /**
     * sets this penguin's angry state to true if the boolean argument is true
     */
    public void setAngry(boolean par1)
    {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);

        if (par1)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 2)));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & -3)));
        }
    }

   /**
     * [This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.]
     */
    abstract public EntityAnimal spawnBabyAnimal(EntityAgeable par1EntityAgeable);

    public void func_48150_h(boolean par1)
    {
        looksWithInterest = par1;
    }

/*    public void func_70918_i(boolean par1)
    {
        if (par1)
        {
            this.dataWatcher.updateObject(19, Byte.valueOf((byte)1));
        }
        else
        {
            this.dataWatcher.updateObject(19, Byte.valueOf((byte)0));
        }
    }
*/
    abstract public boolean canMateWith(EntityAnimal par1EntityAnimal);

    public boolean func_70922_bv()
    {
        return this.dataWatcher.getWatchableObjectByte(19) == 1;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return !this.isTamed() && this.ticksExisted > 2400;
    }

    public EntityAgeable createChild(EntityAgeable par1EntityAgeable)
    {
        return this.spawnBabyAnimal(par1EntityAgeable);
    }
}