package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Amputator;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.MendingSkill;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_Regrowth extends Prayer implements MendingSkill
{
	@Override public String ID() { return "Prayer_Regrowth"; }
	private final static String localizedName = CMLib.lang().L("Regrowth");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_HEALING;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_HEALINGMAGIC;}
	@Override protected int overrideMana(){return Ability.COST_ALL;}
	private static Vector limbsToRegrow = null;

	public Prayer_Regrowth()
	{
		super();
		if(limbsToRegrow==null)
		{
			limbsToRegrow = new Vector();
			limbsToRegrow.addElement("EYE");
			limbsToRegrow.addElement("LEG");
			limbsToRegrow.addElement("FOOT");
			limbsToRegrow.addElement("ARM");
			limbsToRegrow.addElement("HAND");
			limbsToRegrow.addElement("EAR");
			limbsToRegrow.addElement("NOSE");
			limbsToRegrow.addElement("TAIL");
			limbsToRegrow.addElement("WING");
			limbsToRegrow.addElement("ANTENEA");
		}
	}

	@Override
	public boolean supportsMending(Physical item)
	{
		if(!(item instanceof MOB)) return false;
		return (item.fetchEffect("Amputation")!=null);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(!supportsMending(target))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null)return false;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> become(s) surrounded by a bright light."):L("^S<S-NAME> @x1 over <T-NAMESELF> for restorative healing.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Ability A=target.fetchEffect("Amputation");
				if(A!=null)
				{
					final Amputator Amp=(Amputator)A;
					final List<String> missing = Amp.missingLimbNameSet();
					String LookingFor = null;
					boolean found = false;
					String missLimb=null;
					for(int i=0;i<limbsToRegrow.size();i++)
					{
						LookingFor = (String)limbsToRegrow.elementAt(i);
						for(int j=0;j<missing.size();j++)
						{
							missLimb = missing.get(j);
							if(missLimb.toUpperCase().indexOf(LookingFor)>=0)
							{
								found = true;
								break;
							}
						}
						if(found) break;
					}
					if((found)&&(missLimb!=null))
						Amp.unamputate(target, Amp, missLimb.toLowerCase());
					target.recoverCharStats();
					target.recoverPhyStats();
					target.recoverMaxState();
				}
				mob.location().recoverRoomStats();
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 over <T-NAMESELF>, but @x2 does not heed.",prayWord(mob),hisHerDiety(mob)));
		// return whether it worked
		return success;
	}
}
