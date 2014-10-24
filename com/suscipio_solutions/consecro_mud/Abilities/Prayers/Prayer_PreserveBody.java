package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Prayer_PreserveBody extends Prayer
{
	@Override public String ID() { return "Prayer_PreserveBody"; }
	private final static String localizedName = CMLib.lang().L("Preserve Body");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_NEUTRALIZATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;

		if(target==mob)
		{
			mob.tell(L("@x1 doesn't look dead yet.",target.name(mob)));
			return false;
		}
		if(!(target instanceof DeadBody))
		{
			mob.tell(L("You can't preserve that."));
			return false;
		}

		final DeadBody body=(DeadBody)target;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 to preserve <T-NAMESELF>.^?",prayForWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				body.setExpirationDate(0);
				CMLib.threads().deleteTick(body,Tickable.TICKID_DEADBODY_DECAY);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 to preserve <T-NAMESELF>, but fail(s) miserably.",prayForWord(mob)));

		// return whether it worked
		return success;
	}
}
