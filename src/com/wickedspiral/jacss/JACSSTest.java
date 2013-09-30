package com.wickedspiral.jacss;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.*;

/**
 * @author wasche
 * @since 9/30/13
 */
public class JACSSTest
{
    @DataProvider( name="files" )
    public Iterator<Object[]> getFiles()
    {
        List<Object[]> cases = new LinkedList<>();
        
        return cases.iterator();
    }
    
    @Test( dataProvider="files" )
    public void testAll( String source, String expected )
    {
        
    }
}
