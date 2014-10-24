package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Thief_ImprovedDistraction extends ThiefSkill
{
	@Override public String ID() { return "Thief_ImprovedDistraction"; }
	private final static String localizedName = CMLib.lang().L("Improved Distraction");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_DECEPTIVE;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return super.okMessage(myHost,msg);

		final MOB mob=(MOB)affected;
		if((msg.amISource(mob))
		&&(msg.tool()!=null)
		&&(msg.tool().ID().equals("Thief_Distract")))
		{
			helpProficiency(mob, 0);
			final Ability A=mob.fetchAbility("Thief_Distract");
			final float f=getXLEVELLevel(mob);
			final int ableDiv=(int)Math.round(5.0-(f*0.2));
			A.setAbilityCode(proficiency()/ableDiv);
		}
		return super.okMessage(myHost,msg);
	}
}
