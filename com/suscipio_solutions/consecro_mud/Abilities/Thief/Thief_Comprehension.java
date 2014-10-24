package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Comprehension extends ThiefSkill
{
	@Override public String ID() { return "Thief_Comprehension"; }
	private final static String localizedName = CMLib.lang().L("Linguistic Comprehension");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Lang. Comprehension)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	private static final String[] triggerStrings =I(new String[] {"COMPREHEND","COMPREHENSION"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected boolean disregardsArmorCheck(MOB mob){return true;}
	protected Vector queue=new Vector();
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STREETSMARTS;}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(L("You are no longer comprehending languages."));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((queue.size()>0)&&(affected instanceof MOB)&&(proficiencyCheck((MOB)affected,0,false)))
		{
			final CMMsg msg=(CMMsg)queue.firstElement();
			queue.removeElementAt(0);
			final MOB mob=(MOB)affected;
			if((mob.location()!=null)&&(mob.location().okMessage(mob,msg)))
				mob.location().send(mob,msg);
		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected instanceof MOB)
		&&(!msg.amISource((MOB)affected))
		&&((msg.sourceMinor()==CMMsg.TYP_SPEAK)
		   ||(msg.sourceMinor()==CMMsg.TYP_TELL)
		   ||(CMath.bset(msg.sourceMajor(),CMMsg.MASK_CHANNEL)))
		&&(msg.tool() !=null)
		&&(msg.sourceMessage()!=null)
		&&(msg.tool() instanceof Ability)
		&&((((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_LANGUAGE)
		&&(((MOB)affected).fetchEffect(msg.tool().ID())==null))
		{
			final String str=CMStrings.getSayFromMessage(msg.sourceMessage());
			if(str!=null)
			{
				if(CMath.bset(msg.sourceMajor(),CMMsg.MASK_CHANNEL))
					queue.addElement(CMClass.getMsg(msg.source(),null,null,CMMsg.NO_EFFECT,CMMsg.NO_EFFECT,msg.othersCode(),L("@x1 (translated from @x2)",CMStrings.substituteSayInMessage(msg.othersMessage(),str),((Ability)msg.tool()).name())));
				else
				if(msg.amITarget(affected)&&(msg.targetMessage()!=null))
					queue.addElement(CMClass.getMsg(msg.source(),affected,null,CMMsg.NO_EFFECT,msg.targetCode(),CMMsg.NO_EFFECT,L("@x1 (translated from @x2)",CMStrings.substituteSayInMessage(msg.targetMessage(),str),((Ability)msg.tool()).name())));
				else
				if((msg.othersMessage()!=null)&&(msg.othersMessage().indexOf('\'')>0))
				{
					String otherMes=msg.othersMessage();
					if(msg.target()!=null)
						otherMes=CMLib.coffeeFilter().fullOutFilter(((MOB)affected).session(),(MOB)affected,msg.source(),msg.target(),msg.tool(),otherMes,false);
					queue.addElement(CMClass.getMsg(msg.source(),affected,null,CMMsg.NO_EFFECT,msg.othersCode(),CMMsg.NO_EFFECT,L("@x1 (translated from @x2)",CMStrings.substituteSayInMessage(otherMes,str),((Ability)msg.tool()).name())));
				}
			}
		}
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if(target==null) return false;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("You already have comprehension."));
			return false;
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
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,auto?L("<T-NAME> feel(s) more comprehending."):L("^S<S-NAME> listen(s) with intense comprehension.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to listen with comprehension, but fail(s) miserably."));

		// return whether it worked
		return success;
	}
}
