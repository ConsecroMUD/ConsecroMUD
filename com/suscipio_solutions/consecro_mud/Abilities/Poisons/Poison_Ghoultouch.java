package com.suscipio_solutions.consecro_mud.Abilities.Poisons;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Poison_Ghoultouch extends Poison
{
	@Override public String ID() { return "Poison_Ghoultouch"; }
	private final static String localizedName = CMLib.lang().L("Ghoultouch");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"POISONGHOUL"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public long flags(){return Ability.FLAG_PARALYZING|Ability.FLAG_UNHOLY;}

	@Override protected int POISON_TICKS(){return 7;} // 0 means no adjustment!
	@Override protected int POISON_DELAY(){return 1;}
	@Override protected String POISON_DONE(){return "Your muscles relax again.";}
	@Override protected String POISON_START(){return "^G<S-NAME> become(s) stiff and immobile!^?";}
	@Override protected String POISON_AFFECT(){return "";}
	@Override protected String POISON_CAST(){return "^F^<FIGHT^><S-NAME> poison(s) <T-NAMESELF>!^</FIGHT^>^?";}
	@Override protected String POISON_FAIL(){return "<S-NAME> attempt(s) to poison <T-NAMESELF>, but fail(s).";}
	@Override protected int POISON_DAMAGE(){return 0;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if(affected instanceof MOB)
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_MOVE);
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,affectableStats.getStat(CharStats.STAT_CONSTITUTION)-1);
		if(affectableStats.getStat(CharStats.STAT_CONSTITUTION)<=0)
			affectableStats.setStat(CharStats.STAT_CONSTITUTION,1);
	}
}
