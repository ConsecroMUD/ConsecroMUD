package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Arrays;
import java.util.Comparator;

import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.CatalogLibrary;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class CatalogItemNext extends StdWebMacro
{
	@Override public String name() { return "CatalogItemNext"; }
	@Override public boolean isAdminMacro()   {return true;}
	static final String[] DATA={
								"CATALOG_ITEM_NAME",
								"CATALOG_ITEM_USAGE",
								"CATALOG_ITEM_LEVEL",
								"CATALOG_ITEM_CLASS",
								"CATALOG_ITEM_VALUE",
								"CATALOG_ITEM_RATE",
								"CATALOG_ITEM_MASK",
								"CATALOG_ITEM_LIVE",
								"CATALOG_ITEM_AREA",
	};

	public static String getCataStat(Item I, CatalogLibrary.CataData data, int x, String optionalColumn)
	{
		if((I==null)||(data==null)) return "";
		final boolean dataRate=(data.getRate()>0.0);
		switch(x)
		{
		case 0: return I.Name();
		case 1: return ""+data.numReferences();
		case 2: return ""+I.basePhyStats().level();
		case 3: return I.ID();
		case 4: return ""+I.baseGoldValue();
		case 5: return (dataRate)?CMath.toPct(data.getRate()):"";
		case 6: return (dataRate)?(data.getMaskStr()==null?"":data.getMaskStr()):"";
		case 7: return (dataRate)?(""+data.getWhenLive()):"";
		case 8: return ""+data.mostPopularArea();
		default:
			if((optionalColumn!=null)&&(optionalColumn.length()>0))
			{
				if(I.isStat(optionalColumn))
					return I.getStat(optionalColumn);
				if(I.basePhyStats().isStat(optionalColumn))
					return I.basePhyStats().getStat(optionalColumn);
			}
			break;
		}
		return "";
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("ITEM");
		String catagory=httpReq.getUrlParameter("CATACAT");
		if(catagory!=null)
		{
			if(catagory.equalsIgnoreCase("UNCATEGORIZED"))
				catagory="";
			else
			if(catagory.length()==0)
				catagory=null;
			else
				catagory=catagory.toUpperCase().trim();
		}
		final String optCol=httpReq.getUrlParameter("OPTIONALCOLUMN");
		final String optionalColumn;
		if(optCol==null)
			optionalColumn="";
		else
			optionalColumn=optCol.trim().toUpperCase();
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("ITEM");
			for (final String element : DATA)
				httpReq.removeUrlParameter(element);
			if(optionalColumn.length()>0)
				httpReq.removeUrlParameter("CATALOG_ITEM_"+optionalColumn);
			return "";
		}
		String lastID="";
		Item I=null;
		String name=null;
		CatalogLibrary.CataData data=null;
		String[] names=CMLib.catalog().getCatalogItemNames(catagory);
		final String sortBy=httpReq.getUrlParameter("SORTBY");
		if((sortBy!=null)&&(sortBy.length()>0))
		{
			final String[] sortedNames=(String[])httpReq.getRequestObjects().get("CATALOG_ITEM_"+catagory+"_"+sortBy.toUpperCase());
			if(sortedNames!=null)
				names=sortedNames;
			else
			{
				final int sortIndex=CMParms.indexOf(DATA, "CATALOG_ITEM_"+sortBy.toUpperCase());
				if((sortIndex>=0)||(sortBy.equalsIgnoreCase(optionalColumn)))
				{
					final Object[] sortifiable=new Object[names.length];
					for(int s=0;s<names.length;s++)
						sortifiable[s]=new Object[]{
							names[s],
							CMLib.catalog().getCatalogItem(names[s]),
							CMLib.catalog().getCatalogItemData(names[s])};
					Arrays.sort(sortifiable,new Comparator()
					{
						@Override
						public int compare(Object o1, Object o2)
						{
							final Object[] O1=(Object[])o1;
							final Object[] O2=(Object[])o2;
							final String s1=getCataStat((Item)O1[1],(CatalogLibrary.CataData)O1[2],sortIndex, optionalColumn);
							final String s2=getCataStat((Item)O2[1],(CatalogLibrary.CataData)O2[2],sortIndex, optionalColumn);
							if(CMath.isNumber(s1)&&CMath.isNumber(s2))
								return Double.valueOf(CMath.s_double(s1)).compareTo(Double.valueOf(CMath.s_double(s2)));
							else
								return s1.toLowerCase().compareTo(s2.toLowerCase());
						}
					});
					for(int s=0;s<names.length;s++)
						names[s]=(String)((Object[])sortifiable[s])[0];
					httpReq.getRequestObjects().put("CATALOG_ITEM_"+catagory+"_"+sortBy.toUpperCase(),names);
				}
			}
		}
		for (final String name2 : names)
		{
			name="CATALOG-"+name2.toUpperCase().trim();
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!name.equalsIgnoreCase(lastID))))
			{
				data=CMLib.catalog().getCatalogItemData(name2);
				I=CMLib.catalog().getCatalogItem(name2);
				if(I==null) continue;
				httpReq.addFakeUrlParameter("ITEM",name);
				for(int d=0;d<DATA.length;d++)
					httpReq.addFakeUrlParameter(DATA[d],getCataStat(I,data,d,null));
				if(optionalColumn.length()>0)
					httpReq.addFakeUrlParameter("CATALOG_ITEM_"+optionalColumn,getCataStat(I,data,-1,optionalColumn));
				return "";
			}
			lastID=name;
		}
		httpReq.addFakeUrlParameter("ITEM","");
		for (final String element : DATA)
			httpReq.addFakeUrlParameter(element,"");
		if(optionalColumn.length()>0)
			httpReq.addFakeUrlParameter("CATALOG_ITEM_"+optionalColumn,"");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
