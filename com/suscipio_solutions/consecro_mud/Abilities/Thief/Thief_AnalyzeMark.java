package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Thief_AnalyzeMark extends ThiefSkill
{
	@Override public String ID() { return "Thief_AnalyzeMark"; }
	private final static String localizedName = CMLib.lang().L("Analyze Mark");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override protected boolean disregardsArmorCheck(MOB mob){return true;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_COMBATLORE;}

	public MOB getMark(MOB mob)
	{
		final Thief_Mark A=(Thief_Mark)mob.fetchEffect("Thief_Mark");
		if(A!=null)
			return A.mark;
		return null;
	}
	public int getMarkTicks(MOB mob)
	{
		final Thief_Mark A=(Thief_Mark)mob.fetchEffect("Thief_Mark");
		if((A!=null)&&(A.mark!=null))
			return A.ticks;
		return -1;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if(msg.amISource(mob)
			&&((msg.targetMinor()==CMMsg.TYP_LOOK)||(msg.targetMinor()==CMMsg.TYP_EXAMINE))
			&&(msg.target()!=null)
			&&(getMark(mob)==msg.target())
			&&(getMarkTicks(mob)>15)
			&&((mob.fetchAbility(ID())==null)||proficiencyCheck(mob,0,false)))
			{
				if(CMLib.dice().rollPercentage()>50) helpProficiency((MOB)affected, 0);
				final StringBuilder str=CMLib.commands().getScore((MOB)msg.target());
				if(!mob.isMonster())
					mob.session().wraplessPrintln(str.toString());
			}
		}
		super.executeMsg(myHost,msg);
	}
}
