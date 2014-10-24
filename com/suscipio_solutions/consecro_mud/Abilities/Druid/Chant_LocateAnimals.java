package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;




@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_LocateAnimals extends Chant
{
	@Override public String ID() { return "Chant_LocateAnimals"; }
	private final static String localizedName = CMLib.lang().L("Locate Animals");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ANIMALAFFINITY;}
	protected String displayText=L("(Locating Animals)");
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public String displayText(){return displayText;}

	protected List<Room> theTrail=null;
	public int nextDirection=-2;
	@Override public long flags(){return Ability.FLAG_TRACKING;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(tickID==Tickable.TICKID_MOB)
		{
			if(nextDirection==-999)
				return true;

			if((theTrail==null)
			||(affected == null)
			||(!(affected instanceof MOB)))
				return false;

			final MOB mob=(MOB)affected;

			if(nextDirection==999)
			{
				mob.tell(L("The trail seems to pause here."));
				nextDirection=-2;
				unInvoke();
			}
			else
			if(nextDirection==-1)
			{
				mob.tell(L("The trail dries up here."));
				nextDirection=-999;
				unInvoke();
			}
			else
			if(nextDirection>=0)
			{
				mob.tell(L("The trail seems to continue @x1.",Directions.getDirectionName(nextDirection)));
				nextDirection=-2;
			}

		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);

		if(!(affected instanceof MOB))
			return;

		final MOB mob=(MOB)affected;
		if((msg.amISource(mob))
		&&(msg.amITarget(mob.location()))
		&&(CMLib.flags().canBeSeenBy(mob.location(),mob))
		&&(msg.targetMinor()==CMMsg.TYP_LOOK))
			nextDirection=CMLib.tracking().trackNextDirectionFromHere(theTrail,mob.location(),false);
	}

	public MOB animalHere(Room room)
	{
		if(room==null) return null;
		for(int i=0;i<room.numInhabitants();i++)
		{
			final MOB mob=room.fetchInhabitant(i);
			if(CMLib.flags().isAnimalIntelligence(mob))
				return mob;
		}
		return null;
	}

	@Override
	public void affectPhyStats(Physical affectedEnv, PhyStats affectableStats)
	{
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_WORK);
		super.affectPhyStats(affectedEnv, affectableStats);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.fetchEffect(this.ID())!=null)
		{
			mob.tell(L("You are already trying to locate animals."));
			return false;
		}
		final List<Ability> V=CMLib.flags().flaggedAffects(mob,Ability.FLAG_TRACKING);
		for(final Ability A : V) A.unInvoke();

		theTrail=null;
		nextDirection=-2;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if(animalHere(mob.location())!=null)
		{
			mob.tell(L("Try 'look'."));
			return false;
		}

		final boolean success=proficiencyCheck(mob,0,auto);

		final Vector rooms=new Vector();
		final TrackingLibrary.TrackingFlags flags=new TrackingLibrary.TrackingFlags();
		final List<Room> checkSet=CMLib.tracking().getRadiantRooms(mob.location(),flags,20);
		for (final Room room : checkSet)
		{
			final Room R=CMLib.map().getRoom(room);
			if(animalHere(R)!=null)
				rooms.addElement(R);
		}

		while(rooms.size()>10)
			rooms.removeElementAt(CMLib.dice().roll(1,rooms.size(),-1));

		if(rooms.size()>0)
			theTrail=CMLib.tracking().findBastardTheBestWay(mob.location(),rooms,flags,50);

		MOB target=null;
		if((theTrail!=null)&&(theTrail.size()>0))
			target=animalHere(theTrail.get(0));

		if((success)&&(theTrail!=null)&&(target!=null))
		{
			theTrail.add(mob.location());
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("^S<S-NAME> chant(s) for the animals.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				invoker=mob;
				displayText=L("(seeking @x1)",target.name());
				final Chant_LocateAnimals newOne=(Chant_LocateAnimals)this.copyOf();
				if(mob.fetchEffect(newOne.ID())==null)
					mob.addEffect(newOne);
				mob.recoverPhyStats();
				newOne.nextDirection=CMLib.tracking().trackNextDirectionFromHere(newOne.theTrail,mob.location(),false);
			}
		}
		else
			return beneficialVisualFizzle(mob,null,L("<S-NAME> chant(s) for the animals, but nothing happens."));


		// return whether it worked
		return success;
	}
}
