package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class MobileGoodGuardian extends Mobile
{
	@Override public String ID(){return "MobileGoodGuardian";}

	@Override
	public String accountForYourself()
	{
		return "wandering protectiveness against aggression, evilness, or thieflyness";
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		tickStatus=Tickable.STATUS_MISC+0;
		super.tick(ticking,tickID);

		tickStatus=Tickable.STATUS_MISC+1;
		if(tickID!=Tickable.TICKID_MOB)
		{
			tickStatus=Tickable.STATUS_NOT;
			return true;
		}
		if(!canFreelyBehaveNormal(ticking))
		{
			tickStatus=Tickable.STATUS_NOT;
			return true;
		}
		final MOB mob=(MOB)ticking;

		// ridden things dont wander!
		if(ticking instanceof Rideable)
			if(((Rideable)ticking).numRiders()>0)
			{
				tickStatus=Tickable.STATUS_NOT;
				return true;
			}
		tickStatus=Tickable.STATUS_MISC+2;
		if(((mob.amFollowing()!=null)&&(mob.location()==mob.amFollowing().location()))
		||(!CMLib.flags().canTaste(mob)))
		{
			tickStatus=Tickable.STATUS_NOT;
			return true;
		}

		tickStatus=Tickable.STATUS_MISC+3;
		final Room thisRoom=mob.location();
		MOB victim=GoodGuardian.anyPeaceToMake(mob.location(),mob);
		GoodGuardian.keepPeace(mob,victim);
		victim=null;
		int dirCode=-1;
		tickStatus=Tickable.STATUS_MISC+4;
		for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
		{
			tickStatus=Tickable.STATUS_MISC+5+d;
			final Room room=thisRoom.getRoomInDir(d);
			final Exit exit=thisRoom.getExitInDir(d);
			if((room!=null)&&(exit!=null)&&(okRoomForMe(mob,thisRoom,room)))
			{
				tickStatus=Tickable.STATUS_MISC+20+d;
				if(exit.isOpen())
				{
					tickStatus=Tickable.STATUS_MISC+40+d;
					victim=GoodGuardian.anyPeaceToMake(room,mob);
					if(victim!=null)
					{
						dirCode=d;
						break;
					}
					tickStatus=Tickable.STATUS_MISC+60+d;
				}
				tickStatus=Tickable.STATUS_MISC+80+d;
			}
			if(dirCode>=0) break;
			tickStatus=Tickable.STATUS_MISC+100+d;
		}
		tickStatus=Tickable.STATUS_MISC+120;
		if((dirCode>=0)
		&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.MOBILITY)))
		{
			tickStatus=Tickable.STATUS_MISC+121;
			CMLib.tracking().walk(mob,dirCode,false,false);
			tickStatus=Tickable.STATUS_MISC+122;
			GoodGuardian.keepPeace(mob,victim);
			tickStatus=Tickable.STATUS_MISC+123;
		}
		tickStatus=Tickable.STATUS_NOT;
		return true;
	}
}
