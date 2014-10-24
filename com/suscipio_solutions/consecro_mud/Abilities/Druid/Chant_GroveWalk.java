package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_GroveWalk extends Chant
{
	@Override public String ID() { return "Chant_GroveWalk"; }
	private final static String localizedName = CMLib.lang().L("Grove Walk");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_SHAPE_SHIFTING;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<1)
		{
			mob.tell(L("You must specify the name of the location of another grove where there is a druidic monument."));
			return false;
		}
		final String areaName=CMParms.combine(commands,0).trim().toUpperCase();


		Room newRoom=null;
		final boolean hereok=mob.location().findItem(null,"DruidicMonument")!=null;
		try
		{
			final List<Room> rooms=CMLib.map().findRooms(CMLib.map().rooms(), mob,areaName,true,10);
			for(final Room R : rooms)
			{
				for(int i=0;i<R.numItems();i++)
				{
					final Item I=R.getItem(i);
					if((I!=null)&&(I.ID().equals("DruidicMonument")))
					{
						newRoom=R;
						break;
					}
				}
				if(newRoom!=null) break;
			}
		}catch(final NoSuchElementException e){}
		if(!hereok)
		{
			mob.tell(L("There is no druidic monument here.  You can only use this chant in a druidic grove."));
			return false;
		}
		if(newRoom==null)
		{
			mob.tell(L("You can't seem to fixate on a place called '@x1', perhaps it is not a grove?",CMParms.combine(commands,0)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,newRoom,this,verbalCastCode(mob,newRoom,auto),auto?"":L("^S<S-NAME> chant(s) and walk(s) around.^?"));
			if((mob.location().okMessage(mob,msg))&&(newRoom.okMessage(mob,msg)))
			{
				mob.location().send(mob,msg);
				final Set<MOB> h=properTargets(mob,givenTarget,false);
				if(h==null) return false;

				final Room thisRoom=mob.location();
				for (final Object element : h)
				{
					final MOB follower=(MOB)element;
					final CMMsg enterMsg=CMClass.getMsg(follower,newRoom,this,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,L("<S-NAME> emerge(s) from around the stones."));
					final CMMsg leaveMsg=CMClass.getMsg(follower,thisRoom,this,CMMsg.MSG_LEAVE|CMMsg.MASK_MAGIC,L("<S-NAME> disappear(s) around the stones."));
					if(thisRoom.okMessage(follower,leaveMsg)&&newRoom.okMessage(follower,enterMsg))
					{
						if(follower.isInCombat())
						{
							CMLib.commands().postFlee(follower,("NOWHERE"));
							follower.makePeace();
						}
						thisRoom.send(follower,leaveMsg);
						newRoom.bringMobHere(follower,false);
						newRoom.send(follower,enterMsg);
						follower.tell(L("\n\r\n\r"));
						CMLib.commands().postLook(follower,true);
					}
				}
			}

		}
		else
			beneficialVisualFizzle(mob,newRoom,L("<S-NAME> chant(s) and walk(s) around, but nothing happens."));


		// return whether it worked
		return success;
	}
}
