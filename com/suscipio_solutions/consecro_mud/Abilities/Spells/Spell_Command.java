package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_Command extends Spell
{
	@Override public String ID() { return "Spell_Command"; }
	private final static String localizedName = CMLib.lang().L("Command");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){	return Ability.ACODE_SPELL|Ability.DOMAIN_ENCHANTMENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Vector V=new Vector();
		if(commands.size()>0)
		{
			V.addElement(commands.elementAt(0));
			commands.removeElementAt(0);
		}

		final MOB target=getTarget(mob,V,givenTarget);
		if(target==null) return false;

		if(commands.size()==0)
		{
			if(mob.isMonster())
				commands.addElement("FLEE");
			else
			{
				mob.tell(L("Command @x1 to do what?",((String)V.elementAt(0))));
				return false;
			}
		}

		if((!target.mayIFight(mob))||(!target.isMonster()))
		{
			mob.tell(L("You can't command @x1.",target.name(mob)));
			return false;
		}

		if(((String)commands.elementAt(0)).toUpperCase().startsWith("FOL"))
		{
			mob.tell(L("You can't command someone to follow."));
			return false;
		}

		CMObject O=CMLib.english().findCommand(target,(Vector)commands.clone());
		if(O instanceof Command)
		{
			if((!((Command)O).canBeOrdered())||(!((Command)O).securityCheck(mob))||(((Command)O).ID().equals("Sleep")))
			{
				mob.tell(L("You can't command someone to doing that."));
				return false;
			}
		}
		else
		{
			if(O instanceof Ability)
				O=CMLib.english().getToEvoke(target,(Vector)commands.clone());
			if(O instanceof Ability)
			{
				if(CMath.bset(((Ability)O).flags(),Ability.FLAG_NOORDERING))
				{
					mob.tell(L("You can't command @x1 to do that.",target.name(mob)));
					return false;
				}
			}
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> command(s) <T-NAMESELF> to '@x1'.^?",CMParms.combine(commands,0)));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0),null);
			final CMMsg omsg=CMClass.getMsg(mob,target,null,CMMsg.MSG_ORDER,null);
			if((mob.location().okMessage(mob,msg))
			&&((mob.location().okMessage(mob,msg2)))
			&&(mob.location().okMessage(mob, omsg)))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					mob.location().send(mob,msg2);
					mob.location().send(mob,omsg);
					if((msg2.value()<=0)&&(omsg.sourceMinor()==CMMsg.TYP_ORDER))
					{
						invoker=mob;
						target.makePeace();
						target.enqueCommand(commands,Command.METAFLAG_FORCED|Command.METAFLAG_ORDER,0);
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to command <T-NAMESELF>, but it definitely didn't work."));


		// return whether it worked
		return success;
	}
}
