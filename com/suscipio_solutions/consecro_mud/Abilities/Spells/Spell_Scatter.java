package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_Scatter extends Spell
{
	@Override public String ID() { return "Spell_Scatter"; }
	private final static String localizedName = CMLib.lang().L("Scatter");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return CAN_MOBS|CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}

	private Item getItem(MOB mobTarget)
	{
		final Vector goodPossibilities=new Vector();
		final Vector possibilities=new Vector();
		for(int i=0;i<mobTarget.numItems();i++)
		{
			final Item item=mobTarget.getItem(i);
			if(item!=null)
			{
				if(item.amWearingAt(Wearable.IN_INVENTORY))
					possibilities.addElement(item);
				else
					goodPossibilities.addElement(item);
			}
		}
		if(goodPossibilities.size()>0)
			return (Item)goodPossibilities.elementAt(CMLib.dice().roll(1,goodPossibilities.size(),-1));
		else
		if(possibilities.size()>0)
			return (Item)possibilities.elementAt(CMLib.dice().roll(1,possibilities.size(),-1));
		return null;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((target instanceof MOB)&&(target!=mob))
			{
				if(getItem((MOB)target)==null)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Vector areas=new Vector();
		if(commands.size()==0)
			areas.addElement(mob.location().getArea());
		else
		if(((String)commands.lastElement()).equalsIgnoreCase("far"))
		{
			commands.removeElementAt(commands.size()-1);
			for(final Enumeration e=CMLib.map().areas();e.hasMoreElements();)
				areas.addElement(e.nextElement());
		}
		else
		if(((String)commands.lastElement()).equalsIgnoreCase("near"))
		{
			commands.removeElementAt(commands.size()-1);
			areas.addElement(mob.location().getArea());
		}
		else
			areas.addElement(mob.location().getArea());
		final MOB mobTarget=getTarget(mob,commands,givenTarget,true,false);
		Item target=null;
		if(mobTarget!=null)
		{
			target=getItem(mobTarget);
			if(target==null)
				return maliciousFizzle(mob,mobTarget,L("<S-NAME> attempt(s) a scattering spell at <T-NAMESELF>, but nothing happens."));
		}

		List<Item> targets=new Vector();
		if(givenTarget instanceof Item)
			targets.add((Item)givenTarget);
		else
		if(target!=null)
			targets.add(target);
		else
		{
			targets=CMLib.english().fetchItemList(mob,mob,null,commands,Wearable.FILTER_ANY,true);
			if(targets.size()==0)
				mob.tell(L("You don't seem to be carrying that."));
		}

		if(targets.size()==0) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			String str=null;
			if(mobTarget==null)
				str=auto?L("<S-NAME> <S-IS-ARE> enveloped in a scattering field!"):L("^S<S-NAME> utter(s) a scattering spell!^?");
			else
				str=auto?L("<T-NAME> <T-IS-ARE> enveloped in a scattering field!"):L("^S<S-NAME> utter(s) a scattering spell, causing <T-NAMESELF> to resonate.^?");
			CMMsg msg=CMClass.getMsg(mob,mobTarget,this,verbalCastCode(mob,target,auto),str);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					for(int i=0;i<targets.size();i++)
					{
						target=targets.get(i);
						msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
						Room room = null;
						for(int x = 0; (x < 10) && (room == null); x++)
							room=((Area)areas.elementAt(CMLib.dice().roll(1,areas.size(),-1))).getRandomMetroRoom();
						if(mob.location().okMessage(mob,msg) && (room != null))
						{
							mob.location().send(mob,msg);
							if(msg.value()<=0)
							{
								target.unWear();
								if(target.owner() instanceof MOB)
								{
									final MOB owner=(MOB)target.owner();
									mob.location().show(owner,room,target,CMMsg.MASK_ALWAYS|CMMsg.MSG_THROW,L("<O-NAME> vanishes from <S-YOUPOSS> inventory!"));
									room.showOthers(owner,room,target,CMMsg.MASK_ALWAYS|CMMsg.MSG_THROW,L("<O-NAME> appears from out of nowhere!"));
								}
								else
								{
									mob.location().show(mob,room,target,CMMsg.MASK_ALWAYS|CMMsg.MSG_THROW,L("<O-NAME> vanishes!"));
									room.showOthers(mob,room,target,CMMsg.MASK_ALWAYS|CMMsg.MSG_THROW,L("<O-NAME> appears from out of nowhere!"));
								}
								if(!room.isContent(target))
									room.moveItemTo(target,ItemPossessor.Expire.Player_Drop,ItemPossessor.Move.Followers);
								room.recoverRoomStats();
							}
						}
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,mobTarget,L("<S-NAME> attempt(s) a scattering spell, but nothing happens."));


		// return whether it worked
		return success;
	}
}


