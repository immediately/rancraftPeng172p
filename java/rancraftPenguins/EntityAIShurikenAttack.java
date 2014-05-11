package rancraftPenguins;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIShurikenAttack extends EntityAIBase
{
    /** The entity the AI instance has been applied to */
    private final EntityLiving entityHost;

    /**
     * The entity (as a RangedAttackMob) the AI instance has been applied to.
     */
    private final IRangedAttackMob rangedAttackEntityHost;
    private EntityLivingBase attackTarget;

    /**
     * A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
     * maxRangedAttackTime.
     */
    private int rangedAttackTime = 0;
    private float entityMoveSpeed;
    private int field_75318_f = 0;

    /**
     * The maximum time the AI has to wait before performing another ranged attack.
     */
    private int maxRangedAttackTime;
    private float field_82642_h;

    public EntityAIShurikenAttack(IRangedAttackMob par1IRangedAttackMob, float par2, int par3, float par4)
    {
        if (!(par1IRangedAttackMob instanceof EntityLiving))
        {
            throw new IllegalArgumentException("ShurikenAttackGoal requires Mob implements RangedAttackMob");
        }
        else
        {
            this.rangedAttackEntityHost = par1IRangedAttackMob;
            this.entityHost = (EntityLiving)par1IRangedAttackMob;
            this.entityMoveSpeed = par2;
            this.maxRangedAttackTime = par3;
            this.field_82642_h = par4 * par4;
            this.rangedAttackTime = par3 / 2;
            this.setMutexBits(3);
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase var1 = this.entityHost.getAttackTarget();

        if (var1 == null)
        {
            return false;
        }
        else
        {
            this.attackTarget = var1;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.attackTarget = null;
        this.field_75318_f = 0;
        this.rangedAttackTime = this.maxRangedAttackTime / 2;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        double var0 = 25.0D;
        double var1 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        boolean var3 = this.entityHost.getEntitySenses().canSee(this.attackTarget);

        if (var3)
        {
            ++this.field_75318_f;
        }
        else
        {
            this.field_75318_f = 0;
        }
        
        if (var1 <= var0) /* distance^2 less than 25 */
        {
            //this.attackTarget = null;
            this.entityHost.getNavigator().clearPathEntity(); /* stop attacking to make room for leap attack? */
            //this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }
        else if (var1 <= (double)this.field_82642_h && this.field_75318_f >= 20)
        {
            this.entityHost.getNavigator().clearPathEntity();
        }
        else /* can see target or been attacking less than 20 */
        {
            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }

        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
        this.rangedAttackTime = Math.max(this.rangedAttackTime - 1, 0);

        if (this.rangedAttackTime <= 0) /* interval finished -- can take another shot */
        {
            if (var1 <= (double)this.field_82642_h && var3) /* still in range, can still see target */
            {
                //OLD: this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget);
                this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, entityMoveSpeed);
            	//this.doRangedAttack();
                this.rangedAttackTime = this.maxRangedAttackTime;
            }
        }
    }
}
