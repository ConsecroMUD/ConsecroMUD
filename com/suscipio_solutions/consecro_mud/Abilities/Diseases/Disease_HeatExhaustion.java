package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Climate;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Disease_HeatExhaustion extends Disease
{
	@Override public String ID() { return "Disease_HeatExhaustion"; }
	private final static String localizedName = CMLib.lang().L("Heat Exhaustion");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Heat Exhaustion)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}
	@Override public int difficultyLevel(){return 1;}

	@Override protected int DISEASE_TICKS(){return 300;}
	@Override protected int DISEASE_DELAY(){return 3;}
	@Override protected String DISEASE_DONE(){return "Your head stops spinning.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> <S-IS-ARE> overcome by the heat.^?";}
	@Override protected String DISEASE_AFFECT(){return "";}
	@Override public int abilityCode(){return 0;}
	protected Room theRoom=null;
	protected int changeDown=300;

	public Room room(Room R)
	{
		if((theRoom==null)
		&&(R!=null)
		&&(!R.getArea().isProperlyEmpty()))
			theRoom=R.getArea().getRandomProperRoom();
		theRoom=CMLib.map().getRoom(theRoom);
		if(R==theRoom) theRoom=null;
		return theRoom;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		&&(affected==msg.source())
		&&(msg.amITarget(msg.source().location()))
		&&((msg.targetMinor()==CMMsg.TYP_LOOK)||(msg.targetMinor()==CMMsg.TYP_EXAMINE)))
		{
			final Room R=room(msg.source().location());
			if((R==null)||(R==msg.source().location())) return true;
			final CMMsg msg2=CMClass.getMsg(msg.source(),R,msg.tool(),
						  msg.sourceCode(),msg.sourceMessage(),
						  msg.targetCode(),msg.targetMessage(),
						  msg.othersCode(),msg.othersMessage());
			if(R.okMessage(msg.source(),msg2))
			{
				R.executeMsg(msg.source(),msg2);
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((affected instanceof MOB)
		&&(canBeUninvoked()))
		{
			final MOB M=((MOB)affected);
			if(M.location()!=null)
			{
				final Area A=M.location().getArea();
				switch(M.location().domainType())
				{
				case Room.DOMAIN_INDOORS_UNDERWATER:
				case Room.DOMAIN_OUTDOORS_UNDERWATER:
					unInvoke();
					return false;
				}
				Climate C=null;
				if(A!=null)
					C=A.getClimateObj();
				if(C!=null)
					switch(C.weatherType(M.location()))
					{
					case Climate.WEATHER_BLIZZARD:
					case Climate.WEATHER_HAIL:
					case Climate.WEATHER_RAIN:
					case Climate.WEATHER_SNOW:
					case Climate.WEATHER_THUNDERSTORM:
					case Climate.WEATHER_WINTER_COLD:
					{
						unInvoke();
						return false;
					}
					default:
						break;
					}
			}

		}
		if((--changeDown)<=0)
		{
			changeDown=300;
			theRoom=null;
		}
		return true;
	}
}
