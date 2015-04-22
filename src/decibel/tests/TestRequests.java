package decibel.tests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestRequests
{
	public static void runTest( String url, int numberOfTests, int numberOfThreads ) throws InterruptedException
	{
		ExecutorService threadPool = Executors.newFixedThreadPool( numberOfThreads );
		 
		for( int i = 0; i < numberOfTests; ++i )
		{
			threadPool.execute( new TestClient( url, i ) );
		}

		threadPool.shutdown();

		// Wait for all the test threads to finish
		threadPool.awaitTermination( Long.MAX_VALUE, TimeUnit.SECONDS );
	}
}
