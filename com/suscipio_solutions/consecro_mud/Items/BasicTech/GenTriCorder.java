package com.suscipio_solutions.consecro_mud.Items.BasicTech;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.GenericBuilder;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GenTriCorder extends StdTriCorder
{
	@Override public String ID(){	return "GenTriCorder";}

	public GenTriCorder()
	{
		super();
	}

	@Override public boolean isGeneric(){return true;}

	@Override
	public String text()
	{
		return CMLib.coffeeMaker().getPropertiesStr(this,false);
	}

	@Override
	public void setMiscText(String newText)
	{
		miscText="";
		CMLib.coffeeMaker().setPropertiesStr(this,newText,false);
		recoverPhyStats();
	}

	private final static String[] MYCODES={"POWERCAP","ACTIVATED","POWERREM","MANUFACTURER"};
	@Override
	public String getStat(String code)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			return CMLib.coffeeMaker().getGenItemStat(this,code);
		switch(getCodeNum(code))
		{
		case 0: return ""+powerCapacity();
		case 1: return ""+activated();
		case 2: return ""+powerRemaining();
		case 3: return ""+getManufacturerName();
		default:
			return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
		}
	}
	@Override
	public void setStat(String code, String val)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			CMLib.coffeeMaker().setGenItemStat(this,code,val);
		else
		switch(getCodeNum(code))
		{
		case 0: setPowerCapacity(CMath.s_parseLongExpression(val)); break;
		case 1: activate(CMath.s_bool(val)); break;
		case 2: setPowerRemaining(CMath.s_parseLongExpression(val)); break;
		case 3: setManufacturerName(val); break;
		default:
			CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
			break;
		}
	}
	@Override
	protected int getCodeNum(String code)
	{
		for(int i=0;i<MYCODES.length;i++)
			if(code.equalsIgnoreCase(MYCODES[i])) return i;
		return -1;
	}
	private static String[] codes=null;
	@Override
	public String[] getStatCodes()
	{
		if(codes!=null) return codes;
		final String[] MYCODES=CMProps.getStatCodesList(GenTriCorder.MYCODES,this);
		final String[] superCodes=GenericBuilder.GENITEMCODES;
		codes=new String[superCodes.length+MYCODES.length];
		int i=0;
		for(;i<superCodes.length;i++)
			codes[i]=superCodes[i];
		for(int x=0;x<MYCODES.length;i++,x++)
			codes[i]=MYCODES[x];
		return codes;
	}
	@Override
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof GenTriCorder)) return false;
		final String[] theCodes=getStatCodes();
		for(int i=0;i<theCodes.length;i++)
			if(!E.getStat(theCodes[i]).equals(getStat(theCodes[i])))
				return false;
		return true;
	}
}
