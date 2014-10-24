package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_SummonFire extends Chant
{
	@Override public String ID() { return "Chant_SummonFire"; }
	private final static String localizedName = CMLib.lang().L("Summon Fire");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	protected Room FireLocation=null;
	protected Item littleFire=null;
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_DEEPMAGIC;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public long flags(){return Ability.FLAG_HEATING|Ability.FLAG_FIREBASED;}

	@Override
	public void unInvoke()
	{
		if(FireLocation==null)
			return;
		if(littleFire==null)
			return;
		if(canBeUninvoked())
			FireLocation.showHappens(CMMsg.MSG_OK_VISUAL,L("The little magical fire goes out."));
		super.unInvoke();
		if(canBeUninvoked())
		{
			final Item fire=littleFire; // protects against uninvoke loops!
			littleFire=null;
			fire.destroy();
			FireLocation.recoverRoomStats();
			FireLocation=null;
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((((mob.location().domainType()&Room.INDOORS)>0))&&(!auto))
		{
			mob.tell(L("You must be outdoors for this chant to work."));
			return false;
		}
		if(((mob.location().domainType()==Room.DOMAIN_OUTDOORS_CITY)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_SPACEPORT)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_AIR)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE))
		   &&(!auto))
		{
			mob.tell(L("This magic will not work here."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s) for fire.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Item I=CMClass.getItem("GenItem");
				I.basePhyStats().setWeight(50);
				I.setName(L("a magical campfire"));
				I.setDisplayText(L("A roaring magical campfire has been built here."));
				I.setDescription(L("It consists of magically burning flames, consuming no fuel."));
				I.recoverPhyStats();
				I.setMaterial(RawMaterial.RESOURCE_NOTHING);
				I.setMiscText(I.text());
				final Ability B=CMClass.getAbility("Burning");
				I.addNonUninvokableEffect(B);

				mob.location().addItem(I);
				mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("Suddenly, a little magical campfire begins burning here."));
				FireLocation=mob.location();
				littleFire=I;
				beneficialAffect(mob,I,asLevel,0);
				mob.location().recoverPhyStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s) for fire, but nothing happens."));

		// return whether it worked
		return success;
	}
}
