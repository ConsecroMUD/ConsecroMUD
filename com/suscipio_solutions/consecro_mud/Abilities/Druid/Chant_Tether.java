package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Chant_Tether extends Chant
{
	@Override public String ID() { return "Chant_Tether"; }
	private final static String localizedName = CMLib.lang().L("Tether");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PRESERVING;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Tether)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	public Room tetheredTo=null;
	public Room lastRoom=null;


	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if((lastRoom!=mob.location())
			&&(lastRoom!=null))
				tetheredTo=lastRoom;
			lastRoom=mob.location();

			if(msg.amISource(mob)
			&&(msg.target()==null)
			&&(msg.tool()==null)
			&&(msg.sourceMinor()==CMMsg.TYP_DEATH)
			&&(mob.curState().getHitPoints()>0))
			{
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> pulled back by the tether!"));
				if((tetheredTo!=null)&&(tetheredTo!=mob.location()))
					tetheredTo.bringMobHere(mob,false);
				return false;
			}
		}
		return super.okMessage(host,msg);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(L("Your tether has left you."));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if((lastRoom!=mob.location())
			&&(lastRoom!=null))
				tetheredTo=lastRoom;
			lastRoom=mob.location();
			if(mob.fetchEffect("Falling")!=null)
			{
				mob.tell(L("The tether keeps you from falling!"));
				mob.delEffect(mob.fetchEffect("Falling"));
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already tethered."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> become(s) magically tethered!"):L("^S<S-NAME> chant(s) about a magical tether!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				lastRoom=mob.location();
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s) about a magical tether, but the magic fades."));


		// return whether it worked
		return success;
	}
}
