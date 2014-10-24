package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class ChainMailArmor extends StdArmor
{
	@Override public String ID(){	return "ChainMailArmor";}
	public ChainMailArmor()
	{
		super();

		setName("a suit of chain mail armor");
		setDisplayText("a suit of chain mail armor sits here.");
		setDescription("This suit includes a fairly solid looking hauberk with leggings and a coif.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(37);
		basePhyStats().setWeight(60);
		basePhyStats().setAbility(0);
		baseGoldValue=150;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
