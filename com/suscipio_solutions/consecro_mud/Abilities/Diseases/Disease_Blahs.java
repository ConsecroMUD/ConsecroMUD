package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Disease_Blahs extends Disease
{
	@Override public String ID() { return "Disease_Blahs"; }
	private final static String localizedName = CMLib.lang().L("Blahs");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(The Blahs)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}
	@Override public int difficultyLevel(){return 4;}

	@Override protected int DISEASE_TICKS(){return 99999;}
	@Override protected int DISEASE_DELAY(){return 20;}
	@Override protected String DISEASE_DONE(){return "You feel a little better.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> get(s) the blahs.^?";}
	@Override protected String DISEASE_AFFECT(){return "<S-NAME> sigh(s).";}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_CONSUMPTION;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if(affected instanceof MOB)
		{
			if(msg.source()!=affected)
				return true;
			if(msg.source().location()==null)
				return true;

			if((msg.amISource((MOB)affected))
			&&(msg.sourceMessage()!=null)
			&&(msg.tool()==null)
			&&((msg.sourceMinor()==CMMsg.TYP_SPEAK)
			   ||(msg.sourceMinor()==CMMsg.TYP_TELL)
			   ||(CMath.bset(msg.sourceMajor(),CMMsg.MASK_CHANNEL))))
			{
				final Ability A=CMClass.getAbility("Blah");
				if(A!=null)
				{
					A.setProficiency(100);
					A.invoke(msg.source(),null,true,0);
					A.setAffectedOne(msg.source());
					if(!A.okMessage(myHost,msg))
						return false;
				}
			}
		}
		else
		{

		}
		return true;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if(affected==null) return false;
		if(!(affected instanceof MOB)) return true;

		final MOB mob=(MOB)affected;
		if((mob.curState().getFatigue()<CharState.FATIGUED_MILLIS)
		&&(mob.maxState().getFatigue()>Long.MIN_VALUE/2))
			mob.curState().setFatigue(CharState.FATIGUED_MILLIS);
		return true;
	}

}
