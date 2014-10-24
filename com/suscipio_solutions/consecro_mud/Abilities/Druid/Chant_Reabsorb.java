package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_Reabsorb extends Chant
{
	@Override public String ID() { return "Chant_Reabsorb"; }
	private final static String localizedName = CMLib.lang().L("Reabsorb");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_DEEPMAGIC;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(!(target instanceof Item))
				return Ability.QUALITY_INDIFFERENT;
			final Room R=mob.location();
			if(R!=null)
			{
				final int type=R.domainType();
				if((type==Room.DOMAIN_INDOORS_STONE)
				||(type==Room.DOMAIN_INDOORS_WOOD)
				||(type==Room.DOMAIN_INDOORS_MAGIC)
				||(type==Room.DOMAIN_INDOORS_UNDERWATER)
				||(type==Room.DOMAIN_INDOORS_WATERSURFACE)
				||(type==Room.DOMAIN_OUTDOORS_AIR)
				||(type==Room.DOMAIN_OUTDOORS_CITY)
				||(type==Room.DOMAIN_OUTDOORS_SPACEPORT)
				||(type==Room.DOMAIN_OUTDOORS_UNDERWATER)
				||(type==Room.DOMAIN_OUTDOORS_WATERSURFACE))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=this.getTarget(mob,mob.location(),givenTarget,null,commands,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;

		final List<DeadBody> V=CMLib.utensils().getDeadBodies(target);
		for(int v=0;v<V.size();v++)
		{
			final DeadBody D=V.get(v);
			if((D!=null)
			&&(D.playerCorpse())
			&&(!D.mobName().equals(mob.Name())))
			{
				mob.tell(L("You are not allowed to reabsorb a player corpse."));
				return false;
			}
		}
		if(!(target.owner() instanceof Room))
		{
			mob.tell(L("You need to put @x1 on the ground first.",target.name(mob)));
			return false;
		}
		final int type=mob.location().domainType();
		if((type==Room.DOMAIN_INDOORS_STONE)
			||(type==Room.DOMAIN_INDOORS_WOOD)
			||(type==Room.DOMAIN_INDOORS_MAGIC)
			||(type==Room.DOMAIN_INDOORS_UNDERWATER)
			||(type==Room.DOMAIN_INDOORS_WATERSURFACE)
			||(type==Room.DOMAIN_OUTDOORS_AIR)
			||(type==Room.DOMAIN_OUTDOORS_CITY)
			||(type==Room.DOMAIN_OUTDOORS_SPACEPORT)
			||(type==Room.DOMAIN_OUTDOORS_UNDERWATER)
			||(type==Room.DOMAIN_OUTDOORS_WATERSURFACE))
		{
			mob.tell(L("That magic won't work here."));
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> starts vibrating!"):L("^S<S-NAME> chant(s), causing <T-NAMESELF> to decay!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("The ground reabsorbs @x1.",target.name()));
					target.destroy();
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) at <T-NAME>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
