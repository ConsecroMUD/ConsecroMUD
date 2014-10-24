package com.suscipio_solutions.consecro_mud.Items.Weapons;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;


public class Halberd extends StdWeapon
{
	@Override public String ID(){	return "Halberd";}
	public final static int PLAIN					= 0;
	public final static int QUALITY_WEAPON			= 1;
	public final static int EXCEPTIONAL	  			= 2;

	public Halberd()
	{
		super();


		final Random randomizer = new Random(System.currentTimeMillis());
		final int HalberdType = Math.abs(randomizer.nextInt() % 3);

		this.phyStats.setAbility(HalberdType);
		setItemDescription(this.phyStats.ability());

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(10);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(10);
		baseGoldValue=10;
		recoverPhyStats();
		wornLogicalAnd=true;
		properWornBitmap=Wearable.WORN_HELD|Wearable.WORN_WIELD;
		weaponType=TYPE_SLASHING;
		material=RawMaterial.RESOURCE_STEEL;
		weaponClassification=Weapon.CLASS_POLEARM;
	}

	public void setItemDescription(int level)
	{
		switch(level)
		{
			case Claymore.PLAIN:
				setName("a simple halberd");
				setDisplayText("a simple halberd is on the ground.");
				setDescription("It`s a polearm with a large bladed axe on the end.");
				break;
			case Claymore.QUALITY_WEAPON:
				setName("a very nice halberd");
				setDisplayText("a very nice halberd leans against the wall.");
				setDescription("It`s an ornate polearm with a large bladed axe on the end.");
				break;
			case Claymore.EXCEPTIONAL:
				setName("an exceptional halberd");
				setDisplayText("an exceptional halberd is found nearby.");
				setDescription("It`s an ornate polearm with a large bladed axe on the end.  It is well balanced and decorated with fine etchings.");
				break;
			default:
				setName("a simple halberd");
				setDisplayText("a simple halberd is on the ground.");
				setDescription("It`s a polearm with a large bladed axe on the end.");
				break;
		}
	}



}
