package rancraftPenguins.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import rancraftPenguins.EntityPenguin;
import rancraftPenguins.RanCraftPenguins;

@SideOnly(Side.CLIENT)
public class RenderPenguin extends RenderLiving
{
	private static final String penguinTextureLoc = RanCraftPenguins.modID + ":" + "textures/models/";
    //ResourceLocation skinPeng = new ResourceLocation(penguinTextureLoc + "pengEmp.png");
    ResourceLocation skinPeng, skinPengAngry, skinPengTame, skinPengChild, skinPengChildTame;

    public RenderPenguin(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3, String par4SpeciesName)
    {
        super(par1ModelBase, par3);
        this.setRenderPassModel(par2ModelBase);
        skinPeng = new ResourceLocation(penguinTextureLoc + "peng" + par4SpeciesName + ".png");
        skinPengAngry = new ResourceLocation(penguinTextureLoc + "peng" + par4SpeciesName + "_angry.png");
        skinPengTame = new ResourceLocation(penguinTextureLoc + "peng" + par4SpeciesName + "_tame.png");
        skinPengChild = new ResourceLocation(penguinTextureLoc + "peng" + par4SpeciesName + "Ch.png");
        skinPengChildTame = new ResourceLocation(penguinTextureLoc + "peng" + par4SpeciesName + "Ch_tame.png");
		//System.out.printf("Texture path: %s\n", penguinTextureLoc + "peng" + par4SpeciesName + ".png");
    }

	protected ResourceLocation func_110914_a(EntityPenguin par1Entity)
    {
        ResourceLocation retVal;
        
        if(par1Entity.isChild()){
        	retVal = skinPengChild;
        } else { // adult
        	retVal = skinPeng;
        }
        return par1Entity.isTamed() ? skinPengTame : (par1Entity.isAngry() ? skinPengAngry : skinPeng);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.func_110914_a((EntityPenguin) par1Entity);
    }

	/*    public void renderPenguin(EntityPenguin par1EntityPenguin, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRenderLiving(par1EntityPenguin, par2, par4, par6, par8, par9);
    }

    protected void func_25006_b(EntityPenguin entitypenguin, float f)
    {
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
    protected void preRenderCallback(EntityLiving par1EntityLiving, float par2)
    {
        func_25006_b((EntityPenguin)par1EntityLiving, par2);
    }*/

/*    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        renderPenguin((EntityPenguin)par1EntityLiving, par2, par4, par6, par8, par9);
    }*/

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probability, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
/*    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
		//System.out.printf("From Client: Rendering a new %s\n", par1Entity.getEntityName());
        renderPenguin((EntityPenguin)par1Entity, par2, par4, par6, par8, par9);
    }*/
}
