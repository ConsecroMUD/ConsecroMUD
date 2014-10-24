package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class GreatHelm extends StdArmor
{
	@Override public String ID(){	return "GreatHelm";}
	public GreatHelm()
	{
		super();

		setName("a steel Great Helm.");
		setDisplayText("a steel great helm sits here.");
		setDescription("This is a steel helmet that completely encloses the head.");
		properWornBitmap=Wearable.WORN_HEAD;
		wornLogicalAnd=false;
		basePhyStats().setArmor(18);
		basePhyStats().setWeight(10);
		basePhyStats().setAbility(0);
		baseGoldValue=60;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
	}

}
