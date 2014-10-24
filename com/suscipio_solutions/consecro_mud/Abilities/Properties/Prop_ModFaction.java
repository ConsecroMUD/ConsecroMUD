package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.LinkedList;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_ModFaction extends Property
{
	@Override public String ID() { return "Prop_ModFaction"; }
	@Override public String name(){ return "Modifying Faction Gained";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS|Ability.CAN_AREAS|Ability.CAN_ROOMS;}
	protected String operationFormula = "";
	protected String factionID = "";
	protected boolean reactions=false;
	protected boolean gainonly=false;
	protected boolean lossonly=false;
	protected LinkedList<CMath.CompiledOperation> operation = null;
	protected MaskingLibrary.CompiledZapperMask   mask = null;

	@Override
	public String accountForYourself()
	{
		final Faction F=(factionID.length()>0)?CMLib.factions().getFaction(factionID):null;
		final String gainOrLoss=(gainonly)?"gained ":lossonly?"lost ":"";
		final String factionName=
			(factionID.length()==0)?"any faction":
				reactions?"certain factions":
				((F==null)?"some faction":F.name());
		return "Modifies "+gainOrLoss+"faction with "+factionName+": "+operationFormula;
	}

	public int translateAmount(int amount, String val)
	{
		if(amount<0) amount=-amount;
		if(val.endsWith("%"))
			return (int)Math.round(CMath.mul(amount,CMath.div(CMath.s_int(val.substring(0,val.length()-1)),100)));
		return CMath.s_int(val);
	}

	public String translateNumber(String val)
	{
		if(val.endsWith("%"))
			return "@x1 * (" + val.substring(0,val.length()-1) + " / 100)";
		return Integer.toString(CMath.s_int(val));
	}

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		operation = null;
		factionID = "";
		mask=null;
		gainonly=false;
		lossonly=false;
		reactions=false;
		String s=newText.trim();
		int x=s.indexOf(';');
		if(x>=0)
		{
			mask=CMLib.masking().getPreCompiledMask(s.substring(x+1).trim());
			s=s.substring(0,x).trim();
		}
		x=s.indexOf(':');
		if(x>=0)
		{
			factionID=s.substring(0,x).trim();
			if(factionID.startsWith("+"))
			{ gainonly=true; factionID=factionID.substring(1).trim();}
			else
			if(factionID.startsWith("-"))
			{ lossonly=true; factionID=factionID.substring(1).trim();}
			s=s.substring(x+1).trim();
		}
		if(factionID.trim().equalsIgnoreCase("REACTION"))
		{
			factionID="";
			reactions=true;
		}

		operationFormula="Amount "+s;
		if(s.startsWith("="))
			operation = CMath.compileMathExpression(translateNumber(s.substring(1)).trim());
		else
		if(s.startsWith("+"))
			operation = CMath.compileMathExpression("@x1 + "+translateNumber(s.substring(1)).trim());
		else
		if(s.startsWith("-"))
			operation = CMath.compileMathExpression("@x1 - "+translateNumber(s.substring(1)).trim());
		else
		if(s.startsWith("*"))
			operation = CMath.compileMathExpression("@x1 * "+translateNumber(s.substring(1)).trim());
		else
		if(s.startsWith("/"))
			operation = CMath.compileMathExpression("@x1 / "+translateNumber(s.substring(1)).trim());
		else
		if(s.startsWith("(")&&(s.endsWith(")")))
		{
			operationFormula="Amount ="+s;
			operation = CMath.compileMathExpression(s);
		}
		else
			operation = CMath.compileMathExpression(translateNumber(s.trim()));
		operationFormula=CMStrings.replaceAll(operationFormula, "@x1", "Amount");
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((msg.sourceMinor()==CMMsg.TYP_FACTIONCHANGE)
		&&(operation != null)
		&&(((msg.target()==affected)&&(affected instanceof MOB))
		   ||((affected instanceof Item)
			   &&(msg.source()==((Item)affected).owner())
			   &&(!((Item)affected).amWearingAt(Wearable.IN_INVENTORY)))
		   ||(affected instanceof Room)
		   ||(affected instanceof Area))
		&&(msg.value()!=Integer.MAX_VALUE)
		&&(msg.value()!=Integer.MIN_VALUE)
		&&((!gainonly)||(msg.value()>0))
		&&((!lossonly)||(msg.value()<0))
		&&((factionID.length()==0)||(msg.othersMessage().equalsIgnoreCase(factionID)))
		)
		{
			if(reactions)
			{
				final Faction F=CMLib.factions().getFaction(msg.othersMessage());
				if((F==null)||(!F.reactions().hasMoreElements()))
					return super.okMessage(myHost,msg);
			}
			if(mask!=null)
			{
				if(affected instanceof Item)
				{
					if((msg.target()==null)||(!(msg.target() instanceof MOB))||(!CMLib.masking().maskCheck(mask,msg.target(),true)))
						return super.okMessage(myHost,msg);
				}
				else
				if(!CMLib.masking().maskCheck(mask,msg.source(),true))
					return super.okMessage(myHost,msg);
			}
			msg.setValue((int)Math.round(CMath.parseMathExpression(operation, new double[]{msg.value()}, 0.0)));
		}
		return super.okMessage(myHost,msg);
	}
}
