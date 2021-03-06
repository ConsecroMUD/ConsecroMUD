package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_DetectTraps extends ThiefSkill
{
	@Override public String ID() { return "Thief_DetectTraps"; }
	private final static String localizedName = CMLib.lang().L("Detect Traps");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS|Ability.CAN_EXITS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){	return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_ALERT;}
	private static final String[] triggerStrings =I(new String[] {"CHECK"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	protected Environmental lastChecked=null;

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final String whatTounlock=CMParms.combine(commands,0);
		Physical unlockThis=givenTarget;
		Room nextRoom=null;
		int dirCode=-1;
		if(unlockThis==null)
		{
			dirCode=Directions.getGoodDirectionCode(whatTounlock);
			if(dirCode>=0)
			{
				unlockThis=mob.location().getExitInDir(dirCode);
				nextRoom=mob.location().getRoomInDir(dirCode);
			}
		}
		if((unlockThis==null)&&(whatTounlock.equalsIgnoreCase("room")||whatTounlock.equalsIgnoreCase("here")))
			unlockThis=mob.location();
		if(unlockThis==null)
			unlockThis=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);
		if(unlockThis==null) return false;

		final int oldProficiency=proficiency();
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,+((((mob.phyStats().level()+(2*super.getXLEVELLevel(mob))))
											 -unlockThis.phyStats().level())*3),auto);
		Trap theTrap=CMLib.utensils().fetchMyTrap(unlockThis);
		if(unlockThis instanceof Exit)
		{
			if(dirCode<0)
			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				if(mob.location().getExitInDir(d)==unlockThis){ dirCode=d; break;}
			if(dirCode>=0)
			{
				final Exit exit=mob.location().getReverseExit(dirCode);
				Trap opTrap=null;
				Trap roomTrap=null;
				if(nextRoom!=null) roomTrap=CMLib.utensils().fetchMyTrap(nextRoom);
				if(exit!=null) opTrap=CMLib.utensils().fetchMyTrap(exit);
				if((theTrap!=null)&&(opTrap!=null))
				{
					if((theTrap.disabled())&&(!opTrap.disabled()))
						theTrap=opTrap;
				}
				else
				if((opTrap!=null)&&(theTrap==null))
					theTrap=opTrap;
				if((theTrap!=null)&&(theTrap.disabled())&&(roomTrap!=null))
				{
					opTrap=null;
					unlockThis=nextRoom;
					theTrap=roomTrap;
				}
			}
		}
		final String add=(dirCode>=0)?" "+Directions.getInDirectionName(dirCode):"";
		final CMMsg msg=CMClass.getMsg(mob,unlockThis,this,auto?CMMsg.MSG_OK_ACTION:CMMsg.MSG_DELICATE_HANDS_ACT,auto?null:L("<S-NAME> look(s) @x1@x2 over very carefully.",((unlockThis==null)?"":unlockThis.name()),add));
		if((unlockThis!=null)&&(mob.location().okMessage(mob,msg)))
		{
			mob.location().send(mob,msg);
			if((unlockThis==lastChecked)&&((theTrap==null)||(theTrap.disabled())))
				setProficiency(oldProficiency);
			if((!success)||(theTrap==null))
			{
				if(!auto)
					mob.tell(L("You don't find any traps on @x1@x2.",unlockThis.name(),add));
				success=false;
			}
			else
			{
				if(theTrap.disabled())
					mob.tell(L("@x1@x2 is trapped, but the trap looks disabled for the moment.",unlockThis.name(),add));
				else
				if(theTrap.sprung())
					mob.tell(L("@x1@x2 is trapped, and the trap looks sprung.",unlockThis.name(),add));
				else
					mob.tell(L("@x1@x2 definitely looks trapped.",unlockThis.name(),add));
			}
			lastChecked=unlockThis;
		}
		else
			success=false;

		return success;
	}
}
