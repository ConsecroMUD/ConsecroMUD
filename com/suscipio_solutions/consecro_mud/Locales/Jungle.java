package com.suscipio_solutions.consecro_mud.Locales;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class Jungle extends StdRoom
{
	@Override public String ID(){return "Jungle";}
	public Jungle()
	{
		super();
		name="the jungle";
		basePhyStats.setWeight(3);
		recoverPhyStats();
		climask=Places.CLIMASK_WET|CLIMASK_HOT;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_JUNGLE;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((msg.amITarget(this)||(msg.targetMinor()==CMMsg.TYP_ADVANCE)||(msg.targetMinor()==CMMsg.TYP_RETREAT))
		   &&(!msg.source().isMonster())
		   &&(msg.source().curState().getHitPoints()<msg.source().maxState().getHitPoints())
		   &&(CMLib.dice().rollPercentage()==1)
		   &&(CMLib.dice().rollPercentage()==1)
		   &&(!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE)))
		{
			Ability A=null;
			if(CMLib.dice().rollPercentage()>50)
				A=CMClass.getAbility("Disease_Gonorrhea");
			else
				A=CMClass.getAbility("Disease_Malaria");
			if((A!=null)&&(msg.source().fetchEffect(A.ID())==null))
				A.invoke(msg.source(),msg.source(),true,0);
		}
		super.executeMsg(myHost,msg);
	}

	public static final Integer[] resourceList={
		Integer.valueOf(RawMaterial.RESOURCE_PLUMS),
		Integer.valueOf(RawMaterial.RESOURCE_PINEAPPLES),
		Integer.valueOf(RawMaterial.RESOURCE_COCONUTS),
		Integer.valueOf(RawMaterial.RESOURCE_BANANAS),
		Integer.valueOf(RawMaterial.RESOURCE_LIMES),
		Integer.valueOf(RawMaterial.RESOURCE_JADE),
		Integer.valueOf(RawMaterial.RESOURCE_SCALES),
		Integer.valueOf(RawMaterial.RESOURCE_HEMP),
		Integer.valueOf(RawMaterial.RESOURCE_SILK),
		Integer.valueOf(RawMaterial.RESOURCE_FRUIT),
		Integer.valueOf(RawMaterial.RESOURCE_APPLES),
		Integer.valueOf(RawMaterial.RESOURCE_BERRIES),
		Integer.valueOf(RawMaterial.RESOURCE_ORANGES),
		Integer.valueOf(RawMaterial.RESOURCE_COFFEEBEANS),
		Integer.valueOf(RawMaterial.RESOURCE_HERBS),
		Integer.valueOf(RawMaterial.RESOURCE_VINE),
		Integer.valueOf(RawMaterial.RESOURCE_LEMONS),
		Integer.valueOf(RawMaterial.RESOURCE_FUR),
		Integer.valueOf(RawMaterial.RESOURCE_FEATHERS)
	};
	public static final List<Integer> roomResources=new Vector<Integer>(Arrays.asList(resourceList));
	@Override public List<Integer> resourceChoices(){return Jungle.roomResources;}
}
