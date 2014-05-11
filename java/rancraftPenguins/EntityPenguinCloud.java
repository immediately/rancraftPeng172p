package rancraftPenguins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityPenguinCloud extends EntityPenguin {
	final float MAXHEALTHWILD = 7;
	final float MAXHEALTHTAME = 17; // not used currently, b/c they're not tameable
	protected int timeAirborne;
	private int timeAloft = 0; // how long since the penguin touched the ground -- used by ModelPenguinCloud
	private int shearWait = 0; // how long until penguin can be sheared again -- to avoid shearing on the run
	int timeClimbingLimit = 100; // number of ticks to climb for
	int timeFlyingLimit = 200; // number of ticks to fly for
	private float moveSpeed;
	//public static int timeDry = 0;  // how long since the penguin has left the water -- moved to EntityPenguin

    /**
     * target based on proposedDest selected by EntityAIWanderFly or EntityAIFlyAway
     */
    protected ChunkCoordinates currentFlightTarget;

	public EntityPenguinCloud(World par1World) {
		super(par1World);
		//texture = texturePath + "pengCloud.png";
		setSize(0.4F, 1.3F); // width, height?
        moveSpeed = 0.28F; // (was 0.2);
		tasks.addTask(1, new EntityAIFlyAway(this, 2.5F * getMoveSpeed()));
        tasks.addTask(2, new EntityAISwimmingPeng(this, getMoveSpeed()));
		tasks.addTask(3, new EntityAIWander(this, getMoveSpeed()));
		tasks.addTask(3, new EntityAIWanderFly(this, 2.5F * getMoveSpeed()));
		tasks.addTask(4, new EntityAIWatchClosest(this, net.minecraft.entity.player.EntityPlayer.class, 8F));
		tasks.addTask(4, new EntityAILookIdle(this));
		this.isAirBorne = false; // start off on the ground
	}

	/**
	 * returns the directory and filename as a String
	 */
	public String getTexture() {
		return null; //super.getTexture();
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound() {
		if (rand.nextInt(RanCraftPenguins.pengQuietInt) == 0) {
			//return "mob.wolf.growl";
			return RanCraftPenguins.modID+":penguinCloud.healtha";
		} else {
			return null;
		}
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound() {
		return RanCraftPenguins.modID+":penguinCloud.hurt";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound() {
		String retVal;
		int k = rand.nextInt(50); // 2% chance of a wilhelm scream

		if(k == 0){
			retVal = RanCraftPenguins.modID+":penguinCloud.wilhelm";
		} else {
			retVal = RanCraftPenguins.modID+":penguinCloud.death";
		}
		return retVal;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
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
	protected Item getDropItem() {
		return RanCraftPenguins.PengSkinLightBlue;
	}

	public boolean attackEntityAsMob(Entity par1Entity) {
		byte byte0 = 5;
		return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this),
				byte0);
	}

	/**
	 * Checks if the entity's current position is a valid location to spawn this
	 * entity.
	 */
	public boolean getCanSpawnHere() {
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(boundingBox.minY);
		int k = MathHelper.floor_double(posZ);
		return worldObj.getFullBlockLightValue(i, j, k) > 8
				&& super.getCanSpawnHere();
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow,
	 * gets into the saddle on a pig.
	 */
	public boolean interactShearing(EntityPlayer par1EntityPlayer) {
		if (!worldObj.isRemote) {
	        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();
			//System.out.printf("From Client: Interacting...\n");

			if (itemstack != null) { // make sure we're holding something first to prevent crash
		        if ((itemstack.getItem().equals(Items.shears)) || (itemstack.getItem().equals(RanCraftPenguins.PenguinShears))){
					// shear
					if (isDisguised(par1EntityPlayer)) {
			        	if(!this.isAngry() && shearWait < 1) // only chance to work is if penguin isn't trying to flee and hasn't been sheared in 30 sec.
			        	{
			        		setAngry(true);
			        		if (itemstack.getItem().equals(RanCraftPenguins.PenguinShears))
			        		{
			        			this.shearWait = 300; //Wait time is only 15 seconds with penguin shears
			        		} else {
			        			this.shearWait = 600;
			        		}
		        			int k = rand.nextInt(3); // 0-2 drops
		        			if(k > 0){
				                this.worldObj.playSoundAtEntity(this, "mob.sheep.shear", 1.0F, 1.0F);
		        			}
		        			for(int j = 0; j < k; j++) {
			                    EntityItem entityitem = entityDropItem(new ItemStack(RanCraftPenguins.PengDownCloud), 1.0F);
			                    entityitem.motionY += rand.nextFloat() * 0.05F;
			                    entityitem.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
			                    entityitem.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
		            		}
	        				//handleHealthUpdate((byte)6);
			        		itemstack.damageItem(1, par1EntityPlayer); // shears wear out
			        	}
					}
		        }
			}
		}
		return false; //default variable does nothing
	}

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
    	this.setAngry(true); // cloud penguin flies away
		//System.out.printf("Just set angry to true -- so should fly away.\n");
    	
        return super.attackEntityFrom(par1DamageSource, par2);
    }

	/* Whether a player is wearing a full suit of flame penguin armor 
	 * Pickier than EntityPenguinFlam.isDisguised, which can be any penguin armor. */
    protected boolean isDisguised(EntityPlayer entityplayer1)
    {
        InventoryPlayer inventoryplayer1 = entityplayer1.inventory;
        boolean retval = false; // not disguised

		//System.out.printf("Wearing Mask is %b\n", inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinFlameMask));
        if ((inventoryplayer1.armorItemInSlot(3) != null && 
					inventoryplayer1.armorItemInSlot(3).getItem().equals(RanCraftPenguins.PenguinFlameMask))
			 && (inventoryplayer1.armorItemInSlot(2) != null && 
			        inventoryplayer1.armorItemInSlot(2).getItem().equals(RanCraftPenguins.PenguinFlameChestPlate))
			 && (inventoryplayer1.armorItemInSlot(1) != null && 
			        inventoryplayer1.armorItemInSlot(1).getItem().equals(RanCraftPenguins.PenguinFlameLeggings))
			 && (inventoryplayer1.armorItemInSlot(0) != null && 
			        (inventoryplayer1.armorItemInSlot(0).getItem().equals(RanCraftPenguins.PenguinBootsCloud)
        			|| inventoryplayer1.armorItemInSlot(0).getItem().equals(RanCraftPenguins.PenguinFlameFlippers)) ) )
        {
        	retval = true;
        }
    	return retval;
    }

	/**
	 * Will return how many at most can spawn in a chunk at once.
	 */
	public int getMaxSpawnedInChunk() {
		return 4;
	}

	/**
	 * Returns true if the mob is currently able to mate with the specified mob.
	 */
	@Override
	public boolean canMateWith(EntityAnimal par1EntityAnimal) {
		return false;
	}

	@Override
	public EntityAnimal spawnBabyAnimal(EntityAgeable par1EntityAgeable) {
		return null;
	}

	protected void doMoveForward(float distanceToMove){
		this.moveForward = distanceToMove;
	}

	/* from bat */
	public void onUpdate()
    {
        super.onUpdate();

        /* slow down descents */
        if(!this.isJumping){
        	if(this.motionY < 0.0D){ // necessary?
        		this.motionY *= 0.6000000238418579D;
        	}
        }
        this.fallDistance = 0.0F; // so they never get hurt
        
        this.shearWait = this.shearWait > 0?this.shearWait - 1:0;
		//System.out.printf("ShearWait = %d\n", this.shearWait);
    }

	public int getTimeAloft() {
		return timeAloft;
	}

	public void setTimeAloft(int timeAloft) {
		this.timeAloft = timeAloft;
	}

	public float getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(float moveSpeed) {
		this.moveSpeed = moveSpeed;
	}
}
