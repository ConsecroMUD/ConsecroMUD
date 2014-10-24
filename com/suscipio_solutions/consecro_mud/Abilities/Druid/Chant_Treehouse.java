package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_Treehouse extends Chant
{
	@Override public String ID() { return "Chant_Treehouse"; }
	private final static String localizedName = CMLib.lang().L("Treehouse");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Treehouse)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return CAN_ROOMS;}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(affected==null)
			return;
		if(!(affected instanceof Room))
			return;
		final Room room=(Room)affected;
		if(canBeUninvoked())
		{
			final Room R=room.getRoomInDir(Directions.UP);
			if((R!=null)&&(R.roomID().equalsIgnoreCase("")))
			{
				R.showHappens(CMMsg.MSG_OK_VISUAL,L("The treehouse fades away..."));
				while(R.numInhabitants()>0)
				{
					final MOB M=R.fetchInhabitant(0);
					if(M!=null)	room.bringMobHere(M,false);
				}
				while(R.numItems()>0)
				{
					final Item I=R.getItem(0);
					if(I!=null) room.moveItemTo(I);
				}
				R.destroy();
				room.rawDoors()[Directions.UP]=null;
				room.setRawExit(Directions.UP,null);
			}
			room.clearSky();
		}
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target = mob.location();
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("There is already a treehouse above here!"));
			return false;
		}
		boolean isATree=((mob.location().domainType()==Room.DOMAIN_OUTDOORS_WOODS)
					   ||(mob.location().domainType()!=Room.DOMAIN_OUTDOORS_JUNGLE));
		final Item myPlant=Druid_MyPlants.myPlant(mob.location(),mob,0);
		if((myPlant != null) &&((myPlant.material()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_WOODEN))
			isATree=true;
		if((mob.location().myResource()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_WOODEN)
			isATree=true;
		if(!isATree)
		{
			mob.tell(L("There really aren't enough trees here to chant to."));
			return false;
		}
		if(mob.location().roomID().length()==0)
		{
			mob.tell(L("This magic will not work here."));
			return false;
		}
		if((mob.location().getRoomInDir(Directions.UP)!=null)
		&&(mob.location().getRoomInDir(Directions.UP).roomID().length()>0))
		{
			mob.tell(L("You can't create a treehouse here!"));
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

			final CMMsg msg = CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto), auto?"":L("^S<S-NAME> chant(s) for a treehouse!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("A treehouse appears up in a nearby tree!"));
				mob.location().clearSky();
				final Room newRoom=CMClass.getLocale("WoodRoom");
				newRoom.setDisplayText(L("A treehouse"));
				newRoom.setDescription(L("You are up in the treehouse. The view is great from up here!"));
				newRoom.setArea(mob.location().getArea());
				mob.location().rawDoors()[Directions.UP]=newRoom;
				mob.location().setRawExit(Directions.UP,CMClass.getExit("ClimbableExit"));
				newRoom.rawDoors()[Directions.DOWN]=mob.location();
				Ability A=CMClass.getAbility("Prop_RoomView");
				A.setMiscText(CMLib.map().getExtendedRoomID(mob.location()));
				Exit E=CMClass.getExit("ClimbableExit");
				E.addNonUninvokableEffect(A);
				A=CMClass.getAbility("Prop_PeaceMaker");
				if(A!=null) newRoom.addEffect(A);
				A=CMClass.getAbility("Prop_NoRecall");
				if(A!=null) newRoom.addEffect(A);
				A=CMClass.getAbility("Prop_NoSummon");
				if(A!=null) newRoom.addEffect(A);
				A=CMClass.getAbility("Prop_NoTeleport");
				if(A!=null) newRoom.addEffect(A);
				A=CMClass.getAbility("Prop_NoTeleportOut");
				if(A!=null) newRoom.addEffect(A);

				newRoom.setRawExit(Directions.DOWN,E);
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				{
					final Room R=mob.location().rawDoors()[d];
					if((R!=null)
					   &&(d!=Directions.DOWN)
					   &&(d!=Directions.UP)
					   &&(R.roomID().length()>0)
					   &&((R.domainType()&Room.INDOORS)==0))
					{
						newRoom.rawDoors()[d]=R;
						A=CMClass.getAbility("Prop_RoomView");
						A.setMiscText(CMLib.map().getExtendedRoomID(R));
						E=CMClass.getExit("Impassable");
						E.addNonUninvokableEffect(A);
						newRoom.setRawExit(d,E);
					}
				}
				newRoom.getArea().fillInAreaRoom(newRoom);
				beneficialAffect(mob,mob.location(),asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s) for a treehouse, but the magic fades."));

		// return whether it worked
		return success;
	}
}
