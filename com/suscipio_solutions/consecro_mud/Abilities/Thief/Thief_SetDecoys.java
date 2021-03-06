package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_SetDecoys extends ThiefSkill implements Trap
{
	@Override public String ID() { return "Thief_SetDecoys"; }
	private final static String localizedName = CMLib.lang().L("Set Decoys");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return Ability.CAN_ROOMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"SETDECOYS","DECOYS"});
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_DECEPTIVE;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}

	@Override public boolean isABomb(){return false;}
	@Override public void activateBomb(){}
	@Override public boolean disabled(){return false;}
	@Override public void disable(){ unInvoke();}
	@Override public void setReset(int Reset){}
	@Override public int getReset(){return 0;}
	@Override public boolean maySetTrap(MOB mob, int asLevel){return false;}
	@Override public boolean canSetTrapOn(MOB mob, Physical P){return false;}
	@Override public List<Item> getTrapComponents() { return new Vector(); }
	@Override public String requiresToSet(){return "";}
	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{maliciousAffect(mob,P,qualifyingClassLevel+trapBonus,0,-1); return (Trap)P.fetchEffect(ID());}
	private int lastSet=0;

	@Override public boolean sprung(){return false;}

	@Override
	public void spring(MOB mob)
	{
		if((mob==null)||(invoker()==null)) return;
		final Room R=mob.location();
		if(R==null) return;
		if((!invoker().mayIFight(mob))||(!mob.isInCombat())||(CMLib.dice().rollPercentage()<mob.charStats().getSave(CharStats.STAT_SAVE_TRAPS)-(getXLEVELLevel(invoker())*5)))
			R.show(mob,affected,this,CMMsg.MSG_OK_ACTION,L("A decoy pops up, prompting <S-NAME> to glance toward(s) it, but <S-HE-SHE> <S-IS-ARE> not fooled."));
		else
		if(R.show(mob,null,this,CMMsg.MSG_OK_VISUAL,L("A decoy pops up, confusing <S-NAME>!")))
		{
			int max=R.maxRange();
			final int level=getXLEVELLevel(invoker())+2;
			if(level<max) max=level;
			while((mob.isInCombat())&&(mob.rangeToTarget()<max))
			{
				final int r=mob.rangeToTarget();
				if(!R.show(mob,mob.getVictim(),null,CMMsg.MSG_RETREAT,L("<S-NAME> advance(s) toward(s) the decoy.")))
					break;
				if(mob.rangeToTarget()==r)
					break;
			}
		}
		// does not set sprung flag -- as this trap never goes out of use
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((--lastSet)<=0)
		{
			lastSet=5;
			final MOB mob=invoker();
			if((mob==null)||(!(affected instanceof Room)))
			{
				unInvoke();
				return false;
			}
			final Room R=(Room)affected;
			boolean combat=false;
			int numWhoCanSee=0;
			for(int m=0;m<R.numInhabitants();m++)
			{
				final MOB M=R.fetchInhabitant(m);
				if(M!=null)
				{
					if(CMLib.flags().canBeSeenBy(R,M))
					{
						numWhoCanSee++;
						combat=combat||((M!=mob)&&(M.getVictim()==mob));
					}
				}
			}
			MOB target=null;
			int tries=20;
			while(combat&&(R.numInhabitants()>1)&&(target==null)&&((--tries)>=0))
			{
				target=R.fetchRandomInhabitant();
				if((target==mob)||(target.getVictim()!=mob)||(!CMLib.flags().canBeSeenBy(R,target)))
					target=null;
			}
			if(target!=null)
				spring(target);
			else
			if(numWhoCanSee>1)
				R.showHappens(CMMsg.MSG_OK_VISUAL,L("A decoy pops up, causing everyone's gaze to be momentarily distracted towards it."));
			else
			if(numWhoCanSee==1)
				R.showHappens(CMMsg.MSG_OK_VISUAL,L("A decoy pops up, causing your gaze to be momentarily distracted towards it."));
		}
		return true;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
			if(target != null)
			{
				if(target.fetchEffect(ID())!=null)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=(givenTarget!=null)?givenTarget:mob.location();
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("Decoys have already been set here."));
			return false;
		}
		if(mob.isInCombat())
		{
			mob.tell(L("Not while in combat!"));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			if(mob.location().show(mob,target,(auto?CMMsg.MASK_ALWAYS:0)|CMMsg.MSG_THIEF_ACT,L("<S-NAME> set(s) several decoys around the room.")))
				maliciousAffect(mob,target,asLevel,0,-1);
			else
				success=false;
		}
		else
			maliciousFizzle(mob,target,L("<S-NAME> fail(s) to set <S-HIS-HER> decoys properly."));
		return success;
	}
}
