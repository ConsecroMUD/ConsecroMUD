package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Spell_DetectScrying extends Spell
{
	@Override public String ID() { return "Spell_DetectScrying"; }
	private final static String localizedName = CMLib.lang().L("Detect Scrying");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public int enchantQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(((MOB)target).isInCombat()||((MOB)target).isMonster())
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> incant(s) softly to <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final StringBuffer str=new StringBuffer("");
				if(target.session()!=null)
					for(final Session S1 : CMLib.sessions().localOnlineIterable())
						if(target.session().isBeingSnoopedBy(S1))
							str.append(L("@x1 is snooping on <T-NAME>.  ",S1.mob().name()));
				Ability A=target.fetchEffect("Spell_Scry");
				if((A!=null)&&(A.invoker()!=null))
					str.append(L("@x1 is scrying on <T-NAME>.",A.invoker().name()));
				A=target.fetchEffect("Spell_Claireaudience");
				if((A!=null)&&(A.invoker()!=null))
					str.append(L("@x1 is listening to <T-NAME>.",A.invoker().name()));
				A=target.fetchEffect("Spell_Clairevoyance");
				if((A!=null)&&(A.invoker()!=null))
					str.append(L("@x1 is watching <T-NAME>.",A.invoker().name()));
				if(str.length()==0)
					str.append(L("There doesn't seem to be anyone scrying on <T-NAME>."));
				CMLib.commands().postSay(mob,target,str.toString(),false,false);
			}
		}
		else
			beneficialVisualFizzle(mob,target,L("<S-NAME> incant(s) to <T-NAMESELF>, but the spell fizzles."));

		return success;
	}
}
