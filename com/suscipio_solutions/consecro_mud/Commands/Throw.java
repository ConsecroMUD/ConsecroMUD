package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpaceShip;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpellHolder;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class Throw extends StdCommand
{
	public Throw(){}

	private final String[] access=I(new String[]{"THROW","TOSS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((commands.size()==2)&&(mob.isInCombat()))
			commands.addElement(mob.getVictim().location().getContextName(mob.getVictim()));
		if(commands.size()<3)
		{
			mob.tell(L("Throw what, where or at whom?"));
			return false;
		}
		commands.removeElementAt(0);
		final String str=(String)commands.lastElement();
		commands.removeElement(str);
		final String what=CMParms.combine(commands,0);
		Item item=mob.fetchItem(null,Wearable.FILTER_WORNONLY,what);
		if(item==null) item=mob.findItem(null,what);
		if((item==null)||(!CMLib.flags().canBeSeenBy(item,mob)))
		{
			mob.tell(L("You don't seem to have a '@x1'!",what));
			return false;
		}
		if((!item.amWearingAt(Wearable.WORN_HELD))&&(!item.amWearingAt(Wearable.WORN_WIELD)))
		{
			mob.tell(L("You aren't holding or wielding @x1!",item.name()));
			return false;
		}

		final int dir=Directions.getGoodDirectionCode(str);
		Environmental target=null;
		if(dir<0)
			target=mob.location().fetchInhabitant(str);
		else
		{
			target=mob.location().getRoomInDir(dir);
			if((target==null)
			||(mob.location().getExitInDir(dir)==null)
			||(!mob.location().getExitInDir(dir).isOpen()))
			{
				mob.tell(L("You can't throw anything that way!"));
				return false;
			}
			final boolean amOutside=((mob.location().domainType()&Room.INDOORS)==0);
			final boolean isOutside=((((Room)target).domainType()&Room.INDOORS)==0);
			final boolean isUp=(mob.location().getRoomInDir(Directions.UP)==target);
			final boolean isDown=(mob.location().getRoomInDir(Directions.DOWN)==target);

			if(amOutside&&isOutside&&(!isUp)&&(!isDown)
			&&((((Room)target).domainType()&Room.DOMAIN_OUTDOORS_AIR)==0))
			{
				mob.tell(L("That's too far to throw @x1.",item.name()));
				return false;
			}
		}
		if((dir<0)&&((target==null)||((target!=mob.getVictim())&&(!CMLib.flags().canBeSeenBy(target,mob)))))
		{
			mob.tell(L("You can't target @x1 at '@x2'!",item.name(),str));
			return false;
		}

		if(!(target instanceof Room))
		{
			final CMMsg newMsg=CMClass.getMsg(mob,item,null,CMMsg.MSG_REMOVE,null);
			if(mob.location().okMessage(mob,newMsg))
			{
				mob.location().send(mob,newMsg);
				int targetMsg=CMMsg.MSG_THROW;
				if(target instanceof MOB)
				{
					if(item instanceof Weapon)
						targetMsg=CMMsg.MSG_WEAPONATTACK;
					else
					if(item instanceof SpellHolder)
					{
						final List<Ability> V=((SpellHolder)item).getSpells();
						for(int v=0;v<V.size();v++)
							if(V.get(v).abstractQuality()==Ability.QUALITY_MALICIOUS)
							{
								targetMsg=CMMsg.MSG_WEAPONATTACK;
								break;
							}
					}
				}
				final CMMsg msg=CMClass.getMsg(mob,target,item,CMMsg.MSG_THROW,targetMsg,CMMsg.MSG_THROW,L("<S-NAME> throw(s) <O-NAME> at <T-NAMESELF>."));
				if(mob.location().okMessage(mob,msg))
					mob.location().send(mob,msg);
			}
		}
		else
		{
			final boolean useShipDirs=((mob.location() instanceof SpaceShip)||(mob.location().getArea() instanceof SpaceShip));
			final int opDir=Directions.getOpDirectionCode(dir);
			final String inDir=useShipDirs?Directions.getShipInDirectionName(dir):Directions.getInDirectionName(dir);
			final String fromDir=useShipDirs?Directions.getShipFromDirectionName(opDir):Directions.getFromDirectionName(opDir);
			final CMMsg msg=CMClass.getMsg(mob,target,item,CMMsg.MSG_THROW,L("<S-NAME> throw(s) <O-NAME> @x1.",inDir.toLowerCase()));
			final CMMsg msg2=CMClass.getMsg(mob,target,item,CMMsg.MSG_THROW,L("<O-NAME> fl(ys) in from @x1.",fromDir.toLowerCase()));
			if(mob.location().okMessage(mob,msg)&&((Room)target).okMessage(mob,msg2))
			{
				mob.location().send(mob,msg);
				((Room)target).sendOthers(mob,msg2);
			}
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
