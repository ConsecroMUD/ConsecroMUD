package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public interface ClanItem extends Item
{
	public final static int CI_FLAG=0;
	public final static int CI_BANNER=1;
	public final static int CI_GAVEL=2;
	public final static int CI_PROPAGANDA=3;
	public final static int CI_GATHERITEM=4;
	public final static int CI_CRAFTITEM=5;
	public final static int CI_SPECIALSCALES=6;
	public final static int CI_SPECIALSCAVENGER=7;
	public final static int CI_SPECIALOTHER=8;
	public final static int CI_SPECIALTAXER=9;
	public final static int CI_DONATEJOURNAL=10;
	public final static int CI_ANTIPROPAGANDA=11;
	public final static int CI_SPECIALAPRON=12;
	public final static int CI_LEGALBADGE=13;

	public final static String[] CI_DESC={
		"FLAG",
		"BANNER",
		"GAVEL",
		"PROPAGANDA",
		"GATHERITEM",
		"CRAFTITEM",
		"SPECIALSCALES",
		"SPECIALSCAVENGER",
		"SPECIALOTHER",
		"SPECIALTAXER",
		"DONATIONJOURNAL",
		"ANTI-PROPAGANDA",
		"SPECIALAPRON",
		"LEGALBADGE"
	};

	public String clanID();
	public void setClanID(String ID);

	public int ciType();
	public void setCIType(int type);

	public Environmental rightfulOwner();
	public void setRightfulOwner(Environmental E);
}
