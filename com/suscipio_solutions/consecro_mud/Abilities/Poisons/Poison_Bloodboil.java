package com.suscipio_solutions.consecro_mud.Abilities.Poisons;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Poison_Bloodboil extends Poison
{
	@Override public String ID() { return "Poison_Bloodboil"; }
	private final static String localizedName = CMLib.lang().L("Blood Boil");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"POISONBURN"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override protected int POISON_TICKS(){return 20;} // 0 means no adjustment!
	@Override protected int POISON_DELAY(){return 2;}
	@Override protected String POISON_DONE(){return "Your blood stops burning.";}
	@Override protected String POISON_START(){return "^R<S-NAME> turn(s) red.^?";}
	@Override protected String POISON_AFFECT(){return "<S-NAME> cringe(s) as <S-HIS-HER> blood burns.";}
	@Override protected String POISON_CAST(){return "^F^<FIGHT^><S-NAME> sting(s) <T-NAMESELF>!^</FIGHT^>^?";}
	@Override protected String POISON_FAIL(){return "<S-NAME> attempt(s) to sting <T-NAMESELF>, but fail(s).";}
	@Override protected int POISON_DAMAGE(){return (invoker!=null)?CMLib.dice().roll(1,2,0):0;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if(affected instanceof MOB)
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()-20);
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,affectableStats.getStat(CharStats.STAT_CONSTITUTION)-1);
		affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)-5);
		if(affectableStats.getStat(CharStats.STAT_CONSTITUTION)<=0)
			affectableStats.setStat(CharStats.STAT_CONSTITUTION,1);
		if(affectableStats.getStat(CharStats.STAT_STRENGTH)<=0)
			affectableStats.setStat(CharStats.STAT_STRENGTH,1);
	}
}
