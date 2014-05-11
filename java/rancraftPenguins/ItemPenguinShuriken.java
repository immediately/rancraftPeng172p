package rancraftPenguins;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemPenguinShuriken extends Item
{
    private int weaponDamage;

    public ItemPenguinShuriken()
    {
        super();
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabCombat);
        weaponDamage = 0; // no effect
    }

	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		/* how it appears in player's inventory */
	    itemIcon = iconRegister.registerIcon(RanCraftPenguins.modID + ":pengShuriken");
	}

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (!par3EntityPlayer.capabilities.isCreativeMode)
        {
            --par1ItemStack.stackSize;
        }

        /* playSoundAtEntity(entity, soundfile, volume, speed) */
		//System.out.printf("PlaySoundAtEntity: %s.\n", "mob.penguinNinj.shurikenA");
        //par2World.playSoundAtEntity(par3EntityPlayer, "penguinNinj.shurikenA", 1.0F, 1.0F); // / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!par2World.isRemote)
        {
            //par2World.spawnEntityInWorld(new EntityArrow(par2World, par3EntityPlayer, 1.2F));
            // added 1.2F for initial velocity to match signature of one of the constructors
            par2World.spawnEntityInWorld(new EntityPenguinShuriken(par2World, par3EntityPlayer, 1.2F));
            //par2World.spawnEntityInWorld(new EntityPenguinShuriken(par2World));
        }

        return par1ItemStack;
    }

    /**
     * Returns the damage against a given entity.
     */
    public int getDamageVsEntity(Entity par1Entity)
    {
        return weaponDamage;
    }
}
