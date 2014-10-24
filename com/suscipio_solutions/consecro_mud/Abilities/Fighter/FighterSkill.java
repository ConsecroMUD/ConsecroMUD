package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class FighterSkill extends StdAbility
{
	@Override public String ID() { return "FighterSkill"; }
	private final static String localizedName = CMLib.lang().L("FighterSkill");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){ return Ability.ACODE_SKILL;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}

}
