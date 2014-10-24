package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Set;
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
public class Prayer_CallUndead extends Prayer
{
	@Override public String ID() { return "Prayer_CallUndead"; }
	private final static String localizedName = CMLib.lang().L("Call Undead");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_DEATHLORE;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public long flags(){return Ability.FLAG_UNHOLY|Ability.FLAG_TRANSPORTING|Ability.FLAG_SUMMONING;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Room oldRoom=null;
		MOB target=null;
		final Set<MOB> H=mob.getGroupMembers(new HashSet<MOB>());
		if((H.size()==0)||((H.size()==1)&&(H.contains(mob))))
		{
			mob.tell(L("You don't have any controlled undead!"));
			return false;
		}

		boolean allHere=true;
		for (final Object element : H)
		{
			final MOB M=(MOB)element;
			if((M!=mob)&&(M.location()!=mob.location())&&(M.location()!=null))
			{
				allHere=false;
				if((CMLib.flags().canAccess(mob,M.location()))
				&&(!CMLib.flags().isTracking(M)))
				{
					target=M;
					oldRoom=M.location();
					break;
				}
			}
		}
		if((target==null)&&(allHere))
		{
			mob.tell(L("Better look around first."));
			return false;
		}

		if(target==null)
		{
			mob.tell(L("Either they are all en route, or you can not fixate on your undead."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final int adjustment=(target.phyStats().level()-(mob.phyStats().level()+(2*getXLEVELLevel(mob))))*3;
		final boolean success=proficiencyCheck(mob,-adjustment,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> call(s) <S-HIS-HER> undead to come to <S-HIM-HER>!^?"));
			if((mob.location().okMessage(mob,msg))&&(oldRoom != null)&&(oldRoom.okMessage(mob,msg)))
			{
				mob.location().send(mob,msg);
				oldRoom.sendOthers(mob,msg);
				final MOB follower=target;
				final Room newRoom=mob.location();
				final Ability A=CMClass.getAbility("Skill_Track");
				if(A!=null)
				{
					A.invoke(follower,CMParms.parse("\""+CMLib.map().getExtendedRoomID(newRoom)+"\""),newRoom,true,0);
					return true;
				}
			}
		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> attempt(s) to call <S-HIS-HER> undead, but fail(s)."));

		// return whether it worked
		return success;
	}
}
