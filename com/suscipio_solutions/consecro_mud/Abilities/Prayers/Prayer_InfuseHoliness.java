package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Deity;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_InfuseHoliness extends Prayer
{
	@Override public String ID() { return "Prayer_InfuseHoliness"; }
	private final static String localizedName = CMLib.lang().L("Infuse Holiness");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Infused Holiness)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override public long flags(){return Ability.FLAG_HOLY;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS|Ability.CAN_ROOMS|Ability.CAN_EXITS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS|Ability.CAN_ROOMS|Ability.CAN_EXITS;}
	protected int serviceRunning=0;
	@Override public int abilityCode(){return serviceRunning;}
	@Override public void setAbilityCode(int newCode){serviceRunning=newCode;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_GOOD);
		if(CMath.bset(affectableStats.disposition(),PhyStats.IS_EVIL))
			affectableStats.setDisposition(affectableStats.disposition()-PhyStats.IS_EVIL);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null))
			return;
		if(canBeUninvoked())
			if(affected instanceof MOB)
				((MOB)affected).tell(L("Your infused holiness fades."));

		super.unInvoke();

	}


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(serviceRunning==0)
			return super.okMessage(myHost, msg);
		if(((msg.targetMajor() & CMMsg.MASK_MALICIOUS)==CMMsg.MASK_MALICIOUS)
		&&(msg.target() instanceof MOB))
		{
			if(msg.source().getWorshipCharID().equalsIgnoreCase(((MOB)msg.target()).getWorshipCharID()))
			{
				msg.source().tell(L("Not right now -- you're in a service."));
				msg.source().makePeace();
				((MOB)msg.target()).makePeace();
				return false;
			}
		}
		if((msg.sourceMinor() == CMMsg.TYP_LEAVE)&&(msg.source().isMonster()))
		{
			msg.source().tell(L("Not right now -- you're in a service."));
			return false;
		}
		return super.okMessage(myHost, msg);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Physical target;
		if((givenTarget == null)
		&&(CMParms.combine(commands,0).equalsIgnoreCase("room")||CMParms.combine(commands,0).equalsIgnoreCase("here")))
			target=mob.location();
		else
			target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_ANY);
		if(target==null)
			return false;

		Deity D=null;
		if(CMLib.law().getClericInfusion(target)!=null)
		{

			if(target instanceof Room) D=CMLib.law().getClericInfused((Room)target);
			if(D!=null)
				mob.tell(L("There is already an infused aura of @x1 around @x2.",D.Name(),target.name(mob)));
			else
				mob.tell(L("There is already an infused aura around @x1.",target.name(mob)));
			return false;
		}

		D=mob.getMyDeity();
		if(target instanceof Room)
		{
			if(D==null)
			{
				mob.tell(L("The faithless may not infuse holiness in a room."));
				return false;
			}
			final Area A=mob.location().getArea();
			Room R=null;
			for(final Enumeration e=A.getMetroMap();e.hasMoreElements();)
			{
				R=(Room)e.nextElement();
				if(CMLib.law().getClericInfused((Room)target)==D)
				{
					mob.tell(L("There is already a holy place of @x1 in this area at @x2.",D.Name(),R.displayText(mob)));
					return false;
				}
			}
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("A holy aura appears around <T-NAME>."):L("^S<S-NAME> @x1 to infuse a holy aura around <T-NAMESELF>.^?",prayForWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(D!=null) setMiscText(D.Name());
				if((target instanceof Room)
				&&(CMLib.law().doesOwnThisProperty(mob,((Room)target))))
				{
					target.addNonUninvokableEffect((Ability)this.copyOf());
					CMLib.database().DBUpdateRoom((Room)target);
				}
				else
					beneficialAffect(mob,target,asLevel,0);
				target.recoverPhyStats();
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 to infuse a holy aura in <T-NAMESELF>, but fail(s).",prayForWord(mob)));

		return success;
	}
}
