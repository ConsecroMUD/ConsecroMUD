package com.suscipio_solutions.consecro_mud.Items.Weapons;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;


public class Claymore extends Sword
{
	@Override public String ID(){	return "Claymore";}
	public final static int PLAIN					= 0;
	public final static int QUALITY_WEAPON			= 1;
	public final static int EXCEPTIONAL	  			= 2;

	public Claymore()
	{
		super();


		final Random randomizer = new Random(System.currentTimeMillis());
		final int claymoreType = Math.abs(randomizer.nextInt() % 3);

		this.phyStats.setAbility(claymoreType);
		setItemDescription(this.phyStats.ability());

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(10);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(8);
		baseGoldValue=25;
		recoverPhyStats();
		wornLogicalAnd=true;
		properWornBitmap=Wearable.WORN_HELD|Wearable.WORN_WIELD;
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_SLASHING;
	}



	public void setItemDescription(int level)
	{
		switch(level)
		{
			case Claymore.PLAIN:
				setName("a simple claymore");
				setDisplayText("a simple claymore is on the ground.");
				setDescription("It\\`s an oversized two-handed sword.  Someone made it just to make it.");
				break;

			case Claymore.QUALITY_WEAPON:
				setName("a very nice claymore");
				setDisplayText("a very nice claymore leans against the wall.");
				setDescription("It\\`s ornate with an etched hilt.  Someone took their time making it.");
				break;

			case Claymore.EXCEPTIONAL:
				setName("an exceptional claymore");
				setDisplayText("an exceptional claymore is found nearby.");
				setDescription("It\\`s a huge two-handed sword, with a etchings in the blade and a tassel hanging from the hilt.");
				break;

			default:
				setName("a simple claymore");
				setDisplayText("a simple claymore is on the ground.");
				setDescription("It\\`s an oversized two-handed sword.  Someone made it just to make it.");
				break;
		}
	}


}
