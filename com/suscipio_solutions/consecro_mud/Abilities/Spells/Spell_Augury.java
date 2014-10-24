package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Augury extends Spell
{
	@Override public String ID() { return "Spell_Augury"; }
	private final static String localizedName = CMLib.lang().L("Augury");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){	return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((commands.size()<1)&&(givenTarget==null))
		{
			mob.tell(L("Divine the fate of which direction?"));
			return false;
		}
		final String targetName=CMParms.combine(commands,0);

		Exit exit=null;
		Exit opExit=null;
		Room room=null;
		final int dirCode=Directions.getGoodDirectionCode(targetName);
		if(dirCode>=0)
		{
			exit=mob.location().getExitInDir(dirCode);
			room=mob.location().getRoomInDir(dirCode);
			if(room!=null)
				opExit=mob.location().getReverseExit(dirCode);
		}
		else
		{
			mob.tell(L("Divine the fate of which direction?"));
			return false;
		}
		if((exit==null)||(room==null))
		{
			mob.tell(L("You couldn't go that way if you wanted to!"));
			return false;
		}

		if(!super.invoke(mob,commands,null,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> point(s) <S-HIS-HER> finger @x1, incanting.^?",Directions.getDirectionName(dirCode)));
			if(mob.location().okMessage(mob,msg))
			{
				boolean aggressiveMonster=false;
				for(int m=0;m<room.numInhabitants();m++)
				{
					final MOB mon=room.fetchInhabitant(m);
					if(mon!=null)
						for(final Enumeration<Behavior> e=mob.behaviors();e.hasMoreElements();)
						{
							final Behavior B=e.nextElement();
							if((B!=null)&&(B.grantsAggressivenessTo(mob)))
							{
								aggressiveMonster=true;
								break;
							}
						}
				}
				mob.location().send(mob,msg);
				if((aggressiveMonster)
				||CMLib.flags().isDeadlyOrMaliciousEffect(room)
				||CMLib.flags().isDeadlyOrMaliciousEffect(exit)
				||((opExit!=null)&&(CMLib.flags().isDeadlyOrMaliciousEffect(opExit))))
					mob.tell(L("You feel going that way would be bad."));
				else
					mob.tell(L("You feel going that way would be ok."));
			}

		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> point(s) <S-HIS-HER> finger @x1, incanting, but then loses concentration.",Directions.getDirectionName(dirCode)));


		// return whether it worked
		return success;
	}
}
