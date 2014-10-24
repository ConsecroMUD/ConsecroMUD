package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_TremorSense extends Chant
{
	@Override public String ID() { return "Chant_TremorSense"; }
	private final static String localizedName = CMLib.lang().L("Tremor Sense");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Tremor Sense)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_DEEPMAGIC;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS|CAN_ROOMS;}
	protected Vector<Room> rooms=new Vector();

	@Override
	public CMObject copyOf()
	{
		final Chant_TremorSense obj=(Chant_TremorSense)super.copyOf();
		obj.rooms=new Vector<Room>();
		obj.rooms.addAll(rooms);
		return obj;
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
		{
			super.unInvoke();
			return;
		}
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> tremor sense fades."));
		for(int r=0;r<rooms.size();r++)
		{
			final Room R=rooms.elementAt(r);
			final Ability A=R.fetchEffect(ID());
			if((A!=null)&&(A.invoker()==mob))
				A.unInvoke();
		}
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(affected==null) return;
		if(affected instanceof MOB)
		{
			if(msg.amISource((MOB)affected)
			&&((msg.sourceMinor()==CMMsg.TYP_STAND)
			   ||(msg.sourceMinor()==CMMsg.TYP_LEAVE)))
				unInvoke();
		}
		else
		if(affected instanceof Room)
		{
			if((msg.target()==affected)
			&&(msg.targetMinor()==CMMsg.TYP_ENTER)
			&&(!CMLib.flags().isInFlight(msg.source()))
			&&(invoker!=null)
			&&(invoker.location()!=null))
			{
				if(invoker.location()==affected)
					invoker.tell(L("You feel footsteps around you."));
				else
				{
					final int dir=CMLib.tracking().radiatesFromDir((Room)affected,rooms);
					if(dir>=0)
						invoker.tell(L("You feel footsteps @x1",Directions.getInDirectionName(dir)));
				}
			}
			else
			if((msg.tool() instanceof Ability)
			&&((msg.tool().ID().equals("Prayer_Tremor"))
				||(msg.tool().ID().endsWith("_Earthquake"))))
			{
				if(invoker.location()==affected)
					invoker.tell(L("You feel a ferocious rumble."));
				else
				{
					final int dir=CMLib.tracking().radiatesFromDir((Room)affected,rooms);
					if(dir>=0)
						invoker.tell(L("You feel a ferocious rumble @x1",Directions.getInDirectionName(dir)));
				}
			}
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already sensing tremors."));
			return false;
		}

		if((!CMLib.flags().isSitting(mob))||(mob.riding()!=null))
		{
			mob.tell(L("You must be sitting on the ground for this chant to work."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("@x1<T-NAME> gain(s) a sense of the earth!^?",L(auto?"":"^S<S-NAME> chant(s) to <S-HIM-HERSELF>.  ")));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				rooms=new Vector();
				TrackingLibrary.TrackingFlags flags;
				flags = new TrackingLibrary.TrackingFlags()
						.plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
						.plus(TrackingLibrary.TrackingFlag.NOAIR)
						.plus(TrackingLibrary.TrackingFlag.NOWATER);
				CMLib.tracking().getRadiantRooms(mob.location(),rooms,flags,null,5,null);
				for(int r=0;r<rooms.size();r++)
				{
					final Room R=rooms.elementAt(r);
					if((R!=mob.location())
					&&(R.domainType()!=Room.DOMAIN_INDOORS_AIR)
					&&(R.domainType()!=Room.DOMAIN_INDOORS_UNDERWATER)
					&&(R.domainType()!=Room.DOMAIN_INDOORS_WATERSURFACE)
					&&(R.domainType()!=Room.DOMAIN_OUTDOORS_AIR)
					&&(R.domainType()!=Room.DOMAIN_OUTDOORS_UNDERWATER)
					&&(R.domainType()!=Room.DOMAIN_OUTDOORS_WATERSURFACE))
						beneficialAffect(mob,R,asLevel,0);
				}
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s), but nothing happens."));


		// return whether it worked
		return success;
	}
}
