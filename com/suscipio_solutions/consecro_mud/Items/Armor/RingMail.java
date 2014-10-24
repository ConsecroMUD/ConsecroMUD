package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class RingMail extends StdArmor
{
	@Override public String ID(){	return "RingMail";}
	public RingMail()
	{
		super();

		setName("suit of ring mail");
		setDisplayText("a suit of armor made with large metal rings fastened to leather");
		setDescription("A suit of ring mail including everything to protect the body, legs and arms.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(22);
		basePhyStats().setWeight(50);
		basePhyStats().setAbility(0);
		baseGoldValue=200;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
