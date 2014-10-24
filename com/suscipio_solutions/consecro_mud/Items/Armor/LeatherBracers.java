package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class LeatherBracers extends StdArmor
{
	@Override public String ID(){	return "LeatherBracers";}
	public LeatherBracers()
	{
		super();

		setName("a pair of leather bracers");
		setDisplayText("a pair of leather bracers are here.");
		setDescription("Strong enough to protect your forearms against the strongest of feathers...");
		properWornBitmap=Wearable.WORN_LEFT_WRIST | Wearable.WORN_RIGHT_WRIST | Wearable.WORN_ARMS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(1);
		basePhyStats().setWeight(5);
		basePhyStats().setAbility(0);
		baseGoldValue=5;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
	}


}
