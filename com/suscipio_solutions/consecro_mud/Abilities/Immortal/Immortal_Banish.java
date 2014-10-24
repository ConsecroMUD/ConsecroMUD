package com.suscipio_solutions.consecro_mud.Abilities.Immortal;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TimeManager;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Immortal_Banish extends Immortal_Skill
{
	boolean doneTicking=false;
	@Override public String ID() { return "Immortal_Banish"; }
	private final static String localizedName = CMLib.lang().L("Banish");
	@Override public String name() { return localizedName; }
	@Override public String displayText() { return L("(Banished "+timeRemaining()+")"); }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"BANISH"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_IMMORTAL;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(1);}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	protected Room prisonRoom=null;
	protected long releaseTime=0;

	protected String timeRemaining()
	{
		if(releaseTime<=0) return "indefinitely";
		if(releaseTime<System.currentTimeMillis()) return "until any second now.";
		return "for another "+CMLib.english().returnTime(releaseTime-System.currentTimeMillis(),0);
	}

	public Room prison()
	{
		if((prisonRoom!=null)&&(!prisonRoom.amDestroyed()))
			return prisonRoom;

		Room myPrison=null;
		int x=0;
		if((text().length()>0)&&((x=text().indexOf("<P>"))>0))
			myPrison=CMLib.map().getRoom(text().substring(0,x));
		if(myPrison != null)
		{
			prisonRoom = (Room) myPrison.copyOf();
		}
		else
		{
			prisonRoom=CMClass.getLocale("StoneRoom");
			prisonRoom.addNonUninvokableEffect((Ability)copyOf());
			prisonRoom.setArea(CMLib.map().getFirstArea());
			prisonRoom.setDescription(L("You are standing on an immense, grey stone floor that stretches as far as you can see in all directions.  Rough winds plunging from the dark, starless sky tear savagely at your fragile body."));
			prisonRoom.setDisplayText(L("The Hall of Lost Souls"));
			prisonRoom.setRoomID("");
			final Ability A2=CMClass.getAbility("Prop_HereSpellCast");
			if(A2!=null) A2.setMiscText("Spell_Hungerless;Spell_Thirstless");
			if(A2!=null) prisonRoom.addNonUninvokableEffect(A2);
		}
		for(final int dir : Directions.CODES())
		{
			prisonRoom.setRawExit(dir,CMClass.getExit("Open"));
			prisonRoom.rawDoors()[dir]=prisonRoom;
		}
		return prisonRoom;
	}

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		final int x=newText.indexOf("<P>");
		if(x>=0)
			releaseTime=CMath.s_long(newText.substring(x+3));
		prisonRoom=null;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		final Room room=prison();
		if((ticking instanceof MOB)&&(!room.isInhabitant((MOB)ticking)))
			room.bringMobHere((MOB)ticking,false);
		if(releaseTime<=0) return true;
		if(releaseTime>System.currentTimeMillis()) return true;
		unInvoke();
		return false;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if(affected instanceof Room)
		{
			if((msg.tool()!=null)
			&&(msg.tool() instanceof Ability)
			&&(msg.source()!=null)
			&&(msg.source().location()!=null)
			&&(msg.sourceMinor()!=CMMsg.TYP_LEAVE))
			{
				final boolean summon=CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_SUMMONING);
				final boolean teleport=CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_TRANSPORTING);
				final boolean shere=(msg.source().location()==affected)||(msg.source().location().getArea()==affected);
				if(((!shere)&&(!summon)&&(teleport))
				   ||((shere)&&(summon)))
				{
					msg.source().location().showHappens(CMMsg.MSG_OK_VISUAL,L("Magic energy fizzles and is absorbed into the air."));
					return false;
				}
			}
			if((msg.tool()!=null)
			&&(msg.tool() instanceof Ability)
			&&(msg.source()!=null)
			&&(msg.source().location()!=null)
			&&(msg.sourceMinor()!=CMMsg.TYP_ENTER))
			{
				final boolean shere=(msg.source().location()==affected)||(msg.source().location().getArea()==affected);
				final boolean summon=CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_SUMMONING);
				final boolean teleport=CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_TRANSPORTING);
				if(((shere)&&(!summon)&&(teleport))
				   ||((!shere)&&(summon)))
				{
					msg.source().location().showHappens(CMMsg.MSG_OK_VISUAL,L("Magic energy fizzles and is absorbed into the air."));
					return false;
				}
			}
			if((msg.tool()!=null)
			&&(msg.tool() instanceof Ability)
			&&(msg.source()!=null)
			&&(msg.source().location()!=null)
			&&((msg.source().location()==affected)
			   ||(msg.source().location().getArea()==affected))
			&&(CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_SUMMONING)))
			{
				msg.source().location().showHappens(CMMsg.MSG_OK_VISUAL,L("Magic energy fizzles and is absorbed into the air."));
				return false;
			}
			if(msg.sourceMinor()==CMMsg.TYP_RECALL)
			{
				if((msg.source()!=null)&&(msg.source().location()!=null))
					msg.source().location().show(msg.source(),null,CMMsg.MSG_OK_ACTION,L("<S-NAME> attempt(s) to recall, but the magic fizzles."));
				return false;
			}
		}
		return true;
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
		{
			super.unInvoke();
			return;
		}
		final MOB mob=(MOB)affected;

		super.unInvoke();

		mob.tell(L("You are released from banishment!"));
		mob.getStartRoom().bringMobHere(mob,true);
		if(prisonRoom!=null)
		{
			CMLib.map().emptyRoom(prisonRoom, mob.getStartRoom());
			prisonRoom.destroy();
			prisonRoom=null;
		}
		mob.delEffect(this);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		long time=0;
		if(commands.size()>2)
		{
			final String last=((String)commands.lastElement()).toUpperCase();
			final String num=(String)commands.elementAt(commands.size()-2);
			if((CMath.isInteger(num))&&(CMath.s_int(num)>0))
			{
				if("DAYS".startsWith(last))
					time=System.currentTimeMillis()+(TimeManager.MILI_DAY*CMath.s_int(num));
				else
				if("MONTHS".startsWith(last))
					time=System.currentTimeMillis()+(TimeManager.MILI_MONTH*CMath.s_int(num));
				else
				if("HOURS".startsWith(last))
					time=System.currentTimeMillis()+(TimeManager.MILI_HOUR*CMath.s_int(num));
				else
				if("MINUTES".startsWith(last))
					time=System.currentTimeMillis()+(TimeManager.MILI_MINUTE*CMath.s_int(num));
				else
				if("SECONDS".startsWith(last))
					time=System.currentTimeMillis()+(TimeManager.MILI_SECOND*CMath.s_int(num));
				else
				if("TICKS".startsWith(last))
					time=System.currentTimeMillis()+(CMProps.getTickMillis()*CMath.s_int(num));
				if(time>System.currentTimeMillis())
				{
					commands.removeElementAt(commands.size()-1);
					commands.removeElementAt(commands.size()-1);
				}
			}
		}
		Room myPrison = CMLib.map().getRoom(CMParms.combine(commands,1));
		if(myPrison != null && CMLib.map().getExtendedRoomID(myPrison).length()>0)
		{
			while(commands.size() > 1)
				commands.removeElementAt(1);
		}
		else
			myPrison = null;

		final MOB target=getTargetAnywhere(mob,commands,givenTarget,false,true,false);
		if(target==null) return false;

		Immortal_Banish A=(Immortal_Banish)target.fetchEffect(ID());
		if(A!=null)
		{
			A.unInvoke();
			mob.tell(L("@x1 is released from banishment.",target.Name()));
			return true;
		}

		if(!super.invoke(mob,commands,givenTarget,auto, asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),auto?L("<T-NAME> is banished!"):L("^F<S-NAME> banish(es) <T-NAMESELF>.^?"));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				A=(Immortal_Banish)copyOf();
				String prisonID="";
				if(myPrison!=null) prisonID=CMLib.map().getExtendedRoomID(myPrison);
				A.setMiscText(prisonID+"<P>"+time);
				target.addNonUninvokableEffect(A);
				A=(Immortal_Banish)target.fetchEffect(ID());
				if((A!=null)&&(A.prison()!=null)&&(!A.prison().isInhabitant(target)))
				{
					A.prison().bringMobHere(target,false);
					mob.location().send(mob,msg);
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> banished to @x1!",A.prison().displayText()));
					Log.sysOut("Banish",mob.Name()+" banished "+target.name()+" to "+CMLib.map().getExtendedRoomID(A.prison())+".");
				}
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to banish <T-NAMESELF>, but fail(s)."));
		return success;
	}
}
