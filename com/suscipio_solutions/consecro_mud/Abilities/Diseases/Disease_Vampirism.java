package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Disease_Vampirism extends Disease
{
	@Override public String ID() { return "Disease_Vampirism"; }
	private final static String localizedName = CMLib.lang().L("Vampirism");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Vampirism)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY)*6;}
	@Override protected int DISEASE_DELAY(){return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);}
	@Override protected String DISEASE_DONE(){return "Your vampirism lifts.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> seem(s) pale and cold.^?";}
	@Override protected String DISEASE_AFFECT(){return "";}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_CONSUMPTION;}
	@Override public int difficultyLevel(){return 9;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(!(affected instanceof MOB)) return;
		if(((MOB)affected).location()==null) return;
		if(CMLib.flags().isInDark(((MOB)affected).location()))
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_DARK);
		else
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_SEE);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if(msg.amISource(mob)
			   &&(msg.tool()!=null)
			   &&(msg.tool().ID().equals("Skill_Swim")))
			{
				mob.tell(L("You can't swim!"));
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null) return;
		affectableStats.setStat(CharStats.STAT_CHARISMA,affectableStats.getStat(CharStats.STAT_CHARISMA)+1);
	}
}
