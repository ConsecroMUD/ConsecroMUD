package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class StuddedLeatherArmor extends StdArmor
{
	@Override public String ID(){	return "StuddedLeatherArmor";}
	public StuddedLeatherArmor()
	{
		super();

		setName("suit of studded leather armor");
		setDisplayText("a suit of leather armor reinforced with decorative studs");
		setDescription("A suit of studded leather armor including everything to protect the body, legs and arms.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(22);
		basePhyStats().setAbility(0);
		basePhyStats().setWeight(35);
		baseGoldValue=40;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_LEATHER;
	}

}
