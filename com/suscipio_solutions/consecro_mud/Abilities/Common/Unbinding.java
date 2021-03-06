package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Unbinding extends CommonSkill
{
	@Override public String ID() { return "Unbinding"; }
	private final static String localizedName = CMLib.lang().L("Unbinding");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"UNBIND","UNTIE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode() {   return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_BINDING; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	MOB found=null;
	Ability removing=null;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB)&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)affected;
			if(tickUp==3)
			{
				List<Ability> affects=null;
				if(found!=null)
				   affects=CMLib.flags().flaggedAffects(found,Ability.FLAG_BINDING);
				if((affects!=null)&&(affects.size()>0))
				{
					removing=affects.get(0);
					displayText=L("You are removing @x1 from @x2",removing.name(),found.name());
					verb=L("removing @x1 from @x2",removing.name(),found.name());
				}
				else
				{
					final StringBuffer str=new StringBuffer(L("You can't seem to remove any of the bindings.\n\r"));
					commonTell(mob,str.toString());
					unInvoke();
				}
			}
			else
			if((found!=null)&&(mob!=null))
			{
				if(found.location()!=mob.location())
				{
					aborted=true;
					unInvoke();
				}
				if(!CMLib.flags().canBeSeenBy(found,mob))
				{
					aborted=true;
					unInvoke();
				}
				if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
				{
					aborted=true;
					unInvoke();
				}
				if((removing!=null)&&(found.fetchEffect(removing.ID())!=removing))
				{
					aborted=true;
					unInvoke();
				}
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if((affected!=null)&&(affected instanceof MOB))
			{
				final MOB mob=(MOB)affected;
				if((found!=null)&&(removing!=null)&&(!aborted))
				{
					removing.unInvoke();
					if(found.fetchEffect(removing.ID())==null)
						mob.location().show(mob,null,getActivityMessageType(),L("<S-NAME> manage(s) to remove @x1 from @x2.",removing.name(),found.name()));
					else
						mob.location().show(mob,null,getActivityMessageType(),L("<S-NAME> fail(s) to remove @x1 from @x2.",removing.name(),found.name()));
				}
			}
		}
		super.unInvoke();
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(super.checkStop(mob, commands))
			return true;
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if((!auto)&&(target==mob))
		{
			mob.tell(L("You can't unbind yourself!"));
			return false;
		}
		if((!auto)&&mob.isInCombat())
		{
			mob.tell(L("Not while you are fighting!"));
			return false;
		}
		final List<Ability> affects=CMLib.flags().flaggedAffects(target,Ability.FLAG_BINDING);
		if(affects.size()==0)
		{
			mob.tell(L("@x1 does not have any bindings you can remove.",target.name(mob)));
			return false;
		}
		final Ability A=affects.get(0);

		verb=L("unbinding");
		found=null;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int duration=CMLib.ableMapper().lowestQualifyingLevel(A.ID())-(CMLib.ableMapper().qualifyingLevel(mob,A)+(2*getXLEVELLevel(mob)));
		if(duration<5) duration=4;
		final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_DELICATE_HANDS_ACT,L("<S-NAME> begin(s) to unbind <T-NAMESELF>."));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			found=target;
			verb=L("unbinding @x1",found.name());
			displayText=L("You are @x1",verb);
			found=proficiencyCheck(mob,0,auto)?found:null;
			beneficialAffect(mob,mob,asLevel,duration);
		}
		return true;

	}
}
