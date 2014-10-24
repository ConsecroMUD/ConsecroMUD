package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Skill_AttackHalf extends Skill_Attack2
{
	@Override public String ID() { return "Skill_AttackHalf"; }
	private final static String localizedName = CMLib.lang().L("Half Attack");
	@Override public String name() { return localizedName; }
	@Override protected int attackToNerf(){ return 2;}
	@Override protected int roundToNerf(){ return 2;}
	@Override protected double nerfAmount(){ return .8;}
	@Override protected double numberOfFullAttacks(){ return 0.5;}

}
