package com.suscipio_solutions.consecro_mud.Items.BasicTech;
import java.util.Enumeration;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Manufacturer;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session.InputCallback;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdPortal;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Electronics;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ShipComponent;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ShipComponent.ShipEngine.ThrustPort;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpaceShip;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Technical;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.GenericBuilder;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.LocationRoom;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Auctioneer;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Banker;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.PostOffice;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.exceptions.CMException;
import com.suscipio_solutions.consecro_mud.core.interfaces.BoundedObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor.Expire;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor.Move;
import com.suscipio_solutions.consecro_mud.core.interfaces.PrivateProperty;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;

public class GenSpaceShip extends StdPortal implements Electronics, SpaceShip, PrivateProperty
{
	@Override public String ID(){	return "GenSpaceShip";}
	protected String 		readableText	= "";
	protected String 		owner 			= "";
	protected int 			price 			= 1000;
	protected Area 			area			= null;
	protected Manufacturer	cachedManufact  = null;
	protected String	 	manufacturer	= "RANDOM";
	public long[]   		coordinates 	= new long[3];
	public double[] 		direction   	= new double[2];
	public double			roll			= 0.0;
	public long 			speed			= 0;
	protected SpaceObject	spaceTarget 	= null;
	protected double[]		facing			= new double[2];
	protected Boolean		inAirFlag		= Boolean.FALSE;

	public GenSpaceShip()
	{
		super();
		setName("a space ship");
		setDisplayText("a space ship is here.");
		setMaterial(RawMaterial.RESOURCE_STEEL);
		setDescription("");
		basePhyStats().setWeight(10000);
		recoverPhyStats();
		//CMLib.flags().setGettable(this, false);
		CMLib.flags().setSavable(this, false);
	}

	@Override public boolean isGeneric(){return true;}

	@Override public TechType getTechType() { return TechType.SHIP_SPACESHIP; }

	@Override
	public Area getShipArea()
	{
		if(destroyed)
			return null;
		else
		if(area==null)
		{
			area=CMClass.getAreaType("StdSpaceShip");
			final String num=Double.toString(Math.random());
			area.setName(L("UNNAMED_@x1",num.substring(num.indexOf('.')+1)));
			area.setSavable(false);
			area.setTheme(Area.THEME_TECHNOLOGY);
			final Room R=CMClass.getLocale("MetalRoom");
			R.setRoomID(area.Name()+"#0");
			R.setSavable(false);
			area.addProperRoom(R);
			((SpaceShip)area).setKnownSource(this);
			readableText=R.roomID();
		}
		return area;
	}

	@Override
	public Boolean getSetAirFlag(final Boolean setInAirFlag)
	{
		if((setInAirFlag != null) && (setInAirFlag != this.inAirFlag))
			this.inAirFlag = setInAirFlag;
		return this.inAirFlag;
	}
	
	@Override
	public void setShipArea(String xml)
	{
		try
		{
			area=CMLib.coffeeMaker().unpackAreaObjectFromXML(xml);
			if(area instanceof SpaceShip)
			{
				area.setSavable(false);
				((SpaceShip)area).setKnownSource(this);
				for(final Enumeration<Room> r=area.getCompleteMap();r.hasMoreElements();)
					CMLib.flags().setSavable(r.nextElement(), false);
			}
			else
			{
				Log.warnOut("Failed to unpack a space ship area for the space ship");
				getShipArea();
			}
		}
		catch (final CMException e)
		{
			Log.warnOut("Unable to parse space ship xml for some reason.");
		}
	}

	@Override 
	public String keyName() 
	{ 
		return readableText;
	}
	
	@Override 
	public void setKeyName(String newKeyName) 
	{ 
		readableText=newKeyName;
	}

	@Override 
	public String readableText()
	{
		return readableText;
	}
	
	@Override 
	public void setReadableText(String text)
	{
		readableText=text;
	}

	@Override
	public String text()
	{
		return CMLib.coffeeMaker().getPropertiesStr(this,false);
	}

	@Override
	public void setMiscText(String newText)
	{
		miscText="";
		CMLib.coffeeMaker().setPropertiesStr(this,newText,false);
		recoverPhyStats();
	}

	@Override
	public CMObject copyOf()
	{
		final GenSpaceShip s=(GenSpaceShip)super.copyOf();
		s.destroyed=false;
		final String xml=CMLib.coffeeMaker().getAreaObjectXML(getShipArea(), null, null, null, true).toString();
		s.setShipArea(xml);
		/*
		if(s.getShipArea().Name().startsWith("UNNAMED_"))
		{
			String num=Double.toString(Math.random());
			String oldName=s.Name();
			String oldDisplay=s.displayText();
			s.renameSpaceShip("UNNAMED_"+num.substring(num.indexOf('.')+1));
			s.setName(oldName);
			s.setDisplayText(oldDisplay);
		}
		*/
		return s;
	}

	@Override
	public void stopTicking()
	{
		if(area!=null)
		{
			CMLib.threads().deleteAllTicks(area);
			String key=area.Name();
			final String registryNum=area.getBlurbFlag("REGISTRY");
			if(registryNum!=null)
				key+=registryNum;
			CMLib.tech().unregisterAllElectronics(key);
		}
		super.stopTicking();
		this.destroyed=false; // undo the weird thing
	}


	@Override
	protected Room getDestinationRoom()
	{
		getShipArea();
		Room R=null;
		final List<String> V=CMParms.parseSemicolons(readableText(),true);
		if(V.size()>0)
			R=getShipArea().getRoom(V.get(CMLib.dice().roll(1,V.size(),-1)));
		return R;
	}

	@Override
	public void destroy()
	{
		if(area!=null)
			CMLib.map().obliterateArea(area);
		super.destroy();
	}

	@Override public long powerCapacity(){return 0;}
	@Override public void setPowerCapacity(long capacity){}
	@Override public long powerRemaining(){return 0;}
	@Override public int powerNeeds(){return 0;}
	@Override public void setPowerRemaining(long remaining){}
	@Override public void activate(boolean truefalse){}
	
	@Override 
	public boolean activated()
	{
		return true;
	}
	
	@Override 
	public int techLevel() 
	{ 
		return phyStats().ability();
	}
	
	@Override 
	public void setTechLevel(int lvl) 
	{ 
		basePhyStats.setAbility(lvl); recoverPhyStats(); 
	}
	
	@Override 
	public String getManufacturerName() 
	{ 
		return manufacturer; 
	}
	
	@Override 
	public void setManufacturerName(String name) 
	{ 
		cachedManufact=null; 
		if(name!=null) 
			manufacturer=name; 
	}

	@Override public long getMass()
	{
		return basePhyStats().weight()+((area instanceof SpaceShip)?((SpaceShip)area).getMass(): 1000);
	}

	@Override
	public Manufacturer getFinalManufacturer()
	{
		if(cachedManufact==null)
		{
			cachedManufact=CMLib.tech().getManufacturerOf(this,getManufacturerName().toUpperCase().trim());
			if(cachedManufact==null)
				cachedManufact=CMLib.tech().getDefaultManufacturer();
		}
		return cachedManufact;
	}

	@Override 
	public long[] coordinates()
	{
		return coordinates;
	}
	
	@Override 
	public double[] direction()
	{
		return direction;
	}
	
	@Override 
	public double roll() 
	{ 
		return roll; 
	}
	
	@Override 
	public void setRoll(double dir) 
	{ 
		roll =dir; 
	}
	
	@Override 
	public double[] facing() 
	{ 
		return facing; 
	}
	
	@Override 
	public void setFacing(double[] dir) 
	{ 
		if(dir!=null) 
			this.facing=dir; 
	}
	
	@Override 
	public SpaceObject knownTarget()
	{
		return spaceTarget;
	}
	
	@Override 
	public void setKnownTarget(SpaceObject O)
	{
		spaceTarget=O;
	}
	
	@Override 
	public void setCoords(long[] coords)
	{
		if((coords!=null)&&(coords.length==3))
			CMLib.map().moveSpaceObject(this,coords);
	}
	
	@Override 
	public void setDirection(double[] dir)
	{
		if(dir!=null) 
			direction=dir;
	}
	
	@Override 
	public long speed()
	{
		return speed;
	}
	
	@Override 
	public void setSpeed(long v)
	{
		speed=v;
	}

	@Override
	public SpaceObject knownSource()
	{
		return (area instanceof SpaceObject)?((SpaceObject)area).knownSource():null;
	}

	@Override
	public void setKnownSource(SpaceObject O)
	{
		if (area instanceof SpaceObject)
			((SpaceObject)area).setKnownSource(O);
	}

	@Override
	public long radius()
	{
		return (area instanceof SpaceObject)?((SpaceObject)area).radius():50;
	}

	@Override
	public void setRadius(long radius)
	{
		if (area instanceof SpaceObject)
			((SpaceObject)area).setRadius(radius);
	}

	@Override
	public double getOMLCoeff()
	{
		return (area instanceof SpaceShip)?((SpaceShip)area).getOMLCoeff()
				:SpaceObject.ATMOSPHERIC_DRAG_STREAMLINE + ((SpaceObject.ATMOSPHERIC_DRAG_BRICK-SpaceObject.ATMOSPHERIC_DRAG_STREAMLINE)/2.0);
	}

	@Override
	public void setOMLCoeff(double coeff)
	{
		if (area instanceof SpaceShip)
			((SpaceShip)area).setOMLCoeff(coeff);
	}

	@Override
	public void dockHere(LocationRoom R)
	{
		if(!R.isContent(this))
		{
			if(owner()==null)
				R.addItem(this,Expire.Never);
			else
				R.moveItemTo(me, Expire.Never, Move.Followers);
		}
		setCoords(R.coordinates());
		CMLib.map().delObjectInSpace(getShipSpaceObject());
		if (area instanceof SpaceShip)
			((SpaceShip)area).dockHere(R);
		setSpeed(0);
	}

	@Override
	public void unDock(boolean toSpace)
	{
		final LocationRoom R=getIsDocked();
		if(R!=null)
		{
			R.delItem(this);
			setOwner(null);
		}
		if (area instanceof SpaceShip)
			((SpaceShip)area).unDock(toSpace);
		if(R!=null)
		{
			setDirection(R.getDirectionFromCore());
			setFacing(R.getDirectionFromCore());
		}
		if(toSpace)
		{
			final SpaceObject o = getShipSpaceObject();
			if((o != null)&&(R!=null))
				CMLib.map().addObjectToSpace(o,R.coordinates());
		}
	}

	@Override
	public SpaceObject getShipSpaceObject()
	{
		return this;
	}

	@Override
	public LocationRoom getIsDocked()
	{
		if (area instanceof SpaceShip)
			return ((SpaceShip)area).getIsDocked();
		if(owner() instanceof LocationRoom)
			return ((LocationRoom)owner());
		return null;
	}

	@Override 
	public int getPrice() 
	{ 
		return price; 
	}
	
	@Override 
	public void setPrice(int price) 
	{ 
		this.price=price; 
	}
	
	@Override 
	public String getOwnerName() 
	{ 
		return owner; 
	}
	
	@Override 
	public void setOwnerName(String owner) 
	{ 
		this.owner=owner;
	}
	
	@Override
	public CMObject getOwnerObject()
	{
		final String owner=getOwnerName();
		if(owner.length()==0) 
			return null;
		final Clan C=CMLib.clans().getClan(owner);
		if(C!=null) 
			return C;
		return CMLib.players().getLoadPlayer(owner);
	}
	
	@Override public String getTitleID() 
	{ 
		return this.toString(); 
	}

	@Override
	public void renameSpaceShip(String newName)
	{
		final Area area=this.area;
		if(area instanceof SpaceShip)
		{
			final Room oldEntry=getDestinationRoom();
			final String oldName=area.Name();
			String registryNum=area.getBlurbFlag("REGISTRY");
			if(registryNum==null) 
				registryNum="";
			((SpaceShip)area).renameSpaceShip(newName);
			CMLib.tech().unregisterElectronics(null, oldName+registryNum);
			registryNum=Double.toString(Math.random());
			area.addBlurbFlag("REGISTRY Registry#"+registryNum.substring(registryNum.indexOf('.')+1));
			setReadableText(oldEntry.roomID());
			setShipArea(CMLib.coffeeMaker().getAreaObjectXML(area, null, null, null, true).toString());
		}
		for(final String word : new String[]{"NAME","NEWNAME","SHIPNAME","SHIP"})
		{
			for(final String rubs : new String[]{"<>","[]","{}","()"})
			{
				if(Name().indexOf(rubs.charAt(0)+word+rubs.charAt(1))>=0)
					setName(CMStrings.replaceAll(Name(), rubs.charAt(0)+word+rubs.charAt(1), newName));
			}
			for(final String rubs : new String[]{"<>","[]","{}","()"})
			{
				if(displayText().indexOf(rubs.charAt(0)+word+rubs.charAt(1))>=0)
					setDisplayText(CMStrings.replaceAll(displayText(), rubs.charAt(0)+word+rubs.charAt(1), newName));
			}
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost, msg))
			return false;
		if(msg.amITarget(this))
		{
			if((msg.targetMinor()==CMMsg.TYP_OPEN)||(msg.targetMinor()==CMMsg.TYP_CLOSE)||(msg.targetMinor()==CMMsg.TYP_LOCK)||(msg.targetMinor()==CMMsg.TYP_UNLOCK))
			{
				msg.setOthersMessage(CMStrings.replaceAll(msg.othersMessage(), "<T-NAME>", L("a hatch on <T-NAME>")));
				msg.setOthersMessage(CMStrings.replaceAll(msg.othersMessage(), "<T-NAMESELF>", L("a hatch on <T-NAMESELF>")));
				msg.setSourceMessage(CMStrings.replaceAll(msg.othersMessage(), "<T-NAME>", L("a hatch on <T-NAME>")));
				msg.setSourceMessage(CMStrings.replaceAll(msg.othersMessage(), "<T-NAMESELF>", L("a hatch on <T-NAMESELF>")));
				msg.setTargetMessage(CMStrings.replaceAll(msg.othersMessage(), "<T-NAME>", L("a hatch on <T-NAME>")));
				msg.setTargetMessage(CMStrings.replaceAll(msg.othersMessage(), "<T-NAMESELF>", L("a hatch on <T-NAMESELF>")));
			}
		}
		return true;
	}

	protected void stopThisShip(MOB mob)
	{
		setSpeed(0); // if you collide with something massive, your speed ENDS
		List<Electronics> electronics=CMLib.tech().getMakeRegisteredElectronics(getShipArea().Name());
		for(final Electronics E : electronics)
		{
			if(E instanceof ShipComponent.ShipEngine)
			{
				final String code=Technical.TechCommand.THRUST.makeCommand(ShipComponent.ShipEngine.ThrustPort.AFT,Integer.valueOf(0));
				final CMMsg msg2=CMClass.getMsg(mob, E, this, CMMsg.NO_EFFECT, null, CMMsg.MSG_ACTIVATE|CMMsg.MASK_CNTRLMSG, code, CMMsg.NO_EFFECT,null);
				if(E.owner() instanceof Room)
				{
					if(((Room)E.owner()).okMessage(mob, msg2))
						((Room)E.owner()).send(mob, msg2);
				}
				else
				if(E.okMessage(mob, msg2))
					E.executeMsg(mob, msg2);
			}
		}
	}

	protected void sendComputerMessage(final MOB mob, final CMMsg msg)
	{
		List<Electronics> electronics = CMLib.tech().getMakeRegisteredElectronics(getShipArea().Name());
		for(final Electronics E : electronics)
		{
			if(E instanceof Electronics.Computer)
			{
				if(E.owner() instanceof Room)
				{
					//TODO: teach computers to understand what a collision means!!!!
					if(((Room)E.owner()).okMessage(mob, msg))
						((Room)E.owner()).send(mob, msg);
				}
				else
				if(E.okMessage(mob, msg))
					E.executeMsg(mob, msg);
			}
		}
	}
	
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);

		if(msg.amITarget(this))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_GET:
				if(msg.tool() instanceof ShopKeeper)
					transferOwnership(msg.source());
				break;
			case CMMsg.TYP_ACTIVATE:
				if((CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG))&&(msg.targetMessage()!=null))
				{
					final String[] parts=msg.targetMessage().split(" ");
					final TechCommand command=TechCommand.findCommand(parts);
					if(command!=null)
					{
						final Object[] parms=command.confirmAndTranslate(parts);
						if(parms!=null)
						{
							if(command==Technical.TechCommand.ACCELLLERATION)
							{
								if(getIsDocked()!=null)
									unDock(true);
								final ThrustPort dir=(ThrustPort)parms[0];
								final int amount=((Integer)parms[1]).intValue();
								//long specificImpulse=((Long)parms[2]).longValue();
								switch(dir)
								{
								case STARBOARD: 
									facing[0]-=amount; 
									break;
								case PORT: 
									facing[0]+=amount; 
									break;
								case DORSEL: 
									facing[1]-=amount; 
									break;
								case VENTRAL: 
									facing[1]+=amount; 
									break;
								case FORWARD: 
									break;
								case AFT:
								{
									// this will move it, but will also update speed and direction -- all good!
									final double inAirFactor=inAirFlag.booleanValue()?(1.0-getOMLCoeff()):1.0;
									CMLib.map().moveSpaceObject(this,facing(),Math.round((((double)amount/(double)getMass())-1.0)*inAirFactor));
									break;
								}
								}
								facing[0]=facing[0]%(2*Math.PI);
								facing[1]=facing[1]%(2*Math.PI);
							}
						}
					}
				}
				break;
			case CMMsg.TYP_COLLISION:
			{
				final MOB mob=msg.source();
				final boolean hitSomethingMassive;
				
				if((msg.tool() instanceof SpaceObject) // we hit something very very big
				&&(((SpaceObject)msg.tool()).getMass() >= (100 * SpaceObject.Distance.Kilometer.dm)))
				{
					hitSomethingMassive=true;
					stopThisShip(mob);
				}
				else
					hitSomethingMassive=false;
				
				// this only works because Areas don't move.
				// the only way to hit one is to be moving towards it.
				if((speed() <= SpaceObject.ACCELLERATION_DAMAGED) 
				&&(msg.tool() instanceof Area))
				{
					long shortestDistance=Long.MAX_VALUE;
					LocationRoom LR = null;
					for(final Enumeration<Room> r=((Area)msg.tool()).getMetroMap();r.hasMoreElements();)
					{
						final Room R2=r.nextElement();
						if((R2.domainType()==Room.DOMAIN_OUTDOORS_SPACEPORT)
						&&(R2 instanceof LocationRoom))
						{
							long distanceFrom=CMLib.map().getDistanceFrom(coordinates(), ((LocationRoom)R2).coordinates());
							if(distanceFrom<shortestDistance)
							{
								shortestDistance=distanceFrom;
								LR=(LocationRoom)R2;
							}
						}
					}
					if(LR!=null)
					{
						dockHere(LR); // set location and so forth
					}
					else
					{
						// we landed, but there was nowhere to dock!
						stopThisShip(mob);
					}
				}
				else
				if(msg.tool() instanceof SpaceObject)
				{
					if(hitSomethingMassive)
					{
						//destroyThisShip(); //TODO:
					}
					else
					{
						// first, get the direction of the ship relative to the direction of the other object -- angle between two vectors
						// if they are both going in the same direction, very little kenetic damage, speed of faster - speed of slower 
						// if op directions, extra -- speed of both, otherwise normal, otherwise speed of faster
						
						
						
						// we've been -- hit?
						
						//if a planet, just blow up!
						for(final Enumeration<Room> r = getShipArea().getProperMap(); r.hasMoreElements();)
						{
							
						}
					}
				}
				else
				{
					//so there was a collision, but not with a space object?
					Log.errOut("SpaceShip","Collided with "+msg.tool());
				}
				sendComputerMessage(mob,msg);
				break;
			}
			default:
				break;
			}
		}
		else
		if((msg.targetMinor()==CMMsg.TYP_SELL)
		&&(msg.tool()==this)
		&&(msg.target() instanceof ShopKeeper))
		{
			setOwnerName("");
			recoverPhyStats();
		}
		else
		if((msg.targetMinor()==CMMsg.TYP_GIVE)
		&&(msg.tool()==this)
		&&(msg.source()!=null)
		&&(getOwnerName().length()>0)
		&&((msg.source().Name().equals(getOwnerName()))
			||(msg.source().getLiegeID().equals(getOwnerName())&&msg.source().isMarriedToLiege())
			||(CMLib.clans().checkClanPrivilege(msg.source(), getOwnerName(), Clan.Function.PROPERTY_OWNER)))
		&&(msg.target() instanceof MOB)
		&&(!(msg.target() instanceof Banker))
		&&(!(msg.target() instanceof Auctioneer))
		&&(!(msg.target() instanceof PostOffice)))
			transferOwnership((MOB)msg.target());
	}

	protected LocationRoom findNearestDocks(Room R)
	{
		final List<LocationRoom> docks=new XVector<LocationRoom>();
		if(R!=null)
		{
			TrackingLibrary.TrackingFlags flags;
			flags = new TrackingLibrary.TrackingFlags()
					.plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
					.plus(TrackingLibrary.TrackingFlag.NOAIR)
					.plus(TrackingLibrary.TrackingFlag.NOHOMES)
					.plus(TrackingLibrary.TrackingFlag.UNLOCKEDONLY)
					.plus(TrackingLibrary.TrackingFlag.NOWATER);
			final List<Room> rooms=CMLib.tracking().getRadiantRooms(R, flags, 25);
			for(final Room R2 : rooms)
				if((R2.domainType()==Room.DOMAIN_OUTDOORS_SPACEPORT)
				&&(R2 instanceof LocationRoom)
				&&(R.getArea().inMyMetroArea(R2.getArea())))
					docks.add((LocationRoom)R2);
			if(docks.size()==0)
				for(final Enumeration<Room> r=R.getArea().getMetroMap();r.hasMoreElements();)
				{
					final Room R2=r.nextElement();
					if((R2.domainType()==Room.DOMAIN_OUTDOORS_SPACEPORT)
					&&(R2 instanceof LocationRoom))
						docks.add((LocationRoom)R2);
				}
			if(docks.size()==0)
				for(final Room R2 : rooms)
					if((R2.domainType()==Room.DOMAIN_OUTDOORS_SPACEPORT)
					&&(R2 instanceof LocationRoom))
						docks.add((LocationRoom)R2);
		}
		if(docks.size()==0)
			return null;
		return docks.get(CMLib.dice().roll(1, docks.size(), -1));
	}

	protected void transferOwnership(final MOB buyer)
	{
		if(CMLib.clans().checkClanPrivilege(buyer, getOwnerName(), Clan.Function.PROPERTY_OWNER))
		{
			final Pair<Clan,Integer> targetClan=CMLib.clans().findPrivilegedClan(buyer, Clan.Function.PROPERTY_OWNER);
			if(targetClan!=null)
				setOwnerName(targetClan.first.clanID());
			else
				setOwnerName(buyer.Name());
		}
		else
			setOwnerName(buyer.Name());
		recoverPhyStats();
		//String registryNum=Double.toString(Math.random());
		//String randNum=CMStrings.limit(registryNum.substring(registryNum.indexOf('.')+1), 4);
		//renameSpaceShip("SS "+buyer.Name()+", Reg "+randNum);
		final Session session=buyer.session();
		final Room R=CMLib.map().roomLocation(this);
		if(session!=null)
		{
			final GenSpaceShip me=this;
			final InputCallback[] namer=new InputCallback[1];
			namer[0]=new InputCallback(InputCallback.Type.PROMPT)
			{
				@Override public void showPrompt() { session.println(L("\n\rEnter a new name for your ship: ")); }
				@Override public void timedOut() { }
				@Override public void callBack()
				{
					if((this.input.trim().length()==0)
					||(!CMLib.login().isOkName(this.input.trim(),true))
					||(CMLib.tech().getMakeRegisteredKeys().contains(this.input.trim())))
					{
						session.println(L("^ZThat is not a permitted name.^N"));
						session.prompt(namer[0].reset());
						return;
					}
					me.renameSpaceShip(this.input.trim());
					buyer.tell(L("@x1 is now signed over to @x2.",name(),getOwnerName()));
					final LocationRoom finalR=findNearestDocks(R);
					if(finalR==null)
					{
						Log.errOut("Could not dock ship in area "+R.getArea().Name()+" due to lack of spaceport.");
						buyer.tell(L("Nowhere was found to dock your ship.  Please contact the administrators!."));
					}
					else
					{
						me.dockHere(finalR);
						buyer.tell(L("You'll find your ship docked at '@x1'.",finalR.displayText(buyer)));
					}
					if ((buyer.playerStats() != null) && (!buyer.playerStats().getExtItems().isContent(me)))
						buyer.playerStats().getExtItems().addItem(me);
				}
			};
			session.prompt(namer[0]);
		}
		else
		{
			buyer.tell(L("@x1 is now signed over to @x2.",name(),getOwnerName()));
			if ((buyer.playerStats() != null) && (!buyer.playerStats().getExtItems().isContent(this)))
				buyer.playerStats().getExtItems().addItem(this);
			final LocationRoom finalR=findNearestDocks(R);
			if(finalR==null)
				Log.errOut("Could not dock ship in area "+R.getArea().Name()+" due to lack of spaceport.");
			else
				dockHere(finalR);
		}
	}

	@Override
	public BoundedCube getBounds()
	{
		return new BoundedObject.BoundedCube(coordinates(),radius());
	}

	private final static String[] MYCODES={"HASLOCK","HASLID","CAPACITY","CONTAINTYPES","RESETTIME","RIDEBASIS","MOBSHELD",
											"FUELTYPE","POWERCAP","ACTIVATED","POWERREM","MANUFACTURER","AREA","COORDS","RADIUS",
											"ROLL","DIRECTION","SPEED","FACING","OWNER","PRICE","DEFCLOSED","DEFLOCKED"
										  };
	@Override
	public String getStat(String code)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			return CMLib.coffeeMaker().getGenItemStat(this,code);
		switch(getCodeNum(code))
		{
		case 0: return ""+hasALock();
		case 1: return ""+hasADoor();
		case 2: return ""+capacity();
		case 3: return ""+containTypes();
		case 4: return ""+openDelayTicks();
		case 5: return ""+rideBasis();
		case 6: return ""+riderCapacity();
		case 7: return ""+powerCapacity();
		case 8: return ""+activated();
		case 9: return ""+powerRemaining();
		case 10: return getManufacturerName();
		case 11: return (area==null)?"":CMLib.coffeeMaker().getAreaXML(area, null, null, null, true).toString();
		case 12: return CMParms.toStringList(coordinates());
		case 13: return ""+radius();
		case 14: return ""+roll();
		case 15: return CMParms.toStringList(direction());
		case 16: return ""+speed();
		case 17: return CMParms.toStringList(facing());
		case 18: return getOwnerName();
		case 19: return ""+getPrice();
		case 20: return ""+defaultsClosed();
		case 21: return ""+defaultsLocked();
		default:
			return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
		}
	}
	@Override
	public void setStat(String code, String val)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			CMLib.coffeeMaker().setGenItemStat(this,code,val);
		else
		switch(getCodeNum(code))
		{
		case 0: setDoorsNLocks(hasADoor(),isOpen(),defaultsClosed(),CMath.s_bool(val),false,CMath.s_bool(val)&&defaultsLocked()); break;
		case 1: setDoorsNLocks(CMath.s_bool(val),isOpen(),CMath.s_bool(val)&&defaultsClosed(),hasALock(),isLocked(),defaultsLocked()); break;
		case 2: setCapacity(CMath.s_parseIntExpression(val)); break;
		case 3: setContainTypes(CMath.s_parseBitLongExpression(Container.CONTAIN_DESCS,val)); break;
		case 4: setOpenDelayTicks(CMath.s_parseIntExpression(val)); break;
		case 5: break;
		case 6: break;
		case 7: setPowerCapacity(CMath.s_parseIntExpression(val)); break;
		case 8: activate(CMath.s_bool(val)); break;
		case 9: setPowerRemaining(CMath.s_parseLongExpression(val)); break;
		case 10: setManufacturerName(val); break;
		case 11: setShipArea(val); break;
		case 12: setCoords(CMParms.toLongArray(CMParms.parseCommas(val, true))); break;
		case 13: setRadius(CMath.s_long(val)); break;
		case 14: setRoll(CMath.s_double(val)); break;
		case 15: setDirection(CMParms.toDoubleArray(CMParms.parseCommas(val,true))); break;
		case 16: setSpeed(CMath.s_long(val)); break;
		case 17: setFacing(CMParms.toDoubleArray(CMParms.parseCommas(val,true))); break;
		case 18: setOwnerName(val); break;
		case 19: setPrice(CMath.s_int(val)); break;
		case 20: setDoorsNLocks(hasADoor(),isOpen(),CMath.s_bool(val),hasALock(),isLocked(),defaultsLocked()); break;
		case 21: setDoorsNLocks(hasADoor(),isOpen(),defaultsClosed(),hasALock(),isLocked(),CMath.s_bool(val)); break;
		default:
			CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
			break;
		}
	}
	@Override
	protected int getCodeNum(String code)
	{
		for(int i=0;i<MYCODES.length;i++)
			if(code.equalsIgnoreCase(MYCODES[i])) return i;
		return -1;
	}
	private static String[] codes=null;
	@Override
	public String[] getStatCodes()
	{
		if(codes!=null) return codes;
		final String[] MYCODES=CMProps.getStatCodesList(GenSpaceShip.MYCODES,this);
		final String[] superCodes=GenericBuilder.GENITEMCODES;
		codes=new String[superCodes.length+MYCODES.length];
		int i=0;
		for(;i<superCodes.length;i++)
			codes[i]=superCodes[i];
		for(int x=0;x<MYCODES.length;i++,x++)
			codes[i]=MYCODES[x];
		return codes;
	}
	@Override
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof GenSpaceShip)) return false;
		final String[] codes=getStatCodes();
		for(int i=0;i<codes.length;i++)
			if(!E.getStat(codes[i]).equals(getStat(codes[i])))
				return false;
		return true;
	}
}
