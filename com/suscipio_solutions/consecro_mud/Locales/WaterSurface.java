package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.GridLocale;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class WaterSurface extends StdRoom implements Drink
{
	@Override public String ID(){return "WaterSurface";}
	public WaterSurface()
	{
		super();
		name="the water";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_WATERSURFACE;}
	@Override public long decayTime(){return 0;}
	@Override public void setDecayTime(long time){}

	protected String UnderWaterLocaleID(){return "UnderWaterGrid";}
	protected int UnderWaterDomainType(){return Room.DOMAIN_OUTDOORS_UNDERWATER;}
	protected boolean IsUnderWaterFatClass(Room thatSea){return (thatSea instanceof UnderWaterGrid)||(thatSea instanceof UnderWaterThinGrid);}

	@Override
	public void giveASky(int depth)
	{
		if(skyedYet) return;
		if(depth>1000) return;
		super.giveASky(depth+1);
		skyedYet=true;

		if((roomID().length()==0)
		&&(getGridParent()!=null)
		&&(getGridParent().roomID().length()==0))
			return;

		if((rawDoors()[Directions.DOWN]==null)
		&&(domainType()!=UnderWaterDomainType())
		&&(domainType()!=Room.DOMAIN_OUTDOORS_AIR)
		&&(CMProps.getIntVar(CMProps.Int.SKYSIZE)!=0))
		{
			Exit dnE=null;
			final Exit upE=CMClass.getExit("StdOpenDoorway");
			if(CMProps.getIntVar(CMProps.Int.SKYSIZE)>0)
				dnE=upE;
			else
				dnE=CMClass.getExit("UnseenWalkway");
			final GridLocale sea=(GridLocale)CMClass.getLocale(UnderWaterLocaleID());
			sea.setRoomID("");
			sea.setArea(getArea());
			rawDoors()[Directions.DOWN]=sea;
			setRawExit(Directions.DOWN,dnE);
			sea.rawDoors()[Directions.UP]=this;
			sea.setRawExit(Directions.UP,upE);
			for(int d=0;d<4;d++)
			{
				final Room thatRoom=rawDoors()[d];
				Room thatSea=null;
				if((thatRoom!=null)&&(getRawExit(d)!=null))
				{
					thatRoom.giveASky(depth+1);
					thatSea=thatRoom.rawDoors()[Directions.DOWN];
				}
				if((thatSea!=null)
				&&(thatSea.roomID().length()==0)
				&&(IsUnderWaterFatClass(thatSea)))
				{
					sea.rawDoors()[d]=thatSea;
					sea.setRawExit(d,getRawExit(d));
					thatSea.rawDoors()[Directions.getOpDirectionCode(d)]=sea;
					if(thatRoom!=null)
					{
						Exit xo=thatRoom.getRawExit(Directions.getOpDirectionCode(d));
						if((xo==null)||(xo.hasADoor())) xo=upE;
						thatSea.setRawExit(Directions.getOpDirectionCode(d),xo);
					}
					((GridLocale)thatSea).clearGrid(null);
				}
			}
			sea.clearGrid(null);
		}
	}

	@Override
	public void clearSky()
	{
		if(!skyedYet) return;
		super.clearSky();
		final Room room=rawDoors()[Directions.DOWN];
		if(room==null) return;
		if((room.roomID().length()==0)
		&&(IsUnderWaterFatClass(room)))
		{
			((GridLocale)room).clearGrid(null);
			rawDoors()[Directions.DOWN]=null;
			setRawExit(Directions.DOWN,null);
			CMLib.map().emptyRoom(room,null);
			room.destroy();
			skyedYet=false;
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		switch(WaterSurface.isOkWaterSurfaceAffect(this,msg))
		{
		case -1: return false;
		case 1: return true;
		}
		return super.okMessage(myHost,msg);
	}

	public static int isOkWaterSurfaceAffect(Room room, CMMsg msg)
	{
		if(CMLib.flags().isSleeping(room))
			return 0;

		if(((msg.targetMinor()==CMMsg.TYP_LEAVE)
			||(msg.targetMinor()==CMMsg.TYP_ENTER)
			||(msg.targetMinor()==CMMsg.TYP_FLEE))
		&&(msg.amITarget(room))
		&&(msg.sourceMinor()!=CMMsg.TYP_RECALL)
		&&((msg.targetMinor()==CMMsg.TYP_ENTER)
		   ||(!(msg.tool() instanceof Ability))
		   ||(!CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_TRANSPORTING)))
		&&(!CMLib.flags().isFalling(msg.source()))
		&&(!CMLib.flags().isInFlight(msg.source()))
		&&(!CMLib.flags().isWaterWorthy(msg.source())))
		{
			final MOB mob=msg.source();
			boolean hasBoat=false;
			for(int i=0;i<mob.numItems();i++)
			{
				final Item I=mob.getItem(i);
				if((I!=null)&&(I instanceof Rideable)&&(((Rideable)I).rideBasis()==Rideable.RIDEABLE_WATER))
				{	hasBoat=true; break;}
			}
			if((!CMLib.flags().isWaterWorthy(mob))
			&&(!hasBoat)
			&&(!CMLib.flags().isInFlight(mob)))
			{
				mob.tell(CMLib.lang().L("You need to swim or ride a boat that way."));
				return -1;
			}
			else
			if(CMLib.flags().isSwimming(mob))
				if(mob.phyStats().weight()>Math.round(CMath.mul(mob.maxCarry(),0.50)))
				{
					mob.tell(CMLib.lang().L("You are too encumbered to swim."));
					return -1;
				}
		}
		else
		if(((msg.sourceMinor()==CMMsg.TYP_SIT)||(msg.sourceMinor()==CMMsg.TYP_SLEEP))
		&&(!(msg.target() instanceof Exit))
		&&((msg.source().riding()==null)||(!CMLib.flags().isSwimming(msg.source().riding()))))
		{
			msg.source().tell(CMLib.lang().L("You cannot rest here."));
			return -1;
		}
		else
		if(msg.amITarget(room)
		&&(msg.targetMinor()==CMMsg.TYP_DRINK)
		&&(room instanceof Drink))
		{
			if(((Drink)room).liquidType()==RawMaterial.RESOURCE_SALTWATER)
			{
				msg.source().tell(CMLib.lang().L("You don't want to be drinking saltwater."));
				return -1;
			}
			return 1;
		}
		return 0;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		UnderWater.sinkAffects(this,msg);
	}
	@Override public int thirstQuenched(){return 1000;}
	@Override public int liquidHeld(){return Integer.MAX_VALUE-1000;}
	@Override public int liquidRemaining(){return Integer.MAX_VALUE-1000;}
	@Override public int liquidType(){return RawMaterial.RESOURCE_FRESHWATER;}
	@Override public void setLiquidType(int newLiquidType){}
	@Override public void setThirstQuenched(int amount){}
	@Override public void setLiquidHeld(int amount){}
	@Override public void setLiquidRemaining(int amount){}
	@Override public boolean disappearsAfterDrinking(){return false;}
	@Override public boolean containsDrink(){return true;}
	@Override public int amountTakenToFillMe(Drink theSource){return 0;}
	@Override public List<Integer> resourceChoices(){return UnderWater.roomResources;}
}
