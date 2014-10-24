package com.suscipio_solutions.consecro_mud.Items.ClanItems;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ClanItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class StdClanCommonContainer extends StdClanContainer
{
	@Override public String ID(){	return "StdClanCommonContainer";}
	protected int workDown=0;
	public StdClanCommonContainer()
	{
		super();

		setName("a clan workers container");
		basePhyStats.setWeight(1);
		setDisplayText("an workers container belonging to a clan is here.");
		setDescription("");
		secretIdentity="";
		baseGoldValue=1;
		capacity=100;
		setCIType(ClanItem.CI_GATHERITEM);
		material=RawMaterial.RESOURCE_OAK;
		recoverPhyStats();
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((tickID==Tickable.TICKID_CLANITEM)
		&&(owner() instanceof MOB)
		&&(((MOB)owner()).isMonster())
		&&(readableText().length()>0)
		&&(((MOB)owner()).getClanRole(clanID())!=null)
		&&((--workDown)<=0)
		&&(!CMLib.flags().isAnimalIntelligence((MOB)owner())))
		{
			workDown=CMLib.dice().roll(1,5,0);
			final MOB M=(MOB)owner();
			if(M.fetchEffect(readableText())==null)
			{
				final Ability A=CMClass.getAbility(readableText());
				if((A!=null)&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_COMMON_SKILL))
				{
					A.setProficiency(100);
					if(M.numItems()>1)
					{
						Item I=null;
						int tries=0;
						while((I==null)&&((++tries)<20))
						{
							I=M.getRandomItem();
							if((I==null)||(I==this)||(!I.amWearingAt(Wearable.IN_INVENTORY)))
								I=null;
						}
						final Vector V=new Vector();
						if(I!=null)	V.addElement(I.name());
						A.invoke(M,V,null,false,phyStats().level());
					}
					else
						A.invoke(M,new Vector(),null,false,phyStats().level());
				}

			}
		}
		return true;
	}
}
