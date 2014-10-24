package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DoorKey;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class GenKey extends GenItem implements DoorKey
{
	@Override public String ID(){	return "GenKey";}
	public GenKey()
	{
		super();
		setName("a key");
		setDisplayText("a key has been left here.");
		setDescription("");
		setMaterial(RawMaterial.RESOURCE_IRON);
	}


	@Override public boolean isGeneric(){return true;}

	@Override public void setKey(String keyName){readableText=keyName;}
	@Override public String getKey(){return readableText;}
}
