package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_DispelMagic extends Spell
{
	@Override public String ID() { return "Spell_DispelMagic"; }
	private final static String localizedName = CMLib.lang().L("Dispel Magic");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return CAN_ITEMS|CAN_MOBS|CAN_EXITS|CAN_ROOMS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			Ability A=null;
			if(target==mob)
			{
				for(final Enumeration<Ability> a=mob.effects();a.hasMoreElements();)
				{
					A=a.nextElement();
					if((A!=null)
					&&(A.canBeUninvoked())
					&&(A.abstractQuality()==Ability.QUALITY_MALICIOUS)
					&&(A.invoker()!=mob)
					&&(A.invoker().phyStats().level()<=mob.phyStats().level()+5))
						return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
				}
			}
			else
			if(target instanceof MOB)
			{
				for(final Enumeration<Ability> a=((MOB)target).personalEffects();a.hasMoreElements();)
				{
					A=a.nextElement();
					if((A!=null)
					&&((A.abstractQuality()==Ability.QUALITY_BENEFICIAL_OTHERS)
						||(A.abstractQuality()==Ability.QUALITY_BENEFICIAL_SELF))
					&&(A.invoker()==((MOB)target))
					&&(A.invoker().phyStats().level()<=mob.phyStats().level()+5))
						return super.castingQuality(mob, target,Ability.QUALITY_MALICIOUS);
				}
			}
			if((mob.isMonster())&&(mob.isInCombat()))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_ANY);
		if(target==null) return false;

		Ability revokeThis=null;
		boolean foundSomethingAtLeast=false;
		final boolean admin=CMSecurity.isASysOp(mob);
		for(int a=0;a<target.numEffects();a++)
		{
			final Ability A=target.fetchEffect(a);
			if((A!=null)
			&&(A.canBeUninvoked())
			&&(((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SPELL)
			   ||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SONG)
			   ||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_PRAYER)
			   ||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SONG)
			   ||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_CHANT)))
			{
				foundSomethingAtLeast=true;
				if((A.invoker()!=null)
				&&((A.invoker()==mob)
					||(A.invoker().phyStats().level()<=mob.phyStats().level()+5)
					||admin))
					revokeThis=A;
			}
		}

		if(revokeThis==null)
		{
			if(foundSomethingAtLeast)
				mob.tell(mob,target,null,L("The magic on <T-NAME> appears too powerful to dispel."));
			else
			if(auto)
				mob.tell(L("Nothing seems to be happening."));
			else
				mob.tell(mob,target,null,L("<T-NAME> do(es) not appear to be affected by anything you can dispel."));
			return false;
		}


		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int diff=revokeThis.invoker().phyStats().level()-mob.phyStats().level();
		if(diff<0) diff=0;
		else diff=diff*-20;

		final boolean success=proficiencyCheck(mob,diff,auto);
		if(success)
		{
			int affectType=verbalCastCode(mob,target,auto);
			if(((!mob.isMonster())&&(target instanceof MOB)&&(!((MOB)target).isMonster()))
			||(mob==target)
			||(mob.getGroupMembers(new HashSet<MOB>()).contains(target)))
				affectType=CMMsg.MSG_CAST_VERBAL_SPELL;
			if(auto) affectType=affectType|CMMsg.MASK_ALWAYS;

			final CMMsg msg=CMClass.getMsg(mob,target,this,affectType,auto?L("@x1 is dispelled from <T-NAME>.",revokeThis.name()):L("^S<S-NAME> dispel(s) @x1 from <T-NAMESELF>.^?",revokeThis.name()));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				revokeThis.unInvoke();
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to dispel @x1 from <T-NAMESELF>, but flub(s) it.",revokeThis.name()));


		// return whether it worked
		return success;
	}
}
