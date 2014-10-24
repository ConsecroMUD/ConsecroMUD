package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class Shirt extends GenArmor
{
	@Override public String ID(){	return "Shirt";}
	public Shirt()
	{
		super();

		setName("a nice tunic");
		setDisplayText("a plain tunic is folded neatly here.");
		setDescription("It is a plain buttoned tunic.");
		properWornBitmap=Wearable.WORN_TORSO;
		wornLogicalAnd=true;
		basePhyStats().setArmor(2);
		basePhyStats().setWeight(1);
		basePhyStats().setAbility(0);
		baseGoldValue=1;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_COTTON;
	}

}
