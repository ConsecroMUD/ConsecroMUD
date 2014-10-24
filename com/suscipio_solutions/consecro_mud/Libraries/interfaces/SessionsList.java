package com.suscipio_solutions.consecro_mud.Libraries.interfaces;

import java.util.Iterator;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


public interface SessionsList extends CMLibrary
{
	public void stopSessionAtAllCosts(Session S);
	public Session findPlayerSessionOnline(String srchStr, boolean exactOnly);
	public MOB findPlayerOnline(String srchStr, boolean exactOnly);
	public Iterator<Session> all();
	public Iterable<Session> allIterable();
	public Iterator<Session> localOnline();
	public Iterable<Session> localOnlineIterable();
	public int getCountLocalOnline();
	public int getCountAll();
	public Session getAllSessionAt(int index);
	public void add(Session s);
	public void remove(Session s);
}
