package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class GenDress extends GenArmor
{
	@Override public String ID(){	return "GenDress";}
	public GenDress()
	{
		super();

		setName("a nice dress");
		setDisplayText("a nice dress lies here");
		setDescription("a well tailored dress.");
		properWornBitmap=Wearable.WORN_LEGS|Wearable.WORN_WAIST|Wearable.WORN_TORSO;
		wornLogicalAnd=true;
		basePhyStats().setArmor(2);
		basePhyStats().setWeight(3);
		basePhyStats().setAbility(0);
		baseGoldValue=1;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_COTTON;
	}

}
