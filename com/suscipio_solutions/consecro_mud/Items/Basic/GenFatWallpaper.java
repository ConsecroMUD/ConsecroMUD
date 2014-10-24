package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class GenFatWallpaper extends GenWallpaper
{
	@Override public String ID(){	return "GenFatWallpaper";}
	protected String	displayText="";
	@Override public String displayText(){ return displayText;}
	@Override public void setDisplayText(String newText){displayText=newText;}
	protected long expirationDate=0;
	@Override public long expirationDate(){return expirationDate;}
	@Override public void setExpirationDate(long time){expirationDate=time;}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this)
		&&((msg.targetMinor()==CMMsg.TYP_EXPIRE)||(msg.targetMinor()==CMMsg.TYP_DEATH)))
		{
			return true;
		}
		if(!super.okMessage(myHost,msg))
			return false;
		return true;
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this)
		&&((msg.targetMinor()==CMMsg.TYP_EXPIRE)||(msg.targetMinor()==CMMsg.TYP_DEATH)))
			destroy();
		super.executeMsg(myHost,msg);
	}
	private static final String[] CODES={"DISPLAY"};
	@Override
	public String[] getStatCodes()
	{
		final String[] THINCODES=super.getStatCodes();
		final String[] codes=new String[THINCODES.length+1];
		for(int c=0;c<THINCODES.length;c++)
			codes[c]=THINCODES[c];
		codes[THINCODES.length]="DISPLAY";
		return codes;
	}
	protected int getMyCodeNum(String code)
	{
		for(int i=0;i<CODES.length;i++)
			if(code.equalsIgnoreCase(CODES[i])) return i;
		return -1;
	}
	@Override
	public String getStat(String code)
	{
		if(getMyCodeNum(code)<0) return super.getStat(code);
		switch(getMyCodeNum(code))
		{
		case 0:
			return displayText();
		}
		return "";
	}
	@Override
	public void setStat(String code, String val)
	{
		if(getMyCodeNum(code)<0)
			super.setStat(code,val);
		else
		switch(getMyCodeNum(code))
		{
		case 0: setDisplayText(val); break;
		}
	}
	@Override
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof GenFatWallpaper)) return false;
		if(!super.sameAs(E)) return false;
		for(int i=0;i<CODES.length;i++)
			if(!E.getStat(CODES[i]).equals(getStat(CODES[i])))
			{
				return false;
			}
		return true;
	}
}
