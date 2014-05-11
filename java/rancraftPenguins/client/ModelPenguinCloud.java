package rancraftPenguins.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import rancraftPenguins.EntityPenguinCloud;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

// Referenced classes of package net.minecraft.src:
//            ModelBase, ModelRenderer, EntityPenguinCloud, MathHelper, 
//            Entity, EntityLiving

public class ModelPenguinCloud extends ModelBase
{
    private ModelRenderer headmain;
    private ModelRenderer crest1;
    private ModelRenderer crest2;
    private ModelRenderer bodylower;
    private ModelRenderer bodyupper;
    private ModelRenderer tail;
    private ModelRenderer rightwing;
    private ModelRenderer leftwing;
    private ModelRenderer rightfoot;
    private ModelRenderer leftfoot;
    private ModelRenderer beak;

    private float pi = 3.141593F;
	private float headX, headY, headZ, bodyupperY, bodylean;
	private float crest1X, crest1Y, crest1Z, crest2X, crest2Y, crest2Z;

    public ModelPenguinCloud()
    {
        textureWidth = 38;
        textureHeight = 28;
        headX = 0F;
        headY = 15F;
        headZ = 0F;
        bodyupperY = 19F;
        crest1X = 0.25F;
        crest1Y = -0.22F;
        crest1Z = -0.32F;
        crest2X = 0.25F;
        crest2Y = 0.22F;
        crest2Z = 0.32F;
        bodylean = pi / 12;

        headmain = new ModelRenderer(this, 0, 0);
    	/* side,vert (neg is up),forward (neg is forward) Second set is dimensions! */ 
        headmain.addBox(-2F, -3F, -1F, 4, 3, 3);
        headmain.setRotationPoint(headX, headY - bodyupperY, headZ);
        setRotation(headmain, 0F, 0F, 0F);
        beak = new ModelRenderer(this, 15, 0);
        beak.addBox(-1F, -1F, -3F, 2, 1, 2);
        headmain.addChild(beak);
        crest1 = new ModelRenderer(this, 15, 12);
        crest1.addBox(-0.5F, -3.3F, 1.3F, 1, 1, 2);
        setRotation(crest1, crest1X, crest1Y, crest1Z);
        headmain.addChild(crest1);
        crest2 = new ModelRenderer(this, 15, 12);
        crest2.addBox(-0.5F, -3.3F, 1.3F, 1, 1, 2);
        setRotation(crest2, crest2X, crest2Y, crest2Z);
        headmain.addChild(crest2);
        bodyupper = new ModelRenderer(this, 15, 4);
        bodyupper.addBox(-2F, -4F, -1F, 4, 4, 3);
        bodyupper.setRotationPoint(0F, bodyupperY, 0F);
        //setRotation(bodyupper, 0F, 0F, 0F);
        bodyupper.addChild(headmain);
        bodylower = new ModelRenderer(this, 0, 7);
        bodylower.addBox(-2F, -1F, -1F, 4, 5, 3);
        bodylower.setRotationPoint(0F, 19F - bodyupperY, 0F);
        //setRotation(bodylower, 0F, 0F, 0F);
        bodyupper.addChild(bodylower);
        tail = new ModelRenderer(this, 18, 16);
        tail.addBox(-1F, -1F, -1F, 4, 1, 3);
        tail.setRotationPoint(-1F, 23F - bodyupperY, 2F);
        setRotation(tail, -0.5F, 0F, 0F);
        bodyupper.addChild(tail);
        rightwing = new ModelRenderer(this, 9, 16);
        rightwing.addBox(-1F, -1F, -1F, 1, 7, 3);
        rightwing.setRotationPoint(-2F, 16.5F - bodyupperY, -0.4F);
        setRotation(rightwing, 0.037F, 0F, 0F);
        bodyupper.addChild(rightwing);
        leftwing = new ModelRenderer(this, 0, 16);
        leftwing.addBox(0F, -1F, -1F, 1, 7, 3);
        leftwing.setRotationPoint(2F, 16.5F - bodyupperY, -0.4F);
        setRotation(leftwing, 0.037F, 0F, 0F);
        bodyupper.addChild(leftwing);
        rightfoot = new ModelRenderer(this, 18, 22);
        rightfoot.addBox(-1F, 0F, -3F, 2, 1, 4);
        rightfoot.setRotationPoint(-1F, 23F - bodyupperY, 0F);
        bodyupper.addChild(rightfoot);
        leftfoot = new ModelRenderer(this, 18, 22);
        leftfoot.addBox(-1F, 0F, -3F, 2, 1, 4);
        leftfoot.setRotationPoint(1F, 23F - bodyupperY, 0F);
        bodyupper.addChild(leftfoot);
    }

    //public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
		//System.out.printf("  Setting angles : %f\n", headmain.rotateAngleY);
        setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        if (isChild)
        {
            float f = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f, 1.0F / f, 1.0F / f);
            GL11.glTranslatef(0.0F, 24F * par7, 0.0F);
            bodyupper.render(par7);
            GL11.glPopMatrix();
        }
        else
        {
            bodyupper.render(par7);
        }
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
    }

    public void setLivingAnimations(EntityLivingBase entityliving, float f, float f1, float f2)
    {
    	float cosf;
    	float cos2f;
    	float cosHalff;
    	float speedSum;
    	float invFlapSpeed;
    	
        EntityPenguinCloud entitypenguin = (EntityPenguinCloud)entityliving;
    	cosf = MathHelper.cos(f);
    	cos2f = MathHelper.cos(2*f);
    	cosHalff = MathHelper.cos(f/2);
    	speedSum = entitypenguin.getAIMoveSpeed();
		//speedSum = MathHelper.abs((float) entitypenguin.motionX) + MathHelper.abs((float) entitypenguin.motionZ);
		//System.out.printf("  SpeedSum : %f\n", speedSum);
    	if((!entitypenguin.isCollidedVertically || entitypenguin.getMoveSpeed() > 0.3)
    			&& !entitypenguin.isInWater() ){
    		entitypenguin.setTimeAloft(entitypenguin.getTimeAloft() + 1);
    		//System.out.printf("  timeAloft: %d\n", entitypenguin.timeAloft);
    	} else {
    		entitypenguin.setTimeAloft(0);
    		//System.out.printf("  Setting timeAloft to zero: %d\n", entitypenguin.timeAloft);
    		//System.out.printf("  collidedVert: %b, ydelta: %f => %f\n", entitypenguin.isCollidedVertically, entitypenguin.posY, entitypenguin.lastTickPosY);
    	}

        if(entitypenguin.getTimeAloft() > 20 || entitypenguin.isInWater()){ // flying or swimming, so horizontal and flap
    		bodyupper.rotateAngleX = 7 * pi / 16; // almost flat for flying
	        rightwing.rotateAngleX = -1.0F * pi / 2.0F;
	        if(entitypenguin.isInWater()){
	        	rightwing.rotateAngleY = MathHelper.abs(cosHalff * 1.4F) * pi / 2.5F; // flapping wings
	        } else {
	        	rightwing.rotateAngleY = MathHelper.abs(cos2f * 1.4F) * pi / 2.0F; // flapping wings faster
	        }
	        rightwing.rotateAngleZ = 0.0F;
	        // point the toes
	        rightfoot.rotateAngleX = leftfoot.rotateAngleX = pi / 3.0F;
        } else { // on land
    		bodyupper.rotateAngleX = bodylean; // slight lean <= standing is as upright as walking
    		headmain.rotateAngleX = 0 - bodylean; // counter the lean
	        if(speedSum < 0.1F){ // standing
		        rightwing.rotateAngleX = 0.0F;
		        rightwing.rotateAngleY = 0.0F;
		        rightwing.rotateAngleZ = pi / 32.0F; // slightly out
	
		        rightfoot.rotateAngleX = 0 - bodylean; // counter the lean
	    		leftfoot.rotateAngleX = 0 - bodylean; // counter the lean
	        } else { // walking at least slowly
		        rightwing.rotateAngleX = 0.0F;
		        rightwing.rotateAngleY = pi / 3.0F;
		        rightwing.rotateAngleZ = MathHelper.abs(cosf * f1 * 0.2F) + pi / 3.5F;
	
		        rightfoot.rotateAngleX = MathHelper.cos(f * 2.6662F + pi) * 1.4F * f1 - bodylean;
            	leftfoot.rotateAngleX = MathHelper.cos(f * 2.6662F) * 1.4F * f1 - bodylean;
	        }
        }
        leftwing.rotateAngleX = rightwing.rotateAngleX;
        leftwing.rotateAngleY = -1.0F * rightwing.rotateAngleY;
        leftwing.rotateAngleZ = -1.0F * rightwing.rotateAngleZ;

        bodyupper.setRotationPoint(0F, 19F, 0F);

        float f3 = entitypenguin.getInterestedAngle(f2) + entitypenguin.getShakeAngle(f2, 0.0F);
        headmain.rotateAngleZ = f3;

        if(entitypenguin.getPenguinShaking())
        {
            float f4 = entitypenguin.getBrightness(f2) * entitypenguin.getShadingWhileShaking(f2);
            GL11.glColor3f(f4, f4, f4);
        }
		//System.out.printf("  headAngle2 : %f\n", headmain.rotateAngleY);
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
        headmain.rotateAngleX = par5 / 57.29578F - bodylean;
        headmain.rotateAngleY = par4 / 57.29578F;
    }
}
