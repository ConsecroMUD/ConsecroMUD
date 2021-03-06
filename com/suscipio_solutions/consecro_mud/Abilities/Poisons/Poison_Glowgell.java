package com.suscipio_solutions.consecro_mud.Abilities.Poisons;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Poison_Glowgell extends Poison
{
	@Override public String ID() { return "Poison_Glowgell"; }
	private final static String localizedName = CMLib.lang().L("Glowgell");
	@Override public String name() { return localizedName; }
	@Override
	protected int canAffectCode(){return Ability.CAN_MOBS
										 |Ability.CAN_ITEMS
										 |Ability.CAN_EXITS;}
	@Override protected int POISON_DAMAGE(){return 0;}
	@Override protected String POISON_DONE(){return "";}
	@Override protected String POISON_START(){return "^G<S-NAME> start(s) glowing!^?";}
	@Override protected String POISON_AFFECT(){return "";}
	@Override protected String POISON_CAST(){return "^F^<FIGHT^><S-NAME> attempt(s) to smear something on <T-NAMESELF>!^</FIGHT^>^?";}
	@Override protected String POISON_FAIL(){return "<S-NAME> attempt(s) to smear something on <T-NAMESELF>, but fail(s).";}

	@Override
	protected boolean catchIt(MOB mob, Physical target)
	{
		return false;
	}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_GLOWING);
	}
}
