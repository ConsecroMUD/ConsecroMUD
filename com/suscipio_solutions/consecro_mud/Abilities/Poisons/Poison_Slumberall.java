package com.suscipio_solutions.consecro_mud.Abilities.Poisons;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Poison_Slumberall extends Poison
{
	@Override public String ID() { return "Poison_Slumberall"; }
	private final static String localizedName = CMLib.lang().L("Slumberall");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"POISONSLEEP"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override protected int POISON_TICKS(){return 50;} // 0 means no adjustment!
	@Override protected int POISON_DELAY(){return 1;}
	@Override protected String POISON_DONE(){return "You don't feel so drowsy anymore.";}
	@Override protected String POISON_START(){return null;}
	@Override protected String POISON_AFFECT(){return "";}
	@Override protected String POISON_CAST(){return "^F^<FIGHT^><S-NAME> poison(s) <T-NAMESELF>!^</FIGHT^>^?";}
	@Override protected String POISON_FAIL(){return "<S-NAME> attempt(s) to poison <T-NAMESELF>, but fail(s).";}
	@Override protected int POISON_DAMAGE(){return 0;}
	protected boolean fallenYet=false;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if(affected instanceof MOB)
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SLEEPING);
	}

	@Override
	public void unInvoke()
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			CMLib.commands().postStand(mob,true);
		}
		super.unInvoke();
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if(msg.amITarget(mob)&&(fallenYet)&&(msg.targetMinor()==CMMsg.TYP_DAMAGE))
			unInvoke();
		else
		if((msg.amISource(mob))
		&&(!msg.sourceMajor(CMMsg.MASK_ALWAYS))
		&&(msg.sourceMajor()>0)
		&&(msg.sourceMinor()!=CMMsg.TYP_SLEEP))
		{
			mob.tell(L("You are way too drowsy."));
			return false;
		}
		return super.okMessage(myHost,msg);
	}


	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if(mob==null) return false;
		if((!fallenYet)&&(mob.location()!=null))
		{
			fallenYet=true;
			mob.location().show(mob,null,CMMsg.MSG_SLEEP,L("<S-NAME> fall(s) asleep!"));
			mob.recoverPhyStats();
		}
		return true;
	}
}
