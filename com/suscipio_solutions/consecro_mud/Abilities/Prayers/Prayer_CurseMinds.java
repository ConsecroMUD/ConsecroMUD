package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Prayer_CurseMinds extends Prayer
{
	@Override public String ID() { return "Prayer_CurseMinds"; }
	private final static String localizedName = CMLib.lang().L("Curse Minds");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CURSING;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Cursed Mind)");
	@Override public String displayText() { return localizedStaticDisplay; }

	boolean notAgain=false;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!(affected instanceof MOB))
			return super.tick(ticking,tickID);

		if(!super.tick(ticking,tickID))
			return false;
		final MOB mob=(MOB)affected;
		if(mob.isInCombat())
		{
			final MOB newvictim=mob.location().fetchRandomInhabitant();
			if(newvictim!=mob) mob.setVictim(newvictim);
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
			mob.tell(L("Your mind feels less cursed."));
		CMLib.commands().postStand(mob,true);
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_MIND,affectableStats.getStat(CharStats.STAT_SAVE_MIND)-50);
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null) return false;

		boolean success=proficiencyCheck(mob,0,auto);
		boolean nothingDone=true;
		if(success)
		{
			for (final Object element : h)
			{
				final MOB target=(MOB)element;
				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB.  Then tell everyone else
				// what happened.
				final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,auto?"":L("^S<S-NAME> @x1 an unholy curse upon <T-NAMESELF>.^?",prayWord(mob)));
				final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MASK_MALICIOUS|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0),null);
				if((target!=mob)&&(mob.location().okMessage(mob,msg))&&(mob.location().okMessage(mob,msg2)))
				{
					mob.location().send(mob,msg);
					mob.location().send(mob,msg2);
					if((msg.value()<=0)&&(msg2.value()<=0))
					{
						success=maliciousAffect(mob,target,asLevel,15,-1)!=null;
						mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> look(s) confused!"));
					}
					nothingDone=false;
				}
			}
		}

		if(nothingDone)
			return maliciousFizzle(mob,null,L("<S-NAME> attempt(s) to curse everyone, but flub(s) it."));


		// return whether it worked
		return success;
	}
}
