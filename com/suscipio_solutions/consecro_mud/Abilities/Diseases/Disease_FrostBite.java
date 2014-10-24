package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Disease_FrostBite extends Disease
{
	@Override public String ID() { return "Disease_FrostBite"; }
	private final static String localizedName = CMLib.lang().L("Frost Bite");
	@Override public String name() { return localizedName; }
	private String where="feet";
	@Override public String displayText() { return L("(Frost bitten "+where+")"); }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}
	@Override public int difficultyLevel(){return 1;}
	public int[] limbsAffectable={Race.BODY_EAR,Race.BODY_ANTENEA,Race.BODY_FOOT,Race.BODY_HAND,Race.BODY_NOSE};
	@Override protected int DISEASE_TICKS(){return (CMProps.getIntVar( CMProps.Int.TICKSPERMUDDAY ) / 2);}
	@Override protected int DISEASE_DELAY(){return 50;}
	@Override
	protected String DISEASE_DONE()
	{
		if(tickDown>0)
			return "Your frost bite heals.";
		return "Your frost bite has cost you dearly.";
	}
	@Override protected String DISEASE_START(){return "^G<S-NAME> <S-IS-ARE> getting frost bite.^?";}
	@Override protected String DISEASE_AFFECT(){return "";}
	@Override public int abilityCode(){return 0;}

	@Override
	public void unInvoke()
	{
		if((affected instanceof MOB)&&(tickDown<=0))
		{
			final MOB mob=(MOB)affected;
			final Ability A=CMClass.getAbility("Amputation");
			if(A!=null)
			{
				super.unInvoke();
				A.invoke(mob,CMParms.parse(where),mob,true,0);
				mob.recoverCharStats();
				mob.recoverPhyStats();
				mob.recoverMaxState();
			}
			else
				super.unInvoke();
		}
		else
			super.unInvoke();
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null) return;
		if(where==null)
		{
			final Vector choices=new Vector();
			for (final int element : limbsAffectable)
				if(affected.charStats().getBodyPart(element)>0)
					choices.addElement(Integer.valueOf(element));
			if(choices.size()<=0)
			{
				where="nowhere";
				unInvoke();
			}
			else
				where=Race.BODYPARTSTR[((Integer)choices.elementAt(CMLib.dice().roll(1,choices.size(),-1))).intValue()];
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		where=null;
		return super.invoke(mob,commands,givenTarget,auto,asLevel);
	}
}
