package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class DrowChainMailArmor extends StdArmor
{
	@Override public String ID(){	return "DrowChainMailArmor";}
	public DrowChainMailArmor()
	{
		super();

		setName("a suit of dark chain mail armor");
		setDisplayText("a suit of chain mail armor made of dark material sits here.");
		setDescription("This suit includes a fairly solid looking hauberk with leggings and a coif, all constructed from a strong, dark metal.");
		secretIdentity="A suit of Drow Chain Mail Armor";
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(65);
		basePhyStats().setWeight(60);
		basePhyStats().setAbility(0);
		baseGoldValue=1500;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
