package rancraftPenguins;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
//import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
//import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraft.world.World;

// This Is Your Class For Your Armor, Your Have To Extend ItemArmor For Minecraft To Register(Have The Required Methods And Values) This As Armor
// Then You Need To Implement IArmorTextureProvider For It To Be Able To Render
public class PenguinArmor extends ItemArmor {
	// This Is Your Constructor, The First Param Is Item ID, The Second Is Its Material, Third is Icon texture, 
	// The Fourth Is The Rendering ID, and The Final Is The Type (i.e. Helmet, Chestplate, Boots, Legs)
	private String thisIconName;

/*	public PenguinArmor(int par1, ArmorMaterial par2ArmorMaterial, String par3IconTexture, int par4RenderingID, int par5ArmorType){
		super(par2ArmorMaterial, par4RenderingID, par5ArmorType);
		thisIconName = RanCraftPenguins.modID + ":" + par3IconTexture;
	}
*/	
	public PenguinArmor(ArmorMaterial par2ArmorMaterial, String par3IconTexture, int par4RenderingID, int par5ArmorType){
		super(par2ArmorMaterial, par4RenderingID, par5ArmorType);
		thisIconName = RanCraftPenguins.modID + ":" + par3IconTexture;
	}

	//@Override  This could improve efficiency if I can get it to work instead of checking every tick
/*	public void onArmorTickUpdate(EntityPlayer player){
		ItemStack boots = player.getCurrentItemOrArmor(0);
		World world = player.worldObj;
		
		super.onArmorTickUpdate(world, player, boots);

	  	//player takes no fall damage when wearing boots
		if(boots != null) {

			if(boots.getItem() == RanCraftPenguins.PenguinBootsCloud){
				player.fallDistance = 0.0F;
			}
		}
		//System.out.printf("updated fallDistance to %f\n", player.fallDistance);
	}
*/
	
	//@Override
    @SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister)
	{
	     itemIcon = par1IconRegister.registerIcon(thisIconName);
	}

	// This Sets What The Armor Is Supposed To Render Like
	//@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type){
		//String armorPath = "/rancraftPenguins/client/armor/";
		String armorPath = RanCraftPenguins.modID + ":textures/armor/";

		if(stack.getItem() == RanCraftPenguins.PenguinHatEmp || stack.getItem() == RanCraftPenguins.PenguinTunic || stack.getItem() == RanCraftPenguins.PenguinFlippers){
			return armorPath + "penguinhideemp.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinPants){
			return armorPath + "penguinhide_2.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinHatKing){
			return armorPath + "penguinhideking.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinHatAdel){
			return armorPath + "penguinhideadel.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinHatGal){
			return armorPath + "penguinhidegal.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinHatMag){
			return armorPath + "penguinhidemag.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinHatLB){
			return armorPath + "penguinhidelb.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinHatWF){
			return armorPath + "penguinhidewf.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinHatYE){
			return armorPath + "penguinhideye.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinHatCS){
			return armorPath + "penguinhidecs.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinHatGent){
			return armorPath + "penguinhidegent.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinHatAfr){
			return armorPath + "penguinhideafr.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinHatHum){
			return armorPath + "penguinhidehum.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinBootsCloud){
			return armorPath + "penguinhidecloud.png";
		}
		if(stack.getItem() == RanCraftPenguins.PenguinFlameLeggings){
			return armorPath + "penguinflamhide_2.png";
		}
		return armorPath + "penguinflamhide_1.png"; // default
	}

	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
		//System.out.printf("getIsRepairable returns %b.\n", (RanCraftPenguins.PengSkinBlack.equals(par2ItemStack)));
		//System.out.printf("black skin ID returns %d.\n", RanCraftPenguins.PengSkinBlack));
		//System.out.printf("item to use ID returns %d.\n", par2ItemStack));
    	//return RanCraftPenguins.PengSkinBlack.equals(par2ItemStack) ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    	return getArmorRepairItem(par1ItemStack).equals(par2ItemStack) ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

	// Determines which items can be used to repair armor
	private Item getArmorRepairItem(ItemStack stack)
	{
		Item retVal = null;

		if(stack.equals(RanCraftPenguins.PenguinFlameMask)
			|| stack.equals(RanCraftPenguins.PenguinFlameChestPlate)
			|| stack.equals(RanCraftPenguins.PenguinFlameLeggings)
			|| stack.equals(RanCraftPenguins.PenguinFlameFlippers)){
			retVal = RanCraftPenguins.PengScaleRed;
		} else {
			if(stack.equals(RanCraftPenguins.PenguinBootsCloud)){
				retVal = RanCraftPenguins.PengDownCloud;
			} else {
				if(stack.equals(RanCraftPenguins.PenguinHatLB)){
					retVal = RanCraftPenguins.PengSkinBlue;
				} else {
					if(stack.equals(RanCraftPenguins.PenguinHatYE)
						|| stack.equals(RanCraftPenguins.PenguinHatGal)
						|| stack.equals(RanCraftPenguins.PenguinHatHum)
						|| stack.equals(RanCraftPenguins.PenguinHatWF)){
						retVal = RanCraftPenguins.PengSkinBrown;
					} else {
						retVal = RanCraftPenguins.PengSkinBlack;
					}
				}
			}
		}

		return retVal;
	}
}
