package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Chant_StarGazing extends Chant
{
	@Override public String ID() { return "Chant_StarGazing"; }
	private final static String localizedName = CMLib.lang().L("Star Gazing");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Gazing at the Stars)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ENDURING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	long lastTime=0;

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();
		if(canBeUninvoked())
		{
			if(!mob.amDead())
			{
				if(mob.location()!=null)
					mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> end(s) <S-HIS-HER> star gazing."));
				else
					mob.tell(L("You stop star gazing."));
			}
		}
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		if((msg.amISource(mob))
		&&(msg.tool()!=this)
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_CHANNEL))
		&&((CMath.bset(msg.sourceMajor(),CMMsg.MASK_MOVE))
				||(CMath.bset(msg.sourceMajor(),CMMsg.MASK_HANDS))
				||(CMath.bset(msg.sourceMajor(),CMMsg.MASK_MAGIC))
				||(CMath.bset(msg.sourceMajor(),CMMsg.MASK_EYES))))
			unInvoke();
		return;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!(affected instanceof MOB))
			return super.tick(ticking,tickID);

		final MOB mob=(MOB)affected;

		if(tickID!=Tickable.TICKID_MOB) return true;
		if(!mob.isInCombat())
		{
			if(!mob.location().getArea().getClimateObj().canSeeTheStars(mob.location()))
			{
				unInvoke();
				return false;
			}
			if((System.currentTimeMillis()-lastTime)<60000)
				return true;
			if(!proficiencyCheck(null,0,false))
				return true;
			lastTime=System.currentTimeMillis();
			final Room room=mob.location();
			final int myAlignment=mob.fetchFaction(CMLib.factions().AlignID());
			final int total=CMLib.factions().getTotal(CMLib.factions().AlignID());
			final int ratePct=(int)Math.round(CMath.mul(total,.01));
			if(CMLib.factions().getAlignPurity(myAlignment,Faction.Align.INDIFF)<99)
			{
				if(CMLib.factions().getAlignPurity(myAlignment,Faction.Align.EVIL)<CMLib.factions().getAlignPurity(myAlignment,Faction.Align.GOOD))
					CMLib.factions().postFactionChange(mob,this, CMLib.factions().AlignID(), ratePct);
				else
					CMLib.factions().postFactionChange(mob,this, CMLib.factions().AlignID(), -ratePct);
				switch(CMLib.dice().roll(1,10,0))
				{
				case 0: room.show(mob,null,this,CMMsg.MSG_CONTEMPLATE,L("<S-NAME> whisper(s) to infinity.")); break;
				case 1: room.show(mob,null,this,CMMsg.MSG_CONTEMPLATE,L("<S-NAME> learn(s) the patterns of the heavens.")); break;
				case 2: room.show(mob,null,this,CMMsg.MSG_CONTEMPLATE,L("<S-NAME> watch(es) a single point of light.")); break;
				case 3: room.show(mob,null,this,CMMsg.MSG_CONTEMPLATE,L("<S-NAME> embrace(s) the cosmos.")); break;
				case 4: room.show(mob,null,this,CMMsg.MSG_CONTEMPLATE,L("<S-NAME> inhale(s) the heavens.")); break;
				case 5: room.show(mob,null,this,CMMsg.MSG_CONTEMPLATE,L("<S-NAME> watch(es) the stars move across the sky.")); break;
				case 6: room.show(mob,null,this,CMMsg.MSG_CONTEMPLATE,L("<S-NAME> become(s) one with the universe.")); break;
				case 7: room.show(mob,null,this,CMMsg.MSG_CONTEMPLATE,L("<S-NAME> seek(s) the inner beauty of the cosmic order.")); break;
				case 8: room.show(mob,null,this,CMMsg.MSG_CONTEMPLATE,L("<S-NAME> expunge(s) <S-HIS-HER> unnatural thoughts.")); break;
				case 9: room.show(mob,null,this,CMMsg.MSG_CONTEMPLATE,L("<S-NAME> find(s) clarity in the stars.")); break;
				}
			}
		}
		else
		{
			unInvoke();
			return false;
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.isInCombat())
		{
			mob.tell(L("You can't star gaze while in combat!"));
			return false;
		}
		if((mob.location().domainType()&Room.INDOORS)>0)
		{
			mob.tell(L("You must be outdoors for this chant to work."));
			return false;
		}
		if(mob.location().domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
		{
			mob.tell(L("This magic will not work here."));
			return false;
		}
		if(!mob.location().getArea().getClimateObj().canSeeTheStars(mob.location()))
		{
			mob.tell(L("You can't see the stars right now."));
			return false;
		}

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,null,this,somanticCastCode(mob,null,auto),L("^S<S-NAME> begin(s) to gaze at the stars...^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,mob,asLevel,Ability.TICKS_FOREVER);
				helpProficiency(mob, 0);
			}
		}
		else
			return beneficialVisualFizzle(mob,null,L("<S-NAME> chant(s) to begin star gazing, but lose(s) concentration."));

		// return whether it worked
		return success;
	}
}
