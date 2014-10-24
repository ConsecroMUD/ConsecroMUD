package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.LandTitle;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_AreaForSale extends Property implements LandTitle
{
	@Override public String ID() { return "Prop_AreaForSale"; }
	@Override public String name(){ return "Putting an area up for sale";}
	@Override protected int canAffectCode(){return Ability.CAN_AREAS;}
	protected Hashtable lastItemNums=new Hashtable();
	@Override
	public String accountForYourself()
	{ return "For Sale";	}
	protected long lastCall=0;
	protected long lastMobSave=0;
	protected int lastDayDone=-1;

	@Override public boolean allowsExpansionConstruction(){ return false;}

	@Override
	public int getPrice()
	{
		if(text().length()==0)
			return 100000;
		final String s=text();
		int index=s.length();
		while((--index)>=0)
		{
			if((!Character.isDigit(s.charAt(index)))
			&&(!Character.isWhitespace(s.charAt(index))))
				break;
		}
		int price=CMath.s_int(s.substring(index+1).trim());

		if(price<=0) price=100000;
		return price;
	}

	@Override
	public String getUniqueLotID()
	{
		return "AREA_PROPERTY_"+landPropertyID();
	}

	@Override
	public void setPrice(int price)
	{
		setMiscText(getOwnerName()+"/"
			+(rentalProperty()?"RENTAL ":"")
			+((backTaxes()!=0)?"TAX"+backTaxes()+"X ":"")
			+price);
	}

	@Override
	public String getOwnerName()
	{
		if(text().indexOf('/')<0) return "";
		return text().substring(0,text().indexOf('/'));
	}

	@Override
	public String getTitleID()
	{
		if(affected != null)
			return "LAND_TITLE_FOR#"+affected.Name();
		return "";
	}

	@Override
	public CMObject getOwnerObject()
	{
		final String owner=getOwnerName();
		if(owner.length()==0) return null;
		final Clan C=CMLib.clans().getClan(owner);
		if(C!=null) return C;
		return CMLib.players().getLoadPlayer(owner);
	}

	@Override
	public void setOwnerName(String owner)
	{
		setMiscText(owner+"/"
				+(rentalProperty()?"RENTAL ":"")
				+((backTaxes()!=0)?"TAX"+backTaxes()+"X ":"")
				+getPrice());
	}

	@Override
	public int backTaxes()
	{
		if(text().indexOf('/')<0) return 0;
		final int x=text().indexOf("TAX",text().indexOf('/'));
		if(x<0) return 0;
		final String s=CMParms.parse(text().substring(x+3)).firstElement();
		return CMath.s_int(s.substring(0,s.length()-1));
	}
	@Override
	public void setBackTaxes(int tax)
	{
		setMiscText(getOwnerName()+"/"
				+(rentalProperty()?"RENTAL ":"")
				+((tax!=0)?"TAX"+tax+"X ":"")
				+getPrice());
	}

	@Override
	public boolean rentalProperty()
	{
		if(text().indexOf('/')<0) return text().indexOf("RENTAL")>=0;
		return text().indexOf("RENTAL",text().indexOf('/'))>0;
	}
	@Override
	public void setRentalProperty(boolean truefalse)
	{
		setMiscText(getOwnerName()+"/"
				+(truefalse?"RENTAL ":"")
				+((backTaxes()!=0)?"TAX"+backTaxes()+"X ":"")
				+getPrice());
	}

	// update title, since it may affect clusters, worries about ALL involved
	@Override
	public void updateTitle()
	{
		if(affected instanceof Area)
			CMLib.database().DBUpdateArea(((Area)affected).name(),(Area)affected);
		else
		if(affected instanceof Room)
			Log.errOut("Prop_AreaForSale","Prop_AreaForSale goes on an Area, NOT "+CMLib.map().getExtendedRoomID((Room)affected));
		else
		{
			final Area A=CMLib.map().getArea(landPropertyID());
			if(A!=null)
				CMLib.database().DBUpdateArea(A.Name(),A);
		}
	}

	@Override
	public String landPropertyID()
	{
		if((affected!=null)&&(affected instanceof Area))
			((Area)affected).Name();
		else
		if(affected instanceof Room)
			return CMLib.map().getExtendedRoomID((Room)affected);
		return "";
	}

	@Override public void setLandPropertyID(String landID){}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg)) return false;
		Prop_RoomForSale.robberyCheck(this,msg);
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(((msg.sourceMinor()==CMMsg.TYP_SHUTDOWN)
				||((msg.targetMinor()==CMMsg.TYP_EXPIRE)&&(msg.target() instanceof Room))
				||(msg.sourceMinor()==CMMsg.TYP_ROOMRESET))
		&&(affected instanceof Area)
		&&((System.currentTimeMillis()-lastMobSave)>360000))
		{
			lastMobSave=System.currentTimeMillis();
			final List<Room> V=getAllTitledRooms();
			for(int v=0;v<V.size();v++)
			{
				Room R=V.get(v);
				synchronized(("SYNC"+R.roomID()).intern())
				{
					R=CMLib.map().getRoom(R);
					lastMobSave=System.currentTimeMillis();
					final Vector mobs=new Vector();
					for(int m=0;m<R.numInhabitants();m++)
					{
						final MOB M=R.fetchInhabitant(m);
						if((M!=null)
						&&(M.isSavable())
						&&(M.getStartRoom()==R)
						&&((M.basePhyStats().rejuv()==0)||(M.basePhyStats().rejuv()==PhyStats.NO_REJUV)))
							mobs.addElement(M);
					}
					if(!CMSecurity.isSaveFlag("NOPROPERTYMOBS"))
						CMLib.database().DBUpdateTheseMOBs(R,mobs);
				}
			}
			lastMobSave=System.currentTimeMillis();
		}
	}

	@Override
	public List<Room> getAllTitledRooms()
	{
		final List<Room> V=new Vector();
		Area A=null;
		if(affected instanceof Area)
			A=(Area)affected;
		else
		if(affected instanceof Room)
			V.add((Room)affected);
		else
			A=CMLib.map().getArea(landPropertyID());
		if(A!=null)
		{
			for(final Enumeration<Room> e=A.getProperMap();e.hasMoreElements();)
				V.add(e.nextElement());
		}
		return V;
	}
	@Override public List<Room> getConnectedPropertyRooms() { return getAllTitledRooms();}

	// update lot, since its called by the savethread, ONLY worries about itself
	@Override
	public void updateLot(List optPlayerList)
	{
		if(((System.currentTimeMillis()-lastCall)>360000)
		&&(CMProps.getBoolVar(CMProps.Bool.MUDSTARTED)))
		{
			final List<Room> V=getAllTitledRooms();
			for(int v=0;v<V.size();v++)
			{
				final Room R=V.get(v);
				lastCall=System.currentTimeMillis();
				final Integer lastItemNum=(Integer)lastItemNums.get(R);
				lastItemNums.put(R,Integer.valueOf(Prop_RoomForSale.updateLotWithThisData(R,this,false,false,optPlayerList,(lastItemNum==null)?-1:lastItemNum.intValue())));
			}
			lastCall=System.currentTimeMillis();
			Area A=null;
			if(affected instanceof Area)
				A=(Area)affected;
			else
				A=CMLib.map().getArea(landPropertyID());
			if((A!=null)&&(lastDayDone!=A.getTimeObj().getDayOfMonth()))
			{
				lastDayDone=A.getTimeObj().getDayOfMonth();
				if((getOwnerName().length()>0)&&rentalProperty())
					if(Prop_RoomForSale.doRentalProperty(A,A.Name(),getOwnerName(),getPrice()))
					{
						setOwnerName("");
						CMLib.database().DBUpdateArea(A.Name(),A);
					}
			}
		}
	}
}
