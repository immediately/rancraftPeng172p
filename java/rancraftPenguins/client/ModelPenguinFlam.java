package rancraftPenguins.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import org.lwjgl.opengl.GL11;

import rancraftPenguins.EntityPenguinFlam;

// Referenced classes of package net.minecraft.src:
//            ModelBase, ModelRenderer, EntityPenguinFlam, MathHelper, 
//            Entity, EntityLiving

public class ModelPenguinFlam extends ModelBase
{
	private ModelRenderer headmain;
	private ModelRenderer body;
	private ModelRenderer rightwing;
	private ModelRenderer leftwing;
	private ModelRenderer rightleg;
	private ModelRenderer leftleg;
	private ModelRenderer rightfoot;
	private ModelRenderer leftfoot;
	private ModelRenderer beak;

    private float pi = 3.141593F;

	private float bodyY, headX, headY, headZ, wingY, footY, bodyLean;
	private float headUpOffset; // for swimming in lava. Die in water?

    public ModelPenguinFlam()
    {
    	textureWidth = 52;
        textureHeight = 36;

        headX = 1.0F;
        headZ = 1.0F;
        bodyY = 12.0F;
        headY = 3.0F - bodyY;
        footY = 19.0F - bodyY;
        wingY = 7.0F - bodyY;
        bodyLean = 0.0F;

        headmain = new ModelRenderer(this, 11, 1);
    	/* side,vert (neg is up),forward (neg is forward) Second set is dimensions! */
        headmain.addBox(-3F, -4F, -3F, 6, 5, 5);
        headmain.setRotationPoint(headX, headY, headZ);
        beak = new ModelRenderer(this, 34, 5);
        beak.addBox(-2F, -1F, -6F, 4, 1, 3);
        headmain.addChild(beak);
        body = new ModelRenderer(this, 0, 12);
        body.addBox(-3F, -8F, -2F, 8, 17, 6);
        body.setRotationPoint(-1F, bodyY, -1F);
        body.addChild(headmain);
        rightwing = new ModelRenderer(this, 30, 10);
        rightwing.addBox(-1F, -2F, -2F, 2, 12, 4);
        rightwing.setRotationPoint(-4F, wingY, 0F);
        body.addChild(rightwing);
        leftwing = new ModelRenderer(this, 30, 10);
        leftwing.addBox(-1F, -2F, -2F, 2, 12, 4);
        leftwing.setRotationPoint(6F, wingY, 0F);
        body.addChild(leftwing);
        rightleg = new ModelRenderer(this, 1, 5);
        rightleg.addBox(-1F, 1F, -1F, 2, 3, 2); /* side,vert (neg is up),forward (neg is forward) Second set is dimensions! */ 
        rightleg.setRotationPoint(-1F, footY, 1F);
        body.addChild(rightleg);
        leftleg = new ModelRenderer(this, 1, 5);
        leftleg.addBox(-1F, 1F, -1F, 2, 3, 2);
        leftleg.setRotationPoint(2F, footY, 1F);
        body.addChild(leftleg);
        rightfoot = new ModelRenderer(this, 29, 27);
        rightfoot.addBox(-2F, 4F, -5F, 4, 1, 7); /* side,vert (neg is up),forward (neg is forward) Second set is dimensions! */ 
        rightleg.addChild(rightfoot);
        leftfoot = new ModelRenderer(this, 29, 27);
        leftfoot.addBox(-2F, 4F, -5F, 4, 1, 7);
        leftleg.addChild(leftfoot);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        super.render(par1Entity, par2, par3, par4, par5, par6, par7); // <= THIS
        setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
    	body.render(par7);
    }

    private void SetRotation(ModelRenderer model, float x, float y, float z)
    {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
    }

    public void setLivingAnimations(EntityLivingBase entityliving, float f, float f1, float f2)
    {
    	float cosf;
    	float cosHalff;
    	float speedSum;
    	float toePointOffset; // for swimming
    	
        EntityPenguinFlam entitypenguin = (EntityPenguinFlam)entityliving;
    	cosf = MathHelper.cos(f);
    	cosHalff = MathHelper.cos(f/2);
    	speedSum = entitypenguin.getAIMoveSpeed();
        headUpOffset = 0.0F; // head normal
        toePointOffset = 0.0F;

        /* leaving them vertical in lava for now
        if(entitypenguin.handleLavaMovement()){ // swimming, so horizontal and flap
        	body.rotateAngleX = 7 * pi / 16; // almost flat for swimming
            rightwing.rotateAngleX = -1.0F * pi / 2.0F;
            rightwing.rotateAngleY = MathHelper.abs(cosHalff * 1.4F) * pi / 2.5F; // flapping wings
            rightwing.rotateAngleZ = 0.0F;
            headUpOffset = -1.0F * pi / 3.0F; // head up
            // point the toes
            toePointOffset = -1.0F * pi / 4.0F;
        } else { // on land */
            body.rotateAngleX = bodyLean; // leaning forward (LB, YE, and Mag)
	        rightwing.rotateAngleX = 0.0F;
	        if (!entitypenguin.onGround || MathHelper.abs((float)entitypenguin.motionY) > 0.1F) // || entitypenguin.isAirBorne) <= Most of the time!
	        {
	        	rightwing.rotateAngleY = pi / 3.0F;
	        	rightwing.rotateAngleZ = pi / 2.5F;
	        } else { // not jumping (or sitting)
		        if(speedSum < 0.2F){ // standing or slow walk
		        	rightwing.rotateAngleY = 0.0F;
		        	rightwing.rotateAngleZ = 0.0F;
		        } else { // faster walk
		        	rightwing.rotateAngleY = pi / 3.0F;
		        	rightwing.rotateAngleZ = MathHelper.abs(cosf * f1 * 0.8F) + pi / 16.0F;
		        }
	        }
	        leftwing.rotateAngleY = -1.0F * rightwing.rotateAngleY;
	        leftwing.rotateAngleZ = -1.0F * rightwing.rotateAngleZ;
	
	        //float f3 = entitypenguin.getInterestedAngle(f2) + entitypenguin.getShakeAngle(f2, 0.0F);
	        //headmain.rotateAngleZ = f3;
	        //beak.rotateAngleZ = f3;
	        if(entitypenguin.getPenguinShaking())
	        {
	            float f4 = entitypenguin.getBrightness(f2) * entitypenguin.getShadingWhileShaking(f2);
	            GL11.glColor3f(f4, f4, f4);
	        }
        //} /* leaving them vertical in lava for now */
     	rightleg.rotateAngleX = MathHelper.cos(f * 3.0F + pi) * 1.4F * f1 - bodyLean - toePointOffset;
    	leftleg.rotateAngleX = MathHelper.cos(f * 3.0F) * 1.4F * f1 - bodyLean - toePointOffset;

    	leftwing.rotateAngleX = rightwing.rotateAngleX;
	    leftwing.rotateAngleY = -1.0F * rightwing.rotateAngleY;
        leftwing.rotateAngleZ = -1.0F * rightwing.rotateAngleZ;

        body.setRotationPoint(0F, bodyY, 0F);

        float f3 = entitypenguin.getInterestedAngle(f2) + entitypenguin.getShakeAngle(f2, 0.0F);
        headmain.rotateAngleZ = f3;
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
        headmain.rotateAngleX = par5 / 57.29578F - bodyLean + headUpOffset;
        headmain.rotateAngleY = par4 / 57.29578F;
    }
}
