package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Thief_Countertracking extends ThiefSkill
{
	@Override public String ID() { return "Thief_Countertracking"; }
	private final static String localizedName = CMLib.lang().L("Counter-Tracking");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_STEALTHY; }

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return super.okMessage(myHost,msg);

		final MOB mob=(MOB)affected;
		if((!msg.amISource(mob))
		&&(msg.target()==mob)
		&&(msg.tool() instanceof Ability)
		&&(proficiencyCheck(mob,0,false))
		&&(CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_TRACKING)))
		{
			msg.source().tell(L("You can't get a bead on him."));
			return false;
		}
		return super.okMessage(myHost,msg);
	}
}
