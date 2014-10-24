package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Druid_RecoverVoice extends StdAbility
{
	@Override public String ID() { return "Druid_RecoverVoice"; }
	private final static String localizedName = CMLib.lang().L("Recover Voice");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	private static final String[] triggerStrings =I(new String[] {"VRECOVER","RECOVERVOICE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_FITNESS; }


	public List<Ability> returnOffensiveAffects(MOB caster, Physical fromMe)
	{
		final MOB newMOB=CMClass.getFactoryMOB();
		final Vector offenders=new Vector(1);

		for(int a=0;a<fromMe.numEffects();a++) // personal
		{
			final Ability A=fromMe.fetchEffect(a);
			if(A!=null)
			{
				newMOB.recoverPhyStats();
				A.affectPhyStats(newMOB,newMOB.phyStats());
				if((!CMLib.flags().canSpeak(newMOB))
				&&((A.invoker()==null)
				   ||((A.invoker()!=null)
					  &&(A.invoker().phyStats().level()<=(caster.phyStats().level()+10+(2*super.getXLEVELLevel(caster)))))))
						offenders.addElement(A);
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
				if(returnOffensiveAffects(mob,(target)).size()==0)
					return Ability.QUALITY_INDIFFERENT;
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

		final List<Ability> offensiveAffects=returnOffensiveAffects(mob,mob);
		if((!success)||(offensiveAffects.size()==0))
			mob.tell(L("You failed in your vocal meditation."));
		else
		{
			final CMMsg msg=CMClass.getMsg(mob,null,null,CMMsg.TYP_GENERAL|CMMsg.MASK_ALWAYS|CMMsg.MASK_MAGIC,null);
			if(mob.location().okMessage(mob,msg))
			{
				for(int a=offensiveAffects.size()-1;a>=0;a--)
					offensiveAffects.get(a).unInvoke();
			}
		}
		return success;
	}
}

