package com.suscipio_solutions.consecro_mud.Items.Weapons;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;


public class Sword extends StdWeapon
{
	@Override public String ID(){	return "Sword";}
	public Sword()
	{
		super();

		setName("a sword");
		setDisplayText("a rather plain looking sword leans against the wall.");
		setDescription("An plain sword.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(10);
		recoverPhyStats();
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(8);
		recoverPhyStats();
		weaponType=TYPE_SLASHING;
		material=RawMaterial.RESOURCE_STEEL;
		weaponClassification=Weapon.CLASS_SWORD;
	}

	@Override
	public CMObject newInstance()
	{
		if(!ID().equals("Sword"))
		{
			try
			{
				return this.getClass().newInstance();
			}
			catch(final Exception e){}
			return new Sword();
		}
		final Random randomizer = new Random(System.currentTimeMillis());
		final int swordType = Math.abs(randomizer.nextInt() % 6);
		switch (swordType)
		{
			case 0:  return new Rapier();
			case 1:	 return new Katana();
			case 2:	 return new Longsword();
			case 3:	 return new Scimitar();
			case 4:	 return new Claymore();
			case 5:	 return new Shortsword();
			default:
				try
				{
					return this.getClass().newInstance();
				}
				catch(final Exception e){}
				return new Sword();
		}

	}
}
