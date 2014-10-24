package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MusicalInstrument;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class GenPiano extends GenRideable implements MusicalInstrument
{
	@Override public String ID(){	return "GenPiano";}
	public GenPiano()
	{
		super();
		setName("a generic piano");
		setDisplayText("a generic piano sits here.");
		setDescription("");
		baseGoldValue=1015;
		basePhyStats().setLevel(1);
		recoverPhyStats();
		basePhyStats().setWeight(2000);
		rideBasis=Rideable.RIDEABLE_SIT;
		riderCapacity=2;
		setMaterial(RawMaterial.RESOURCE_OAK);
	}

	@Override public void recoverPhyStats(){CMLib.flags().setReadable(this,false); super.recoverPhyStats();}
	@Override public int instrumentType(){return CMath.s_int(readableText);}
	@Override public void setInstrumentType(int type){readableText=(""+type);}

}
