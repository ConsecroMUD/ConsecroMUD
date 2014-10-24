package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_Den extends Chant
{
	@Override public String ID() { return "Chant_Den"; }
	private final static String localizedName = CMLib.lang().L("Den");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Den)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_DEEPMAGIC;}
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
				R.showHappens(CMMsg.MSG_OK_VISUAL,L("The den fades away..."));
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
			mob.tell(L("There is already a den here!"));
			return false;
		}
		if(mob.location().domainType()!=Room.DOMAIN_INDOORS_CAVE)
		{
			mob.tell(L("This magic will only work in a cave."));
			return false;
		}
		if(mob.location().roomID().length()==0)
		{
			mob.tell(L("This magic will not work here."));
			return false;
		}
		final Vector dirChoices=new Vector();
		for(final int dir : Directions.CODES())
		{
			if(mob.location().getRoomInDir(dir)==null)
				dirChoices.addElement(Integer.valueOf(dir));
		}
		if(dirChoices.size()==0)
		{
			mob.tell(L("This magic will not work here."));
			return false;
		}
		final int d=((Integer)dirChoices.elementAt(CMLib.dice().roll(1,dirChoices.size(),-1))).intValue();

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

			final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob,null,auto), auto?"":L("^S<S-NAME> chant(s) for a den!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("Your den, carefully covered, appears to the @x1!",Directions.getDirectionName(d)));
				final Room newRoom=CMClass.getLocale("CaveRoom");
				newRoom.setDisplayText(L("A musty den"));
				newRoom.setDescription(L("You are in a dark rocky den!"));
				newRoom.setArea(mob.location().getArea());
				mob.location().rawDoors()[d]=newRoom;
				mob.location().setRawExit(d,CMClass.getExit("HiddenWalkway"));
				newRoom.rawDoors()[Directions.getOpDirectionCode(d)]=mob.location();
				Ability A=CMClass.getAbility("Prop_RoomView");
				A.setMiscText(CMLib.map().getExtendedRoomID(mob.location()));
				final Exit E=CMClass.getExit("Open");
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

				newRoom.setRawExit(Directions.getOpDirectionCode(d),E);
				newRoom.getArea().fillInAreaRoom(newRoom);
				beneficialAffect(mob,mob.location(),asLevel,CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH));
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s) for a den, but the magic fades."));

		// return whether it worked
		return success;
	}
}
