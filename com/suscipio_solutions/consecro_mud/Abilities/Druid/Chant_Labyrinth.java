package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.GridLocale;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_Labyrinth extends Chant
{
	@Override public String ID() { return "Chant_Labyrinth"; }
	private final static String localizedName = CMLib.lang().L("Labyrinth");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Labyrinth)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_DEEPMAGIC;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return CAN_ROOMS;}
	Room oldRoom=null;

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(affected==null)
			return;
		if(!(affected instanceof Room))
			return;
		final Room room=(Room)affected;
		if((canBeUninvoked())&&(room instanceof GridLocale)&&(oldRoom!=null))
			((GridLocale)room).clearGrid(oldRoom);
		for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			room.setRawExit(d,null);
		super.unInvoke();
		room.destroy();
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(((msg.sourceMinor()==CMMsg.TYP_QUIT)
			||(msg.sourceMinor()==CMMsg.TYP_SHUTDOWN)
			||((msg.targetMinor()==CMMsg.TYP_EXPIRE)&&(msg.target()==oldRoom))
			||(msg.sourceMinor()==CMMsg.TYP_ROOMRESET))
		&&(msg.source().location()!=null)
		&&(msg.source().location().getGridParent()==affected))
		{
			if(oldRoom!=null)
				oldRoom.bringMobHere(msg.source(),false);
			if(msg.source()==invoker)
				unInvoke();
		}
		return super.okMessage(host,msg);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.location().roomID().length()==0)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.location().roomID().length()==0)
		{
			mob.tell(L("You cannot invoke the labyrinth here."));
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

			final CMMsg msg = CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto), auto?"":L("^S<S-NAME> chant(s) twistedly!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("Something is happening..."));

				final Room newRoom=CMClass.getLocale("CaveMaze");
				((GridLocale)newRoom).setXGridSize(10);
				((GridLocale)newRoom).setYGridSize(10);
				newRoom.setDisplayText(L("The Labyrinth"));
				newRoom.addNonUninvokableEffect(CMClass.getAbility("Prop_NoTeleportOut"));
				final StringBuffer desc=new StringBuffer("");
				desc.append("You are lost in dark twisting caverns.  The darkness covers you like a blanket. Every turn looks the same.");
				newRoom.setArea(mob.location().getArea());
				oldRoom=mob.location();
				newRoom.setDescription(desc.toString());
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				{
					final Room R=mob.location().rawDoors()[d];
					final Exit E=mob.location().getRawExit(d);
					if((R!=null)&&(R.roomID().length()>0))
					{
						newRoom.rawDoors()[d]=R;
						newRoom.setRawExit(d,E);
					}
				}
				newRoom.getArea().fillInAreaRoom(newRoom);
				beneficialAffect(mob,newRoom,asLevel,0);
				final Vector everyone=new Vector();
				for(int m=0;m<oldRoom.numInhabitants();m++)
				{
					final MOB follower=oldRoom.fetchInhabitant(m);
					everyone.addElement(follower);
				}

				for(int m=0;m<everyone.size();m++)
				{
					final MOB follower=(MOB)everyone.elementAt(m);
					if(follower==null) continue;
					final Room newerRoom=((GridLocale)newRoom).getRandomGridChild();
					final CMMsg enterMsg=CMClass.getMsg(follower,newerRoom,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,L("<S-NAME> appears out of thin air."));
					final CMMsg leaveMsg=CMClass.getMsg(follower,oldRoom,this,verbalCastCode(mob,oldRoom,auto),L("<S-NAME> disappear(s) into the labyrinth."));
					if(oldRoom.okMessage(follower,leaveMsg)&&newerRoom.okMessage(follower,enterMsg))
					{
						if(follower.isInCombat())
							follower.makePeace();
						oldRoom.send(follower,leaveMsg);
						newerRoom.bringMobHere(follower,false);
						newerRoom.send(follower,enterMsg);
						follower.tell(L("\n\r\n\r"));
						CMLib.commands().postLook(follower,true);
					}
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s) twistedly, but the magic fades."));

		// return whether it worked
		return success;
	}
}
