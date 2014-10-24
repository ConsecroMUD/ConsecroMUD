package com.suscipio_solutions.consecro_mud.WebMacros.grinder;

import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.AbilityComponent;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class GrinderComponent
{
	public String name() { return "GrinderComponent"; }

	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final String last=httpReq.getUrlParameter("COMPONENT");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			final String fixedCompID=last.replace(' ','_').toUpperCase();
			final List<AbilityComponent> set=new Vector<AbilityComponent>();
			int posDex=1;
			while(httpReq.isUrlParameter(fixedCompID+"_PIECE_CONNECTOR_"+posDex) && httpReq.getUrlParameter(fixedCompID+"_PIECE_CONNECTOR_"+posDex).trim().length()>0)
			{
				final String mask=httpReq.getUrlParameter(fixedCompID+"_PIECE_MASK_"+posDex);
				final String str=httpReq.getUrlParameter(fixedCompID+"_PIECE_STRING_"+posDex);
				final String amt=httpReq.getUrlParameter(fixedCompID+"_PIECE_AMOUNT_"+posDex);
				final String conn=httpReq.getUrlParameter(fixedCompID+"_PIECE_CONNECTOR_"+posDex);
				final String loc=httpReq.getUrlParameter(fixedCompID+"_PIECE_LOCATION_"+posDex);
				final String type=httpReq.getUrlParameter(fixedCompID+"_PIECE_TYPE_"+posDex);
				final String consumed=httpReq.getUrlParameter(fixedCompID+"_PIECE_CONSUMED_"+posDex);
				if(!conn.equalsIgnoreCase("DELETE"))
				{
					final AbilityComponent able=(AbilityComponent)CMClass.getCommon("DefaultAbilityComponent");
					able.setAmount(CMath.s_int(amt));
					if(posDex==1)
						able.setConnector(AbilityComponent.CompConnector.AND);
					else
						able.setConnector(AbilityComponent.CompConnector.valueOf(conn));
					able.setConsumed((consumed!=null)&&(consumed.equalsIgnoreCase("on")||consumed.equalsIgnoreCase("checked")));
					able.setLocation(AbilityComponent.CompLocation.valueOf(loc));
					able.setMask(mask);
					able.setType(AbilityComponent.CompType.valueOf(type), str);
					set.add(able);
				}
				posDex++;
			}

			if(CMLib.ableMapper().getAbilityComponentMap().containsKey(last.toUpperCase().trim()))
			{
				final List<AbilityComponent> xset=CMLib.ableMapper().getAbilityComponentMap().get(last.toUpperCase().trim());
				xset.clear();
				xset.addAll(set);
			}
			else
				CMLib.ableMapper().getAbilityComponentMap().put(last.toUpperCase().trim(),set);
			CMLib.ableMapper().alterAbilityComponentFile(last.toUpperCase().trim(),false);
		}
		return "";
	}
}
