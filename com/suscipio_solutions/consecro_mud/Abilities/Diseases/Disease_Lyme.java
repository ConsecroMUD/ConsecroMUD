package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Disease_Lyme extends Disease
{
	@Override public String ID() { return "Disease_Lyme"; }
	private final static String localizedName = CMLib.lang().L("Lyme Disease");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Lyme Disease)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return 9*CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);}
	@Override protected int DISEASE_DELAY(){return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);}
	@Override protected String DISEASE_DONE(){return "Your lyme disease goes away.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> get(s) lyme disease!^?";}
	@Override protected String DISEASE_AFFECT(){return "";}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_CONSUMPTION|DiseaseAffect.SPREAD_DAMAGE;}
	@Override public int difficultyLevel(){return 5;}
	int days=0;

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return super.okMessage(myHost,msg);

		final MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if((msg.amISource(mob))
		&&(days>0)
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Ability)
		&&(mob.fetchAbility(msg.tool().ID())==msg.tool())
		&&(CMLib.dice().rollPercentage()>(mob.charStats().getSave(CharStats.STAT_SAVE_MIND)+25)))
		{
			mob.tell(L("Your headaches make you forget @x1!",msg.tool().name()));
			return false;
		}

		return super.okMessage(myHost,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if(affected==null) return false;
		if(!(affected instanceof MOB)) return true;

		final MOB mob=(MOB)affected;
		if((!mob.amDead())&&(getTickDownRemaining()==1))
		{
			MOB diseaser=invoker;
			if(diseaser==null) diseaser=mob;
			Ability A=null;
			if(CMLib.dice().rollPercentage()>50)
				A=CMClass.getAbility("Disease_Fever");
			else
			if(CMLib.dice().rollPercentage()>50)
				A=CMClass.getAbility("Disease_Amnesia");
			else
			if(CMLib.dice().rollPercentage()>50)
				A=CMClass.getAbility("Disease_Arthritis");
			else
				A=CMClass.getAbility("Disease_Fever");
			if(A!=null)
			{
				A.invoke(diseaser,mob,true,0);
				A=mob.fetchEffect(A.ID());
				if(A!=null) A.makeLongLasting();
			}
		}
		else
		if((!mob.amDead())&&((--diseaseTick)<=0))
		{
			days++;
			diseaseTick=DISEASE_DELAY();
			if(CMLib.dice().rollPercentage()<mob.charStats().getSave(CharStats.STAT_SAVE_DISEASE))
			{
				unInvoke();
				return false;
			}
			return true;
		}
		return true;
	}
}
