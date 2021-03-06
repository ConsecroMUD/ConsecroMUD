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


public class Woods extends StdRoom
{
	@Override public String ID(){return "Woods";}
	public Woods()
	{
		super();
		name="the woods";
		basePhyStats.setWeight(3);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_WOODS;}


	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((msg.amITarget(this)||(msg.targetMinor()==CMMsg.TYP_ADVANCE)||(msg.targetMinor()==CMMsg.TYP_RETREAT))
		&&(!msg.source().isMonster())
		&&(CMLib.dice().rollPercentage()==1)
		&&(CMLib.dice().rollPercentage()==1)
		&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE)))
		{
			final Ability A=CMClass.getAbility("Disease_PoisonIvy");
			if((A!=null)&&(msg.source().fetchEffect(A.ID())==null))
				A.invoke(msg.source(),msg.source(),true,0);
		}
		super.executeMsg(myHost,msg);
	}

	public static final Integer[] resourceList={
		Integer.valueOf(RawMaterial.RESOURCE_WOOD),
		Integer.valueOf(RawMaterial.RESOURCE_PINE),
		Integer.valueOf(RawMaterial.RESOURCE_BALSA),
		Integer.valueOf(RawMaterial.RESOURCE_OAK),
		Integer.valueOf(RawMaterial.RESOURCE_MAPLE),
		Integer.valueOf(RawMaterial.RESOURCE_REDWOOD),
		Integer.valueOf(RawMaterial.RESOURCE_IRONWOOD),
		Integer.valueOf(RawMaterial.RESOURCE_SAP),
		Integer.valueOf(RawMaterial.RESOURCE_YEW),
		Integer.valueOf(RawMaterial.RESOURCE_HICKORY),
		Integer.valueOf(RawMaterial.RESOURCE_TEAK),
		Integer.valueOf(RawMaterial.RESOURCE_CEDAR),
		Integer.valueOf(RawMaterial.RESOURCE_ELM),
		Integer.valueOf(RawMaterial.RESOURCE_CHERRYWOOD),
		Integer.valueOf(RawMaterial.RESOURCE_BEECHWOOD),
		Integer.valueOf(RawMaterial.RESOURCE_WILLOW),
		Integer.valueOf(RawMaterial.RESOURCE_SYCAMORE),
		Integer.valueOf(RawMaterial.RESOURCE_SPRUCE),
		Integer.valueOf(RawMaterial.RESOURCE_FRUIT),
		Integer.valueOf(RawMaterial.RESOURCE_APPLES),
		Integer.valueOf(RawMaterial.RESOURCE_BERRIES),
		Integer.valueOf(RawMaterial.RESOURCE_PEACHES),
		Integer.valueOf(RawMaterial.RESOURCE_CHERRIES),
		Integer.valueOf(RawMaterial.RESOURCE_ORANGES),
		Integer.valueOf(RawMaterial.RESOURCE_LEMONS),
		Integer.valueOf(RawMaterial.RESOURCE_FUR),
		Integer.valueOf(RawMaterial.RESOURCE_NUTS),
		Integer.valueOf(RawMaterial.RESOURCE_HERBS),
		Integer.valueOf(RawMaterial.RESOURCE_HONEY),
		Integer.valueOf(RawMaterial.RESOURCE_VINE),
		Integer.valueOf(RawMaterial.RESOURCE_HIDE),
		Integer.valueOf(RawMaterial.RESOURCE_FEATHERS),
		Integer.valueOf(RawMaterial.RESOURCE_LEATHER)};
	public static final List<Integer> roomResources=new Vector<Integer>(Arrays.asList(resourceList));
	@Override public List<Integer> resourceChoices(){return Woods.roomResources;}
}
