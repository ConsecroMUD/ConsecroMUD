package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;




public class CatalogCatNext extends StdWebMacro
{
	@Override public String name() { return "CatalogCatNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		String last=httpReq.getUrlParameter("CATACAT");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("CATACAT");
			return "";
		}
		final boolean mobs=parms.containsKey("MOBS")||parms.containsKey("MOB");
		if(!httpReq.getRequestObjects().containsKey("CATACATS"+(mobs?"M":"I")))
			httpReq.getRequestObjects().put("CATACATS"+(mobs?"M":"I"), mobs?CMLib.catalog().getMobCatalogCatagories():CMLib.catalog().getItemCatalogCatagories());
		final String[] cats=(String[])httpReq.getRequestObjects().get("CATACATS"+(mobs?"M":"I"));
		if(parms.containsKey("WIDTH"))
			return ""+100/(cats.length+1);
		String lastID=null;
		if((last!=null)&&(last.equalsIgnoreCase("")))
			last="UNCATEGORIZED";
		for(String cat : cats)
		{
			if(cat.length()==0)
				cat="UNCATEGORIZED";
			if((last==null)||((lastID!=null)&&(last.equals(lastID))&&(!cat.equals(lastID))))
			{
				httpReq.addFakeUrlParameter("CATACAT",cat);
				return "";
			}
			lastID=cat;
		}
		httpReq.addFakeUrlParameter("CATACAT","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
