package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_MagneticEarth extends Chant
{
	@Override public String ID() { return "Chant_MagneticEarth"; }
	private final static String localizedName = CMLib.lang().L("Magnetic Earth");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_DEEPMAGIC;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return Ability.CAN_ROOMS;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if((affected!=null)&&(affected instanceof Room))
		{
			final Room R=(Room)affected;
			final Vector toGo=new Vector();
			boolean didSomething=false;
			for(int m=0;m<R.numInhabitants();m++)
			{
				final MOB M=R.fetchInhabitant(m);
				if((M!=null)&&(M!=invoker))
				{
					toGo.clear();
					for(int i=0;i<M.numItems();i++)
					{
						final Item I=M.getItem(i);
						if((I!=null)
						&&(((I.material()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_METAL)
						   ||((I.material()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_MITHRIL))
						&&(I.container()==null)
						&&(I.amWearingAt(Wearable.IN_INVENTORY)
						   ||I.amWearingAt(Wearable.WORN_HELD)
						   ||I.amWearingAt(Wearable.WORN_WIELD)
						   ||I.amWearingAt(Wearable.WORN_EYES)
						   ||I.amWearingAt(Wearable.WORN_MOUTH)))
							toGo.addElement(I);
					}
					for(int i=0;i<toGo.size();i++)
					{
						final Item I=(Item)toGo.elementAt(i);
						if(CMLib.commands().postDrop(M,I,true,true,false))
						{
							didSomething=true;
							R.show(M,I,CMMsg.MSG_OK_VISUAL,L("<T-NAME> is pulled away from <S-NAME> to the magnetic ground!"));
						}
					}
				}
			}
			if(didSomething)
			{
				R.recoverRoomStats();
				R.recoverRoomStats();
			}
		}
		return true;
	}

	protected boolean checked=false;
	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		if((!checked)
		&&(msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(affected instanceof Room))
		{
			checked=true;
			if(!CMLib.threads().isTicking(this,-1))
				CMLib.threads().startTickDown(this,Tickable.TICKID_SPELL_AFFECT,1);
		}
		super.executeMsg(host,msg);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final Room R=mob.location();
			if(R!=null)
			{
				if((R.domainType()!=Room.DOMAIN_INDOORS_CAVE)
				&&(R.domainType()!=Room.DOMAIN_OUTDOORS_CITY)
				&&(R.domainType()!=Room.DOMAIN_OUTDOORS_MOUNTAINS)
				&&(R.domainType()!=Room.DOMAIN_OUTDOORS_ROCKS))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room target=mob.location();
		if(target==null) return false;
		if((!auto)
		&&(mob.location().domainType()!=Room.DOMAIN_INDOORS_CAVE)
		&&(mob.location().domainType()!=Room.DOMAIN_OUTDOORS_CITY)
		&&(mob.location().domainType()!=Room.DOMAIN_OUTDOORS_MOUNTAINS)
		&&(mob.location().domainType()!=Room.DOMAIN_OUTDOORS_ROCKS))
		{
			mob.tell(L("This chant only works in caves, mountains, or rocky areas."));
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
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s) to the ground.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					for(int i=0;i<target.numInhabitants();i++)
					{
						final MOB M=target.fetchInhabitant(i);
						if((M!=null)&&(mob!=M))
							mob.location().show(mob,M,CMMsg.MASK_MALICIOUS|CMMsg.TYP_OK_VISUAL,null);
					}
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("The ground gains a powerful magnetic field!"));
					maliciousAffect(mob,target,asLevel,0,-1);
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) the ground, but the magic fades."));
		// return whether it worked
		return success;
	}
}
