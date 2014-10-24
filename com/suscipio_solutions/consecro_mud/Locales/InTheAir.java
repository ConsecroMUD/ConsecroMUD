package com.suscipio_solutions.consecro_mud.Locales;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Climate;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class InTheAir extends StdRoom
{
	@Override public String ID(){return "InTheAir";}
	public InTheAir()
	{
		super();
		basePhyStats.setWeight(1);
		name="the sky";
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_AIR;}


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg)) return false;
		return isOkAirAffect(this,msg);
	}

	public static void airAffects(Room room, CMMsg msg)
	{
		if(CMLib.flags().isSleeping(room)) return;
		boolean foundReversed=false;
		boolean foundNormal=false;
		final Vector<Physical> needToFall=new Vector<Physical>();
		final Vector<Physical> mightNeedAdjusting=new Vector<Physical>();
		for(int i=0;i<room.numInhabitants();i++)
		{
			final MOB mob=room.fetchInhabitant(i);
			if((mob!=null)
			&&((mob.getStartRoom()==null)||(mob.getStartRoom()!=room)))
			{
				final Ability A=mob.fetchEffect("Falling");
				if(A!=null)
				{
					if(A.proficiency()>=100)
					{
						foundReversed=true;
						mightNeedAdjusting.addElement(mob);
					}
					foundNormal=foundNormal||(A.proficiency()<=0);
				}
				else
					needToFall.addElement(mob);
			}
		}
		for(int i=0;i<room.numItems();i++)
		{
			final Item item=room.getItem(i);
			if(item!=null)
			{
				final Ability A=item.fetchEffect("Falling");
				if(A!=null)
				{
					if(A.proficiency()>=100)
					{
						foundReversed=true;
						mightNeedAdjusting.addElement(item);
					}
					foundNormal=foundNormal||(A.proficiency()<=0);
				}
				else
				if(item.container()==null)
					needToFall.addElement(item);
			}
		}
		final int avg=((foundReversed)&&(!foundNormal))?100:0;
		for(final Physical P : mightNeedAdjusting)
		{
			final Ability A=P.fetchEffect("Falling");
			if(A!=null) A.setProficiency(avg);
		}
		final TrackingLibrary tracker = CMLib.tracking();
		for(final Physical P : needToFall)
			tracker.makeFall(P,room,avg);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		InTheAir.airAffects(this,msg);
	}

	public static boolean isOkAirAffect(Room room, CMMsg msg)
	{
		if(CMLib.flags().isSleeping(room))
			return true;
		if((msg.sourceMinor()==CMMsg.TYP_SIT)
		&&(!(msg.target() instanceof Exit))
		&&(!msg.sourceMajor(CMMsg.MASK_ALWAYS)))
		{
			msg.source().tell(CMLib.lang().L("You can't sit here."));
			return false;
		}
		if((msg.sourceMinor()==CMMsg.TYP_SLEEP)&&(!msg.sourceMajor(CMMsg.MASK_ALWAYS)))
		{
			msg.source().tell(CMLib.lang().L("You can't sleep here."));
			return false;
		}

		if((msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(msg.amITarget(room)))
		{
			final MOB mob=msg.source();
			if((!CMLib.flags().isInFlight(mob))&&(!CMLib.flags().isFalling(mob)))
			{
				mob.tell(CMLib.lang().L("You can't fly."));
				return false;
			}
			if(CMLib.dice().rollPercentage()>50)
			switch(room.getArea().getClimateObj().weatherType(room))
			{
			case Climate.WEATHER_BLIZZARD:
				room.show(mob,null,CMMsg.MSG_OK_VISUAL,CMLib.lang().L("The swirling blizzard inhibits <S-YOUPOSS> progress."));
				return false;
			case Climate.WEATHER_HAIL:
				room.show(mob,null,CMMsg.MSG_OK_VISUAL,CMLib.lang().L("The hail storm inhibits <S-YOUPOSS> progress."));
				return false;
			case Climate.WEATHER_RAIN:
				room.show(mob,null,CMMsg.MSG_OK_VISUAL,CMLib.lang().L("The rain storm inhibits <S-YOUPOSS> progress."));
				return false;
			case Climate.WEATHER_SLEET:
				room.show(mob,null,CMMsg.MSG_OK_VISUAL,CMLib.lang().L("The biting sleet inhibits <S-YOUPOSS> progress."));
				return false;
			case Climate.WEATHER_THUNDERSTORM:
				room.show(mob,null,CMMsg.MSG_OK_VISUAL,CMLib.lang().L("The thunderstorm inhibits <S-YOUPOSS> progress."));
				return false;
			case Climate.WEATHER_WINDY:
				room.show(mob,null,CMMsg.MSG_OK_VISUAL,CMLib.lang().L("The hard winds inhibit <S-YOUPOSS> progress."));
				return false;
			}
		}
		InTheAir.airAffects(room,msg);
		return true;
	}
}
