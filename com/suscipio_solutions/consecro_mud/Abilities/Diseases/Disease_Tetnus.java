package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Disease_Tetnus extends Disease
{
	@Override public String ID() { return "Disease_Tetnus"; }
	private final static String localizedName = CMLib.lang().L("Tetanus");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Tetanus)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY)*6;}
	@Override protected int DISEASE_DELAY(){return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);}
	@Override protected String DISEASE_DONE(){return "Your tetnus clears up!";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> seem(s) ill.^?";}
	@Override protected String DISEASE_AFFECT(){return "<S-NAME> <S-IS-ARE> getting slower...";}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_CONSUMPTION;}
	@Override public int difficultyLevel(){return 0;}
	protected int dexDown=1;

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
			mob.location().show(mob,null,CMMsg.MSG_NOISE,DISEASE_AFFECT());
			dexDown++;
			return true;
		}
		return true;
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null) return;
		if(dexDown<0) return;
		affectableStats.setStat(CharStats.STAT_DEXTERITY,affectableStats.getStat(CharStats.STAT_DEXTERITY)-dexDown);
		if(affectableStats.getStat(CharStats.STAT_DEXTERITY)<=0)
		{
			dexDown=-1;
			CMLib.combat().postDeath(invoker(),affected,null);
		}
	}
}
