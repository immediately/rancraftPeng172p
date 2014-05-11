package rancraftPenguins;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

public class ItemPenguinFlameRepeater extends ItemBow {

	public ItemPenguinFlameRepeater() {
		super();
        maxStackSize = 1;
        setMaxDamage(1152); // 3x durability of a normal bow
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabCombat);
	}

	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
	    itemIcon = iconRegister.registerIcon(RanCraftPenguins.modID + ":pengFlamRepeater");
	}

    public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i)
    {
    	// do nothing, unlike ItemBow.java
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
	    int var6 = this.getMaxItemUseDuration(par1ItemStack); // - par4; /* always maximum */
		
        ArrowLooseEvent event = new ArrowLooseEvent(par3EntityPlayer, par1ItemStack, var6);
	    MinecraftForge.EVENT_BUS.post(event);
	    //var6 = event.charge;
	
	    boolean var5 = par3EntityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, par1ItemStack) > 0;
	
	    if (var5 || par3EntityPlayer.inventory.hasItem(Items.arrow))
	    {
	        float var7 = 1.0F; // velocity multiplier (orig 1.5

	        EntityArrow var8 = new EntityArrow(par2World, par3EntityPlayer, var7 * 2.0F);

            var8.setIsCritical(true);
	        int var9 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, par1ItemStack);
	
	        if (var9 > 0)
	        {
	            var8.setDamage(var8.getDamage() + (double)var9 * 0.5D + 0.5D);
	        }
	
	        int var10 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, par1ItemStack);
	
	        if (var10 > 0)
	        {
	            var8.setKnockbackStrength(var10);
	        }
	
	        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, par1ItemStack) > 0)
	        {
	            var8.setFire(100);
	        }
	
	        par1ItemStack.damageItem(1, par3EntityPlayer);
	        par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + var7 * 0.5F);
	
	        if (var5)
	        {
	            var8.canBePickedUp = 2;
	        }
	        else
	        {
	            par3EntityPlayer.inventory.consumeInventoryItem(Items.arrow);
	        }
	
	        if (!par2World.isRemote)
	        {
	            par2World.spawnEntityInWorld(var8);
	        }
	    }

        return par1ItemStack;
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
    	return RanCraftPenguins.PengScaleRed.equals(par2ItemStack) ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }
}
