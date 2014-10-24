package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_AstralSpirit extends Property
{
	@Override public String ID() { return "Prop_AstralSpirit"; }
	@Override public String name(){ return "Astral Spirit";}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Spirit Form)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	private Race race=null;


	@Override public long flags(){return Ability.FLAG_ADJUSTER|Ability.FLAG_IMMUNER;}

	public Race spiritRace()
	{
		if(race==null)
			race=CMClass.getRace("Spirit");
		return race;
	}
	@Override
	public boolean autoInvocation(MOB mob)
	{
		if((mob!=null)&&(mob.fetchEffect(ID())==null))
		{
			mob.addNonUninvokableEffect(this);
			return true;
		}
		return false;
	}

	@Override
	public String accountForYourself()
	{ return "an astral spirit";	}

	public void peaceAt(MOB mob)
	{
		final Room room=mob.location();
		if(room==null) return;
		for(int m=0;m<room.numInhabitants();m++)
		{
			final MOB inhab=room.fetchInhabitant(m);
			if((inhab!=null)&&(inhab.getVictim()==mob))
				inhab.setVictim(null);
		}
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;
		final MOB mob=(MOB)affected;

		if((msg.amISource(mob))&&(!msg.sourceMajor(CMMsg.MASK_ALWAYS)))
		{
			if((msg.sourceMinor()==CMMsg.TYP_DISPOSSESS)&&(msg.source().soulMate()!=null))
			{
				Ability A=msg.source().fetchEffect("Chant_AstralProjection");
				if(A==null) A=msg.source().soulMate().fetchEffect("Chant_AstralProjection");
				if(A!=null)
				{
					A.unInvoke();
					return false;
				}
			}
			else
			if((msg.targetMinor()==CMMsg.TYP_SIT)&&(msg.target() instanceof DeadBody))
			{
				final Vector<String> V=CMParms.parse(text().toUpperCase());
				if(!V.contains("SELF-RES"))
				{
					mob.tell(L("You lack that power"));
					return false;
				}
			}
			if((msg.tool()!=null)&&(msg.tool().ID().equalsIgnoreCase("Skill_Revoke")))
			   return super.okMessage(myHost,msg);
			else
			if(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
			{
				mob.tell(L("You are unable to attack in this incorporeal form."));
				peaceAt(mob);
				return false;
			}
			else
			if((msg.sourceMajor(CMMsg.MASK_HANDS))
			||(msg.sourceMajor(CMMsg.MASK_MOUTH)))
			{
				if(msg.sourceMajor(CMMsg.MASK_SOUND))
					mob.tell(L("You are unable to make sounds in this incorporeal form."));
				else
					mob.tell(L("You are unable to do that this incorporeal form."));
				peaceAt(mob);
				return false;
			}
		}
		else
		if((msg.amITarget(mob))&&(!msg.amISource(mob))
		   &&(!msg.targetMajor(CMMsg.MASK_ALWAYS)))
		{
			mob.tell(L("@x1 doesn't seem to be here.",mob.name()));
			return false;
		}
		return true;
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		affectableStats.setMyRace(spiritRace());
		super.affectCharStats(affected, affectableStats);
	}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		// when this spell is on a MOBs Affected list,
		// it should consistantly put the mob into
		// a sleeping state, so that nothing they do
		// can get them out of it.
		affectableStats.setWeight(0);
		affectableStats.setHeight(-1);
		affectableStats.setName(L("The spirit of @x1",affected.name()));
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_GOLEM);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_INVISIBLE);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_NOT_SEEN);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_FLYING);
		affectableStats.setDisposition(affectableStats.disposition()&~PhyStats.IS_SITTING);
		affectableStats.setDisposition(affectableStats.disposition()&~PhyStats.IS_SLEEPING);
		affectableStats.setDisposition(affectableStats.disposition()&~PhyStats.IS_CUSTOM);
		affectableStats.setSensesMask(affectableStats.sensesMask()&~PhyStats.CAN_NOT_MOVE);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_SPEAK);
	}
}
