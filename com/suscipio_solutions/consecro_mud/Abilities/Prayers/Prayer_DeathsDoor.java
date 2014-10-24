package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_DeathsDoor extends Prayer
{
	@Override public String ID() { return "Prayer_DeathsDoor"; }
	private final static String localizedName = CMLib.lang().L("Deaths Door");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Deaths Door)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_HOLYPROTECTION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public long flags(){return Ability.FLAG_HOLY;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			final Room startRoom=mob.getStartRoom();
			if(msg.amISource(mob)
			&&(msg.sourceMinor()==CMMsg.TYP_DEATH)
			&&(startRoom!=null))
			{
				if(mob.fetchAbility("Dueling")!=null)
					return super.okMessage(host,msg);
				final Room oldRoom=mob.location();
				mob.resetToMaxState();
				oldRoom.show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> pulled back from death's door!"));
				startRoom.bringMobHere(mob,false);
				unInvoke();
				for(int a=mob.numEffects()-1;a>=0;a--) // personal effects
				{
					final Ability A=mob.fetchEffect(a);
					if(A!=null) A.unInvoke();
				}
				if((oldRoom!=startRoom) && oldRoom.isInhabitant(mob) && startRoom.isInhabitant(mob))
					oldRoom.delInhabitant(mob); // hopefully unnecessary
				return false;
			}
		}
		return super.okMessage(host,msg);
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
			mob.tell(L("Your deaths door protection fades."));
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
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> become(s) guarded at deaths door!"):L("^S<S-NAME> @x1 for <T-NAME> to be guarded at deaths door!^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 for <T-NAMESELF>, but there is no answer.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
