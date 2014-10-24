package com.suscipio_solutions.consecro_mud.Items.Armor;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;



public class StdThinArmor extends StdItem implements Armor
{
	@Override public String ID(){	return "StdThinArmor";}
	int sheath=0;
	short layer=0;
	short layerAttributes=0;

	public StdThinArmor()
	{
		super();

		setName("a piece of armor");
		setDisplayText("a piece of armor here.");
		setDescription("Thick padded leather with strips of metal interwoven.");
		properWornBitmap=Wearable.WORN_EYES;
		wornLogicalAnd=false;
		basePhyStats().setArmor(1);
		basePhyStats().setAbility(0);
		baseGoldValue=150;
		setUsesRemaining(100);
		recoverPhyStats();
	}

	@Override
	public void setUsesRemaining(int newUses)
	{
		if(newUses==Integer.MAX_VALUE)
			newUses=100;
		super.setUsesRemaining(newUses);
	}
	@Override public short getClothingLayer(){return layer;}
	@Override public void setClothingLayer(short newLayer){layer=newLayer;}
	@Override public short getLayerAttributes(){return layerAttributes;}
	@Override public void setLayerAttributes(short newAttributes){layerAttributes=newAttributes;}

	@Override
	public boolean canWear(MOB mob, long where)
	{
		if(where==0) 
			return (whereCantWear(mob)==0);
		if((rawProperLocationBitmap()&where)!=where)
			return false;
		return mob.freeWearPositions(where,getClothingLayer(),getLayerAttributes())>0;
	}


	@Override
	public SizeDeviation getSizingDeviation(MOB mob)
	{
		return SizeDeviation.FITS;
	}

	@Override
	public boolean subjectToWearAndTear()
	{
		return false;
	}
	@Override
	public String secretIdentity()
	{
		String id=super.secretIdentity();
		if(phyStats().ability()>0)
			id=name()+" +"+phyStats().ability()+((id.length()>0)?"\n\r":"")+id;
		else
		if(phyStats().ability()<0)
			id=name()+" "+phyStats().ability()+((id.length()>0)?"\n\r":"")+id;
		return id+"\n\r"+L("Base Protection: @x1",""+phyStats().armor());
	}
}
