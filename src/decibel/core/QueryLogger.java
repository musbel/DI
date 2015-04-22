package decibel.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;

public class QueryLogger
{
	private static QueryLogger instance = null;
	private static String logFile;
	private static BufferedWriter writer = null;
	
	private QueryLogger( String filename )
	{
		logFile = filename;
	
		synchronized( this )
		{
			File file = new File( logFile );
			try
			{
				writer = new BufferedWriter( new FileWriter( file, true ) );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	private void log( String text )
	{
		if( writer != null )
		{
			synchronized( writer )
			{
				try
				{
					writer.write( text );
					writer.newLine();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void log( String host, String query )
	{
		Calendar calendar = Calendar.getInstance();
		Timestamp timestamp = new java.sql.Timestamp( calendar.getTime().getTime() );

		StringBuffer logText = new StringBuffer();
		logText.append( "[" + timestamp.toString() + "] " );
		logText.append( "[" + host + "] " );
		logText.append( "[" + query.length() + "] " );
		logText.append( "[" + query + "]" );
		
		log( logText.toString() );
	}

	public void close()
	{
		try
		{
			writer.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	public static QueryLogger getLogger( String filename )
	{
		if( instance == null || logFile != filename )
		{
			instance = new QueryLogger( filename );
		}
		
		return instance;
	}
}
