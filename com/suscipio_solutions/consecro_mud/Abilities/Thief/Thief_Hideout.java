package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor.Expire;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor.Move;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Thief_Hideout extends ThiefSkill
{
	@Override public String ID() { return "Thief_Hideout"; }
	private final static String localizedName = CMLib.lang().L("Hideout");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(In your hideout)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"HIDEOUT"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STREETSMARTS;}

	public Room previousLocation=null;
	public Room shelter=null;

	public Room getPreviousLocation(MOB mob)
	{
		if(previousLocation==null)
		{
			if(text().length()>0)
				previousLocation=CMLib.map().getRoom(text());
			while((previousLocation==null)||(!CMLib.flags().canAccess(mob, previousLocation)))
				previousLocation=CMLib.map().getRandomRoom();
		}
		return previousLocation;
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB M=(MOB)affected;

		if(canBeUninvoked())
		{
			if(shelter==null)
				shelter=M.location();
			Room backToRoom=M.getStartRoom();
			int i=0;
			final LinkedList<MOB> mobs=new LinkedList<MOB>();
			for(final Enumeration<MOB> m=shelter.inhabitants();m.hasMoreElements();)
				mobs.add(m.nextElement());
			for(final MOB mob : mobs)
			{
				if(mob==null) break;
				mob.tell(L("You slip back onto the streets."));

				final CMMsg enterMsg=CMClass.getMsg(mob,previousLocation,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,L("<S-NAME> walk(s) in out of nowhere."));
				backToRoom=getPreviousLocation(mob);
				if(backToRoom==null)
					backToRoom=mob.getStartRoom();
				backToRoom.bringMobHere(mob,false);
				backToRoom.send(mob,enterMsg);
				CMLib.commands().postLook(mob,true);
			}
			final LinkedList<Item> items=new LinkedList<Item>();
			for(final Enumeration<Item> e=shelter.items();e.hasMoreElements();)
				items.add(e.nextElement());
			for(final Item I : items)
			{
				if(I.container()==null)
					backToRoom.moveItemTo(I, Expire.Player_Drop, Move.Followers);
			}
			i=0;
			while(i<shelter.numItems())
			{
				final Item I=shelter.getItem(i);
				backToRoom.moveItemTo(I, Expire.Player_Drop, Move.Followers);
				if(shelter.isContent(I))
					i++;
			}
			shelter=null;
			previousLocation=null;
		}
		super.unInvoke();
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(((msg.sourceMinor()==CMMsg.TYP_QUIT)
			||(msg.sourceMinor()==CMMsg.TYP_SHUTDOWN)
			||(msg.sourceMinor()==CMMsg.TYP_DEATH)
			||((msg.targetMinor()==CMMsg.TYP_EXPIRE)&&(msg.target()==shelter))
			||(msg.sourceMinor()==CMMsg.TYP_ROOMRESET))
		&&(shelter!=null)
		&&(shelter.isInhabitant(msg.source())))
		{
			getPreviousLocation(msg.source()).bringMobHere(msg.source(),false);
			unInvoke();
		}
		else
		if(((msg.sourceMinor()==CMMsg.TYP_LEAVE)&&(msg.target()==shelter))
		||(msg.sourceMinor()==CMMsg.TYP_RECALL))
		{
			getPreviousLocation(msg.source()).bringMobHere(msg.source(),false);
			unInvoke();
			return false;
		}
		return super.okMessage(host,msg);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if(mob.fetchEffect(ID())!=null)
		{
			mob.fetchEffect(ID()).unInvoke();
			return false;
		}

		final Room thisRoom=mob.location();
		if(thisRoom.domainType()!=Room.DOMAIN_OUTDOORS_CITY)
		{
			mob.tell(L("You must be on the streets to enter your hideout."));
			return false;
		}
		TrackingLibrary.TrackingFlags flags;
		flags = new TrackingLibrary.TrackingFlags()
				.plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
				.plus(TrackingLibrary.TrackingFlag.NOAIR);
		final List<Room> nearbyRooms=CMLib.tracking().getRadiantRooms(thisRoom, flags, 2);
		for(final Room room : nearbyRooms)
		{
			switch(room.domainType())
			{
			case Room.DOMAIN_INDOORS_STONE:
			case Room.DOMAIN_INDOORS_METAL:
			case Room.DOMAIN_INDOORS_WOOD:
			case Room.DOMAIN_OUTDOORS_CITY:
				break;
			default:
				mob.tell(L("You must be deep in an urban area to enter your hideout."));
				return false;
			}
		}

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			previousLocation=thisRoom;
			shelter=CMClass.getLocale("HideoutShelter");
			final Exit E=CMClass.getExit("OpenDescriptable");
			E.setDisplayText(L("The way back to @x1",thisRoom.displayText(mob)));
			final int dir=CMLib.dice().roll(1, 4, -1);
			shelter.setRawExit(dir, E);
			shelter.rawDoors()[dir]=thisRoom;
			final Room newRoom=shelter;
			shelter.setArea(mob.location().getArea());
			miscText=CMLib.map().getExtendedRoomID(thisRoom);

			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_THIEF_ACT,auto?"":L("<S-NAME> slip(s) away."));
			final CMMsg enterMsg=CMClass.getMsg(mob,newRoom,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,null,CMMsg.MSG_ENTER,L("<S-NAME> duck(s) into the hideout."));
			if(thisRoom.okMessage(mob,msg) && newRoom.okMessage(mob,enterMsg))
			{
				if(mob.isInCombat())
				{
					CMLib.commands().postFlee(mob,("NOWHERE"));
					mob.makePeace();
				}
				thisRoom.send(mob,msg);
				newRoom.bringMobHere(mob,false);
				thisRoom.delInhabitant(mob);
				newRoom.send(mob,enterMsg);
				mob.tell(L("\n\r\n\r"));
				CMLib.commands().postLook(mob,true);
				beneficialAffect(mob,mob,asLevel,999999);
			}
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> attemp(s) to slip away, and fail(s)."));

		return success;
	}
}
