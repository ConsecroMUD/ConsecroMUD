package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Skill_Puppeteer extends BardSkill
{
	@Override public String ID() { return "Skill_Puppeteer"; }
	private final static String localizedName = CMLib.lang().L("Puppeteer");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"PUPPETEER","PUPPET"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_FOOLISHNESS;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected==null)||(!(affected instanceof Item)))
			return true;

		final Item puppet=(Item)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if((msg.amISource(invoker()))
		&&(!msg.sourceMajor(CMMsg.MASK_ALWAYS))
		&&((msg.sourceMajor(CMMsg.MASK_HANDS))
		||(msg.sourceMajor(CMMsg.MASK_MOVE)))
		&&(msg.targetMinor()!=CMMsg.TYP_SPEAK)
		&&(msg.targetMinor()==CMMsg.TYP_ORDER)
		&&(msg.targetMinor()!=CMMsg.TYP_PANIC)
		&&(!((msg.tool()!=null)&&(msg.tool() instanceof Song)))
		&&(!((msg.tool()!=null)&&(msg.tool() instanceof Skill_Puppeteer)))
		&&(!((msg.tool()!=null)&&(msg.tool() instanceof Dance)))
		&&(!msg.amITarget(puppet)))
		{
			if((!msg.source().isInCombat())&&(msg.target() instanceof MOB))
			{
				if((msg.sourceMajor(CMMsg.MASK_MALICIOUS))
				||(msg.targetMajor(CMMsg.MASK_MALICIOUS)))
					msg.source().setVictim((MOB)msg.target());
			}
			invoker().location().show(invoker(),puppet,CMMsg.MSG_OK_ACTION,L("<S-NAME> animate(s) <T-NAMESELF>."));
			return false;
		}
		else
		if(msg.amITarget(puppet))
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_GET:
			case CMMsg.TYP_PUSH:
			case CMMsg.TYP_PULL:
			case CMMsg.TYP_REMOVE:
				unInvoke();
				break;
			}
		return super.okMessage(myHost,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		final MOB M=invoker();
		if((affected!=null)
		&&(affected instanceof Item)
		&&(((Item)affected).owner()!=null)
		&&(((Item)affected).owner() instanceof Room)
		&&(M!=null)
		&&(M.location().isContent((Item)affected)))
		{
			if(M.isInCombat())
			{
				final boolean isHit=(CMLib.combat().rollToHit(CMLib.combat().adjustedAttackBonus(M,M.getVictim())+(5*getXLEVELLevel(M))
							+((Item)affected).phyStats().attackAdjustment(),CMLib.combat().adjustedArmor(M.getVictim()), 0));
				if(!isHit)
					M.location().show(M,M.getVictim(),affected,CMMsg.MSG_OK_ACTION,L("<O-NAME> attacks <T-NAME> and misses!"));
				else
					CMLib.combat().postDamage(M,M.getVictim(),affected,
											CMLib.dice().roll(1,affected.phyStats().level()+(2*getXLEVELLevel(M)),1),
											CMMsg.MASK_ALWAYS|CMMsg.TYP_WEAPONATTACK,
											Weapon.TYPE_BASHING,affected.name()+" attacks and <DAMAGE> <T-NAME>!");
			}
			else
			if(CMLib.dice().rollPercentage()>75)
			switch(CMLib.dice().roll(1,5,0))
			{
			case 1:
				M.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 walks around.",affected.name()));
				break;
			case 2:
				M.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 waves its little arms.",affected.name()));
				break;
			case 3:
				M.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 hugs you.",affected.name()));
				break;
			case 4:
				M.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 makes a few fake attacks.",affected.name()));
				break;
			case 5:
				M.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 dances around.",affected.name()));
				break;
			}
		}
		else
			unInvoke();
		return super.tick(ticking,tickID);
	}

	@Override
	public void unInvoke()
	{
		if((affected!=null)
		&&(affected instanceof Item)
		&&(((Item)affected).owner()!=null)
		&&(((Item)affected).owner() instanceof Room))
			((Room)((Item)affected).owner()).showHappens(CMMsg.MSG_OK_ACTION,L("@x1 stops moving.",affected.name()));
		super.unInvoke();
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_FLYING);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("@x1 is already animated!",target.name(mob)));
			return false;
		}
		if((!target.Name().toLowerCase().endsWith(" puppet"))
		&&(!target.Name().toLowerCase().endsWith(" marionette")))
		{
			mob.tell(L("That's not a puppet!"));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_NOISYMOVEMENT,CMMsg.TYP_DELICATE_HANDS_ACT,CMMsg.TYP_DELICATE_HANDS_ACT,null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				target.unWear();
				if(mob.isMine(target))
					mob.location().show(mob,target,CMMsg.MSG_DROP,L("<S-NAME> start(s) animating <T-NAME>!"));
				else
					mob.location().show(mob,target,CMMsg.MSG_OK_ACTION,L("<S-NAME> start(s) animating <T-NAME>!"));
				if(mob.location().isContent(target))
					beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			mob.location().show(mob,target,CMMsg.MSG_OK_ACTION,L("<T-NAME> twitch(es) oddly, but does nothing more."));


		// return whether it worked
		return success;
	}
}
