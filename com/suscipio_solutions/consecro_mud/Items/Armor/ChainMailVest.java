package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class ChainMailVest extends StdArmor
{
	@Override public String ID(){	return "ChainMailVest";}
	public ChainMailVest()
	{
		super();

		setName("a chain mail vest");
		setDisplayText("a chain mail vest sits here.");
		setDescription("This is fairly solid looking vest made of chain mail.");
		properWornBitmap=Wearable.WORN_TORSO;
		wornLogicalAnd=false;
		basePhyStats().setArmor(25);
		basePhyStats().setWeight(30);
		basePhyStats().setAbility(0);
		baseGoldValue=75;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
