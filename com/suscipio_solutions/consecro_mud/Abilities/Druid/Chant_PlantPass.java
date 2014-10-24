package com.suscipio_solutions.consecro_mud.Abilities.Druid;
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
public class Chant_PlantPass extends Chant
{
	@Override public String ID() { return "Chant_PlantPass"; }
	private final static String localizedName = CMLib.lang().L("Plant Pass");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_SHAPE_SHIFTING;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override public long flags(){return Ability.FLAG_TRANSPORTING;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<1)
		{
			mob.tell(L("You must specify the name of the location of one of your plants.  Use your 'My Plants' skill if necessary."));
			return false;
		}
		final String areaName=CMParms.combine(commands,0).trim().toUpperCase();

		final Item myPlant=Druid_MyPlants.myPlant(mob.location(),mob,0);
		if(myPlant==null)
		{
			mob.tell(L("There doesn't appear to be any of your plants here to travel through."));
			return false;
		}

		final Vector candidates=Druid_MyPlants.myPlantRooms(mob);
		Room newRoom=null;
		for(int m=0;m<candidates.size();m++)
		{
			final Room room=(Room)candidates.elementAt(m);
			if(CMLib.english().containsString(room.displayText(mob),areaName))
			{
			   newRoom=room;
			   break;
			}
		}
		if(newRoom==null)
		{
			mob.tell(L("You can't seem to fixate on a place called '@x1', perhaps you have nothing growing there?",CMParms.combine(commands,0)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,myPlant,this,CMMsg.MASK_MOVE|verbalCastCode(mob,myPlant,auto),auto?"":L("^S<S-NAME> chant(s) to <T-NAMESELF> and <S-IS-ARE> drawn into it!^?"));
			if((mob.location().okMessage(mob,msg))&&(newRoom.okMessage(mob,msg)))
			{
				mob.location().send(mob,msg);
				final Set<MOB> h=properTargets(mob,givenTarget,false);
				if(h==null) return false;

				final Room thisRoom=mob.location();
				for (final Object element : h)
				{
					final MOB follower=(MOB)element;
					final CMMsg enterMsg=CMClass.getMsg(follower,newRoom,this,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,L("<S-NAME> emerge(s) from the ground."));
					final CMMsg leaveMsg=CMClass.getMsg(follower,thisRoom,this,CMMsg.MSG_LEAVE|CMMsg.MASK_MAGIC,L("<S-NAME> <S-IS-ARE> sucked into @x1.",myPlant.name()));
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
			beneficialVisualFizzle(mob,myPlant,L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
