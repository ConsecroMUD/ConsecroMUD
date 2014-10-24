package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_WizInvis extends Property
{
	@Override public String ID() { return "Prop_WizInvis"; }

	@Override public long flags(){return Ability.FLAG_ADJUSTER;}

	@Override
	public String displayText()
	{
		if(CMath.bset(abilityCode(),PhyStats.IS_CLOAKED|PhyStats.IS_NOT_SEEN))
			return "(Wizard Invisibility)";
		else
		if(CMath.bset(abilityCode(),PhyStats.IS_NOT_SEEN))
			return "(WizUndetectable)";
		else
		if(CMath.bset(abilityCode(),PhyStats.IS_CLOAKED))
			return "(Cloaked)";
		else
			return "";
	}
	@Override public String name(){ return "Wizard Invisibility";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected boolean disabled=false;
	protected int abilityCode=PhyStats.IS_NOT_SEEN|PhyStats.IS_CLOAKED;
	@Override public int abilityCode(){return abilityCode;}
	@Override public void setAbilityCode(int newCode){abilityCode=newCode;}

	@Override
	public String accountForYourself()
	{ return "Wizard Invisibile";	}


	@Override public boolean canBeUninvoked(){return true;}
	public boolean isAnAutoEffect(){return false;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		// when this spell is on a MOBs Affected list,
		// it should consistantly put the mob into
		// a sleeping state, so that nothing they do
		// can get them out of it.
		affectableStats.setDisposition(affectableStats.disposition()|abilityCode);
		if(CMath.bset(abilityCode(),PhyStats.IS_NOT_SEEN))
		{
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_INVISIBLE);
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_HIDDEN);
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SNEAKING);
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_FLYING);
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_CLIMBING);
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SWIMMING);
		}
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_HIDDEN);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_SNEAKERS);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_DARK);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_INVISIBLE);
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affected.curState().setHunger(affected.maxState().maxHunger(affected.baseWeight()));
		affected.curState().setThirst(affected.maxState().maxThirst(affected.baseWeight()));
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		if(affected==null) return;
		final Physical being=affected;

		if(this.canBeUninvoked())
		{
			being.delEffect(this);
			if(being instanceof Room)
				((Room)being).recoverRoomStats();
			else
			if(being instanceof MOB)
			{
				if(((MOB)being).location()!=null)
					((MOB)being).location().recoverRoomStats();
				else
				{
					being.recoverPhyStats();
					((MOB)being).recoverCharStats();
					((MOB)being).recoverMaxState();
				}
			}
			else
				being.recoverPhyStats();
			mob.tell(L("You begin to fade back into view."));
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS)&&(msg.amITarget(affected))&&(affected!=null)&&(!disabled)))
		{
			if(msg.source()!=msg.target())
			{
				msg.source().tell(L("Ah, leave @x1 alone.",affected.name()));
				if(affected instanceof MOB)
					((MOB)affected).makePeace();
			}
			return false;
		}
		else
		if((affected!=null)&&(affected instanceof MOB))
		{
			if((CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))&&(msg.amISource((MOB)affected)))
				disabled=true;
			else
			if((msg.amISource((MOB)affected))
			&&(msg.source().isAttribute(MOB.Attrib.SYSOPMSGS))
			&&(msg.source().location()!=null)
			&&(!CMSecurity.isAllowed(msg.source(),msg.source().location(),CMSecurity.SecFlag.SYSMSGS)))
				msg.source().setAttribute(MOB.Attrib.SYSOPMSGS,false);
		}

		return super.okMessage(myHost,msg);
	}
}
