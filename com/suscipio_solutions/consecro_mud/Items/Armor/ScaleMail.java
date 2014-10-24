package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class ScaleMail extends StdArmor
{
	@Override public String ID(){	return "ScaleMail";}
	public ScaleMail()
	{
		super();

		setName("a suit of Scalemail");
		setDisplayText("a suit of armor made of overlapping leather scales.");
		setDescription("This suit of armor is made of overlapping leather scales and will provide protection for the torso, arms, and legs.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(29);
		basePhyStats().setWeight(60);
		basePhyStats().setAbility(0);
		baseGoldValue=240;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
