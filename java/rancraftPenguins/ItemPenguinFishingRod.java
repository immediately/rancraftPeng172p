package rancraftPenguins;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemPenguinFishingRod extends ItemFishingRod
{
    @SideOnly(Side.CLIENT)
    private IIcon theIcon;
    private IIcon[] IconAni = new IIcon[2];
	static int rodIcon;
	private int counter = 5;
    private static final String __OBFID = "CL_00000034";

    public ItemPenguinFishingRod()
    {
        this.setMaxDamage(96);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    public static final String[] field_94601_a = new String[] {RanCraftPenguins.modID + ":pengFishingRod", RanCraftPenguins.modID + ":pengFishingRod_empty"};

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    /**
     * Returns true if this item should be rotated by 180 degrees around the Y axis when being held in an entities
     * hands.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldRotateAroundWhenRendering()
    {
        return true;
    }

    public void setIconTo(int zeroOrOne){
    	rodIcon = zeroOrOne;
		//System.out.printf("Setting rod icon to %d\n", rodIcon);
    	//this.itemIcon = IconAni[0];
    	return;
    }

    /**
     * Called whenever the player stops using this item. Args: itemStack, world, entityPlayer
     */
    @SideOnly(Side.CLIENT)
    public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){
    	//this.setIconToEmptyRod();
    	this.itemIcon = IconAni[0];
    }
    
    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (par3EntityPlayer.fishEntity != null)
        {
            int i = par3EntityPlayer.fishEntity.func_146034_e();
            par1ItemStack.damageItem(i, par3EntityPlayer);
            par3EntityPlayer.swingItem();
            rodIcon = 0;
            //if(!par2World.isRemote){
            //	this.itemIcon = IconAni[0]; // icon 0 includes string
            //}
        }
        else
        {
            par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        	rodIcon = 1;
            if (!par2World.isRemote)
            {
    			//System.out.printf("FishingRod: Trying to spawn a hook!\n");
            	//rancraftPenguins.RanCraftPenguins.Channel.sendToServer(derp)
            	//par2World.spawnEntityInWorld(new EntityFishHook(par2World, par3EntityPlayer));
                par2World.spawnEntityInWorld(new EntityPenguinFishHook(par2World, par3EntityPlayer));
            }

            par3EntityPlayer.swingItem();
        }

        return par1ItemStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onUpdate(ItemStack par1ItemStack, World world, Entity entity, int par4, boolean flag)
	{
    	this.counter--;
		//System.out.printf("Counter: %d ", this.counter);
    	if(counter < 1){
    		counter = 15; // every fifteenth tick
			//System.out.printf("Updating itemIcon to %d in the client\n", this.rodIcon);
	        this.itemIcon = IconAni[this.rodIcon];
    	}
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
        this.itemIcon = IconAni[0]; // initially sets icon to the first element of the array
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_94597_g()
    {
        return this.theIcon;
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
    	return par2ItemStack.getItem() == RanCraftPenguins.PengFeatherBlack? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    /**
     * Checks isDamagable and if it cannot be stacked
     */
    public boolean isItemTool(ItemStack par1ItemStack)
    {
        return super.isItemTool(par1ItemStack);
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return 1;
    }
}