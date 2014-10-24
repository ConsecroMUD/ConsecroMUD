package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class LeatherArmor extends StdArmor
{
	@Override public String ID(){	return "LeatherArmor";}
	public LeatherArmor()
	{
		super();

		setName("a suit of leather armor");
		setDisplayText("a suit of leather armor including a breastplate, arms, and legs.");
		setDescription("This is a fairly decent looking suit of leather armor.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(12);
		basePhyStats().setWeight(15);
		basePhyStats().setAbility(0);
		baseGoldValue=10;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
	}

}
