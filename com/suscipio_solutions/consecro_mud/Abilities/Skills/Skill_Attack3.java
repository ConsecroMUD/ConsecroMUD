package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Skill_Attack3 extends Skill_Attack2
{
	@Override public String ID() { return "Skill_Attack3"; }
	private final static String localizedName = CMLib.lang().L("Third Attack");
	@Override public String name() { return localizedName; }
	@Override protected int attackToNerf(){ return 3;}
	@Override protected int roundToNerf(){ return 1;}
	@Override protected double nerfAmount(){ return .6;}
	@Override protected double numberOfFullAttacks(){ return 1.0;}

}
