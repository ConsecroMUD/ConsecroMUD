package com.suscipio_solutions.consecro_mud.Abilities.Poisons;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Poison_Mindsap extends Poison
{
	@Override public String ID() { return "Poison_Mindsap"; }
	private final static String localizedName = CMLib.lang().L("Mindsap");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"POISONSAP"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override protected int POISON_TICKS(){return 50;} // 0 means no adjustment!
	@Override protected int POISON_DELAY(){return 1;}
	@Override protected String POISON_DONE(){return "Your thoughts clear up.";}
	@Override protected String POISON_START(){return "^G<S-NAME> seem(s) confused!^?";}
	@Override protected String POISON_AFFECT(){return "";}
	@Override protected String POISON_CAST(){return "^F^<FIGHT^><S-NAME> poison(s) <T-NAMESELF>!^</FIGHT^>^?";}
	@Override protected String POISON_FAIL(){return "<S-NAME> attempt(s) to poison <T-NAMESELF>, but fail(s).";}
	@Override protected int POISON_DAMAGE(){return 0;}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,affectableStats.getStat(CharStats.STAT_CONSTITUTION)-1);
		if(affectableStats.getStat(CharStats.STAT_CONSTITUTION)<=0)
			affectableStats.setStat(CharStats.STAT_CONSTITUTION,1);
		affectableStats.setStat(CharStats.STAT_INTELLIGENCE,affectableStats.getStat(CharStats.STAT_INTELLIGENCE)-10);
		if(affectableStats.getStat(CharStats.STAT_INTELLIGENCE)<=0)
			affectableStats.setStat(CharStats.STAT_INTELLIGENCE,1);
	}
}
