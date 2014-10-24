package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Disease_Narcolepsy extends Disease
{
	@Override public String ID() { return "Disease_Narcolepsy"; }
	private final static String localizedName = CMLib.lang().L("Narcolepsy");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Narcolepsy)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return 99999;}
	@Override protected int DISEASE_DELAY(){return (int)(CMProps.getMillisPerMudHour()/CMProps.getTickMillis());}
	@Override protected String DISEASE_DONE(){return "Your narcolepsy is cured!";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> seem(s) sleepy.^?";}
	@Override protected String DISEASE_AFFECT(){return "<S-NAME> <S-IS-ARE> getting sleepy...";}
	@Override public int abilityCode(){return 0;}
	@Override public int difficultyLevel(){return 6;}
	protected int attDown=1;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{

		if(!super.tick(ticking,tickID))	return false;
		if(affected==null) return false;
		if(!(affected instanceof MOB)) return true;

		final MOB mob=(MOB)affected;
		if((!mob.amDead())&&((--diseaseTick)<=0))
		{
			diseaseTick=DISEASE_DELAY();
			if(mob.maxState().getFatigue()>Long.MIN_VALUE/2)
				mob.curState().adjFatigue(mob.curState().getFatigue()+CharState.FATIGUED_MILLIS,mob.maxState());
			mob.location().show(mob,null,CMMsg.MSG_NOISE,DISEASE_AFFECT());
			if(!CMLib.flags().isSleeping(mob))
			{
				final Command C=CMClass.getCommand("Sleep");
				try{if(C!=null) C.execute(mob,CMParms.parse("Sleep"),Command.METAFLAG_FORCED);}catch(final Exception e){}
			}
			return true;
		}
		return true;
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)/2);
		if(affectableStats.getStat(CharStats.STAT_STRENGTH)<1)
			affectableStats.setStat(CharStats.STAT_STRENGTH,1);
		super.affectCharStats(affected,affectableStats);
	}

}
