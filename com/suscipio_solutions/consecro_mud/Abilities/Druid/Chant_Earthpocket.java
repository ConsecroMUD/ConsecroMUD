package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Chant_Earthpocket extends Chant
{
	@Override public String ID() { return "Chant_Earthpocket"; }
	private final static String localizedName = CMLib.lang().L("Earthpocket");
	@Override public String name() { return localizedName; }
	@Override public String displayText() { return L("(Earthpocket: "+(super.tickDown/CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY))+")"); }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ROCKCONTROL;}
	private Container pocket=null;

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
			mob.tell(L("Your earthpocket fades away, dumping its contents into your inventory!"));
			final List<Item> V=pocket.getContents();
			for(int v=0;v<V.size();v++)
			{
				V.get(v).setContainer(null);
				mob.moveItemTo(V.get(v));
			}
			pocket.destroy();
			pocket=null;
		}
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if((msg.source()!=affected)
		&&((msg.target()==pocket)||(msg.tool()==pocket))
		&&(CMath.bset(msg.sourceMajor(),CMMsg.MASK_HANDS)
		   ||CMath.bset(msg.sourceMajor(),CMMsg.MASK_MOVE)
		   ||CMath.bset(msg.sourceMajor(),CMMsg.MASK_DELICATE)
		   ||CMath.bset(msg.sourceMajor(),CMMsg.MASK_MOUTH)))
		{
			msg.source().tell(L("The dark pocket draws away from you, preventing your action."));
			return false;
		}
		return true;
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		movePocket();
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		movePocket();
		return true;
	}

	public void movePocket()
	{
		if((affected instanceof MOB)&&(pocket!=null))
		{
			if(pocket.owner() instanceof MOB)
				pocket.removeFromOwnerContainer();
			else
			if(pocket.owner() instanceof Room)
			{
				if(((MOB)affected).location()!=null)
				{
					if(((MOB)affected).location().domainType()==Room.DOMAIN_INDOORS_CAVE)
					{
						if(CMath.bset(pocket.basePhyStats().disposition(),PhyStats.IS_NOT_SEEN))
						{
							pocket.basePhyStats().setDisposition(pocket.basePhyStats().disposition()-PhyStats.IS_NOT_SEEN);
							pocket.recoverPhyStats();
						}
						((MOB)affected).location().moveItemTo(pocket);
					}
					else
					if(!CMath.bset(pocket.basePhyStats().disposition(),PhyStats.IS_NOT_SEEN))
					{
						pocket.basePhyStats().setDisposition(pocket.basePhyStats().disposition()|PhyStats.IS_NOT_SEEN);
						pocket.recoverPhyStats();
					}
				}
				else
				if(!CMath.bset(pocket.basePhyStats().disposition(),PhyStats.IS_NOT_SEEN))
				{
					pocket.basePhyStats().setDisposition(pocket.basePhyStats().disposition()|PhyStats.IS_NOT_SEEN);
					pocket.recoverPhyStats();
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

		if(mob.location().domainType()!=Room.DOMAIN_INDOORS_CAVE)
		{
			mob.tell(L("The earthpocket can only be summoned or seen in a cave."));
			return false;
		}

		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already connected with an earthpocket."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) for a connection with a mystical dimension!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				pocket=(Container)CMClass.getItem("GenContainer");
				pocket.setCapacity(Integer.MAX_VALUE);
				pocket.basePhyStats().setSensesMask(PhyStats.SENSE_ITEMNOTGET);
				pocket.basePhyStats().setWeight(0);
				pocket.setMaterial(RawMaterial.RESOURCE_NOTHING);
				pocket.setName(L("an earthpocket"));
				pocket.setDisplayText(L("an empty pitch-black pocket is in the wall here."));
				pocket.setDescription(L("It looks like an endless black hole in the wall.  Very mystical."));
				pocket.recoverPhyStats();
				target.location().addItem(pocket);
				beneficialAffect(mob,target,asLevel,CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH));
				target.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("A dark pocket of energy appears in a nearby wall."));
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s), but nothing more happens."));

		// return whether it worked
		return success;
	}
}
