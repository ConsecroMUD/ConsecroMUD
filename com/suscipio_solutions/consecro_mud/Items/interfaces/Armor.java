package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


public interface Armor extends Item
{
	public static final String[] LAYERMASK_DESCS={"SEETHROUGH","MULTIWEAR"};
	public static final short LAYERMASK_SEETHROUGH=(short)1;
	public static final short LAYERMASK_MULTIWEAR=(short)2;
	public short getClothingLayer();
	public void setClothingLayer(short newLayer);
	public short getLayerAttributes();
	public void setLayerAttributes(short newAttributes);
	
	public SizeDeviation getSizingDeviation(MOB mob);
	
	public enum SizeDeviation
	{
		TOO_LARGE,
		TOO_SMALL,
		FITS
	}
}
