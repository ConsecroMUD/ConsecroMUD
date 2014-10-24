package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;

public interface StatisticsLibrary extends CMLibrary
{
	public void update();
	public void bump(CMObject E, int type);
}
