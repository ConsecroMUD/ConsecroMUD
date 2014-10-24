package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_AuraHeal extends Prayer
{
	@Override public String ID() { return "Prayer_AuraHeal"; }
	private final static String localizedName = CMLib.lang().L("Aura of Healing");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Heal Aura)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_HEALING;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_HEALINGMAGIC;}
	private int ratingTickDown=4;

	public Prayer_AuraHeal()
	{
		super();

		ratingTickDown = 4;
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof Room)))
			return;
		final Room R=(Room)affected;

		super.unInvoke();

		if(canBeUninvoked())
			R.showHappens(CMMsg.MSG_OK_VISUAL,L("The healing aura around you fades."));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected==null)||(!(affected instanceof Room)))
			return super.tick(ticking,tickID);

		if((--ratingTickDown)>=0) return super.tick(ticking,tickID);
		ratingTickDown=4;

		HashSet H=null;
		if((invoker()!=null)&&(invoker().location()==affected))
		{
			H=new HashSet();
			invoker().getGroupMembers(H);
		}
		final Room R=(Room)affected;
		for(int i=0;i<R.numInhabitants();i++)
		{
			final MOB M=R.fetchInhabitant(i);
			if((M!=null)
			   &&(M.curState().getHitPoints()<M.maxState().getHitPoints())
			   &&((H==null)
				||(M.getVictim()==null)
				||(!H.contains(M.getVictim()))))
			{
				final int oldHP=M.curState().getHitPoints();
				if(invoker()!=null)
				{
					final int healing=CMLib.dice().roll(2,adjustedLevel(invoker(),0),4);
					CMLib.combat().postHealing(invoker(),M,this,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,healing,null);
				}
				else
				{
					final int healing=CMLib.dice().roll(2,CMLib.ableMapper().lowestQualifyingLevel(ID()),4);
					CMLib.combat().postHealing(M,M,this,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,healing,null);
				}
				if(M.curState().getHitPoints()>oldHP)
					M.tell(L("You feel a little better!"));
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(((MOB)target).charStats().getMyRace().racialCategory().equals("Undead"))
					return Ability.QUALITY_INDIFFERENT;
				if(target!=mob)
				{
					if(((MOB)target).charStats().getMyRace().racialCategory().equals("Undead"))
						return super.castingQuality(mob, target,Ability.QUALITY_MALICIOUS);
				}
			}
			return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room target=mob.location();
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("The aura of healing is already here."));
			return false;
		}
		if(target.fetchEffect("Prayer_AuraHarm")!=null)
		{
			target.fetchEffect("Prayer_AuraHarm").unInvoke();
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 for all to feel better.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("A healing aura descends over the area!"));
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 for an aura of healing, but <S-HIS-HER> plea is not answered.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
