package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_ChainLightening extends Spell
{
	@Override public String ID() { return "Spell_ChainLightening"; }
	private final static String localizedName = CMLib.lang().L("Chain Lightning");
	@Override public String name() { return localizedName; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(2);}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){	return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}
	@Override public long flags(){return Ability.FLAG_AIRBASED;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null) h=new HashSet<MOB>();

		final Set<MOB> myGroup=mob.getGroupMembers(new HashSet<MOB>());
		final Vector targets=new Vector(h);
		for (final Object element : h)
			targets.addElement(element);
		for (final Object element : myGroup)
		{
			final MOB M=(MOB)element;
			if((M!=mob)&&(!targets.contains(M))) targets.addElement(M);
		}
		if(!targets.contains(mob))
			targets.addElement(mob);

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final int maxDie=(adjustedLevel(mob,asLevel)+(2*super.getX1Level(mob)))/2;
		int damage = CMLib.dice().roll(maxDie,8,maxDie);

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			if(mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),L(auto?"A thunderous crack of lightning erupts!":"^S<S-NAME> invoke(s) a thunderous crack of lightning.^?")+CMLib.protocol().msp("lightning.wav",40)))
			{
				while(damage>0)
				{
					final int oldDamage=damage;
					for(int i=0;i<targets.size();i++)
					{
						final MOB target=(MOB)targets.elementAt(i);
						if(target.amDead()||(target.location()!=mob.location()))
						{
							int count=0;
							for(int i2=0;i2<targets.size();i2++)
							{
								final MOB M2=(MOB)targets.elementAt(i2);
								if((!M2.amDead())
								   &&(mob.location()!=null)
								   &&(mob.location().isInhabitant(M2))
								   &&(M2.location()==mob.location()))
									 count++;
							}
							if(count<2)
								return true;
							continue;
						}

						// it worked, so build a copy of this ability,
						// and add it to the affects list of the
						// affected MOB.  Then tell everyone else
						// what happened.
						final boolean oldAuto=auto;
						if((target==mob)||(myGroup.contains(target)))
						   auto=true;
						final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
						final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_ELECTRIC|(auto?CMMsg.MASK_ALWAYS:0),null);
						auto=oldAuto;
						if((mob.location().okMessage(mob,msg))&&((mob.location().okMessage(mob,msg2))))
						{
							mob.location().send(mob,msg);
							mob.location().send(mob,msg2);
							invoker=mob;

							int dmg=damage;
							if((msg.value()>0)||(msg2.value()>0)||myGroup.contains(target)||(mob==target))
								dmg = (int)Math.round(CMath.div(dmg,2.0));
							if(target.location()==mob.location())
							{
								CMLib.combat().postDamage(mob,target,this,dmg,CMMsg.MASK_ALWAYS|CMMsg.TYP_ELECTRIC,Weapon.TYPE_STRIKING,"The bolt <DAMAGE> <T-NAME>!");
								damage = (int)Math.round(CMath.div(damage,2.0));
								if(damage<5){ damage=0; break;}
							}
						}
					}
					if(oldDamage==damage)
						break;
				}
			}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> attempt(s) to invoke a ferocious spell, but the spell fizzles."));


		// return whether it worked
		return success;
	}
}
