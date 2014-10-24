package com.suscipio_solutions.consecro_mud.Abilities.Immortal;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Immortal_Stinkify extends Immortal_Skill
{
	boolean doneTicking=false;
	@Override public String ID() { return "Immortal_Stinkify"; }
	private final static String localizedName = CMLib.lang().L("Stinkify");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"STINKIFY"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_IMMORTAL;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(1);}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTargetAnywhere(mob,commands,givenTarget,false,true,true);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),auto?L("A stink cloud surrounds <T-NAME>!"):L("^F<S-NAME> stinkif(ys) <T-NAMESELF>.^?"));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(target.playerStats()!=null)
				{
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> stinkier!"));
					target.playerStats().adjHygiene(PlayerStats.HYGIENE_DELIMIT+1);
					Log.sysOut("Stinkify",mob.Name()+" stinkied "+target.name()+".");
				}
				else
					mob.tell(mob,target,null,L("<T-NAME> is a mob.  Try a player."));
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to stinkify <T-NAMESELF>, but fail(s)."));
		return success;
	}
}
