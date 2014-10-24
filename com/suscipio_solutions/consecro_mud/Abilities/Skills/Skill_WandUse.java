package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Skill_WandUse extends StdSkill
{
	@Override public String ID() { return "Skill_WandUse"; }
	private final static String localizedName = CMLib.lang().L("Wands");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_ARCANELORE;}
	@Override public int abilityCode(){return (invoker==null)?0:(getXLEVELLevel(invoker));}
}
