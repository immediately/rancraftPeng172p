package rancraftPenguins;

import java.util.Calendar;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;

/* Ninja Penguin is based on Skeleton, extends EntityMob, not EntityPenguin */
public class EntityPenguinNinj extends EntityMob implements IRangedAttackMob
{
    /** The ItemStack that any Ninja Penguin holds (a katana). */
    private static final ItemStack heldItem = new ItemStack(RanCraftPenguins.PenguinKatanaS, 1);
    final int MAXHEALTHWILD = 20;
	private float moveSpeed;
    /**
     * This time increases while penguin is shaking and emitting water particles.
     */
    protected float timePenguinIsShaking;
    protected float prevTimePenguinIsShaking;
	public int timeDry = 0;  // how long since the penguin has left the water


    public EntityPenguinNinj(World par1World)
    {
        super(par1World);
        this.moveSpeed = 0.35F;
        //tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAISwimmingPengNinj(this, 2* getMoveSpeed()));
        //tasks.addTask(2, new EntityAISwimmingPengNinj(this, 0.0005F));
        //this.tasks.addTask(4, new EntityAIShurikenAttack(this, 0.25F, 1, 32)); // shurikens! (2=arrow, 3=snowball, 4=arrowcustom)
        this.tasks.addTask(4, new EntityAIShurikenAttack(this, 0.25F, 60, 10.0F));
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F)); /* lowered priority */
        this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.3F * this.moveSpeed, false));
        this.tasks.addTask(6, new EntityAIWander(this, this.moveSpeed));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(4, new EntityAIHurtByTargetCustom(this, true));
        //this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 16.0F, 0, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    // This is the only version that's needed for penguins (don't need the one with no args, unlike wolves)
    public void applyEntityAttributes(float moveSpeed, float maxHealthTame, float maxHealthWild)
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(moveSpeed);

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealthWild);
    }

/*    public void applyEntityAttributes() // wolf version has no args
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(moveSpeed);

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(MAXHEALTHWILD);
    }
*/

    /**
     * Sets the size of this mob (useful for adjusting penguin's dimensions when it swims)
     */
    protected void setSize2(float width, float height)
    {
    	super.setSize(width, height);
    }

    /* overrides the fn for EntityLiving */
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        if (rand.nextInt(RanCraftPenguins.pengQuietInt) == 0) {
        	return RanCraftPenguins.modID+":penguinNinj.angry";
        } else {
        	return null;
        }
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
    	return RanCraftPenguins.modID+":penguinNinj.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
    	return RanCraftPenguins.modID+":penguinNinj.death";
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    @Override
    public ItemStack getHeldItem()
    {
        return heldItem;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEFINED;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
    	int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);
        /* Only at night, but not in pitch darkness, so generally not underground (except maybe near lava) */
        return worldObj.getFullBlockLightValue(i, j, k) > 3 && !worldObj.isDaytime() && super.getCanSpawnHere();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        super.onDeath(par1DamageSource);
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected Item getDropItem()
    {
        return RanCraftPenguins.PenguinShuriken;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = this.rand.nextInt(8 + 2*par2);
        int var4;

        for (var4 = 0; var4 < var3; ++var4)
        {
            this.dropItem(RanCraftPenguins.PenguinShuriken, 1);
            //this.func_145779_a(RanCraftPenguins.PenguinShuriken, 1);
        }

        var3 = this.rand.nextInt(3);

        if (var3 < 1) /* 1/3 chance to drop */
        {
            this.dropItem(RanCraftPenguins.PenguinKatanaD, 1);
            //this.func_145779_a(RanCraftPenguins.PenguinKatanaD, 1);
        }
    }

    //protected void dropRareDrop(int par1)
    //{
    //    this.dropItem(RanCraftPenguins.PenguinShuriken.itemID, 16);
    //}

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        byte byte0 = (6);
        return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), byte0);
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
	@Override
    public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLiving, float par2)
    {
        EntityPenguinShuriken var2 = new EntityPenguinShuriken(this.worldObj, this, par1EntityLiving, 1.6F, 12.0F);
        //EntityPenguinShuriken var2 = new EntityPenguinShuriken(this.worldObj);
        //RanCraftPenguins.PenguinShuriken.setDamage((double)(par2 * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.worldObj.difficultySetting * 0.11F));
        int var3 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
        int var4 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());

        if (var3 > 0)
        {
            var2.setDamage(var2.getDamage() + (double)var3 * 0.5D + 0.5D);
        }

        if (var4 > 0)
        {
            var2.setKnockbackStrength(var4);
        }

        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(var2);
    }

//	@Override
//	public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase,
//			float f) {
		// TODO Auto-generated method stub
		
//	}

	public float getMoveSpeed() {
		return moveSpeed;
	}
}