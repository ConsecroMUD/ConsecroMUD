package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class GenBelt extends GenArmor
{
	@Override public String ID(){	return "GenBelt";}
	public GenBelt()
	{
		super();

		setName("a knitted weapon belt");
		setDisplayText("a knitted weapon belt is crumpled up here.");
		setDescription("a belt knitted from tough cloth with a simple sheath built in.");
		properWornBitmap=Wearable.WORN_WAIST;
		wornLogicalAnd=false;
		basePhyStats().setArmor(0);
		basePhyStats().setWeight(1);
		setCapacity(20);
		basePhyStats().setAbility(0);
		setContainTypes(Container.CONTAIN_DAGGERS|Container.CONTAIN_ONEHANDWEAPONS|Container.CONTAIN_SWORDS|Container.CONTAIN_OTHERWEAPONS);
		baseGoldValue=1;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_COTTON;
	}
}
