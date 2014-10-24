package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Prayer_FleshRock extends Prayer
{
	@Override public String ID() { return "Prayer_FleshRock"; }
	private final static String localizedName = CMLib.lang().L("Flesh Rock");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Flesh to Rock)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CORRUPTION;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	protected CharState prevState=null;

	public Item statue=null;
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_MOB)
		&&(affected!=null)
		&&(statue!=null)
		&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			mob.makePeace();
			if((statue.owner()!=null)&&(statue.owner()!=mob.location()))
			{
				Room room=null;
				if(statue.owner() instanceof MOB)
					room=((MOB)statue.owner()).location();
				else
				if(statue.owner() instanceof Room)
					room=(Room)statue.owner();
				if((room!=null)&&(room!=mob.location()))
					room.bringMobHere(mob,false);
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;
			if(msg.source().getVictim()==mob)
				msg.source().setVictim(null);
			if(mob.isInCombat())
			{
				final MOB victim=mob.getVictim();
				if(victim!=null) victim.makePeace();
				mob.makePeace();
			}
			mob.recoverMaxState();
			mob.resetToMaxState();
			mob.curState().setHunger(1000);
			mob.curState().setThirst(1000);
			mob.recoverCharStats();
			mob.recoverPhyStats();

			// when this spell is on a MOBs Affected list,
			// it should consistantly prevent the mob
			// from trying to do ANYTHING except sleep
			if(msg.amISource(mob))
			{
				if((!msg.sourceMajor(CMMsg.MASK_ALWAYS))
				&&(msg.sourceMajor()>0))
				{
					mob.tell(L("Statues can't do that."));
					return false;
				}
			}
		}
		if(!super.okMessage(myHost,msg))
			return false;

		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;
			if(msg.source().getVictim()==affected)
				msg.source().setVictim(null);
			if(mob.isInCombat())
			{
				final MOB victim=mob.getVictim();
				if(victim!=null) victim.makePeace();
				mob.makePeace();
			}
		}
		return true;
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		// when this spell is on a MOBs Affected list,
		// it should consistantly put the mob into
		// a sleeping state, so that nothing they do
		// can get them out of it.
		if(affected instanceof MOB)
		{
			//affectableStats.setReplacementName("a statue of "+affected.name());
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_MOVE);
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_HEAR);
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_SMELL);
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_SPEAK);
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_TASTE);
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_NOT_SEEN);
		}
	}


	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
		{
			if(statue!=null) statue.destroy();
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> flesh is no longer made of rock."));
			if(prevState!=null) prevState.copyInto(mob.curState());
			CMLib.commands().postStand(mob,true);
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+(2*super.getXLEVELLevel(mob)));
		if(levelDiff<0) levelDiff=0;
		boolean success=proficiencyCheck(mob,-(levelDiff*5),auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 and point(s) at <T-NAMESELF>.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					int a=0;
					while(a<target.numEffects()) // personal effects
					{
						final Ability A=target.fetchEffect(a);
						final int s=target.numEffects();
						if(A!=null) A.unInvoke();
						if(target.numEffects()==s)
							a++;
					}
					CMLib.commands().postStand(target,true);
					statue=CMClass.getItem("GenItem");
					String name=target.name();
					if(name.startsWith("A ")) name="a "+name.substring(2);
					if(name.startsWith("An ")) name="an "+name.substring(3);
					if(name.startsWith("The ")) name="the "+name.substring(4);
					statue.setName(L("a rocky statue of @x1",name));
					statue.setDisplayText(L("a rocky statue of @x1 stands here.",name));
					statue.setDescription(L("It`s a hard rocky statue, which looks exactly like @x1.",name));
					statue.setMaterial(RawMaterial.RESOURCE_GRANITE);
					statue.basePhyStats().setWeight(2000);
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> turn(s) into rock!!"));
					success=maliciousAffect(mob,target,asLevel,(mob.phyStats().level()+(2*super.getXLEVELLevel(mob))),-1)!=null;
					target.makePeace();
					if(mob.getVictim()==target) mob.setVictim(null);
					final Ability A=target.fetchEffect(ID());
					if(success&&(A!=null))
					{
						mob.location().addItem(statue);
						statue.addEffect(A);
						A.setAffectedOne(target);
						statue.recoverPhyStats();
						((Prayer_FleshRock)A).prevState=(CharState)target.curState().copyOf();
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> @x1 and point(s) at <T-NAMESELF>, but nothing happens.",prayWord(mob)));

		// return whether it worked
		return success;
	}
}
