package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Social;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Play_Blues extends Play
{
	@Override public String ID() { return "Play_Blues"; }
	private final static String localizedName = CMLib.lang().L("Blues");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected boolean maliciousButNotAggressiveFlag(){return true;}
	@Override protected String songOf(){return L("the Blues");}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg)) return false;
		// the sex rules
		if(!(affected instanceof MOB)) return true;

		final MOB myChar=(MOB)affected;
		if((msg.target()!=null)&&(msg.target() instanceof MOB)&&(myChar!=invoker()))
		{
			if((msg.amISource(myChar)||(msg.amITarget(myChar))
			&&(msg.tool() instanceof Social)
			&&(msg.tool().Name().equals("MATE <T-NAME>")
				||msg.tool().Name().equals("SEX <T-NAME>"))))
			{
				if(msg.amISource(myChar))
					myChar.tell(L("You really don't feel like it."));
				else
				if(msg.amITarget(myChar))
					msg.source().tell(L("@x1 doesn't look like @x2 feels like it.",myChar.name(),myChar.charStats().heshe()));
				return false;
			}
		}
		return true;
	}
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((affected!=null)&&(affected instanceof MOB)&&(affected!=invoker()))
		{
			final MOB mob=(MOB)affected;
			mob.curState().adjHunger(-2,mob.maxState().maxHunger(mob.baseWeight()));
			if(CMLib.dice().rollPercentage()>(adjustedLevel(invoker(),0)/4))
			{
				final Ability A=CMClass.getAbility("Disease_Depression");
				if(A!=null) A.invoke(invoker(),affected,true,0);
			}
		}
		return true;
	}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if((invoker==null)||(invoker==affected))
			return;
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
											-((invoker().charStats().getStat(CharStats.STAT_CHARISMA)/4)
													+(adjustedLevel(invoker(),0))));
	}
	@Override
	public void affectCharStats(MOB mob, CharStats stats)
	{
		super.affectCharStats(mob,stats);
		if((invoker()!=null)&&(invoker()!=mob))
			stats.setStat(CharStats.STAT_SAVE_JUSTICE,stats.getStat(CharStats.STAT_SAVE_JUSTICE)-(invoker().charStats().getStat(CharStats.STAT_CHARISMA)+getXLEVELLevel(invoker())));
	}
}

