package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Daydream extends Spell
{
	@Override public String ID() { return "Spell_Daydream"; }
	private final static String localizedName = CMLib.lang().L("Daydream");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ILLUSION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<1)
		{
			mob.tell(L("Invoke a daydream about what?"));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),L("^S<S-NAME> invoke(s) a day-dreamy spell.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				try
				{
					for(final Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
					{
						final Room R=(Room)r.nextElement();
						if(CMLib.flags().canAccess(mob,R))
						for(int i=0;i<R.numInhabitants();i++)
						{
							final MOB inhab=R.fetchInhabitant(i);
							if((inhab!=null)
							&&(!inhab.isMonster())
							&&(inhab.session().isAfk())
							&&(!CMLib.flags().isSleeping(inhab)))
							{
								msg=CMClass.getMsg(mob,inhab,this,verbalCastCode(mob,inhab,auto),null);
								if(R.okMessage(mob,msg))
									inhab.tell(L("You daydream @x1.",CMParms.combine(commands,0)));
							}
						}
					}
				}catch(final NoSuchElementException nse){}
			}
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to invoke a daydream, but fizzle(s) the spell."));


		// return whether it worked
		return success;
	}
}

