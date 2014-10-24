package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Shield;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class GenShield extends GenArmor implements Shield
{
	@Override public String ID(){	return "GenShield";}
	public GenShield()
	{
		super();

		setName("a shield");
		setDisplayText("a sturdy round shield sits here.");
		setDescription("");
		properWornBitmap=Wearable.WORN_HELD;
		wornLogicalAnd=false;
		basePhyStats().setArmor(10);
		basePhyStats().setAbility(0);
		basePhyStats().setWeight(15);
		recoverPhyStats();
		material=RawMaterial.RESOURCE_OAK;
	}
	@Override public boolean isGeneric(){return true;}


}
