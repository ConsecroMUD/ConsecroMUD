package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
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
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_SummonTree extends Chant_SummonPlants
{
	@Override public String ID() { return "Chant_SummonTree"; }
	private final static String localizedName = CMLib.lang().L("Summon Tree");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTGROWTH;}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	protected int material=0;
	protected int oldMaterial=-1;

	@Override
	protected Item buildMyPlant(MOB mob, Room room)
	{
		final int code=material&RawMaterial.RESOURCE_MASK;
		final Item newItem=CMClass.getBasicItem("GenItem");
		final String name=CMLib.english().startWithAorAn(RawMaterial.CODES.NAME(code).toLowerCase()+" tree");
		newItem.setName(name);
		newItem.setDisplayText(L("@x1 grows here.",newItem.name()));
		newItem.setDescription("");
		newItem.basePhyStats().setWeight(10000);
		CMLib.flags().setGettable(newItem,false);
		newItem.setMaterial(material);
		newItem.setSecretIdentity(mob.Name());
		newItem.setMiscText(newItem.text());
		room.addItem(newItem);
		final Chant_SummonTree newChant=new Chant_SummonTree();
		newItem.basePhyStats().setLevel(10+newChant.getX1Level(mob));
		newItem.setExpirationDate(0);
		room.showHappens(CMMsg.MSG_OK_ACTION,L("a tall, healthy @x1 tree sprouts up.",RawMaterial.CODES.NAME(code).toLowerCase()));
		room.recoverPhyStats();
		newChant.PlantsLocation=room;
		newChant.littlePlants=newItem;
		if(CMLib.law().doesOwnThisProperty(mob,room))
		{
			newChant.setInvoker(mob);
			newChant.setMiscText(mob.Name());
			newItem.addNonUninvokableEffect(newChant);
		}
		else
			newChant.beneficialAffect(mob,newItem,0,(newChant.adjustedLevel(mob,0)*240)+450);
		room.recoverPhyStats();
		return newItem;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if((PlantsLocation==null)||(littlePlants==null)) return false;
		if(PlantsLocation.myResource()!=littlePlants.material())
		{
			oldMaterial=PlantsLocation.myResource();
			PlantsLocation.setResource(littlePlants.material());
		}
		for(int i=0;i<PlantsLocation.numInhabitants();i++)
		{
			final MOB M=PlantsLocation.fetchInhabitant(i);
			if(M.fetchEffect("Chopping")!=null)
			{
				unInvoke();
				break;
			}
		}
		return true;
	}

	@Override
	public void unInvoke()
	{
		if((canBeUninvoked())&&(PlantsLocation!=null)&&(oldMaterial>=0))
			PlantsLocation.setResource(oldMaterial);
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{

		material=RawMaterial.RESOURCE_OAK;
		if((mob.location().myResource()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_WOODEN)
			material=mob.location().myResource();
		else
		{
			final List<Integer> V=mob.location().resourceChoices();
			final Vector V2=new Vector();
			if(V!=null)
			for(int v=0;v<V.size();v++)
			{
				if(((V.get(v).intValue()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_WOODEN)
				&&((V.get(v).intValue())!=RawMaterial.RESOURCE_WOOD))
					V2.addElement(V.get(v));
			}
			if(V2.size()>0)
				material=((Integer)V2.elementAt(CMLib.dice().roll(1,V2.size(),-1))).intValue();
		}

		return super.invoke(mob,commands,givenTarget,auto,asLevel);
	}
}
