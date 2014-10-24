package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_Gateway extends Prayer
{
	@Override public String ID() { return "Prayer_Gateway"; }
	private final static String localizedName = CMLib.lang().L("Gateway");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return 0;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY|Ability.FLAG_TRANSPORTING;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CREATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	Room newRoom=null;
	Room oldRoom=null;

	@Override
	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if(newRoom!=null)
			{
				newRoom.showHappens(CMMsg.MSG_OK_VISUAL,L("The divine gateway closes."));
				newRoom.rawDoors()[Directions.GATE]=null;
				newRoom.setRawExit(Directions.GATE,null);
			}
			if(oldRoom!=null)
			{
				oldRoom.showHappens(CMMsg.MSG_OK_VISUAL,L("The divine gateway closes."));
				oldRoom.rawDoors()[Directions.GATE]=null;
				oldRoom.setRawExit(Directions.GATE,null);
			}
		}
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((auto||mob.isMonster())&&(commands.size()==0))
			commands.addElement(CMLib.map().getRandomRoom().displayText());
		if(commands.size()<1)
		{
			mob.tell(L("Pray for a gateway to where?"));
			return false;
		}
		if((mob.location().getRoomInDir(Directions.GATE)!=null)
		||(mob.location().getExitInDir(Directions.GATE)!=null))
		{
			mob.tell(L("A gateway cannot be created here."));
			return false;
		}
		final String areaName=CMParms.combine(commands,0).trim().toUpperCase();
		oldRoom=null;
		newRoom=null;
		try
		{
			final List<Room> rooms=CMLib.map().findRooms(CMLib.map().rooms(), mob, areaName,true,10);
			if(rooms.size()>0)
				newRoom=rooms.get(CMLib.dice().roll(1,rooms.size(),-1));
		}catch(final NoSuchElementException e){}

		if(newRoom==null)
		{
			mob.tell(L("You don't know of a place called '@x1'.",CMParms.combine(commands,0)));
			return false;
		}

		int profNeg=0;
		for(int i=0;i<newRoom.numInhabitants();i++)
		{
			final MOB t=newRoom.fetchInhabitant(i);
			if(t!=null)
			{
				int adjustment=t.phyStats().level()-(mob.phyStats().level()+(2*super.getXLEVELLevel(mob)));
				if(t.isMonster()) adjustment=adjustment*3;
				profNeg+=adjustment;
			}
		}
		profNeg+=newRoom.numItems()*20;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,-profNeg,auto);

		if((success)
		&&((newRoom.getRoomInDir(Directions.GATE)==null)
		&&(newRoom.getExitInDir(Directions.GATE)==null)))
		{
			final CMMsg msg=CMClass.getMsg(mob,mob.location(),this,verbalCastCode(mob,mob.location(),auto),L("^S<S-NAME> @x1 for a blinding, divine gateway here.^?",prayWord(mob)));
			final CMMsg msg2=CMClass.getMsg(mob,newRoom,this,verbalCastCode(mob,newRoom,auto),L("A blinding, divine gateway appears here."));
			if((mob.location().okMessage(mob,msg))&&(newRoom.okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				newRoom.send(mob,msg2);
				final Exit e=CMClass.getExit("GenExit");
				e.setDescription(L("A divine gateway to somewhere"));
				e.setDisplayText(L("A divine gateway to somewhere"));
				e.setDoorsNLocks(false,true,false,false,false,false);
				e.setExitParams("gateway","close","open","closed.");
				e.setName(L("a divine gateway"));
				mob.location().rawDoors()[Directions.GATE]=newRoom;
				newRoom.rawDoors()[Directions.GATE]=mob.location();
				mob.location().setRawExit(Directions.GATE,e);
				newRoom.setRawExit(Directions.GATE,e);
				oldRoom=mob.location();
				beneficialAffect(mob,e,asLevel,15);
			}
		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> @x1 for a gateway, but nothing happens.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
