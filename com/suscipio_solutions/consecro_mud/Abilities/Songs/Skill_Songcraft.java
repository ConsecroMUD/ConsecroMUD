package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Skill_Songcraft extends BardSkill
{
	@Override public String ID() { return "Skill_Songcraft"; }
	private final static String localizedName = CMLib.lang().L("Songcraft");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_ARCANELORE;}
	public String lastID="";
	public int craftType(){return Ability.ACODE_SONG;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(!(affected instanceof MOB))
		   return;
		final MOB mob=(MOB)affected;
		if((msg.sourceMinor()==CMMsg.TYP_CAST_SPELL)
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS))
		&&(!msg.amISource(mob))
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Ability)
		&&(msg.sourceMessage()!=null)
		&&(msg.sourceMessage().length()>0)
		&&((((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES)==craftType())
		&&(!lastID.equalsIgnoreCase(msg.tool().ID()))
		&&(mob.location()!=null)
		&&(mob.location().isInhabitant(msg.source()))
		&&(CMLib.flags().canBeSeenBy(msg.source(),mob))
		&&(msg.source().fetchAbility(msg.tool().ID())!=null))
		{
			final boolean hasAble=(mob.fetchAbility(ID())!=null);
			final int lowestLevel=CMLib.ableMapper().lowestQualifyingLevel(msg.tool().ID());
			int myLevel=0;
			if(hasAble) myLevel=adjustedLevel(mob,0)-lowestLevel+1;
			final int lvl=(mob.phyStats().level()/3)+getXLEVELLevel(mob);
			if(myLevel<lvl) myLevel=lvl;
			if(((!hasAble)||proficiencyCheck(mob,0,false))&&(lowestLevel<=myLevel))
			{
				final Ability A=(Ability)copyOf();
				A.setMiscText(msg.tool().ID());
				lastID=msg.tool().ID();
				msg.addTrailerMsg(CMClass.getMsg(mob,msg.source(),A,CMMsg.MSG_OK_VISUAL,L("<T-NAME> casts '@x1'.",msg.tool().name()),CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null));
				helpProficiency(mob, 0);
			}
		}
	}
}
