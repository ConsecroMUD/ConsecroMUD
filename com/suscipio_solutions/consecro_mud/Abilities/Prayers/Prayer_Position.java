package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.collections.ReadOnlyVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_Position extends Prayer
{
	@Override public String ID() { return "Prayer_Position"; }
	private final static String localizedName = CMLib.lang().L("Position");
	@Override public String name() { return localizedName; }
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_COMMUNING;}
	public Room lastPosition=null;

	protected int getRoomDirection(Room R, Room toRoom, Vector ignore)
	{
		for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			if((R.getRoomInDir(d)==toRoom)
			&&(R!=toRoom)
			&&(!ignore.contains(R)))
				return d;
		return -1;
	}
	public String trailTo(Room R1, Room R2)
	{
		final Vector set=new Vector();
		TrackingLibrary.TrackingFlags flags;
		flags = new TrackingLibrary.TrackingFlags()
				.plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS);
		CMLib.tracking().getRadiantRooms(R1,set,flags,R2,Integer.MAX_VALUE,null);
		int foundAt=-1;
		for(int i=0;i<set.size();i++)
		{
			final Room R=(Room)set.elementAt(i);
			if(R==R2){ foundAt=i; break;}
		}
		if(foundAt<0) return "You can't get to '"+R2.roomID()+"' from here.";
		Room checkR=R2;
		final Vector trailV=new Vector();
		trailV.addElement(R2);
		boolean didSomething=false;
		while(checkR!=R1)
		{
			didSomething=false;
			for(int r=0;r<foundAt;r++)
			{
				final Room R=(Room)set.elementAt(r);
				if(getRoomDirection(R,checkR,trailV)>=0)
				{
					trailV.addElement(R);
					foundAt=r;
					checkR=R;
					didSomething=true;
					break;
				}
			}
			if(!didSomething)
				return "No trail was found?!";
		}
		final Vector theDirTrail=new Vector();
		final Vector empty=new ReadOnlyVector();
		for(int s=trailV.size()-1;s>=1;s--)
		{
			final Room R=(Room)trailV.elementAt(s);
			final Room RA=(Room)trailV.elementAt(s-1);
			theDirTrail.addElement(Character.toString(Directions.getDirectionName(getRoomDirection(R,RA,empty)).charAt(0))+" ");
		}
		final StringBuffer theTrail=new StringBuffer("");
		char lastDir='\0';
		int lastNum=0;
		while(theDirTrail.size()>0)
		{
			final String s=(String)theDirTrail.elementAt(0);
			if(lastNum==0)
			{
				lastDir=s.charAt(0);
				lastNum=1;
			}
			else
			if(s.charAt(0)==lastDir)
				lastNum++;
			else
			{
				if(lastNum==1)
					theTrail.append(Character.toString(lastDir)+" ");
				else
					theTrail.append(Integer.toString(lastNum)+Character.toString(lastDir)+" ");
				lastDir=s.charAt(0);
				lastNum=1;
			}
			theDirTrail.removeElementAt(0);
		}
		if(lastNum==1)
			theTrail.append(Character.toString(lastDir));
		else
		if(lastNum>0)
			theTrail.append(Integer.toString(lastNum)+Character.toString(lastDir));
		return theTrail.toString();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(lastPosition==null) lastPosition=mob.getStartRoom();
		if(lastPosition==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> @x1 for a position check.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.tell(L("The trail from @x1 to here is: @x2",lastPosition.name(),trailTo(lastPosition,mob.location())));
				lastPosition=mob.location();
			}
		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> @x1 for a position check, but fail(s).",prayWord(mob)));

		return success;
	}
}
