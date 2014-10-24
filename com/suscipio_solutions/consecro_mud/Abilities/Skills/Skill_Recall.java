package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Skill_Recall extends StdSkill
{
	@Override public String ID() { return "Skill_Recall"; }
	private final static String localizedName = CMLib.lang().L("Recall");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"RECALL","/"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean group=false;//"GROUP".startsWith(CMParms.combine(commands,0).toUpperCase());
		final boolean success=(!mob.isInCombat())||proficiencyCheck(mob,getXLEVELLevel(mob)*10,auto);
		if(success)
		{
			final Room recalledRoom=mob.location();
			if(recalledRoom == null) return false;
			Room recallRoom=CMLib.map().getStartRoom(mob);
			if((recallRoom==null)&&(!mob.isMonster()))
			{
				mob.setStartRoom(CMLib.login().getDefaultStartRoom(mob));
				recallRoom=CMLib.map().getStartRoom(mob);
			}

			if(recallRoom == null)
			{
				mob.tell(L("You've nowhere to recall TO!"));
				return false;
			}
			CMMsg msg=CMClass.getMsg(mob,recalledRoom,this,CMMsg.MSG_RECALL,CMMsg.MSG_LEAVE,CMMsg.MSG_RECALL,auto?L("<S-NAME> disappear(s) into the Java Plane!"):L("<S-NAME> recall(s) body and spirit to the Java Plane!"));
			CMMsg msg2=CMClass.getMsg(mob,recallRoom,this,CMMsg.MASK_MOVE|CMMsg.TYP_RECALL,CMMsg.MASK_MOVE|CMMsg.MSG_ENTER,CMMsg.MASK_MOVE|CMMsg.TYP_RECALL,null);
			if(((recalledRoom.okMessage(mob,msg))&&(recallRoom.okMessage(mob,msg2)))
			||CMSecurity.isAllowed(mob,recalledRoom,CMSecurity.SecFlag.GOTO))
			{
				if(mob.isInCombat())
					CMLib.commands().postFlee(mob,"NOWHERE");
				recalledRoom.send(mob,msg);
				recallRoom.send(mob,msg2);
				if(recalledRoom.isInhabitant(mob))
				{
					if(recallRoom.isInhabitant(mob)&&(recallRoom==recalledRoom))
						beneficialWordsFizzle(mob,null,L("<S-NAME> attempt(s) to recall, but go(es) nowhere."));
					else
						recallRoom.bringMobHere(mob,false);
				}
				for(int f=0;f<mob.numFollowers();f++)
				{
					final MOB follower=mob.fetchFollower(f);

					if((follower!=null)
					&&(follower.isMonster())
					&&(!follower.isPossessing())
					&&(CMLib.flags().isInTheGame(follower,true))
					&&(!follower.isAttribute(MOB.Attrib.AUTOGUARD)))
					{
						Room fRecalledRoom=recalledRoom;
						if(group)fRecalledRoom=follower.location();
						msg=CMClass.getMsg(follower,fRecalledRoom,this,CMMsg.MSG_RECALL,CMMsg.MSG_LEAVE,CMMsg.MSG_RECALL,auto?L("<S-NAME> disappear(s) into the Java Plane!"):L("<S-NAME> <S-IS-ARE> sucked into the vortex created by @x1s recall.",mob.name()));
						if(((follower.location()==fRecalledRoom))
						&&(fRecalledRoom.isInhabitant(follower))
						&&(fRecalledRoom.okMessage(follower,msg)||CMSecurity.isAllowed(mob,recalledRoom,CMSecurity.SecFlag.GOTO)))
						{
							msg2=CMClass.getMsg(follower,fRecalledRoom,this,CMMsg.MASK_MOVE|CMMsg.TYP_RECALL,CMMsg.MASK_MOVE|CMMsg.MSG_ENTER,CMMsg.MASK_MOVE|CMMsg.TYP_RECALL,null);
							if(recallRoom.okMessage(follower,msg2)||CMSecurity.isAllowed(mob,recalledRoom,CMSecurity.SecFlag.GOTO))
							{
								if(follower.isInCombat())
									CMLib.commands().postFlee(follower,("NOWHERE"));
								recallRoom.send(follower,msg2);
								if(fRecalledRoom.isInhabitant(follower))
									recallRoom.bringMobHere(follower,false);
							}
						}
					}
				}
			}
		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> attempt(s) to recall, but <S-HIS-HER> plea goes unheard."));

		// return whether it worked
		return success;
	}

}
