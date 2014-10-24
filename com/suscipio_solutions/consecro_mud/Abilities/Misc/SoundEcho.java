package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



@SuppressWarnings({"unchecked","rawtypes"})
public class SoundEcho extends StdAbility
{
	@Override public String ID() { return "SoundEcho"; }
	private final static String localizedName = CMLib.lang().L("Sound Echo");
	@Override public String name() { return localizedName; }
	protected String displayText="";
	@Override public String displayText(){ return displayText;}
	@Override protected int canAffectCode(){return CAN_ROOMS|CAN_AREAS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_PROPERTY;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}

	public static MOB bmob=null;
	public synchronized MOB blindMOB()
	{
		if(bmob!=null) return bmob;
		bmob=CMClass.getMOB("StdMOB");
		if(bmob!=null)
		{
			bmob.setName(L("Someone"));
			bmob.basePhyStats().setSensesMask(PhyStats.CAN_NOT_SEE);
			bmob.recoverPhyStats();
		}
		return bmob;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((CMath.bset(msg.othersMajor(),CMMsg.MASK_SOUND))
		&&(CMath.bset(msg.sourceMajor(),CMMsg.MASK_SOUND))
		&&(msg.othersMessage()!=null)
		&&(msg.othersMessage().indexOf("You hear an echo: ")<0)
		&&(msg.source().location()!=null))
		{
			synchronized(this)
			{
				int range=CMath.s_int(text());
				if(range==0) range=10;
				final Room sourceRoom=msg.source().location();
				String str=msg.othersMessage();
				str=CMLib.coffeeFilter().fullOutFilter(null,blindMOB(),msg.source(),msg.target(),msg.tool(),str,false);
				CMMsg echoMsg=(CMMsg)msg.copyOf();
				final Vector doneRooms=new Vector();
				if(echoMsg.sourceMessage()!=null)
					echoMsg.setSourceMessage("You hear an echo: "+CMLib.coffeeFilter().fullOutFilter(null,blindMOB(),msg.source(),msg.target(),msg.tool(),msg.sourceMessage(),false));
				if(echoMsg.targetMessage()!=null)
					echoMsg.setTargetMessage("You hear an echo: "+CMLib.coffeeFilter().fullOutFilter(null,blindMOB(),msg.source(),msg.target(),msg.tool(),msg.targetMessage(),false));
				if(echoMsg.othersMessage()!=null)
					echoMsg.setOthersMessage("You hear an echo: "+CMLib.coffeeFilter().fullOutFilter(null,blindMOB(),msg.source(),msg.target(),msg.tool(),msg.othersMessage(),false));
				msg.addTrailerMsg(echoMsg);
				echoMsg=CMClass.getMsg(msg.source(),msg.target(),msg.tool(),CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null,msg.othersCode(),str);
				final Vector rooms=new Vector();
				TrackingLibrary.TrackingFlags flags;
				flags = new TrackingLibrary.TrackingFlags()
						.plus(TrackingLibrary.TrackingFlag.OPENONLY)
						.plus(TrackingLibrary.TrackingFlag.AREAONLY);
				CMLib.tracking().getRadiantRooms(sourceRoom,rooms,flags,null,range/2,null);
				Room room=null;
				for(int v=0;v<rooms.size();v++)
				{
					room=(Room)rooms.elementAt(v);
					if((room!=sourceRoom)&&(!doneRooms.contains(room)))
					{
						doneRooms.add(room);
						if(CMLib.dice().rollPercentage()<50)
						{
							final int direction=CMLib.tracking().radiatesFromDir(room,rooms);
							echoMsg.setOthersMessage("You hear an echo coming from "+Directions.getFromDirectionName(direction)+": "+str);
						}
						else
							echoMsg.setOthersMessage("You hear an echo coming from "+Directions.getFromDirectionName(CMLib.dice().roll(1,Directions.NUM_DIRECTIONS(),-1))+": "+str);
						room.sendOthers(msg.source(),echoMsg);
					}
				}
				rooms.clear();
				CMLib.tracking().getRadiantRooms(sourceRoom,rooms,flags,null,range,null);
				for(int v=0;v<rooms.size();v++)
				{
					room=(Room)rooms.elementAt(v);
					if((room!=sourceRoom)&&(!doneRooms.contains(room)))
					{
						doneRooms.add(room);
						if(room.numInhabitants()>0)
						{
							if(CMLib.dice().rollPercentage()<50)
							{
								final int direction=CMLib.tracking().radiatesFromDir(room,rooms);
								echoMsg.setOthersMessage("You hear a faint echo coming from "+Directions.getFromDirectionName(direction)+".");
							}
							else
								echoMsg.setOthersMessage("You hear a faint echo coming from "+Directions.getDirectionName(CMLib.dice().roll(1,Directions.NUM_DIRECTIONS(),-1))+".");
							room.sendOthers(msg.source(),echoMsg);
						}
					}
				}
			}
		}
	}
}
