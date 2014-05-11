package rancraftPenguins.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import rancraftPenguins.EntityPenguin;
import rancraftPenguins.EntityPenguinNinj;
import rancraftPenguins.RanCraftPenguins;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPenguinNinj extends RenderLiving /* if it extends RenderPenguin, super calls will call that, which we don't want */
{
	private static final String penguinTextureLoc = RanCraftPenguins.modID + ":" + "textures/models/";
    ResourceLocation skinPeng = new ResourceLocation(penguinTextureLoc + "pengNinj.png");

    public RenderPenguinNinj(ModelBase par1ModelBase, float par2)
    {
        super(par1ModelBase, par2);
        //ResourceLocation skinPeng, skinPengAngry, skinPengTame, skinPengChild, skinPengChildTame;
        //skinPeng = new ResourceLocation(penguinTextureLoc + "pengNinj.png");
    }

    public void renderPenguinNinj(EntityPenguinNinj par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRender(par1Entity, par2, par4, par6, par8, par9);
    }

    protected void renderEquippedItems(EntityLivingBase par1EntityLiving, float par2)
    {
        super.renderEquippedItems(par1EntityLiving, par2);
        ItemStack itemstack = par1EntityLiving.getHeldItem();
    	float speedSum;
    	boolean isSwimming;
    	float f4;

		//speedSum = MathHelper.abs((float) par1EntityLiving.motionX) + MathHelper.abs((float) par1EntityLiving.motionZ);
    	speedSum = par1EntityLiving.getAIMoveSpeed();
    	isSwimming = par1EntityLiving.isInWater();

		if (!isSwimming && itemstack != null) // no katana in the water (they render weirdly on back or on wing flipper)
        {
            GL11.glPushMatrix();
            f4 = 0.400F; // smaller for ninja penguin than for player
            GL11.glScalef(f4, -f4, f4);

	        if(speedSum < 0.2F){ // CHANGE TO < 0.2 after debugging in water, standing, or slow walk, katana sheathed
	            /* This one is good for sheath */
	            ModelPenguinNinj.body.postRender(0.0425F);
	            ((rancraftPenguins.ItemPenguinKatana) RanCraftPenguins.PenguinKatanaS).setIconIndex(0); // sheathed
	            GL11.glTranslatef(-0.5F, -1.6F, 0.4F); // moves to a certain location relative to the postRender argument above
                GL11.glRotatef(200.0F, 45.0F, 0.0F, 90.0F); // rotate image, rotate clockwise on penguin top-down view, rotate on own axis, top-down view , ?
                // apparently these values for isX and isZ have meaning. Changing them to 1 messes up the angles.
	        } else { // walking faster, katana drawn
	        	ModelPenguinNinj.rightwing.postRender(0.0425F);
	            ((rancraftPenguins.ItemPenguinKatana) RanCraftPenguins.PenguinKatanaS).setIconIndex(1); // drawn
	            /* for held sword */
	            GL11.glTranslatef(-1.0F, -0.9F, -1.9F); // (f/b, grip-handle/grip-blade, l/r)
	            // x, y and z can be rotated individually
                GL11.glRotatef(-90F, 0F, 0F, 1F); // angle, isX, isY, isZ (Z is as if turning wrist)
                GL11.glRotatef(65F, 0F, 1F, 0F); // angle, isX, isY, isZ (Y points sword higher or lower)
	            GL11.glRotatef(-90F, 1F, 0F, 0F); // angle, isX, isY, isZ  (X is as if turning wrist too)
	        }
            GL11.glScalef(1.0F, 1.0F, 1.0F); // this can flip the Katana image from icon to ninja penguin's back

            renderManager.itemRenderer.renderItem(par1EntityLiving, itemstack, 0);

            /* if (itemstack.getItem().func_46058_c()) original from 1.2.5 */
            if (itemstack.getItem().shouldRotateAroundWhenRendering())
            {
                renderManager.itemRenderer.renderItem(par1EntityLiving, itemstack, 1);
            }

            GL11.glPopMatrix();
        }
    }

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
	       float f2 = 1.0F;
	        GL11.glColor3f(f2, f2, f2);
	        //ItemStack itemstack = par1EntityLiving.getHeldItem();
	        //this.func_82420_a(par1EntityLiving, itemstack);
	        double d3 = par4 - (double)par1EntityLiving.yOffset;

	        super.doRender(par1EntityLiving, par2, d3, par6, par8, par9);
	        //this.field_82423_g.heldItemRight = this.field_82425_h.heldItemRight = this.modelBipedMain.heldItemRight = 0;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.func_110914_a((EntityPenguinNinj)par1Entity);
    }

    protected ResourceLocation func_110914_a(EntityPenguinNinj par1EntityPenguinNinj)
    {
        return skinPeng;
    }

}
