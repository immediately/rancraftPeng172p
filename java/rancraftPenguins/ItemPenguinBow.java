package rancraftPenguins;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class ItemPenguinBow extends ItemBow {
	int clickDuration = 0;
    int penguinBowTransitions[] = {4,8,12,16}; /* points where image switches */

    public static final String[] bowPullIconNameArray = new String[] {RanCraftPenguins.modID + ":pengBow_1", RanCraftPenguins.modID + ":pengBow_2", RanCraftPenguins.modID + ":pengBow_3", RanCraftPenguins.modID + ":pengBow_4"};
    
    //@SideOnly(Side.CLIENT)
    private IIcon[] IconAni = new IIcon[4];

    public ItemPenguinBow() {
        this.maxStackSize = 1;
        setMaxDamage(576); // 1.5 x durability of normal bow
        this.setCreativeTab(CreativeTabs.tabCombat);
	}

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.IconAni = new IIcon[bowPullIconNameArray.length];

        for (int i = 0; i < this.IconAni.length; ++i)
        {
        	IconAni[i] = par1IconRegister.registerIcon(bowPullIconNameArray[i]);
        }
        // WORKS: (actually sets it)
        this.itemIcon = IconAni[0]; // initially sets icon to the first element of the array
	}

    /**
     * used to cycle through icons based on their used duration, i.e. for the bow
     */
    @SideOnly(Side.CLIENT)
    public IIcon getItemIconForUseDuration(int par1)
    {
        return this.IconAni[par1];
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 144000; /* 2x a normal bow */
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        ArrowNockEvent event = new ArrowNockEvent(par3EntityPlayer, par1ItemStack);
        MinecraftForge.EVENT_BUS.post(event);
	    clickDuration = 1;

        if (event.isCanceled())
        {
            return event.result;
        }

        if (par3EntityPlayer.capabilities.isCreativeMode || par3EntityPlayer.inventory.hasItem(Items.arrow))
        {
            par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        }

        return par1ItemStack;
    }

    /**
	 * called when the player releases the use item button. Args: itemstack, world, entityplayer, itemInUseCount
	 */
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4)
	{
	    int var6 = this.getMaxItemUseDuration(par1ItemStack) - par4;
	    clickDuration = 0;
		//System.out.printf("From Client: clickDuration2 reset to %d\n", clickDuration);

	    if(clickDuration > penguinBowTransitions[3]){
	    	clickDuration = penguinBowTransitions[3];
    	}
        if(clickDuration > penguinBowTransitions[2]){ /* only amplify it if it's already at the final image */
        	clickDuration *= 2;
        }

        ArrowLooseEvent event = new ArrowLooseEvent(par3EntityPlayer, par1ItemStack, var6);
	    MinecraftForge.EVENT_BUS.post(event);
	    if (event.isCanceled())
	    {
	        return;
	    }
	    var6 = event.charge;
	
	    boolean var5 = par3EntityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, par1ItemStack) > 0;
	
	    if (var5 || par3EntityPlayer.inventory.hasItem(Items.arrow))
	    {
	        float var7 = (float)var6 / 12.0F;
	        var7 = (var7 * var7 + var7 * 2.0F) / 3.0F;
	
	        if ((double)var7 < 0.1D)
	        {
	            return;
	        }
	
	        if (var7 > 1.1F)
	        {
	            var7 = 1.1F;
	        }
	
	        EntityArrow var8 = new EntityArrow(par2World, par3EntityPlayer, var7 * 2.0F);
	
	        if (var7 == 1.1F)
	        {
	            var8.setIsCritical(true); /* arrow critical */
	        }
	
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
	}
	
    @Override
    @SideOnly(Side.CLIENT)
    public void onUpdate(ItemStack par1ItemStack, World world, Entity entity, int par4, boolean flag)
	{
        EntityPlayer entityplayer = (EntityPlayer)entity;
        ItemStack itemstack1 = entityplayer.inventory.getCurrentItem();
        if(clickDuration > 0){
        	clickDuration++;
		}else
		{
			//System.out.printf("From Client: clickDuration is %d which apparently is not greater than zero.\n", clickDuration);
        	this.itemIcon = IconAni[0];
		}        

		//System.out.printf("From Client: clickDuration incremented to %d\n", clickDuration);

		if (clickDuration > penguinBowTransitions[3]){
        	clickDuration = penguinBowTransitions[3];
        }
        //if(entityplayer.isUsingItem() && itemstack1.equals(RanCraftPenguins.PenguinBow))
        if(entityplayer.isUsingItem() && itemstack1.getDisplayName().compareTo("Penguin Bow") == 0)
        {
    		//System.out.printf("From Client: itemstack is penguinbow is %b.\n",  itemstack1.equals(RanCraftPenguins.PenguinBow));
    		//System.out.printf("From Client: itemstack name diff from penguinbow is %d.\n",  itemstack1.getDisplayName().compareTo("Penguin Bow"));
    		//System.out.printf("From Client: Set icon index according to clickDuration of %d.\n", clickDuration);
            if(clickDuration > penguinBowTransitions[2])
			{
            	this.itemIcon = IconAni[3];
			}else if(clickDuration > penguinBowTransitions[1])
			{
            	this.itemIcon = IconAni[2];
			}else if(clickDuration > penguinBowTransitions[0])
			{
            	this.itemIcon = IconAni[1];
			}else
			{
            	this.itemIcon = IconAni[0];
			}        
		}
	}
    
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
		//System.out.printf("getIsRepairable returns %b.\n", (RanCraftPenguins.PengSkinBlack.itemID == par2ItemStack.itemID));
		//System.out.printf("black skin ID returns %d.\n", RanCraftPenguins.PengSkinBlack.itemID);
		//System.out.printf("item to use ID returns %d.\n", par2ItemStack.itemID);
    	return RanCraftPenguins.PengSkinBlack.equals(par2ItemStack) ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }
 }
