package rancraftPenguins.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import rancraftPenguins.EntityPenguinAfr;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelPenguinAfr extends ModelBase
{
    private ModelRenderer headmain;
    private ModelRenderer beak;
    private ModelRenderer body;
    private ModelRenderer tail;
    private ModelRenderer rightwing;
    private ModelRenderer leftwing;
    private ModelRenderer rightleg;
    private ModelRenderer leftleg;
    private ModelRenderer rightfoot;
    private ModelRenderer leftfoot;

    private float pi = 3.141593F;

	private float bodyY, headX, headY, headZ, wingY, footY, tailY, bodyLean;
	private float headUpOffset; // for swimming

	public ModelPenguinAfr()
    {
    	textureWidth = 41;
        textureHeight = 35;
        headX = 0.0F;
        headZ = 0.0F;
        bodyY = 11.0F;
        headY = 11.0F - bodyY;
        footY = 20.0F - bodyY;
        wingY = 14.0F - bodyY;
        tailY = 20.0F - bodyY;
        bodyLean = pi / 12.0F;

        headmain = new ModelRenderer(this, 0, 0);
    	/* side,vert (neg is up),forward (neg is forward) Second set is dimensions! */ 
        headmain.addBox(-3F, -3F, -2F, 5, 4, 4);
        headmain.setRotationPoint(headX, headY, headZ);
        beak = new ModelRenderer(this, 23, 0);
        beak.addBox(-2F, -1F, -5F, 3, 1, 3);
        headmain.addChild(beak);
        body = new ModelRenderer(this, 0, 11);
        body.addBox(-3F, 1F, -2F, 5, 10, 4);
        body.setRotationPoint(0F, bodyY, 0F);
        body.addChild(headmain);
        tail = new ModelRenderer(this, 15, 28);
        tail.addBox(-2F, -1F, -1F, 3, 2, 5);
        tail.setRotationPoint(0F, tailY, 2F);
        //setRotation(tail, 0 - (pi / 10.0F), 0F, 0F);
        setRotation(tail, -0.1487F, 0F, 0F);
        body.addChild(tail);
        rightwing = new ModelRenderer(this, 32, 12);
        rightwing.addBox(-1F, -2F, -2F, 1, 7, 3);
        rightwing.setRotationPoint(-3F, wingY, 1F);
        body.addChild(rightwing);
        leftwing = new ModelRenderer(this, 18, 12);
        leftwing.addBox(0F, -2F, -2F, 1, 7, 3);
        leftwing.setRotationPoint(2F, wingY, 1F);
        body.addChild(leftwing);
        rightleg = new ModelRenderer(this, 23, 6);
        rightleg.addBox(-1F, 1F, -1F, 2, 2, 2);
        rightleg.setRotationPoint(-2F, footY, 1F);
        body.addChild(rightleg);
        leftleg = new ModelRenderer(this, 23, 6);
        leftleg.addBox(-1F, 1F, -1F, 2, 2, 2);
        leftleg.setRotationPoint(1F, footY, 1F);
        body.addChild(leftleg);
        rightfoot = new ModelRenderer(this, 0, 28);
        rightfoot.addBox(-1F, 3F, -3F, 2, 1, 5);
        rightleg.addChild(rightfoot);
        leftfoot = new ModelRenderer(this, 0, 28);
        leftfoot.addBox(-1F, 3F, -3F, 2, 1, 5);
        leftleg.addChild(leftfoot);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        if (this.isChild)
        {
            float f = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f, 1.0F / f, 1.0F / f);
            GL11.glTranslatef(0.0F, 24F * par7, 0.0F);
            body.render(par7);
            GL11.glPopMatrix();
        }
        else
        {
        	body.render(par7);
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
    	int sittingOffset;
    	float cosf;
    	float cosHalff;
    	float speedSum;
    	float toePointOffset; // for swimming
    	
        EntityPenguinAfr entitypenguin = (EntityPenguinAfr)entityliving;
    	cosf = MathHelper.cos(f);
    	cosHalff = MathHelper.cos(f/2);
    	speedSum = entitypenguin.getAIMoveSpeed();
      	sittingOffset = 0;
        headUpOffset = 0.0F; // head normal
        toePointOffset = 0.0F;

		if (entitypenguin.isSitting()) // sitting
        {
        	sittingOffset = 2;
            body.rotateAngleX = 0.0F; // not leaning forward (LB, YE, and Mag)
        	rightleg.rotateAngleX = pi * 1.5F;
        	leftleg.rotateAngleX = pi * 1.5F;
	        rightwing.rotateAngleX = 0.0F;
	        rightwing.rotateAngleY = 0.0F;
	        rightwing.rotateAngleZ = 0.0F;
         } else { // not sitting
        	sittingOffset = 0;
            if(entitypenguin.isInWater()){ // swimming, so horizontal and flap
            	body.rotateAngleX = 7 * pi / 16; // almost flat for swimming
                rightwing.rotateAngleX = -1.0F * pi / 2.0F;
                rightwing.rotateAngleY = MathHelper.abs(cosHalff * 1.4F) * pi / 2.5F; // flapping wings
                rightwing.rotateAngleZ = 0.0F;
                headUpOffset = -1.0F * pi / 3.0F; // head up
                // point the toes
                toePointOffset = -1.0F * pi / 4.0F;
            } else { // on land
                body.rotateAngleX = bodyLean; // leaning forward (LB, YE, and Mag)
		        rightwing.rotateAngleX = 0.0F;
		        if (!entitypenguin.onGround || MathHelper.abs((float)entitypenguin.motionY) > 0.1F) // || entitypenguin.isAirBorne) <= Most of the time!
		        {
			        rightwing.rotateAngleY = pi / 3.0F;
			        rightwing.rotateAngleZ = pi / 2.5F;
		        } else { // not jumping (or sitting)
			        if(speedSum < 0.2F){ // standing or slow walk
				        rightwing.rotateAngleY = 0.0F;
				        rightwing.rotateAngleZ = MathHelper.abs(cosf * f1 * 0.2F) + pi / 32.0F;
			        } else { // faster walk
				        rightwing.rotateAngleY = pi / 3.0F;
				        rightwing.rotateAngleZ = MathHelper.abs(cosf * f1 * 0.2F) + pi / 4.0F;
			        }
		        }
            }
         	rightleg.rotateAngleX = MathHelper.cos(f * 3.0F + pi) * 1.4F * f1 - bodyLean - toePointOffset;
        	leftleg.rotateAngleX = MathHelper.cos(f * 3.0F) * 1.4F * f1 - bodyLean - toePointOffset;
        }
	    leftwing.rotateAngleX = rightwing.rotateAngleX;
	    leftwing.rotateAngleY = -1.0F * rightwing.rotateAngleY;
        leftwing.rotateAngleZ = -1.0F * rightwing.rotateAngleZ;

        body.setRotationPoint(0F, bodyY + sittingOffset, 0F);

        float f3 = entitypenguin.getInterestedAngle(f2) + entitypenguin.getShakeAngle(f2, 0.0F);
        headmain.rotateAngleZ = f3;

        if(entitypenguin.getPenguinShaking())
        {
            float f4 = entitypenguin.getBrightness(f2) * entitypenguin.getShadingWhileShaking(f2);
            GL11.glColor3f(f4, f4, f4);
        }
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
        headmain.rotateAngleX = par5 / 57.29578F - bodyLean + headUpOffset;
        headmain.rotateAngleY = par4 / 57.29578F;
    }
}
