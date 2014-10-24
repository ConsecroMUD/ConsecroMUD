package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Disease_Yawning extends Disease
{
	@Override public String ID() { return "Disease_Yawning"; }
	private final static String localizedName = CMLib.lang().L("Yawning");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Yawning)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return 30;}
	@Override protected int DISEASE_DELAY(){return 3;}
	@Override protected String DISEASE_DONE(){return "You stop yawning.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> seem(s) really tired.^?";}
	@Override protected String DISEASE_AFFECT(){return "<S-NAME> stretch(es) and yawn(s).";}
	@Override protected boolean DISEASE_REQSEE(){return true;}
	@Override protected boolean DISEASE_MALICIOUS(){return false;}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_PROXIMITY;}
	@Override public int difficultyLevel(){return 0;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		if(affected==null)
			return false;

		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		MOB diseaser=invoker;
		if(diseaser==null) diseaser=mob;
		if((getTickDownRemaining()==1)
		&&(!mob.amDead())
		&&(!CMLib.flags().isSleeping(mob))
		&&(CMLib.dice().rollPercentage()>mob.charStats().getSave(CharStats.STAT_SAVE_DISEASE)))
		{
			mob.delEffect(this);
			final Ability A=CMClass.getAbility("Disease_Yawning");
			A.invoke(diseaser,mob,true,0);
		}
		else
		if((!mob.amDead())
		&&((--diseaseTick)<=0)
		&&(!CMLib.flags().isSleeping(mob)))
		{
			diseaseTick=DISEASE_DELAY();
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_NOISE,DISEASE_AFFECT()+CMLib.protocol().msp("yawn.wav",40));
			if((mob.location()!=null)&&(mob.location().okMessage(mob,msg)))
				mob.location().send(mob,msg);
			catchIt(mob);
			return true;
		}
		return true;
	}
}
