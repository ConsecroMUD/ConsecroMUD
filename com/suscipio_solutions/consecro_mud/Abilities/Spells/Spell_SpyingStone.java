package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.LinkedList;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_SpyingStone extends Spell
{
	@Override public String ID() { return "Spell_SpyingStone"; }
	private final static String localizedName = CMLib.lang().L("Spying Stone");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Spying Stone)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	protected LinkedList<String> msgs=new LinkedList<String>();

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost, msg);
		if((msg.targetMinor()==CMMsg.TYP_SPEAK)
		&&((msg.source()==invoker())
			||((invoker()!=null) && msg.source().Name().equalsIgnoreCase(invoker().Name())))
		&&(msg.target()==affected)
		&&(msg.sourceMessage().toUpperCase().indexOf("SPEAK")>=0))
		{
			final Room room=CMLib.map().roomLocation(affected);
			if(room!=null)
			{
				final StringBuilder str=new StringBuilder("");
				for(final String m : msgs)
					str.append(m).append("\n\r");
				if(str.length()==0) str.append(L("Nothing!"));
				room.showHappens(CMMsg.MSG_SPEAK, affected,L("^S<S-NAME> grow(s) a mouth and say(s) '^N@x1^S'^N",str.toString()));
				msgs.clear();
			}
		}
		else
		if((msg.othersCode()!=CMMsg.NO_EFFECT)
		&&(msg.othersMessage()!=null)
		&&(msg.othersMessage().length()>0))
			msgs.add(CMLib.coffeeFilter().fullOutFilter(null, null, msg.source(), msg.target(), msg.tool(), CMStrings.removeColors(msg.othersMessage()), false));
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!(target instanceof Item))
		{
			mob.tell(L("You can't cast this spell on that."));
			return false;
		}

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(L("@x1 is already a spying stone!",target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> point(s) <S-HIS-HER> finger at <T-NAMESELF>, incanting.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
				mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("<T-NAME> open(s) a pair of strange eyes, which become transluscent."));
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> point(s) at <T-NAMESELF>, incanting, but nothing happens."));


		// return whether it worked
		return success;
	}
}
