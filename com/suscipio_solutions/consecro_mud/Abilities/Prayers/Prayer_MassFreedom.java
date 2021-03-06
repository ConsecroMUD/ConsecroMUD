package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.MendingSkill;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_MassFreedom extends Prayer implements MendingSkill
{
	@Override public String ID() { return "Prayer_MassFreedom"; }
	private final static String localizedName = CMLib.lang().L("Mass Freedom");
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
		final Vector offenders=new Vector(1);

		final CMMsg msg=CMClass.getMsg(newMOB,null,null,CMMsg.MSG_SIT,null);
		for(int a=0;a<fromMe.numEffects();a++) // personal
		{
			final Ability A=fromMe.fetchEffect(a);
			if(A!=null)
			{
				try
				{
					newMOB.recoverPhyStats();
					A.affectPhyStats(newMOB,newMOB.phyStats());
					final int clas=A.classificationCode()&Ability.ALL_ACODES;
					if((!CMLib.flags().aliveAwakeMobileUnbound(newMOB,true))
					   ||(CMath.bset(A.flags(),Ability.FLAG_BINDING))
					   ||(!A.okMessage(newMOB,msg)))
					if((A.invoker()==null)
					||((clas!=Ability.ACODE_SPELL)&&(clas!=Ability.ACODE_CHANT)&&(clas!=Ability.ACODE_PRAYER)&&(clas!=Ability.ACODE_SONG))
					||((A.invoker()!=null)
					   &&(A.invoker().phyStats().level()<=(caster.phyStats().level()+1+(2*super.getXLEVELLevel(caster))))))
					 	offenders.addElement(A);
				}
				catch(final Exception e)
				{}
			}
		}
		newMOB.destroy();
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
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?L("A feeling of freedom flows through the air"):L("^S<S-NAME> @x1 for freedom, and the area begins to fill with divine glory.^?",prayWord(mob)));
			final Room room=mob.location();
			if((room!=null)&&(room.okMessage(mob,msg)))
			{
				room.send(mob,msg);
				for(int i=0;i<room.numInhabitants();i++)
				{
					final MOB target=room.fetchInhabitant(i);
					if(target==null) break;

					final List<Ability> offensiveAffects=returnOffensiveAffects(mob,target);

					if(offensiveAffects.size()>0)
					{
						// it worked, so build a copy of this ability,
						// and add it to the affects list of the
						// affected MOB.  Then tell everyone else
						// what happened.
						for(int a=offensiveAffects.size()-1;a>=0;a--)
							offensiveAffects.get(a).unInvoke();
						if((!CMLib.flags().stillAffectedBy(target,offensiveAffects,false))&&(target.location()!=null))
							target.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> seem(s) less constricted."));
					}
				}
			}
		}
		else
			this.beneficialWordsFizzle(mob,null,L("<S-NAME> @x1 for freedom, but nothing happens.",prayWord(mob)));

		// return whether it worked
		return success;
	}
}
