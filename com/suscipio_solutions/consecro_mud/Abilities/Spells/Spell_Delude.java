package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Delude extends Spell
{
	@Override public String ID() { return "Spell_Delude"; }
	private final static String localizedName = CMLib.lang().L("Delude");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Delude spell)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	int previousAlignment=500;
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_TRANSMUTATION;}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
		{
			if(mob.playerStats()!=null)
				mob.playerStats().setLastUpdated(0);
			CMLib.factions().postFactionChange(mob,this, CMLib.factions().AlignID(), previousAlignment-mob.fetchFaction(CMLib.factions().AlignID()));
			if(mob.fetchFaction(CMLib.factions().AlignID()) != previousAlignment)
				mob.addFaction(CMLib.factions().AlignID(), previousAlignment);
			mob.tell(L("Your attitude returns to normal."));
			CMLib.utensils().confirmWearability(mob);
		}
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already deluding others."));
			return false;
		}
		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);


		if((success)&&(CMLib.factions().getFaction(CMLib.factions().AlignID())!=null))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> incant(s) and meditate(s).^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					previousAlignment=target.fetchFaction(CMLib.factions().AlignID());

					target.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> undergo(es) a change of attitude"));
					success=beneficialAffect(mob,target,asLevel,0)!=null;
					if(success)
					{
						int which=0;
						if(CMLib.flags().isEvil(target))
							which=1;
						else
						if(CMLib.flags().isGood(target))
							which=2;
						else
						if(CMLib.dice().rollPercentage()>50)
							which=1;
						else
							which=2;
						Enumeration e;
						switch(which)
						{
							case 1:
								// find a good range, set them within that
								int newAlign=0;
								e=CMLib.factions().getRanges(CMLib.factions().AlignID());
								if(e!=null)
								for(;e.hasMoreElements();)
								{
									final Faction.FRange R=(Faction.FRange)e.nextElement();
									if(R.alignEquiv()==Faction.Align.GOOD)
									{
										newAlign = R.random();
										break;
									}
								}
								CMLib.factions().postFactionChange(target,this, CMLib.factions().AlignID(), newAlign-target.fetchFaction(CMLib.factions().AlignID()));
								CMLib.utensils().confirmWearability(target);
								return true;
							case 2:
								// find an evil range, set them within that
								newAlign=0;
								e=CMLib.factions().getRanges(CMLib.factions().AlignID());
								if(e!=null)
								for(;e.hasMoreElements();)
								{
									final Faction.FRange R=(Faction.FRange)e.nextElement();
									if(R.alignEquiv()==Faction.Align.EVIL)
									{
										newAlign = R.random();
										break;
									}
								}
								CMLib.factions().postFactionChange(target,this, CMLib.factions().AlignID(), newAlign-target.fetchFaction(CMLib.factions().AlignID()));
								CMLib.utensils().confirmWearability(target);
								return true;
						}
					}
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> incant(s) and meditate(s), but fizzle(s) the spell."));


		// return whether it worked
		return success;
	}
}
