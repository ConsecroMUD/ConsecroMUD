package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Disease_Giggles extends Disease
{
	@Override public String ID() { return "Disease_Giggles"; }
	private final static String localizedName = CMLib.lang().L("Contagious Giggles");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(The Giggles)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return 15;}
	@Override protected int DISEASE_DELAY(){return 3;}
	@Override protected String DISEASE_DONE(){return "You feel more serious.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> start(s) giggling.^?";}
	@Override protected String DISEASE_AFFECT(){return "<S-NAME> giggle(s) and laugh(s) uncontrollably.";}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_PROXIMITY;}
	@Override public int difficultyLevel(){return 3;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if(affected==null) return false;
		if(!(affected instanceof MOB)) return true;

		final MOB mob=(MOB)affected;
		MOB diseaser=invoker;
		if(diseaser==null) diseaser=mob;
		if((!mob.amDead())&&((--diseaseTick)<=0))
		{
			diseaseTick=DISEASE_DELAY();
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_NOISE,DISEASE_AFFECT());
			if((mob.location()!=null)&&(mob.location().okMessage(mob,msg)))
				mob.location().send(mob,msg);
			catchIt(mob);
			return true;
		}
		return true;
	}
}
