package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Chant_Rockthought extends Chant
{
	@Override public String ID() { return "Chant_Rockthought"; }
	private final static String localizedName = CMLib.lang().L("Rockthought");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Rockthought)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ENDURING;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	CMMsg stubb=null;

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		if((affected instanceof MOB)
		&&(stubb==null)
		&&(msg.amISource((MOB)affected))
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS)
		&&(msg.othersCode()!=CMMsg.NO_EFFECT)
		&&(msg.othersMessage()!=null)
		&&(msg.othersMessage().length()>0)))
			stubb=msg;
		super.executeMsg(host,msg);
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if((affected instanceof MOB)
		&&(stubb!=null)
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS)
		&&(!stubb.equals(msg))))
		{
			// this can cause all kinds of potential problems ..
			// the number of checks to get around them probably isn't worth the cost.
		}
		return super.okMessage(host,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected instanceof MOB)
		&&(stubb!=null)
		&&(((MOB)affected).location()!=null)
		&&(((MOB)affected).location().okMessage(affected,stubb)))
			((MOB)affected).location().send((MOB)affected,stubb);
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,auto?"":L("^S<S-NAME> chant(s) at <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					stubb=null;
					success=maliciousAffect(mob,target,asLevel,20,CMMsg.MSK_CAST_VERBAL|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0))!=null;
					if(success)
					{
						if(target.isInCombat()) target.makePeace();
						target.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> look(s) stubborn."));
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) at <T-NAMESELF>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
