package rancraftPenguins;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityPenguinShuriken extends EntityArrow
{
    private double damage = 0.5D;
    private int ticksInAir = 0;
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private Block field_145790_g;
    private int inTile = 0;
    private int inData = 0;
    private boolean inGround = false;
    private int ticksInGround;

    /** 1 if the player can pick up the shuriken */
    public int canBePickedUp = 0;

    /** The amount of knockback a shuriken applies when it hits a mob. */
    private int knockbackStrength;

    public EntityPenguinShuriken(World par1World)
    {
        super(par1World);
        this.renderDistanceWeight = 10.0D;
        this.setSize(0.3F, 0.3F);
    }

    // constructor for ninja
    public EntityPenguinShuriken(World par1World, EntityLivingBase par2EntityLiving, EntityLivingBase par1EntityLiving, float par4, float par5)
    {
        super(par1World);
        this.renderDistanceWeight = 10.0D;
        this.shootingEntity = par2EntityLiving;

        if (par2EntityLiving instanceof EntityPlayer)
        {
            this.canBePickedUp = 1;
        }

        this.posY = par2EntityLiving.posY + (double)par2EntityLiving.getEyeHeight() - 0.10000000149011612D;
        double d0 = par1EntityLiving.posX - par2EntityLiving.posX;
        double d1 = par1EntityLiving.boundingBox.minY + (double)(par1EntityLiving.height / 3.0F) - this.posY;
        double d2 = par1EntityLiving.posZ - par2EntityLiving.posZ;
        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);

        if (d3 >= 1.0E-7D)
        {
            float f2 = (float)(Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
            float f3 = (float)(-(Math.atan2(d1, d3) * 180.0D / Math.PI));
            double d4 = d0 / d3;
            double d5 = d2 / d3;
            this.setLocationAndAngles(par2EntityLiving.posX + d4, this.posY, par2EntityLiving.posZ + d5, f2, f3);
            this.yOffset = 0.0F;
            float f4 = (float)d3 * 0.2F;
            this.setThrowableHeading(d0, d1 + (double)f4, d2, par4, par5);
        }
    }

    // constructor for player
    public EntityPenguinShuriken(World par1World, EntityLivingBase par2EntityLiving, float par3)
    {
        super(par1World, par2EntityLiving, par3);
        this.renderDistanceWeight = 10.0D;
        this.shootingEntity = par2EntityLiving;

        if (par2EntityLiving instanceof EntityPlayer)
        {
            this.canBePickedUp = 1;
        }

        this.setSize(0.1F, 0.1F); // how far you can pick them up from
        this.setLocationAndAngles(par2EntityLiving.posX, par2EntityLiving.posY + (double)par2EntityLiving.getEyeHeight(), par2EntityLiving.posZ, par2EntityLiving.rotationYaw, par2EntityLiving.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, par3 * 1.5F, 1.0F);
    }

    /**
     * Called to update the entity's position/logic.
     * Overriding this massive fn for just one stupid little sound effect
     */
    public void onUpdate()
    {
        //super.onUpdate();

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f) * 180.0D / Math.PI);
        }

        Block block = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);

        if (block.getMaterial() != Material.air)
        {
            block.setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);

            if (axisalignedbb != null && axisalignedbb.isVecInside(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0)
        {
            --this.arrowShake;
        }

        if (this.inGround)
        {
            //int j = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
            //int k = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
            int j = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);

            //if (j == this.inTile && k == this.inData)
            if (block == this.field_145790_g && j == this.inData)
            {
                ++this.ticksInGround;

                if (this.ticksInGround == 800)
                {
                    this.setDead();
                }
            }
            else
            {
                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        }
        else
        {
            ++this.ticksInAir;
            Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
            Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec3, vec31, false, true, false);
            vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
            vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (movingobjectposition != null)
            {
                vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            }

            Entity entity = null;
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            int l;
            float f1;

            for (l = 0; l < list.size(); ++l)
            {
                Entity entity1 = (Entity)list.get(l);

                if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksInAir >= 5))
                {
                    f1 = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double)f1, (double)f1, (double)f1);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec3, vec31);

                    if (movingobjectposition1 != null)
                    {
                        double d1 = vec3.distanceTo(movingobjectposition1.hitVec);

                        if (d1 < d0 || d0 == 0.0D)
                        {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null)
            {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            if (movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)movingobjectposition.entityHit;

                if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer))
                {
                    movingobjectposition = null;
                }
            }

            float f2;
            float f3;

            if (movingobjectposition != null)
            {
                if (movingobjectposition.entityHit != null)
                {
                    f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    int i1 = MathHelper.ceiling_double_int((double)f2 * this.damage);

                    if (this.getIsCritical())
                    {
                        i1 += this.rand.nextInt(i1 / 2 + 2);
                    }

                    DamageSource damagesource = null;

                    if (this.shootingEntity == null)
                    {
                        damagesource = DamageSource.causeArrowDamage(this, this);
                    }
                    else
                    {
                        damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
                    }

                    if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman))
                    {
                        movingobjectposition.entityHit.setFire(5);
                    }

                    if (movingobjectposition.entityHit.attackEntityFrom(damagesource, i1))
                    {
                        if (movingobjectposition.entityHit instanceof EntityLiving)
                        {
                            EntityLiving entityliving = (EntityLiving)movingobjectposition.entityHit;

                            if (!this.worldObj.isRemote)
                            {
                                entityliving.setArrowCountInEntity(entityliving.getArrowCountInEntity() + 1);
                            }

                            if (this.knockbackStrength > 0)
                            {
                                f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

                                if (f3 > 0.0F)
                                {
                                    movingobjectposition.entityHit.addVelocity(this.motionX * (double)this.knockbackStrength * 0.6000000238418579D / (double)f3, 0.1D, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D / (double)f3);
                                }
                            }

                            if (this.shootingEntity != null)
                            {
                                //EnchantmentThorns.func_92096_a(this.shootingEntity, entityliving, this.rand);
                                EnchantmentHelper.func_151384_a(entityliving, this.shootingEntity);
                                EnchantmentHelper.func_151385_b((EntityLivingBase)this.shootingEntity, entityliving);
                            }

                            if (this.shootingEntity != null && movingobjectposition.entityHit != this.shootingEntity && movingobjectposition.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP)
                            {
                                //((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(6, 0));
                                ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                            }
                        }

                        this.playSound("step.stone", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                        if (!(movingobjectposition.entityHit instanceof EntityEnderman))
                        {
                            this.setDead();
                        }
                    }
                    else
                    {
                        this.motionX *= -0.10000000149011612D;
                        this.motionY *= -0.10000000149011612D;
                        this.motionZ *= -0.10000000149011612D;
                        this.rotationYaw += 180.0F;
                        this.prevRotationYaw += 180.0F;
                        this.ticksInAir = 0;
                    }
                }
                else
                {
                    this.xTile = movingobjectposition.blockX;
                    this.yTile = movingobjectposition.blockY;
                    this.zTile = movingobjectposition.blockZ;
                    this.field_145790_g = block;
                    this.inTile = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                    this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                    this.motionX = (double)((float)(movingobjectposition.hitVec.xCoord - this.posX));
                    this.motionY = (double)((float)(movingobjectposition.hitVec.yCoord - this.posY));
                    this.motionZ = (double)((float)(movingobjectposition.hitVec.zCoord - this.posZ));
                    f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
                    this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
                    this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
                    this.playSound("step.stone", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.arrowShake = 7;
                    this.setIsCritical(false);

                    //if (this.inTile != 0)
                    //{
                    //    Block.blocksList[this.inTile].onEntityCollidedWithBlock(this.worldObj, this.xTile, this.yTile, this.zTile, this);
                    //}
                    if (this.field_145790_g.getMaterial() != Material.air)
                    {
                        this.field_145790_g.onEntityCollidedWithBlock(this.worldObj, this.xTile, this.yTile, this.zTile, this);
                    }
                }
            }

            if (this.getIsCritical())
            {
                for (l = 0; l < 4; ++l)
                {
                    this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)l / 4.0D, this.posY + this.motionY * (double)l / 4.0D, this.posZ + this.motionZ * (double)l / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

            for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f2) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
            {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float f4 = 0.99F;
            f1 = 0.05F;

            if (this.isInWater())
            {
                for (int j1 = 0; j1 < 4; ++j1)
                {
                    f3 = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f3, this.posY - this.motionY * (double)f3, this.posZ - this.motionZ * (double)f3, this.motionX, this.motionY, this.motionZ);
                }

                f4 = 0.8F;
            }

            this.motionX *= (double)f4;
            this.motionY *= (double)f4;
            this.motionZ *= (double)f4;
            this.motionY -= (double)f1;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.func_145775_I();
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("xTile", (short)this.xTile);
        par1NBTTagCompound.setShort("yTile", (short)this.yTile);
        par1NBTTagCompound.setShort("zTile", (short)this.zTile);
        par1NBTTagCompound.setShort("life", (short)this.ticksInGround);
        par1NBTTagCompound.setByte("inTile", (byte)Block.getIdFromBlock(this.field_145790_g));
        par1NBTTagCompound.setByte("inData", (byte)this.inData);
        par1NBTTagCompound.setByte("shake", (byte)this.arrowShake);
        par1NBTTagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        par1NBTTagCompound.setByte("pickup", (byte)this.canBePickedUp);
        par1NBTTagCompound.setDouble("damage", this.damage);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.xTile = par1NBTTagCompound.getShort("xTile");
        this.yTile = par1NBTTagCompound.getShort("yTile");
        this.zTile = par1NBTTagCompound.getShort("zTile");
        this.ticksInGround = par1NBTTagCompound.getShort("life");
        this.field_145790_g = Block.getBlockById(par1NBTTagCompound.getByte("inTile") & 255);
        this.inData = par1NBTTagCompound.getByte("inData") & 255;
        this.arrowShake = par1NBTTagCompound.getByte("shake") & 255;
        this.inGround = par1NBTTagCompound.getByte("inGround") == 1;

        if (par1NBTTagCompound.hasKey("damage", 99))
        {
            this.damage = par1NBTTagCompound.getDouble("damage");
        }

        if (par1NBTTagCompound.hasKey("pickup", 99))
        {
            this.canBePickedUp = par1NBTTagCompound.getByte("pickup");
        }
        else if (par1NBTTagCompound.hasKey("player", 99))
        {
            this.canBePickedUp = par1NBTTagCompound.getBoolean("player") ? 1 : 0;
        }
    }

	/**
	 * Called by a player entity when they collide with an entity
	 */
	public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
		if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
			//boolean var2 = ((this.canBePickedUp == 1) && !(par1EntityPlayer.capabilities.isCreativeMode));
			// boolean var2 = this.canBePickedUp == 1 || this.canBePickedUp == 2
			// && par1EntityPlayer.capabilities.isCreativeMode;
			// System.out.printf("From Client: thiscanbepickedup is %d\n",
			// this.canBePickedUp);

			// if (this.canBePickedUp == 1 &&
			// !par1EntityPlayer.inventory.addItemStackToInventory(new
			// ItemStack(RanCraftPenguins.PenguinShuriken, 1)))
			// {
			// var2 = false;
			// }

			if (this.canBePickedUp == 1) {
				if(!(par1EntityPlayer.capabilities.isCreativeMode)){
					par1EntityPlayer.inventory
							.addItemStackToInventory(new ItemStack(
									RanCraftPenguins.PenguinShuriken, 1));
					par1EntityPlayer.onItemPickup(this, 1);
				}
				this.playSound("random.pop",0.2F,((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				this.setDead(); // disappears regardless of whether we're on creative 
			}
		}
	}
}
