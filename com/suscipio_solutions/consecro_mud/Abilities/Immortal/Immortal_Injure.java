package com.suscipio_solutions.consecro_mud.Abilities.Immortal;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Amputator;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Immortal_Injure extends Immortal_Skill
{
	@Override public String ID() { return "Immortal_Injure"; }
	private final static String localizedName = CMLib.lang().L("Injure");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"INJURE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_IMMORTAL;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(1);}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		String part=null;
		if((commands.size()<3)&&(mob.isInCombat()))
		{
			part=CMParms.combine(commands).toUpperCase();
			commands.clear();
		}
		else
		if((commands.size()==2)||(commands.size()==3))
		{
			part=CMParms.combine(commands,1).toUpperCase();
			commands.remove(1);
			if(commands.size()>1)
				commands.remove(1);
		}
		else
		if(commands.size()>3)
		{
			part=CMParms.combine(commands,2).toUpperCase();
			commands.remove(1);
		}
		final MOB target=getTargetAnywhere(mob,commands,givenTarget,false,true,true);
		if(target==null) return false;
		Amputator A=(Amputator)target.fetchEffect("Amputation");
		if(A==null) A=(Amputator)CMClass.getAbility("Amputation");
		final List<String> remainingLimbList=A.remainingLimbNameSet(target);
		if(target.charStats().getBodyPart(Race.BODY_HEAD)>0)
			remainingLimbList.add("head");
		if(target.charStats().getBodyPart(Race.BODY_TORSO)>0)
			remainingLimbList.add("torso");
		String gone=null;
		for(int i=0;i<remainingLimbList.size();i++)
			if((part==null)||remainingLimbList.get(i).toUpperCase().endsWith(part))
			{
				gone=remainingLimbList.get(i);
				break;
			}
		if((gone==null)||(part==null))
		{
			if(part==null)
				mob.tell(target,null,null,L("<S-NAME> has no parts."));
			else
				mob.tell(target,null,null,L("<S-NAME> has no part called '@x1'.",part.toLowerCase()));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),auto?L("A stink cloud surrounds <T-NAME>!"):L("^F<S-NAME> injure(s) <T-YOUPOSS> @x1.^?",gone.toLowerCase()));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				Log.sysOut("Immortal_Injure",mob.Name()+" injures "+target.name()+".");
				Ability A2=CMClass.getAbility("Injury");
				if(A2!=null)
				{
					final int percentOff=target.maxState().getHitPoints()/5;
					if(target.curState().getHitPoints()>(target.curState().getHitPoints()-percentOff))
						target.curState().setHitPoints(target.curState().getHitPoints()-percentOff);
					A2.invoke(mob,new XVector(),target,true,0);
					A2=target.fetchEffect("Injury");
					if(A2!=null)
						A2.setMiscText("+"+part.toLowerCase()+"=20");
				}
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to injure <T-NAMESELF>, but fail(s)."));
		return success;
	}
}
