package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.GenericBuilder;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GenPlayingCard extends StdPlayingCard
{
	@Override public String ID(){	return "GenPlayingCard";}
	protected String readableText="";

	public GenPlayingCard()
	{
		super();
	}

	@Override public boolean isGeneric(){return true;}

	@Override public String readableText(){return readableText;}
	@Override public void setReadableText(String text){ readableText=text;}

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

	@Override
	public String getStat(String code)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			return CMLib.coffeeMaker().getGenItemStat(this,code);
		return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
	}
	@Override
	public void setStat(String code, String val)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			CMLib.coffeeMaker().setGenItemStat(this,code,val);
		CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code,val);
	}
	private static String[] codes=null;
	@Override
	public String[] getStatCodes()
	{
		if(codes==null)
			codes=CMProps.getStatCodesList(GenericBuilder.GENITEMCODES,this);
		return codes;
	}
	@Override
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof GenWand)) return false;
		for(int i=0;i<getStatCodes().length;i++)
			if(!E.getStat(getStatCodes()[i]).equals(getStat(getStatCodes()[i])))
				return false;
		return true;
	}
}
