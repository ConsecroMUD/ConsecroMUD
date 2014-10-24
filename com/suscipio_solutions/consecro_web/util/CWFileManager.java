package com.suscipio_solutions.consecro_web.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.suscipio_solutions.consecro_web.interfaces.FileManager;


public class CWFileManager implements FileManager
{

	@Override
	public char getFileSeparator()
	{
		return File.separatorChar;
	}

	@Override
	public File createFileFromPath(String localPath)
	{
		return new File(localPath);
	}
	@Override
	public File createFileFromPath(File parent, String localPath)
	{
		return new File(parent, localPath);
	}
	@Override
	public InputStream getFileStream(File file) throws IOException, FileNotFoundException
	{
		return new BufferedInputStream(new FileInputStream(file));
	}
	@Override
	public RandomAccessFile getRandomAccessFile(File file) throws IOException, FileNotFoundException
	{
		return new RandomAccessFile(file,"r");
	}
	@Override
	public byte[] readFile(File file) throws IOException, FileNotFoundException 
	{
		BufferedInputStream bs = null;
		final byte[] fileBuf = new byte[(int)file.length()];
		try
		{
			bs=new BufferedInputStream(new FileInputStream(file));
			bs.read(fileBuf);
		}
		finally
		{
			if(bs!=null)
				bs.close();
		}
		return fileBuf;
	}

	@Override
	public boolean supportsRandomAccess(File file)
	{
		return true;
	}

	@Override
	public boolean allowedToReadData(File file)
	{
		return file.exists() && file.canRead();
	}
}
