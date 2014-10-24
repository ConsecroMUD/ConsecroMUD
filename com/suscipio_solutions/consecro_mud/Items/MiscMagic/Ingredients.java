package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Decayable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Ingredients extends BagOfEndlessness
{
	@Override public String ID(){	return "Ingredients";}
	boolean alreadyFilled=false;
	public Ingredients()
	{
		super();
		setName("an ingredients bag");
		secretIdentity="The Immortal's Secret Ingredient Bag";
		recoverPhyStats();
	}

	protected Item makeResource(String name, int type)
	{
		Item I=null;
		if(((type&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_FLESH)
		||((type&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_VEGETATION))
			I=CMClass.getItem("GenFoodResource");
		else
		if((type&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_LIQUID)
			I=CMClass.getItem("GenLiquidResource");
		else
			I=CMClass.getItem("GenResource");
		I.setName(name);
		I.setDisplayText(L("@x1 has been left here.",name));
		I.setDescription(L("It looks like @x1",name));
		I.setMaterial(type);
		I.setBaseValue(RawMaterial.CODES.VALUE(type));
		I.basePhyStats().setWeight(1);
		CMLib.materials().addEffectsToResource(I);
		I.recoverPhyStats();
		I.setContainer(this);
		if(I instanceof Decayable)
		{
			((Decayable)I).setDecayTime(0);
			final Ability A=I.fetchEffect("Poison_Rotten");
			if(A!=null) I.delEffect(A);
		}
		if(owner() instanceof Room)
			((Room)owner()).addItem(I);
		else
		if(owner() instanceof MOB)
			((MOB)owner()).addItem(I);
		return I;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((!alreadyFilled)&&(owner()!=null))
		{
			alreadyFilled=true;
			if(getContents().size()==0)
			for(final int rsc : RawMaterial.CODES.ALL())
				makeResource(RawMaterial.CODES.NAME(rsc).toLowerCase(),rsc);
		}
		else
		if(msg.amITarget(this)
		&&(msg.tool() instanceof Decayable)
		&&(msg.tool() instanceof Item)
		&&(((Item)msg.tool()).container()==this)
		&&(((Item)msg.tool()).owner() !=null))
		{
			((Decayable)msg.tool()).setDecayTime(0);
			final Ability A=((Item)msg.tool()).fetchEffect("Poison_Rotten");
			if(A!=null) ((Item)msg.tool()).delEffect(A);
		}
		super.executeMsg(myHost,msg);
	}
}
