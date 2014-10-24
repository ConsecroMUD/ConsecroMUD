package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Undead_WeakEnergyDrain extends StdAbility
{
	@Override public String ID() { return "Undead_WeakEnergyDrain"; }
	private final static String localizedName = CMLib.lang().L("Weak Energy Drain");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Drained of Energy)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}
	private static final String[] triggerStrings =I(new String[] {"DRAINWEAKENERGY"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL;}
	public int levelsDown=1;
	public int direction=1;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null) return;
		if(levelsDown<0) return;
		final int attacklevel=affectableStats.attackAdjustment()/affectableStats.level();
		affectableStats.setLevel(affectableStats.level()-(levelsDown*direction));
		if(affectableStats.level()<=0)
		{
			levelsDown=-1;
			CMLib.combat().postDeath(invoker(),(MOB)affected,null);
		}
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()-(attacklevel*(levelsDown*direction)));
	}

	@Override
	public void affectCharState(MOB affected, CharState affectableState)
	{
		super.affectCharState(affected,affectableState);
		if(affected==null) return;
		final int hplevel=affectableState.getHitPoints()/affected.basePhyStats().level();
		affectableState.setHitPoints(affectableState.getHitPoints()-(hplevel*(levelsDown*direction)));
		final int manalevel=affectableState.getMana()/affected.basePhyStats().level();
		affectableState.setMana(affectableState.getMana()-(manalevel*(levelsDown*direction)));
		final int movelevel=affectableState.getMovement()/affected.basePhyStats().level();
		affectableState.setMovement(affectableState.getMovement()-(movelevel*(levelsDown*direction)));
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null) return;
		int newLevel=affected.basePhyStats().level()-(direction*(levelsDown-affectableStats.combinedSubLevels()));
		if(newLevel<0) newLevel=0;
		affectableStats.setClassLevel(affectableStats.getCurrentClass(),newLevel);
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if((canBeUninvoked())
		&&(ID().equals("Undead_WeakEnergyDrain")))
			mob.tell(L("The energy drain is lifted."));
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=null;
		Ability reAffect=null;
		if(mob.isInCombat())
		{
			if(mob.rangeToTarget()>0)
			{
				mob.tell(L("You are too far away to touch!"));
				return false;
			}
			final MOB victim=mob.getVictim();
				reAffect=victim.fetchEffect("Undead_WeakEnergyDrain");
			if(reAffect==null)
				reAffect=victim.fetchEffect("Undead_EnergyDrain");
			if(reAffect!=null) target=victim;
		}
		if(target==null)
			target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		String str=null;
		if(success)
		{
			str=auto?"":L("^S<S-NAME> extend(s) an energy draining hand to <T-NAMESELF>!^?");
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_UNDEAD|(auto?CMMsg.MASK_ALWAYS:0),str);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> drained!"));
					if(reAffect!=null)
					{
						if(reAffect instanceof Undead_WeakEnergyDrain)
							((Undead_WeakEnergyDrain)reAffect).levelsDown++;
						((StdAbility)reAffect).setTickDownRemaining(((StdAbility)reAffect).getTickDownRemaining()+5);
						mob.recoverPhyStats();
						mob.recoverCharStats();
						mob.recoverMaxState();
					}
					else
					{
						direction=1;
						if(target.charStats().getMyRace().racialCategory().equalsIgnoreCase("Undead"))
							direction=-1;
						success=maliciousAffect(mob,target,asLevel,10,-1)!=null;
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to drain <T-NAMESELF>, but fail(s)."));

		return success;
	}
}
