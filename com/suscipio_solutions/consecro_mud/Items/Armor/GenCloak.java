package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class GenCloak extends GenArmor
{
	@Override public String ID(){	return "GenCloak";}
	public GenCloak()
	{
		super();

		setName("a hooded cloak");
		setDisplayText("a hooded cloak is here");
		setDescription("");
		properWornBitmap=Wearable.WORN_ABOUT_BODY;
		wornLogicalAnd=false;
		basePhyStats().setArmor(1);
		basePhyStats().setWeight(1);
		basePhyStats().setAbility(0);
		baseGoldValue=1;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_COTTON;
		readableText="a hooded figure";
	}

	@Override
	public void affectPhyStats(Physical host, PhyStats stats)
	{
		if(!amWearingAt(Wearable.IN_INVENTORY))
			stats.setName(readableText());
	}
}
