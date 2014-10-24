package com.suscipio_solutions.consecro_mud.Items.ClanItems;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ClanItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class StdClanSpecialItem extends StdClanItem
{
	@Override public String ID(){	return "StdClanSpecialItem";}
	private Behavior B=null;
	private String flag="";

	public StdClanSpecialItem()
	{
		super();

		setName("a clan item");
		basePhyStats.setWeight(1);
		setDisplayText("an item belonging to a clan is here.");
		setDescription("");
		secretIdentity="";
		baseGoldValue=1;
		setCIType(ClanItem.CI_SPECIALOTHER);
		material=RawMaterial.RESOURCE_PINE;
		recoverPhyStats();
	}

	@Override
	public void setReadableText(String text)
	{
		if((text.equalsIgnoreCase("GOOD"))
		||(text.equalsIgnoreCase("EVIL")))
			flag=text;
		else
			super.setReadableText(text);
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		if(((ciType()==ClanItem.CI_SPECIALSCALES)||(ciType()==ClanItem.CI_SPECIALTAXER))
		&&(owner() instanceof MOB)
		&&(clanID().length()>0)
		&&(((MOB)owner()).isMonster())
		&&((((MOB)owner()).getClanRole(clanID())!=null)
		&&(CMLib.flags().aliveAwakeMobile((MOB)owner(),true))
		&&(!CMLib.flags().isAnimalIntelligence((MOB)owner())))
		&&(B!=null))
			B.executeMsg(owner(),msg);
	}
	@Override
	public boolean okMessage(Environmental affecting, CMMsg msg)
	{
		if(!super.okMessage(affecting,msg))
			return false;
		if((ciType()==ClanItem.CI_SPECIALTAXER)
		&&(B!=null)
		&&(owner() instanceof MOB))
			return B.okMessage(owner(),msg);
		return true;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((tickID==Tickable.TICKID_CLANITEM)
		&&(owner() instanceof MOB)
		&&(clanID().length()>0)
		&&(((MOB)owner()).isMonster())
		&&((((MOB)owner()).getClanRole(clanID())!=null)
		&&(CMLib.flags().aliveAwakeMobileUnbound((MOB)owner(),true))
		&&(!CMLib.flags().isAnimalIntelligence((MOB)owner()))))
		{
			switch(ciType())
			{
			case ClanItem.CI_SPECIALSCAVENGER:
				{
					final MOB mob=(MOB)owner();
					final Room R=((MOB)owner()).location();
					if(R!=null)
					{
						final Item I=R.getRandomItem();
						if((I!=null)&&(I.container()==null))
							CMLib.commands().postGet(mob,null,I,false);
					}
					break;
				}
			case ClanItem.CI_SPECIALSCALES:
				{
					if(((B==null))
					||(!flag.equalsIgnoreCase("EVIL")&&(!B.ID().equals("GoodExecutioner")))
					||(flag.equalsIgnoreCase("EVIL")&&(!B.ID().equals("EvilExecutioner"))))
					{
						if(flag.equalsIgnoreCase("EVIL"))
							B=CMClass.getBehavior("EvilExecutioner");
						else
							B=CMClass.getBehavior("GoodExecutioner");
					}
					break;
				}
			case ClanItem.CI_SPECIALTAXER:
				{
					if((B==null)||(!B.ID().equals("TaxCollector")))
						B=CMClass.getBehavior("TaxCollector");
					if(B!=null) B.tick(owner(),Tickable.TICKID_MOB);
					break;
				}
			}
		}
		return true;
	}
}
