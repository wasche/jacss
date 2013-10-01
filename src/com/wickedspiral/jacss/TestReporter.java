package com.wickedspiral.jacss;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * @author wasche
 * @since 10/1/13
 */
public class TestReporter implements ITestListener
{
    private static final Pattern ASSERT_EQ_PATTERN = Pattern.compile( "(.*) expected \\[(.*)\\] but found \\[(.*)\\]", Pattern.MULTILINE | Pattern.DOTALL );

    @Override
    public void onFinish( ITestContext iTestContext )
    {
    }

    @Override
    public void onTestStart( ITestResult iTestResult )
    {
    }

    @Override
    public void onTestSuccess( ITestResult iTestResult )
    {
    }

    @Override
    public void onTestFailure( ITestResult testResult )
    {
        @SuppressWarnings( "ThrowableResultOfMethodCallIgnored" )
        Throwable throwable = testResult.getThrowable();
        if ( throwable instanceof AssertionError )
        {
            Matcher m = ASSERT_EQ_PATTERN.matcher( throwable.getMessage() );
            if ( m.matches() )
            {
                String test = m.group( 1 );
                String expected = m.group( 2 ).replaceAll( "\n", "\\\\n" );
                String actual = m.group( 3 ).replaceAll( "\n", "\\\\n" );
                System.out.println( test );
                System.out.println( "- " + expected );
                System.out.println( "+ " + actual );
            }
            else
            {
                System.out.println( "failed: " + throwable.getMessage() );
            }
        }
        else
        {
            System.out.println( throwable.getClass().getSimpleName() + ": " + testResult.getParameters()[0] );
        }
    }

    @Override
    public void onTestSkipped( ITestResult iTestResult )
    {
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage( ITestResult iTestResult )
    {
    }

    @Override
    public void onStart( ITestContext iTestContext )
    {
    }
}
