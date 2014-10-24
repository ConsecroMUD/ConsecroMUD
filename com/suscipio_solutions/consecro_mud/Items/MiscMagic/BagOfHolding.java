package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.Basic.SmallSack;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;




public class BagOfHolding extends SmallSack implements MiscMagic
{
	@Override public String ID(){	return "BagOfHolding";}
	public BagOfHolding()
	{
		super();

		setName("a small sack");
		setDisplayText("a small black sack is crumpled up here.");
		setDescription("A nice silk sack to put your things in.");
		secretIdentity="A Bag of Holding";
		basePhyStats().setLevel(1);
		capacity=1000;
		baseGoldValue=25000;
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		recoverPhyStats();
	}



	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		if((msg.targetMinor()==CMMsg.TYP_PUT)
		&&(msg.target() instanceof BagOfHolding)
		&&(msg.tool() instanceof BagOfHolding))
		{
			((Item)msg.target()).destroy();
			((Item)msg.tool()).destroy();
			msg.source().tell(L("The bag implodes in your hands!"));
		}
	}

	@Override
	public void recoverPhyStats()
	{
		basePhyStats().setWeight(0);
		super.recoverPhyStats();
		basePhyStats().setWeight(-recursiveWeight());
		super.recoverPhyStats();
	}
}
