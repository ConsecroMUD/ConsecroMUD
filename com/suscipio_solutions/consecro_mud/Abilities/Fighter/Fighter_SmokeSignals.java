package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Climate;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Fighter_SmokeSignals extends FighterSkill
{
	@Override public String ID() { return "Fighter_SmokeSignals"; }
	private final static String localizedName = CMLib.lang().L("Smoke Signals");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_NATURELORE;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	private static final String[] triggerStrings =I(new String[] {"SMOKESIGNALS","SMOKESIGNAL"});
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((affected instanceof MOB)
		&&(!msg.amISource((MOB)affected))
		&&(msg.tool() instanceof Ability)
		&&(msg.tool().ID().equals("Fighter_SmokeSignals"))
		&&(msg.sourceMinor()==CMMsg.NO_EFFECT)
		&&(msg.targetMinor()==CMMsg.NO_EFFECT)
		&&(msg.targetMessage()!=null)
		&&(msg.othersMessage()!=null))
			msg.addTrailerMsg(CMClass.getMsg((MOB)affected,null,null,CMMsg.MSG_OK_VISUAL,L("The smoke signals seem to say '@x1'.",msg.targetMessage()),CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null));
		super.executeMsg(myHost,msg);
	}

	public Item getRequiredFire(MOB mob)
	{
		Item fire=null;
		for(int i=0;i<mob.location().numItems();i++)
		{
			final Item I2=mob.location().getItem(i);
			if((I2!=null)&&(I2.container()==null)&&(CMLib.flags().isOnFire(I2)))
			{
				fire=I2;
				break;
			}
		}
		if((fire==null)||(!mob.location().isContent(fire)))
		{
			mob.tell(L("A fire will need to be built first."));
			return null;
		}
		return fire;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((!auto)&&(mob.isInCombat()))
		{
			mob.tell(L("Not while you are fighting!"));
			return false;
		}

		if(getRequiredFire(mob)==null) return false;
		Room R=mob.location();
		int weather=R.getArea().getClimateObj().weatherType(R);

		if(((R.domainType()&Room.INDOORS)==Room.INDOORS)
		||(R.domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER))
		{
			mob.tell(L("You can't signal anyone from here."));
			return false;
		}
		else
		if((weather==Climate.WEATHER_BLIZZARD)
		||(weather==Climate.WEATHER_DUSTSTORM)
		||(weather==Climate.WEATHER_HAIL)
		||(weather==Climate.WEATHER_RAIN)
		||(weather==Climate.WEATHER_SLEET)
		||(weather==Climate.WEATHER_SNOW)
		||(weather==Climate.WEATHER_THUNDERSTORM))
		{
			mob.tell(L("You won't be able to get a signal up in these weather conditions."));
			return false;
		}


		if(commands.size()==0)
		{
			mob.tell(L("You need to specify the message to send up in the smoke signals."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_NOISYMOVEMENT,auto?L("<T-NAME> begin(s) smoking uncontrollably!"):L("<S-NAME> puff(s) up a mighty series of smoke signals!"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final String str=CMParms.combine(commands,0);
				final CMMsg msg2=CMClass.getMsg(mob,null,this,CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,str,CMMsg.MSG_OK_VISUAL,L("You see some smoke signals in the distance."));
				final TrackingLibrary.TrackingFlags flags=new TrackingLibrary.TrackingFlags();
				final List<Room> checkSet=CMLib.tracking().getRadiantRooms(mob.location(),flags,50);
				for(final Iterator<Room> r=checkSet.iterator();r.hasNext();)
				{
					R=r.next();
					weather=R.getArea().getClimateObj().weatherType(R);
					if((R!=mob.location())
					&&((R.domainType()&Room.INDOORS)==0)
					&&(R.domainType()!=Room.DOMAIN_OUTDOORS_UNDERWATER)
					&&(weather!=Climate.WEATHER_BLIZZARD)
					&&(weather!=Climate.WEATHER_DUSTSTORM)
					&&(weather!=Climate.WEATHER_HAIL)
					&&(weather!=Climate.WEATHER_RAIN)
					&&(weather!=Climate.WEATHER_SLEET)
					&&(weather!=Climate.WEATHER_SNOW)
					&&(weather!=Climate.WEATHER_THUNDERSTORM)
					&&(R.okMessage(mob,msg2)))
					   R.sendOthers(msg.source(),msg2);
				}
			}
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to send a smoke signal, but goof(s) it up."));
		return success;
	}
}
