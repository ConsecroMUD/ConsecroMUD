package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class DrowDagger extends Dagger
{
	@Override public String ID(){	return "DrowDagger";}
	public DrowDagger()
	{
		super();

		setName("a dagger");
		setDisplayText("a dagger with a dark metallic blade.");
		setDescription("A dagger made out of a very dark material metal.");
		secretIdentity="A Drow dagger";
		basePhyStats().setAbility(CMLib.dice().roll(1,6,0));
		basePhyStats().setLevel(1);
		basePhyStats().setWeight(4);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(4);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		baseGoldValue=2500;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_BASHING;
	}



}
