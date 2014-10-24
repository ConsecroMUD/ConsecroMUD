package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_Boomerang extends Spell
{
	@Override public String ID() { return "Spell_Boomerang"; }
	private final static String localizedName = CMLib.lang().L("Returning");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){	return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	protected MOB owner=null;

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(!super.okMessage(host,msg))
			return false;
		if((msg.tool()==affected)
		&&(msg.sourceMinor()==CMMsg.TYP_SELL))
		{
			unInvoke();
			if(affected!=null)	affected.delEffect(this);
		}
		return true;
	}

	public MOB getOwner(Item I)
	{
		if(owner==null)
		{
			if((I.owner()!=null)
			&&(I.owner() instanceof MOB)
			&&(I.owner().Name().equals(text())))
				owner=(MOB)I.owner();
			else
				owner=CMLib.players().getPlayer(text());
		}
		return owner;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(affected instanceof Item)
		{
			final Item I=(Item)affected;
			final MOB owner=getOwner(I);
			if((owner!=null)&&(I.owner()!=null)&&(I.owner()!=owner))
			{
				if(!owner.isMine(I))
				{
					owner.tell(L("@x1 returns to your inventory!",I.name(owner)));
					I.unWear();
					I.setContainer(null);
					owner.moveItemTo(I);
				}
				else
				{
					I.unWear();
					I.setContainer(null);
					owner.moveItemTo(I);
					I.setOwner(owner);
				}
			}
		}
		return (tickID!=Tickable.TICKID_ITEM_BOUNCEBACK);
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		if((msg.targetMinor()==CMMsg.TYP_GET)
		&&(msg.amITarget(affected))
		&&(text().length()==0))
		{
			setMiscText(msg.source().Name());
			msg.source().tell(L("@x1 will now return back to you.",affected.name()));
			makeNonUninvokable();
		}
		if((affected instanceof Item)&&(text().length()>0))
		{
			final Item I=(Item)affected;
			final MOB owner=getOwner(I);
			if((owner!=null)&&(I.owner()!=null)&&(I.owner()!=owner))
			{
				if((msg.sourceMinor()==CMMsg.TYP_DROP)||(msg.target()==I)||(msg.tool()==I))
				{
					msg.addTrailerMsg(CMClass.getMsg(owner,null,CMMsg.NO_EFFECT,null));
				}
			}
			else
			if(!CMLib.threads().isTicking(this, -1))
				CMLib.threads().startTickDown(this, Tickable.TICKID_ITEM_BOUNCEBACK, 1);
		}
	}

	// this fixes a damn PUT bug
	@Override
	public void affectPhyStats(Physical affectedEnv, PhyStats stats)
	{
		super.affectPhyStats(affectedEnv, stats);
		if(affectedEnv instanceof Item)
		{
			final Item item = (Item)affectedEnv;
			if(item.container()!=null)
			{
				final Item container = item.ultimateContainer(null);
				if((container.amDestroyed())||(container.owner() != item.owner()))
					item.setContainer(null);
			}
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,somanticCastCode(mob,target,auto),auto?"":L("^S<S-NAME> point(s) at <T-NAMESELF> and cast(s) a spell.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("<T-NAME> glows slightly!"));
				mob.tell(L("@x1 will now await someone to GET it before acknowleding its new master.",target.name(mob)));
				setMiscText("");
				beneficialAffect(mob,target,asLevel,0);
				target.recoverPhyStats();
				mob.recoverPhyStats();
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> point(s) at <T-NAMESELF>, but fail(s) to cast a spell."));


		// return whether it worked
		return success;
	}
}
