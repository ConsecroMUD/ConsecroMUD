package com.suscipio_solutions.consecro_mud.Items.BasicTech;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.GenericBuilder;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GenElecPanel extends StdElecPanel
{
	@Override public String ID(){	return "GenElecPanel";}
	protected String readableText="";
	public GenElecPanel()
	{
		super();
		setName("a generic electronics panel");
		basePhyStats.setWeight(2);
		setDisplayText("a generic electric panel is mounted here.");
		setDescription("");
		baseGoldValue=5;
		basePhyStats().setLevel(1);
		recoverPhyStats();
		setMaterial(RawMaterial.RESOURCE_STEEL);
	}

	@Override public boolean isGeneric(){return true;}

	@Override
	public String text()
	{
		return CMLib.coffeeMaker().getPropertiesStr(this,false);
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

	private final static String[] MYCODES={"HASLOCK","HASLID","CAPACITY", "CONTAINTYPES","RESETTIME","POWERCAP", "ACTIVATED","POWERREM","PANTYPE","DEFCLOSED","DEFLOCKED"};
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
		case 5: return ""+powerCapacity();
		case 6: return ""+activated();
		case 7: return ""+powerRemaining();
		case 8: return ""+panelType();
		case 9: return ""+defaultsClosed();
		case 10: return ""+defaultsLocked();
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
		case 5: setPowerCapacity(CMath.s_parseLongExpression(val)); break;
		case 6: activate(CMath.s_bool(val)); break;
		case 7: setPowerRemaining(CMath.s_parseLongExpression(val)); break;
		case 8: try
				{
					setPanelType(TechType.valueOf(val.toUpperCase().trim()));
				}catch(final Exception e){}
				break;
		case 9: setDoorsNLocks(hasADoor(),isOpen(),CMath.s_bool(val),hasALock(),isLocked(),defaultsLocked()); break;
		case 10: setDoorsNLocks(hasADoor(),isOpen(),defaultsClosed(),hasALock(),isLocked(),CMath.s_bool(val)); break;
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
		final String[] MYCODES=CMProps.getStatCodesList(GenElecPanel.MYCODES,this);
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
		if(!(E instanceof GenElecPanel)) return false;
		final String[] theCodes=getStatCodes();
		for(int i=0;i<theCodes.length;i++)
			if(!E.getStat(theCodes[i]).equals(getStat(theCodes[i])))
				return false;
		return true;
	}
}
