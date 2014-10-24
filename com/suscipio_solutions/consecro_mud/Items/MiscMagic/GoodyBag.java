package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ImmortalOnly;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Pill;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MoneyLibrary;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MoneyLibrary.MoneyDenomination;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GoodyBag extends BagOfEndlessness implements ImmortalOnly
{
	@Override public String ID(){	return "GoodyBag";}
	boolean alreadyFilled=false;
	public GoodyBag()
	{
		super();
		setName("a goody bag");
		setDisplayText("a small bag is sitting here.");
		setDescription("A nice little bag to put your things in.");
		secretIdentity="The Immortal's Goody Bag";
		recoverPhyStats();
	}

	private void putInBag(Item I)
	{
		I.setContainer(this);
		if(owner() instanceof Room)
			((Room)owner()).addItem(I);
		else
		if(owner() instanceof MOB)
			((MOB)owner()).addItem(I);
		I.recoverPhyStats();
	}

	public void addMoney(double value)
	{
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((!alreadyFilled)&&(owner()!=null))
		{
			alreadyFilled=true;
			if(getContents().size()==0)
			{
				final List<String> V=CMLib.beanCounter().getAllCurrencies();
				for(int v=0;v<V.size();v++)
				{
					final String currency=V.get(v);
					final MoneyLibrary.MoneyDenomination[] DV=CMLib.beanCounter().getCurrencySet(currency);
					for (final MoneyDenomination element : DV)
					{
						final Coins C=CMLib.beanCounter().makeBestCurrency(currency,element.value,owner(),this);
						if(C!=null)	C.setNumberOfCoins(100);
					}
				}
				Item I=CMClass.getItem("GenSuperPill");
				I.setName(L("a training pill"));
				I.setDisplayText(L("A small round pill has been left here."));
				((Pill)I).setSpellList("train+1");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a practice pill"));
				I.setDisplayText(L("A tiny little pill has been left here."));
				((Pill)I).setSpellList("prac+1");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a quest point pill"));
				I.setDisplayText(L("A questy little pill has been left here."));
				((Pill)I).setSpellList("ques+1");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a 100 exp pill"));
				I.setDisplayText(L("An important little pill has been left here."));
				((Pill)I).setSpellList("expe+100");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a 500 exp pill"));
				I.setDisplayText(L("An important little pill has been left here."));
				((Pill)I).setSpellList("expe+500");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a 1000 exp pill"));
				I.setDisplayText(L("An important little pill has been left here."));
				((Pill)I).setSpellList("expe+1000");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a 2000 exp pill"));
				I.setDisplayText(L("An important little pill has been left here."));
				((Pill)I).setSpellList("expe+2000");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a 5000 exp pill"));
				I.setDisplayText(L("An important little pill has been left here."));
				((Pill)I).setSpellList("expe+5000");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a strength pill"));
				I.setDisplayText(L("An strong little pill has been left here."));
				((Pill)I).setSpellList("str+1");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("an intelligence pill"));
				I.setDisplayText(L("An smart little pill has been left here."));
				((Pill)I).setSpellList("int+1");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a wisdom pill"));
				I.setDisplayText(L("A wise little pill has been left here."));
				((Pill)I).setSpellList("wis+1");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a dexterity pill"));
				I.setDisplayText(L("A quick little pill has been left here."));
				((Pill)I).setSpellList("dex+1");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a constitution pill"));
				I.setDisplayText(L("A nutricious little pill has been left here."));
				((Pill)I).setSpellList("con+1");
				putInBag(I);
				I=CMClass.getItem("GenSuperPill");
				I.setName(L("a charisma pill"));
				I.setDisplayText(L("A pretty little pill has been left here."));
				((Pill)I).setSpellList("cha+1");
				putInBag(I);
			}
		}
		super.executeMsg(myHost,msg);
	}
}
