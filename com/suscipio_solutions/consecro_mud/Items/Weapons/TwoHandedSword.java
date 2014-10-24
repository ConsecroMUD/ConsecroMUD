package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;



public class TwoHandedSword extends Sword
{
	@Override public String ID(){	return "TwoHandedSword";}
	public TwoHandedSword()
	{
		super();

		setName("a two-handed sword");
		setDisplayText("a heavy two-handed sword hangs on the wall.");
		setDescription("It has a metallic pommel, and a very large blade.");
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(15);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(10);
		baseGoldValue=50;
		wornLogicalAnd=true;
		properWornBitmap=Wearable.WORN_HELD|Wearable.WORN_WIELD;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_SLASHING;
	}


}
