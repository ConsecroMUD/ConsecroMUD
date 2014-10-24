package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_TimeStop extends Spell
{
	@Override public String ID() { return "Spell_TimeStop"; }
	private final static String localizedName = CMLib.lang().L("Time Stop");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Time is Stopped)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ROOMS|CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int overrideMana(){return 100;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ALTERATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}

	protected Vector fixed=new Vector();

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		fixed=new Vector();
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected!=null)&&(canBeUninvoked()))
		{
			if(affected instanceof Room)
			{
				final Room room=(Room)affected;
				room.showHappens(CMMsg.MSG_OK_VISUAL, L("Time starts moving again..."));
				if(invoker!=null)
				{
					final Ability me=invoker.fetchEffect(ID());
					if(me!=null)
						me.unInvoke();
				}
				CMLib.threads().resumeTicking(room,-1);
				for(int i=0;i<fixed.size();i++)
				{
					final MOB mob2=(MOB)fixed.elementAt(i);
					CMLib.threads().resumeTicking(mob2,-1);
				}
				fixed=new Vector();
			}
			else
			if(affected instanceof MOB)
			{
				final MOB mob=(MOB)affected;
				CMLib.threads().resumeTicking(mob,-1);
				if(mob.location()!=null)
				{
					mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("Time starts moving again..."));
					final Ability me=mob.location().fetchEffect(ID());
					if(me!=null)
						me.unInvoke();
				}
			}
		}
		super.unInvoke();
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		   &&(affected instanceof Room))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_ENTER:
			case CMMsg.TYP_LEAVE:
			case CMMsg.TYP_FLEE:
				if(msg.source()==invoker)
					msg.source().tell(L("You cannot travel beyond the time stopped area."));
				else
					msg.source().tell(L("Nothing just happened.  You didn't do that."));
				return false;
			default:
				if((msg.source() == invoker)
				&&(msg.target() != invoker)
				&&(msg.target() instanceof MOB)
				&&((CMath.bset(msg.sourceMajor(),CMMsg.MASK_MALICIOUS))
				||(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
				||(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))))
				{
					if(invoker.getVictim()==null)
						invoker.setVictim((MOB)msg.target());
				}
				else
				if((msg.source()!=invoker)
				   &&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS))
				   &&(!CMath.bset(msg.targetMajor(),CMMsg.MASK_ALWAYS)))
				{
					msg.source().tell(L("Time is stopped. Nothing just happened.  You didn't do that."));
					return false;
				}
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Physical target = mob.location();

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(mob,null,null,L("Time has already been stopped here!"));
			return false;
		}


		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.

			CMMsg msg = CMClass.getMsg(mob, target, this,verbalCastCode(mob,target,auto),L((auto?"T":"^S<S-NAME> speak(s) and gesture(s) and t")+"ime suddenly STOPS!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					final Room room=mob.location();
					fixed=new Vector();
					final Set<MOB> grpMembers = mob.getGroupMembers(new HashSet<MOB>());
					for(int m=0;m<room.numInhabitants();m++)
					{
						final MOB mob2=room.fetchInhabitant(m);
						if((mob2!=mob)&&(mob.mayIFight(mob2)))
						{
							msg=CMClass.getMsg(mob,mob2,this,CMMsg.MASK_MALICIOUS|CMMsg.TYP_MIND,null);
							if(!grpMembers.contains(mob2))
							{
								if(room.okMessage(mob, msg))
								{
									room.send(mob, msg);
									if(msg.value()>0)
										return false;
								}
								else
									return beneficialWordsFizzle(mob,null,L("<S-NAME> incant(s) for awhile, but the spell fizzles."));
							}
						}
					}
					CMLib.threads().suspendTicking(room,-1);
					for(int m=0;m<room.numInhabitants();m++)
					{
						final MOB mob2=room.fetchInhabitant(m);
						if(mob2!=mob)
						{
							fixed.addElement(mob2);
							CMLib.threads().suspendTicking(mob2,-1);
						}
					}
					beneficialAffect(mob,room,asLevel,2);
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> incant(s) for awhile, but the spell fizzles."));

		// return whether it worked
		return success;
	}
}
