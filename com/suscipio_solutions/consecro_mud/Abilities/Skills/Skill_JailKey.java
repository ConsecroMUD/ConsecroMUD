package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Skill_JailKey extends StdSkill
{
	@Override public String ID() { return "Skill_JailKey"; }
	private final static String localizedName = CMLib.lang().L("Jail Key");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_EXITS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"JAILKEY","JKEY"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_LEGAL; }
	public int code=0;

	@Override public int abilityCode(){return code;}
	@Override public void setAbilityCode(int newCode){code=newCode;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final String whatTounlock=CMParms.combine(commands,0);
		Exit unlockThis=null;
		final int dirCode=Directions.getGoodDirectionCode(whatTounlock);
		if((dirCode>=0)&&(mob.location()!=null))
		{
			unlockThis=mob.location().getExitInDir(dirCode);
			final Room unlockThat=mob.location().getRoomInDir(dirCode);
			if(unlockThat==null) unlockThis=null;
			if(unlockThis!=null)
			{
				LegalBehavior B=null;

				final Area legalA=CMLib.law().getLegalObject(mob.location());
				if(legalA!=null) B=CMLib.law().getLegalBehavior(legalA);
				if(B==null)
					unlockThis=null;
				else
				if(!B.isJailRoom(legalA,new XVector(mob.location())))
					unlockThis=null;
			}
		}

		if(unlockThis==null)
		{
			if(dirCode<0)
				mob.tell(L("You should specify a direction."));
			else
			{
				final Exit E=mob.location().getExitInDir(dirCode);
				if(E==null)
					mob.tell(L("You must specify a jail door direction."));
				else
				if(!E.hasADoor())
					mob.tell(L("You must specify a jail **DOOR** direction."));
				else
				if(!E.hasALock())
					mob.tell(L("You must specify a **JAIL** door direction."));
				else
				if(E.isOpen())
					mob.tell(L("@x1 is open already.",E.name()));
				else
					mob.tell(L("That's not a jail door."));
			}
			return false;
		}

		if(!unlockThis.hasALock())
		{
			mob.tell(L("There is no lock on @x1!",unlockThis.name()));
			return false;
		}

		if(unlockThis.isOpen())
		{
			mob.tell(L("@x1 is open!",unlockThis.name()));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(!success)
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) <S-HIS-HER> jailkey on @x1 and fail(s).",unlockThis.name()));
		else
		{
			CMMsg msg=CMClass.getMsg(mob,unlockThis,this,auto?CMMsg.MSG_OK_VISUAL:(CMMsg.MSG_THIEF_ACT),CMMsg.MSG_OK_VISUAL,CMMsg.MSG_OK_VISUAL,null);
			if(mob.location().okMessage(mob,msg))
			{
				if(!unlockThis.isLocked())
					msg=CMClass.getMsg(mob,unlockThis,null,CMMsg.MSG_OK_VISUAL,CMMsg.MSG_LOCK,CMMsg.MSG_OK_VISUAL,auto?L("@x1 vibrate(s) and click(s).",unlockThis.name()):L("<S-NAME> use(s) <S-HIS-HER> jailkey and relock(s) @x1.",unlockThis.name()));
				else
					msg=CMClass.getMsg(mob,unlockThis,null,CMMsg.MSG_OK_VISUAL,CMMsg.MSG_UNLOCK,CMMsg.MSG_OK_VISUAL,auto?L("@x1 vibrate(s) and click(s).",unlockThis.name()):L("<S-NAME> use(s) <S-HIS-HER> jailkey and unlock(s) @x1.",unlockThis.name()));
				CMLib.utensils().roomAffectFully(msg,mob.location(),dirCode);
			}
		}

		return success;
	}
}
