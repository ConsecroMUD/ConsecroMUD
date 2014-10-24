package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_ObscureSelf extends Spell
{
	@Override public String ID() { return "Spell_ObscureSelf"; }
	private final static String localizedName = CMLib.lang().L("Obscure Self");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Obscure Self)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ILLUSION;}
	private final static String[][] stuff={
		{"<S-NAME>","<T-NAME>","someone"},
		{"<S-HIS-HER>","<T-HIS-HER>","his or her"},
		{"<S-HIM-HER>","<T-HIM-HER>","him or her"},
		{"<S-NAMESELF>","<T-NAMESELF>","someone"},
		{"<S-HE-SHE>","<T-HE-SHE>","he or she"},
		{"<S-YOUPOSS>","<T-YOUPOSS>","someone's"},
		{"<S-HIM-HERSELF>","<T-HIM-HERSELF>","him or herself"},
		{"<S-HIS-HERSELF>","<T-HIS-HERSELF>","his or herself"}
	};


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		String othersMessage=msg.othersMessage();
		String sourceMessage=msg.sourceMessage();
		String targetMessage=msg.targetMessage();
		boolean somethingsChanged=false;
		int x=0;
		if((msg.amITarget(mob))&&(msg.targetMinor()!=CMMsg.TYP_DAMAGE))
		{
			if((!msg.amISource(mob))&&((msg.targetMinor()==CMMsg.TYP_LOOK)
										||(msg.targetMinor()==CMMsg.TYP_EXAMINE)
										||(msg.targetMinor()==CMMsg.TYP_READ)))
			{
				msg.source().tell(L("He or she is too vague to make out any details."));
				return false;
			}

			if(othersMessage!=null)
			{
				for (final String[] element : stuff)
				{
					x=othersMessage.indexOf(element[1]);
					while(x>=0)
					{
						somethingsChanged=true;
						othersMessage=othersMessage.substring(0,x)+element[2]+othersMessage.substring(x+(element[1]).length());
						x=othersMessage.indexOf(element[1]);
					}
				}
			}
			if((!msg.amISource(mob))&&(sourceMessage!=null))
			{
				for (final String[] element : stuff)
				{
					x=sourceMessage.indexOf(element[1]);
					while(x>=0)
					{
						somethingsChanged=true;
						sourceMessage=sourceMessage.substring(0,x)+element[2]+sourceMessage.substring(x+(element[1]).length());
						x=sourceMessage.indexOf(element[1]);
					}
				}
			}
		}
		if(msg.amISource(mob))
		{
			if(othersMessage!=null)
			{
				for (final String[] element : stuff)
				{
					x=othersMessage.indexOf(element[0]);
					while(x>=0)
					{
						somethingsChanged=true;
						othersMessage=othersMessage.substring(0,x)+element[2]+othersMessage.substring(x+(element[0]).length());
						x=othersMessage.indexOf(element[0]);
					}
				}
			}
			if((!msg.amITarget(mob))&&(targetMessage!=null))
			{
				for (final String[] element : stuff)
				{
					x=targetMessage.indexOf(element[0]);
					while(x>=0)
					{
						somethingsChanged=true;
						targetMessage=targetMessage.substring(0,x)+element[2]+targetMessage.substring(x+(element[0]).length());
						x=targetMessage.indexOf(element[0]);
					}
				}
			}
		}
		if(somethingsChanged)
			msg.modify(msg.source(),msg.target(),msg.tool(),msg.sourceCode(),sourceMessage,msg.targetCode(),targetMessage,msg.othersCode(),othersMessage);
		return true;
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
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> seem(s) a bit less obscure."));
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already obscure."));
			return false;
		}

		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("^S<T-NAME> become(s) obscure!"):L("^S<S-NAME> whisper(s) to <S-HIM-HERSELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> whisper(s) to <S-HIM-HERSELF>, but nothing happens."));
		// return whether it worked
		return success;
	}
}
