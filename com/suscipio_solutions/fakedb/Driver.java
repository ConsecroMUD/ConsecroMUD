package com.suscipio_solutions.fakedb;

import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;


public class Driver implements java.sql.Driver
{
   static
   {
	  try
	  {
		 java.sql.DriverManager.registerDriver(new Driver());
	  }
	  catch (final java.sql.SQLException E)
	  {
		 E.printStackTrace();
	  }
   }

   public Driver()
   {
   }

   @Override
public synchronized java.sql.Connection connect(String url, Properties info) throws java.sql.SQLException
   {
	  final Properties p=parseUrl(url,info);
	  if (p==null) return null;
	  return new Connection(p.getProperty("PATH"));
   }

   @Override
public synchronized boolean acceptsURL(String url) throws java.sql.SQLException
   {
	  return parseUrl(url,null)!=null;
   }

   @Override
public java.sql.DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws java.sql.SQLException
   {
	  return new java.sql.DriverPropertyInfo[0];
   }

   @Override
public int getMajorVersion()
   {
	   return 3;
   }

   @Override
public int getMinorVersion()
   {
	   return 0;
   }

   @Override
public boolean jdbcCompliant()
   {
	   return false;
   }

   /**
	*
	* @param url
	* @param defaults
	* @return
	*/
   private Properties parseUrl(String url, Properties defaults)
   {
	  if (!url.startsWith("jdbc:fakedb:")) return null;

	  String path=url.substring(12);
	  if ((path.length()>0)&&(!path.endsWith(java.io.File.separator)))
		 path=path+java.io.File.separator;

	  final Properties result=new Properties(defaults);
	  result.put("PATH",path.replace('/',java.io.File.separatorChar));

	  return result;
   }

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}
}
