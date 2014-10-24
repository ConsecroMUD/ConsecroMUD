package com.suscipio_solutions.consecro_mud.Commands;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class GConsider extends StdCommand
{
	public GConsider(){}

	private final String[] access=I(new String[]{"GCONSIDER","GCOS","GCO"});
	@Override public String[] getAccessWords(){return access;}

	public int relativeLevelDiff(MOB mob1, Set<MOB> mobs)
	{
		if((mob1==null)||(mobs==null)) return 0;
		MOB mob2=mobs.iterator().next();
		if(mob2.amFollowing()!=null) mob2=mob2.amUltimatelyFollowing();

		final int mob2Armor=CMLib.combat().adjustedArmor(mob2);
		final int mob1Armor=CMLib.combat().adjustedArmor(mob1);
		final double mob1Attack=CMLib.combat().adjustedAttackBonus(mob1,mob2);
		final int mob1Dmg=mob1.phyStats().damage();
		final int mob2Hp=mob2.baseState().getHitPoints();
		final int mob1Hp=mob1.baseState().getHitPoints();

		double mob2HitRound=0.0;
		for (final Object element : mobs)
		{
			final MOB mob=(MOB)element;
			final double mob2Attack=CMLib.combat().adjustedAttackBonus(mob,mob1);
			final int mob2Dmg=mob.phyStats().damage();
			mob2HitRound+=(((CMath.div(CMLib.dice().normalizeBy5((int)Math.round(50.0*mob2Attack/mob1Armor)),100.0))*CMath.div(mob2Dmg,2.0))+1.0)*CMath.mul(mob.phyStats().speed(),1.0);
		}
		final double mob1HitRound=(((CMath.div(CMLib.dice().normalizeBy5((int)Math.round(50.0*mob1Attack/mob2Armor)),100.0))*CMath.div(mob1Dmg,2.0))+1.0)*CMath.mul(mob1.phyStats().speed(),1.0);
		final double mob2SurvivalRounds=CMath.div(mob2Hp,mob1HitRound);
		final double mob1SurvivalRounds=CMath.div(mob1Hp,mob2HitRound);

		//int levelDiff=(int)Math.round(CMath.div((mob1SurvivalRounds-mob2SurvivalRounds),1));
		final double levelDiff=(mob1SurvivalRounds-mob2SurvivalRounds)/2;
		final int levelDiffed=(int)Math.round(Math.sqrt(Math.abs(levelDiff)));

		return levelDiffed*(levelDiff<0.0?-1:1);
	}


	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Consider whom?"));
			return false;
		}
		commands.removeElementAt(0);
		final String targetName=CMParms.combine(commands,0);
		final MOB target=mob.location().fetchInhabitant(targetName);
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("I don't see '@x1' here.",targetName));
			return false;
		}

		final int relDiff=relativeLevelDiff(target,mob.getGroupMembers(new HashSet<MOB>()));
		final int lvlDiff=(target.phyStats().level()-mob.phyStats().level());
		final int realDiff=(relDiff+lvlDiff)/2;

		int theDiff=2;
		if(mob.phyStats().level()>20) theDiff=3;
		if(mob.phyStats().level()>40) theDiff=4;
		if(mob.phyStats().level()>60) theDiff=5;
		if(mob.phyStats().level()>80) theDiff=6;

		final int levelDiff=Math.abs(realDiff);
		if(levelDiff<theDiff)
		{
			mob.tell(L("The perfect match!"));
			return false;
		}
		else
		if(realDiff<0)
		{
			if(realDiff>-(2*theDiff))
			{
				mob.tell(L("@x1 might give you a fight.",target.charStats().HeShe()));
				return false;
			}
			else
			if(realDiff>-(3*theDiff))
			{
				mob.tell(L("@x1 is hardly worth your while.",target.charStats().HeShe()));
				return false;
			}
			else
			if(realDiff>-(4*theDiff))
			{
				mob.tell(L("@x1 is a pushover.",target.charStats().HeShe()));
				return false;
			}
			else
			{
				mob.tell(L("@x1 is not worth the effort.",target.charStats().HeShe()));
				return false;
			}

		}
		else
		if(realDiff<(2*theDiff))
		{
			mob.tell(L("@x1 looks a little tough.",target.charStats().HeShe()));
			return false;
		}
		else
		if(realDiff<(3*theDiff))
		{
			mob.tell(L("@x1 is a serious threat.",target.charStats().HeShe()));
			return false;
		}
		else
		if(realDiff<(4*theDiff))
		{
			mob.tell(L("@x1 will clean your clock.",target.charStats().HeShe()));
			return false;
		}
		else
		{
			mob.tell(L("@x1 WILL KILL YOU DEAD!",target.charStats().HeShe()));
			return false;
		}
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
