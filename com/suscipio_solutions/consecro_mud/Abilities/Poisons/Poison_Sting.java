package com.suscipio_solutions.consecro_mud.Abilities.Poisons;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Poison_Sting extends Poison
{
	@Override public String ID() { return "Poison_Sting"; }
	private final static String localizedName = CMLib.lang().L("Sting");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"POISONSTING"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override protected int POISON_TICKS(){return 10;} // 0 means no adjustment!
	@Override protected int POISON_DELAY(){return 2;}
	@Override protected String POISON_DONE(){return "The stinging poison runs its course.";}
	@Override protected String POISON_START(){return "^G<S-NAME> turn(s) green.^?";}
	@Override protected String POISON_AFFECT(){return "<S-NAME> cringe(s) from the poisonous itch.";}
	@Override protected String POISON_CAST(){return "^F^<FIGHT^><S-NAME> sting(s) <T-NAMESELF>!^</FIGHT^>^?";}
	@Override protected String POISON_FAIL(){return "<S-NAME> attempt(s) to sting <T-NAMESELF>, but fail(s).";}
	@Override protected int POISON_DAMAGE(){return (invoker!=null)?2:0;}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,affectableStats.getStat(CharStats.STAT_CONSTITUTION)-1);
		if(affectableStats.getStat(CharStats.STAT_CONSTITUTION)<=0)
			affectableStats.setStat(CharStats.STAT_CONSTITUTION,1);
	}
}
