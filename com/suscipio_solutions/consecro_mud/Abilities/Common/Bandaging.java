package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.MendingSkill;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Bandaging extends CommonSkill implements MendingSkill
{
	@Override public String ID() { return "Bandaging"; }
	private final static String localizedName = CMLib.lang().L("Bandaging");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"BANDAGE","BANDAGING"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_ANATOMY;}

	protected Physical bandaging=null;
	protected boolean messedUp=false;
	public Bandaging()
	{
		super();
		displayText=L("You are bandaging...");
		verb=L("bandaging");
	}
	@Override
	public boolean supportsMending(Physical item)
	{
		if(!(item instanceof MOB)) return false;
		return (item.fetchEffect("Bleeding")!=null)||(item.fetchEffect("Injury")!=null);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)affected;
			if((bandaging==null)||(mob.location()==null))
			{
				messedUp=true;
				unInvoke();
			}
			if((bandaging instanceof MOB)&&(!mob.location().isInhabitant((MOB)bandaging)))
			{
				messedUp=true;
				unInvoke();
			}
			if(mob.curState().adjHitPoints(super.getXLEVELLevel(invoker())+(int)Math.round(CMath.div(mob.phyStats().level(),2.0)),mob.maxState()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> mend(s) and heal(s)."));
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
				if((bandaging!=null)&&(!aborted))
				{
					if((messedUp)||(bandaging==null))
						commonTell(mob,L("You've failed to bandage @x1!",bandaging.name()));
					else
					{
						Ability A=bandaging.fetchEffect("Bleeding");
						if(A!=null) A.unInvoke();
						A=bandaging.fetchEffect("Injury");
						if(A!=null) A.unInvoke();
					}
				}
			}
		}
		super.unInvoke();
	}

	public double healthPct(MOB mob){ return CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints());}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(super.checkStop(mob, commands))
			return true;
		verb=L("bandaging");
		bandaging=null;
		final MOB target=super.getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if((target.fetchEffect("Bleeding")==null)
		&&(target.fetchEffect("Injury")==null))
		{
			super.commonTell(mob,target,null,L("<T-NAME> <T-IS-ARE> not bleeding or injured!"));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		messedUp=!proficiencyCheck(mob,0,auto);
		int duration=3+(int)Math.round(10*(1.0-healthPct(target)))-getXLEVELLevel(mob);
		if(duration<3) duration=3;
		verb=L("bandaging @x1",target.name());
		bandaging=target;
		final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_DELICATE_HANDS_ACT,L("<S-NAME> begin(s) bandaging up <T-YOUPOSS> wounds."));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,mob,asLevel,duration);
		}
		return true;
	}
}
