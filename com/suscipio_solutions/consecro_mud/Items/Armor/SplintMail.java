package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class SplintMail extends StdArmor
{
	@Override public String ID(){	return "SplintMail";}
	public SplintMail()
	{
		super();

		setName("suit of splint mail");
		setDisplayText("a suit of splint mail.");
		setDescription("A suit of splint mail armor including everything to protect the body, legs and arms.");
		properWornBitmap=Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
		wornLogicalAnd=true;
		basePhyStats().setArmor(44);
		basePhyStats().setWeight(60);
		basePhyStats().setAbility(0);
		baseGoldValue=160;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
