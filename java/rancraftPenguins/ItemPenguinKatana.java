package rancraftPenguins;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
//import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;
//import net.minecraft.util.Icon;

public class ItemPenguinKatana extends ItemSword {
    private int isDrawn;

    public static final String[] field_94601_a = new String[] {RanCraftPenguins.modID + ":pengKatanaSheathed", RanCraftPenguins.modID + ":pengKatanaWielded"};
    
    //@SideOnly(Side.CLIENT)
    public IIcon[] IconAni = new IIcon[2];

    public ItemPenguinKatana(ToolMaterial par2, int par3)
    {
        super(par2);
    	isDrawn = (par3 == 1)?1:0;
        setCreativeTab(CreativeTabs.tabCombat);
        setMaxDamage(300); // number of uses before it wears out. Iron = 250, Diamond = 1561
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.IconAni = new IIcon[field_94601_a.length];

        for (int i = 0; i < this.IconAni.length; ++i)
        {
        	IconAni[i] = par1IconRegister.registerIcon(field_94601_a[i]);
        }
        // WORKS: (actually sets it)
        this.itemIcon = IconAni[isDrawn]; // initially sets icon to the first element of the array
    }

    /* allows RenderPenguinNinj to change the appearance of the Ninja's katana */
    public void setIconIndex(int par1IconIndex)
    {
    	this.itemIcon = IconAni[par1IconIndex];
    }
}
