package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_Anger extends Prayer
{
	@Override public String ID() { return "Prayer_Anger"; }
	private final static String localizedName = CMLib.lang().L("Anger");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}

	private boolean anyoneIsFighting(Room R)
	{
		if(R==null) return false;
		for(int i=0;i<R.numInhabitants();i++)
		{
			final MOB inhab=R.fetchInhabitant(i);
			if((inhab!=null)&&(inhab.isInCombat()))
				return true;
		}
		return false;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(!anyoneIsFighting(mob.location()))
				return Ability.QUALITY_INDIFFERENT;
			if(mob.location().numInhabitants()>3)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		final boolean someoneIsFighting=anyoneIsFighting(mob.location());

		if((success)&&(!someoneIsFighting)&&(mob.location().numInhabitants()>3))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?L("A feeling of anger descends"):L("^S<S-NAME> rage(s) for anger.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				for(int i=0;i<mob.location().numInhabitants();i++)
				{
					final MOB inhab=mob.location().fetchInhabitant(i);
					if((inhab!=null)&&(inhab!=mob)&&(!inhab.isInCombat()))
					{
						int tries=0;
						MOB target=null;
						while((tries<100)&&(target==null))
						{
							target=mob.location().fetchRandomInhabitant();
							if(target!=null)
							{
								if(target==inhab) target=null;
								if(target==mob) target=null;
							}
							tries++;
						}
						final CMMsg amsg=CMClass.getMsg(mob,inhab,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0),null);
						if((target!=null)&&(mob.location().okMessage(mob,amsg)))
						{
							inhab.tell(L("You feel angry."));
							inhab.setVictim(target);
						}
					}
				}
			}
		}
		else
			maliciousFizzle(mob,null,L("<S-NAME> @x1 for rage, but nothing happens.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
