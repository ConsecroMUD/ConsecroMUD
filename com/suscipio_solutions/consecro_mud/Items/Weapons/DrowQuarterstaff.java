package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class DrowQuarterstaff extends Mace
{
	@Override public String ID(){	return "DrowQuarterstaff";}
	public DrowQuarterstaff()
	{
		super();

		setName("a quarterstaff");
		setDisplayText("a quarterstaff is on the ground.");
		setDescription("A quarterstaff made out of a very dark material metal.");
		secretIdentity="A Drow quarterstaff";
		basePhyStats().setAbility(CMLib.dice().roll(1,6,0));
		basePhyStats().setLevel(1);
		basePhyStats().setWeight(4);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(6);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		baseGoldValue=2500;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_STEEL;
		weaponType=TYPE_BASHING;
	}



}
