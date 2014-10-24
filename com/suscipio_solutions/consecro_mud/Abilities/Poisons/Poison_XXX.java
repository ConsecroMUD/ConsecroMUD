package com.suscipio_solutions.consecro_mud.Abilities.Poisons;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Poison_XXX extends Poison
{
	@Override public String ID() { return "Poison_XXX"; }
	private final static String localizedName = CMLib.lang().L("XXX");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"POISONXXX"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override protected int POISON_TICKS(){return 25;} // 0 means no adjustment!
	@Override protected int POISON_DELAY(){return 2;}
	@Override protected String POISON_DONE(){return "The poison runs its course.";}
	@Override protected String POISON_START(){return "^G<S-NAME> turn(s) green.^?";}
	@Override protected String POISON_AFFECT(){return "<S-NAME> cringe(s) in horrible pain as the poison courses through <S-HIS-HER> blood.";}
	@Override protected String POISON_CAST(){return "^F^<FIGHT^><S-NAME> bite(s) <T-NAMESELF>!^</FIGHT^>^?";}
	@Override protected String POISON_FAIL(){return "<S-NAME> attempt(s) to bite <T-NAMESELF>, but fail(s).";}
	@Override protected int POISON_DAMAGE(){return (invoker!=null)?CMLib.dice().roll(1,99,1):0;}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,affectableStats.getStat(CharStats.STAT_CONSTITUTION)-5);
		affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)-1);
		if(affectableStats.getStat(CharStats.STAT_CONSTITUTION)<=0)
			affectableStats.setStat(CharStats.STAT_CONSTITUTION,1);
		if(affectableStats.getStat(CharStats.STAT_STRENGTH)<=0)
			affectableStats.setStat(CharStats.STAT_STRENGTH,1);
	}
}
