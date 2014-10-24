package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class DrowSword extends Longsword
{
	@Override public String ID(){	return "DrowSword";}
	public DrowSword()
	{
		super();

		setName("a longsword");
		setDisplayText("a fancy longsword has been dropped on the ground.");
		setDescription("A one-handed sword with a very dark blade.");
		secretIdentity="A Drow Sword";
		basePhyStats().setAbility(CMLib.dice().roll(1,6,0));
		basePhyStats().setLevel(1);
		basePhyStats().setWeight(4);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(8);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		baseGoldValue=2500;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_SLASHING;
	}



}
