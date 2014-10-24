package com.suscipio_solutions.consecro_mud.Libraries;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TextEncoders;
import com.suscipio_solutions.consecro_mud.core.B64Encoder;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Log;


public class CMEncoder extends StdLibrary implements TextEncoders
{
	@Override public String ID(){return "CMEncoder";}
	private byte[] encodeBuffer = new byte[65536];
	private final Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION);
	private final Inflater decompresser = new Inflater();

	public CMEncoder()
	{
		super();
	}

	@Override
	public synchronized String decompressString(byte[] b)
	{
		try
		{
			if ((b == null)||(b.length==0)) return "";

			decompresser.reset();
			decompresser.setInput(b);

			synchronized (encodeBuffer)
			{
				final int len = decompresser.inflate(encodeBuffer);
				return new String(encodeBuffer, 0, len, CMProps.getVar(CMProps.Str.CHARSETINPUT));
			}
		}
		catch (final Exception ex)
		{
			Log.errOut(Thread.currentThread().getName(), "Error occurred during decompression: "+ex.getMessage());
			encodeBuffer=new byte[65536];
			return "";
		}
	}

	@Override
	public synchronized byte[] compressString(String s)
	{
		byte[] result = null;

		try
		{
			compresser.reset();
			compresser.setInput(s.getBytes(CMProps.getVar(CMProps.Str.CHARSETINPUT)));
			compresser.finish();

			synchronized (encodeBuffer)
			{
				if(s.length()>encodeBuffer.length)
					encodeBuffer=new byte[s.length()];
				encodeBuffer[0]=0;

				final int len = compresser.deflate(encodeBuffer);
				result = new byte[len];
				System.arraycopy(encodeBuffer, 0, result, 0, len);
			}
		}
		catch (final Exception ex)
		{
			Log.errOut("MUD", "Error occurred during compression: "+ex.getMessage());
			encodeBuffer=new byte[65536];
		}

		return result;
	}

	@Override
	public String makeRandomHashString(final String password)
	{
		final int salt=(int)Math.round(CMath.random() * Integer.MAX_VALUE);
		final int passHash=(password+salt).toLowerCase().hashCode();
		return "|"+B64Encoder.B64encodeBytes(ByteBuffer.allocate(4).putInt(salt).array())
			  +"|"+B64Encoder.B64encodeBytes(ByteBuffer.allocate(4).putInt(passHash).array());
	}

	@Override
	public boolean isARandomHashString(final String password)
	{
		return ((password.length()>2) && (password.startsWith("|")) && (password.indexOf('|',1)>1));
	}

	@Override
	public boolean checkAgainstRandomHashString(final String checkString, final String hashString)
	{
		final int hashDex=hashString.indexOf('|',1);
		final int salt=ByteBuffer.wrap(B64Encoder.B64decode(hashString.substring(1,hashDex))).getInt();
		final int hash=ByteBuffer.wrap(B64Encoder.B64decode(hashString.substring(hashDex+1))).getInt();
		return hash==(checkString+salt).toLowerCase().hashCode();
	}

	@Override
	public String generateRandomPassword()
	{
		final StringBuilder str=new StringBuilder("");
		for(int i=0;i<10;i++)
		{
			if((i%2)==0)
				str.append(CMLib.dice().roll(1, 10, -1));
			else
				str.append((char)('a'+CMLib.dice().roll(1, 26, -1)));
		}
		return str.toString();
	}

}
