package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Disease_Migraines extends Disease
{
	@Override public String ID() { return "Disease_Migraines"; }
	private final static String localizedName = CMLib.lang().L("Migraine Headaches");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Migraine Headaches)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return 99999;}
	@Override protected int DISEASE_DELAY(){return 50;}
	@Override protected String DISEASE_DONE(){return "Your headaches stop.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> get(s) terrible headaches.^?";}
	@Override protected String DISEASE_AFFECT(){return "";}
	@Override public int abilityCode(){return 0;}
	@Override public int difficultyLevel(){return 4;}
	public HashSet<Ability> forgotten=new HashSet<Ability>();
	public HashSet<Ability> remember=new HashSet<Ability>();

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if((msg.amISource(mob))
		&&(msg.tool() instanceof Ability))
		{
			if(remember.contains(msg.tool()))
				return true;
			if(forgotten.contains(msg.tool()))
			{
				mob.tell(L("Your headaches make you forget @x1!",msg.tool().name()));
				return false;
			}
			if(mob.fetchAbility(msg.tool().ID())==msg.tool())
			{
				if(CMLib.dice().rollPercentage()>(mob.charStats().getSave(CharStats.STAT_SAVE_MIND)+35))
				{
					forgotten.add((Ability)msg.tool());
					mob.tell(L("Your headaches make you forget @x1!",msg.tool().name()));
					return false;
				}
				else
					remember.add((Ability)msg.tool());
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if(affected==null) return false;
		return true;
	}
}
