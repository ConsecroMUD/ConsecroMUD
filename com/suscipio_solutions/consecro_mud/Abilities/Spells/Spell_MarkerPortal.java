package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_MarkerPortal extends Spell
{
	@Override public String ID() { return "Spell_MarkerPortal"; }
	private final static String localizedName = CMLib.lang().L("Marker Portal");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	@Override public long flags(){return Ability.FLAG_TRANSPORTING;}
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
				newRoom.showHappens(CMMsg.MSG_OK_VISUAL,L("The swirling portal closes."));
				newRoom.rawDoors()[Directions.GATE]=null;
				newRoom.setRawExit(Directions.GATE,null);
			}
			if(oldRoom!=null)
			{
				oldRoom.showHappens(CMMsg.MSG_OK_VISUAL,L("The swirling portal closes."));
				oldRoom.rawDoors()[Directions.GATE]=null;
				oldRoom.setRawExit(Directions.GATE,null);
			}
		}
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		newRoom=null;
		oldRoom=null;

		try
		{
			for(final Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
			{
				final Room R=(Room)r.nextElement();
				if(CMLib.flags().canAccess(mob,R))
					for(final Enumeration<Ability> a=R.effects();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if((A!=null)
						&&(A.ID().equals("Spell_SummonMarker"))
						&&(A.invoker()==mob))
						{
							newRoom=R;
							break;
						}
					}
				if(newRoom!=null) break;
			}
		}catch(final NoSuchElementException nse){}
		if(newRoom==null)
		{
			mob.tell(L("You can't seem to focus on your marker.  Are you sure you've already summoned it?"));
			return false;
		}
		oldRoom=mob.location();
		if(oldRoom==newRoom)
		{
			mob.tell(L("But your marker is HERE!"));
			return false;
		}

		if((oldRoom.getRoomInDir(Directions.GATE)!=null)
		||(oldRoom.getExitInDir(Directions.GATE)!=null))
		{
			mob.tell(L("A portal cannot be created here."));
			return false;
		}

		int profNeg=0;
		for(int i=0;i<newRoom.numInhabitants();i++)
		{
			final MOB t=newRoom.fetchInhabitant(i);
			if(t!=null)
			{
				int adjustment=t.phyStats().level()-(mob.phyStats().level()+(2*getXLEVELLevel(mob)));
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
			final CMMsg msg=CMClass.getMsg(mob,oldRoom,this,verbalCastCode(mob,oldRoom,auto),L("^S<S-NAME> conjur(s) a blinding, swirling portal here.^?"));
			final CMMsg msg2=CMClass.getMsg(mob,newRoom,this,verbalCastCode(mob,newRoom,auto),L("A blinding, swirling portal appears here."));
			if((oldRoom.okMessage(mob,msg))&&(newRoom.okMessage(mob,msg2)))
			{
				oldRoom.send(mob,msg);
				newRoom.send(mob,msg2);
				final Exit e=CMClass.getExit("GenExit");
				e.setDescription(L("A swirling portal to somewhere"));
				e.setDisplayText(L("A swirling portal to somewhere"));
				e.setDoorsNLocks(false,true,false,false,false,false);
				e.setExitParams("portal","close","open","closed.");
				e.setName(L("a swirling portal"));
				final Ability A1=CMClass.getAbility("Prop_RoomView");
				if(A1!=null)
				{
					A1.setMiscText(CMLib.map().getExtendedRoomID(newRoom));
					e.addNonUninvokableEffect(A1);
				}
				final Exit e2=(Exit)e.copyOf();
				final Ability A2=CMClass.getAbility("Prop_RoomView");
				if(A2!=null)
				{
					A2.setMiscText(CMLib.map().getExtendedRoomID(mob.location()));
					e2.addNonUninvokableEffect(A2);
				}
				oldRoom.rawDoors()[Directions.GATE]=newRoom;
				newRoom.rawDoors()[Directions.GATE]=oldRoom;
				oldRoom.setRawExit(Directions.GATE,e);
				newRoom.setRawExit(Directions.GATE,e2);
				beneficialAffect(mob,e,asLevel,5);
			}
		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> attempt(s) to conjur a portal, but fizzle(s) the spell."));


		// return whether it worked
		return success;
	}
}
