package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.GenericBuilder;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class GenRideable extends StdRideable
{
	@Override public String ID(){	return "GenRideable";}
	protected String readableText="";
	public GenRideable()
	{
		super();
		setName("a generic boat");
		setDisplayText("a generic boat sits here.");
		setDescription("");
		basePhyStats().setWeight(2000);
		rideBasis=Rideable.RIDEABLE_WATER;
		setMaterial(RawMaterial.RESOURCE_OAK);
		recoverPhyStats();
	}

	@Override public boolean isGeneric(){return true;}

	@Override
	public String text()
	{
		return CMLib.coffeeMaker().getPropertiesStr(this,false);
	}

	@Override
	public String keyName()
	{
		return readableText;
	}
	@Override
	public void setKeyName(String newKeyName)
	{
		readableText=newKeyName;
	}
	@Override public String readableText(){return readableText;}
	@Override public void setReadableText(String text){readableText=text;}
	@Override
	public void setMiscText(String newText)
	{
		miscText="";
		CMLib.coffeeMaker().setPropertiesStr(this,newText,false);
		recoverPhyStats();
	}
	private final static String[] MYCODES={"HASLOCK","HASLID","CAPACITY","CONTAINTYPES","RESETTIME","RIDEBASIS","MOBSHELD","DEFCLOSED","DEFLOCKED"};
	@Override
	public String getStat(String code)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			return CMLib.coffeeMaker().getGenItemStat(this,code);
		switch(getCodeNum(code))
		{
		case 0: return ""+hasALock();
		case 1: return ""+hasADoor();
		case 2: return ""+capacity();
		case 3: return ""+containTypes();
		case 4: return ""+openDelayTicks();
		case 5: return ""+rideBasis();
		case 6: return ""+riderCapacity();
		case 7: return ""+defaultsClosed();
		case 8: return ""+defaultsLocked();
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
		case 0: setDoorsNLocks(hasADoor(),isOpen(),defaultsClosed(),CMath.s_bool(val),false,CMath.s_bool(val)&&defaultsLocked()); break;
		case 1: setDoorsNLocks(CMath.s_bool(val),isOpen(),CMath.s_bool(val)&&defaultsClosed(),hasALock(),isLocked(),defaultsLocked()); break;
		case 2: setCapacity(CMath.s_parseIntExpression(val)); break;
		case 3: setContainTypes(CMath.s_parseBitLongExpression(Container.CONTAIN_DESCS,val)); break;
		case 4: setOpenDelayTicks(CMath.s_parseIntExpression(val)); break;
		case 5: setRideBasis(CMath.s_parseListIntExpression(Rideable.RIDEABLE_DESCS,val)); break;
		case 6: setRiderCapacity(CMath.s_parseIntExpression(val)); break;
		case 7: setDoorsNLocks(hasADoor(),isOpen(),CMath.s_bool(val),hasALock(),isLocked(),defaultsLocked()); break;
		case 8: setDoorsNLocks(hasADoor(),isOpen(),defaultsClosed(),hasALock(),isLocked(),CMath.s_bool(val)); break;
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
		final String[] MYCODES=CMProps.getStatCodesList(GenRideable.MYCODES,this);
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
		if(!(E instanceof GenRideable)) return false;
		final String[] codes=getStatCodes();
		for(int i=0;i<codes.length;i++)
			if(!E.getStat(codes[i]).equals(getStat(codes[i])))
				return false;
		return true;
	}
}
