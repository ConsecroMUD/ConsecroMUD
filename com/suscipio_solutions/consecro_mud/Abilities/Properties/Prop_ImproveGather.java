package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_ImproveGather extends Property
{
	@Override public String ID() { return "Prop_ImproveGather"; }
	@Override public String name(){ return "Improve Gathering Skills";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS|Ability.CAN_AREAS|Ability.CAN_ROOMS;}
	protected MaskingLibrary.CompiledZapperMask   mask = null;
	protected String[] improves=new String[] {"ALL"};
	protected int improvement=2;

	@Override
	public String accountForYourself()
	{ return "Improves common skills "+CMParms.toStringList(improves)+". Gain: "+improvement; }


	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		this.improvement=CMParms.getParmInt(newText, "AMT", improvement);
		final String maskStr=CMParms.getParmStr(newText, "MASK", "");
		if((maskStr==null)||(maskStr.length()==0))
			mask=null;
		else
			mask=CMLib.masking().maskCompile(maskStr);
		final String skillStr=CMParms.getParmStr(newText, "SKILLS", "ALL");
		final List<String> skills=CMParms.parseCommas(skillStr.toUpperCase().trim(), true);
		improves=skills.toArray(new String[0]);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((msg.tool() instanceof Ability)
		&&(CMath.bset(((Ability)msg.tool()).classificationCode(),Ability.DOMAIN_GATHERINGSKILL)
		&&(improvement != ((Ability)msg.tool()).abilityCode())
		&&(msg.source().location()!=null)
		&&(msg.source()==affected)||(msg.source().location()==affected)||(msg.source().location().getArea()==affected)
			||((affected instanceof Item)&&(((Item)affected).owner()==msg.source())&&(!((Item)affected).amWearingAt(Wearable.IN_INVENTORY))))
		&&(msg.source().fetchEffect(msg.tool().ID())==msg.tool())
		&&(CMParms.contains(improves, "ALL")||CMParms.contains(improves, msg.tool().ID().toUpperCase()))
		&&((mask==null)||(CMLib.masking().maskCheck(mask, msg.source(), true))))
		{
			((Ability)msg.tool()).setAbilityCode(improvement);
		}
		super.executeMsg(myHost, msg);
	}
}
