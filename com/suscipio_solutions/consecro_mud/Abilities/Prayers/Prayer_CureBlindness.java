package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.MendingSkill;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_CureBlindness extends Prayer implements MendingSkill
{
	@Override public String ID() { return "Prayer_CureBlindness"; }
	private final static String localizedName = CMLib.lang().L("Cure Blindness");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_RESTORATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override public long flags(){return Ability.FLAG_HOLY;}

	@Override
	public boolean supportsMending(Physical item)
	{
		if(!(item instanceof MOB)) return false;
		final MOB caster=CMClass.getFactoryMOB();
		caster.basePhyStats().setLevel(CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL));
		caster.phyStats().setLevel(CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL));
		final boolean canMend=returnOffensiveAffects(caster,item).size()>0;
		caster.destroy();
		return canMend;
	}

	public List<Ability> returnOffensiveAffects(MOB caster, Physical fromMe)
	{
		final MOB newMOB=CMClass.getFactoryMOB();
		final MOB newerMOB=CMClass.getFactoryMOB();
		final Vector offenders=new Vector(1);

		final CMMsg msg=CMClass.getMsg(newMOB,newerMOB,null,CMMsg.MSG_LOOK,null);
		for(int a=0;a<fromMe.numEffects();a++) // personal
		{
			final Ability A=fromMe.fetchEffect(a);
			if(A!=null)
			{
				newMOB.recoverPhyStats();
				A.affectPhyStats(newMOB,newMOB.phyStats());
				if((!CMLib.flags().canSee(newMOB))
				   ||(!A.okMessage(newMOB,msg)))
				if((A.invoker()==null)
				   ||((A.invoker()!=null)
					  &&(A.invoker().phyStats().level())<=(caster.phyStats().level()+1+(2*getXLEVELLevel(caster)))))
						offenders.addElement(A);
			}
		}
		newMOB.destroy();
		newerMOB.destroy();
		return offenders;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(supportsMending(target))
					return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_OTHERS);
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		final List<Ability> offensiveAffects=returnOffensiveAffects(mob,target);

		if((success)&&(offensiveAffects.size()>0))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("A visible glow surrounds <T-NAME>."):L("^S<S-NAME> @x1 for <T-NAMESELF> to see the light.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				for(int a=offensiveAffects.size()-1;a>=0;a--)
					offensiveAffects.get(a).unInvoke();
			}
		}
		else
			beneficialWordsFizzle(mob,target,auto?"":L("<S-NAME> @x1 for <T-NAMESELF>, but nothing happens.",prayWord(mob)));

		// return whether it worked
		return success;
	}
}
