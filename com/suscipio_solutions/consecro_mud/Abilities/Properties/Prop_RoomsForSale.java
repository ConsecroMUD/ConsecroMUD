package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.ArrayList;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.LandTitle;


@SuppressWarnings("rawtypes")
public class Prop_RoomsForSale extends Prop_RoomForSale
{
	@Override public String ID() { return "Prop_RoomsForSale"; }
	@Override public String name(){ return "Putting a cluster of rooms up for sale";}
	protected String uniqueLotID=null;

	protected void fillCluster(Room R, List<Room> V)
	{
		V.add(R);
		for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
		{
			final Room R2=R.getRoomInDir(d);
			if((R2!=null)&&(R2.roomID().length()>0)&&(!V.contains(R2)))
			{
				final Ability A=R2.fetchEffect(ID());
				if((R2.getArea()==R.getArea())&&(A!=null))
					fillCluster(R2,V);
				else
				{
					V.remove(R); // purpose here is to put the "front" door up front.
					V.add(0,R);
				}
			}
		}
	}

	@Override
	public List<Room> getAllTitledRooms()
	{
		final List<Room> V=new ArrayList<Room>();
		Room R=null;
		if(affected instanceof Room)
			R=(Room)affected;
		else
			R=CMLib.map().getRoom(landPropertyID());
		if(R!=null) fillCluster(R,V);
		return V;
	}

	@Override
	public String getTitleID()
	{
		if(affected instanceof Room)
			return "LAND_TITLE_FOR#"+CMLib.map().getExtendedRoomID((Room)affected);
		else
		{
			final Room R=CMLib.map().getRoom(landPropertyID());
			if(R!=null)
				return "LAND_TITLE_FOR#"+CMLib.map().getExtendedRoomID(R);
		}
		return "";
	}

	// update title, since it may affect room clusters, worries about EVERYONE
	@Override
	public void updateTitle()
	{
		final List<Room> V=getAllTitledRooms();
		final String owner=getOwnerName();
		final int price=getPrice();
		final boolean rental=rentalProperty();
		final int back=backTaxes();
		String uniqueID="ROOMS_PROPERTY_"+this;
		if(V.size()>0)
			uniqueID="ROOMS_PROPERTY_"+CMLib.map().getExtendedRoomID(V.get(0));
		for(int v=0;v<V.size();v++)
		{
			Room R=V.get(v);
			synchronized(("SYNC"+R.roomID()).intern())
			{
				R=CMLib.map().getRoom(R);
				final LandTitle A=(LandTitle)R.fetchEffect(ID());
				if((A!=null)
				&&((!A.getOwnerName().equals(owner))
				   ||(A.getPrice()!=price)
				   ||(A.backTaxes()!=back)
				   ||(A.rentalProperty()!=rental)))
				{
					A.setOwnerName(owner);
					A.setPrice(price);
					A.setBackTaxes(back);
					A.setRentalProperty(rental);
					CMLib.database().DBUpdateRoom(R);
				}
				if(A instanceof Prop_RoomsForSale)
					((Prop_RoomsForSale)A).uniqueLotID=uniqueID;
			}
		}
	}

	@Override
	public String getUniqueLotID()
	{
		if(uniqueLotID==null) updateTitle();
		return uniqueLotID;
	}

	// update lot, since its called by the savethread, ONLY worries about itself
	@Override
	public void updateLot(List optPlayerList)
	{
		if(affected instanceof Room)
		{
			lastItemNums=updateLotWithThisData((Room)affected,this,false,scheduleReset,optPlayerList,lastItemNums);
			if((lastDayDone!=((Room)affected).getArea().getTimeObj().getDayOfMonth())
			&&(CMProps.getBoolVar(CMProps.Bool.MUDSTARTED)))
			{
				final Room R=(Room)affected;
				lastDayDone=R.getArea().getTimeObj().getDayOfMonth();
				final List<Room> V=getAllTitledRooms();
				for(int v=0;v<V.size();v++)
				{
					final Room R2=V.get(v);
					final Prop_RoomForSale PRFS=(Prop_RoomForSale)R2.fetchEffect(ID());
					if(PRFS!=null)
						PRFS.lastDayDone=R.getArea().getTimeObj().getDayOfMonth();
				}
				if((getOwnerName().length()>0)&&rentalProperty()&&(R.roomID().length()>0))
					if(doRentalProperty(R.getArea(),R.roomID(),getOwnerName(),getPrice()))
					{
						setOwnerName("");
						updateTitle();
						lastItemNums=updateLotWithThisData((Room)affected,this,false,scheduleReset,optPlayerList,lastItemNums);
					}
			}
			scheduleReset=false;
		}
	}
}
