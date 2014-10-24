package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_GrowClub extends Chant
{
	@Override public String ID() { return "Chant_GrowClub"; }
	private final static String localizedName = CMLib.lang().L("Grow Club");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTGROWTH;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((mob.isInCombat())&&(mob.fetchWieldedItem()==null))
			{
				final Room R=mob.location();
				if((R!=null)
				&&(R.findItem(null,"club")==null)
				&&((R.domainType()==Room.DOMAIN_OUTDOORS_WOODS)
				||((R.myResource()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_WOODEN)
				||(R.domainType()==Room.DOMAIN_OUTDOORS_JUNGLE)))
					return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((mob.location().domainType()!=Room.DOMAIN_OUTDOORS_WOODS)
		&&((mob.location().myResource()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_WOODEN)
		&&(mob.location().domainType()!=Room.DOMAIN_OUTDOORS_JUNGLE))
		{
			mob.tell(L("This magic will not work here."));
			return false;
		}
		int material=RawMaterial.RESOURCE_OAK;
		if((mob.location().myResource()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_WOODEN)
			material=mob.location().myResource();
		else
		{
			final List<Integer> V=mob.location().resourceChoices();
			final Vector V2=new Vector();
			if(V!=null)
			for(int v=0;v<V.size();v++)
			{
				if((V.get(v).intValue()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_WOODEN)
					V2.addElement(V.get(v));
			}
			if(V2.size()>0)
				material=((Integer)V2.elementAt(CMLib.dice().roll(1,V2.size(),-1))).intValue();
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s) to the trees.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Weapon newItem=CMClass.getWeapon("GenWeapon");
				newItem.setName(L("@x1 club",RawMaterial.CODES.NAME(material).toLowerCase()));
				newItem.setName(CMLib.english().startWithAorAn(newItem.Name()));
				newItem.setDisplayText(L("@x1 sits here",newItem.name()));
				newItem.setDescription(L("It looks like the limb of a tree."));
				newItem.setMaterial(material);
				newItem.basePhyStats().setWeight(10);
				final int level=mob.phyStats().level();
				newItem.basePhyStats().setLevel(level);
				newItem.basePhyStats().setAttackAdjustment(0);
				int damage=6;
				try{ damage=(((level+(2*super.getXLEVELLevel(mob)))-1)/2)+2;}catch(final Exception t){}
				if(damage<6) damage=6;
				newItem.basePhyStats().setDamage(damage+super.getX1Level(mob));
				newItem.recoverPhyStats();
				newItem.setBaseValue(0);
				newItem.setWeaponClassification(Weapon.CLASS_BLUNT);
				newItem.setWeaponType(Weapon.TYPE_BASHING);
				newItem.setMiscText(newItem.text());
				mob.location().addItem(newItem,ItemPossessor.Expire.Resource);
				mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("A good looking club grows out of a tree and drops."));
				mob.location().recoverPhyStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s) to the trees, but nothing happens."));

		// return whether it worked
		return success;
	}
}
