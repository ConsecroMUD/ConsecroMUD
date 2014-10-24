package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Poison extends ThiefSkill
{
	// **
	// This is a deprecated skill provided only
	// for backwards compatibility.  It has been
	// replaced with Thief_UsePoison
	// **
	@Override public String ID() { return "Thief_Poison"; }
	private final static String localizedName = CMLib.lang().L("Deprecated Poison");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"DOPOISON"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		String str=null;
		if(success)
		{
			str=auto?"":L("^F^<FIGHT^><S-NAME> attempt(s) to poison <T-NAMESELF>!^</FIGHT^>^?");
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_THIEF_ACT,str,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.MSG_THIEF_ACT|(auto?CMMsg.MASK_ALWAYS:0),str,CMMsg.MSG_NOISYMOVEMENT,str);
			final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MASK_MALICIOUS|CMMsg.TYP_POISON,null,CMMsg.MASK_MALICIOUS|CMMsg.TYP_POISON|(auto?CMMsg.MASK_ALWAYS:0),null,CMMsg.NO_EFFECT,null);
			CMLib.color().fixSourceFightColor(msg);
			final Room R=mob.location();
			if(R.okMessage(mob,msg) && R.okMessage(mob,msg2))
			{
				R.send(mob,msg);
				R.send(mob,msg2);
				if((msg.value()<=0) && (msg2.value()<=0))
				{
					final Ability A=CMClass.getAbility("Poison");
					A.invoke(mob,target,true,asLevel);
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to poison <T-NAMESELF>, but fail(s)."));

		return success;
	}
}
