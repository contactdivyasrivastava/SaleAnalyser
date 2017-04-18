/**
 * 
 */
package com.saleanalyser.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;

import com.saleanalyser.model.Adjustment;
import com.saleanalyser.model.ProductSale;

/**
 * @author Divya
 *
 */
public class SaleAnalyserMainTest extends TestCase {

	private static SaleAnalyserMain saleAnalyserMain;
		
	public SaleAnalyserMainTest(){
		
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@BeforeClass
	protected void setUp() throws Exception {
		super.setUp();
		saleAnalyserMain = new SaleAnalyserMain();
		
	
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	

	@Test
	public void testProductSalesAndValue(){
		Map<String,ProductSale> productRecords = saleAnalyserMain.getProductRecords();
		String product="";
		int expectedSales, actualSales;
		float expectedValue, actualValue;
		
		
		for (Map.Entry<String,ProductSale> entry : productRecords.entrySet()) {
			product=entry.getKey();
			actualSales= entry.getValue().getTotalCount();
			actualValue=entry.getValue().getTotalValue();
			switch (product.toUpperCase())  
		     {
		       case "PINEAPPLE":
		    	   expectedSales=130;expectedValue=2140.0f;
		       assertEquals(expectedSales, actualSales);
		       assertEquals(expectedValue, actualValue);
		       break;

		       case "APPLE":
		    	   expectedSales=64;expectedValue=1060.0f;
		    	   assertEquals(expectedSales, actualSales);
			       assertEquals(expectedValue, actualValue);
		       break;

		       case "MANGO":
		    	   expectedSales=44;expectedValue=20.0f;
		    	   assertEquals(expectedSales, actualSales);
			       assertEquals(expectedValue, actualValue);
		       break;

		       case "ORANGE":
		    	   expectedSales=169;expectedValue=9670.0f;
		    	   assertEquals(expectedSales, actualSales);
			       assertEquals(expectedValue, actualValue);
		       break;

		            
		     }   
		}
		
	}
	
	@Test
	public void testAdjustments(){
		List<Adjustment> adjustmentList = saleAnalyserMain.getAdjustmentList();
		Iterator<Adjustment> itr = adjustmentList.iterator();
		String product="",operation="";
		float expectedSalesAfterOperation, actualSalesAfterOperation,
		expectedSalesBeforeOperation, actualSalesBeforeOperation;
		
		while (itr.hasNext()) {
			Adjustment adj =(Adjustment) itr.next();
			actualSalesBeforeOperation=adj.getSalesBeforeOperation();
			actualSalesAfterOperation=adj.getSalesAfterOperation();
			operation = adj.getOperationApplied();
			switch (product.toUpperCase())  
		     {
		       case "PINEAPPLE":
		    	   expectedSalesAfterOperation=1260.0f;
		    	   expectedSalesBeforeOperation=420.0f;
		    	   assertEquals("ADD", operation);
		    	   assertEquals(expectedSalesBeforeOperation, actualSalesBeforeOperation);
		    	   assertEquals(expectedSalesAfterOperation, actualSalesAfterOperation);
		       break;

		       case "APPLE":
		    	   expectedSalesAfterOperation=630.0f;
		    	   expectedSalesBeforeOperation=210.0f;
		    	   assertEquals("ADD", operation);
		    	   assertEquals(expectedSalesBeforeOperation, actualSalesBeforeOperation);
			       assertEquals(expectedSalesAfterOperation, actualSalesAfterOperation);
		       break;

		       case "MANGO":
		    	   expectedSalesAfterOperation=-210.0f;
		    	   expectedSalesBeforeOperation=210.0f;
		    	   assertEquals("SUBTRACT", operation);
		    	   assertEquals(expectedSalesBeforeOperation, actualSalesBeforeOperation);
			       assertEquals(expectedSalesAfterOperation, actualSalesAfterOperation);
		       break;

		       case "ORANGE":
		    	   expectedSalesAfterOperation=8400.0f;
		    	   expectedSalesBeforeOperation=420.0f;
		    	   assertEquals("MULTIPLY", operation);
		    	   assertEquals(expectedSalesBeforeOperation, actualSalesBeforeOperation);
			       assertEquals(expectedSalesAfterOperation, actualSalesAfterOperation);
		       break;

		            
		     }   
			
	}
	
	}
	
	/**
	 * Test method for {@link com.saleanalyser.util.SaleAnalyserMain#main(java.lang.String[])}.
	 */
	@Test
	public void testMain() {
		
		String path ;
		
		path = "./src/test/resources/Message.xml";
		//path = "./MessageNegative1.xml";
		//path = "./MessageNegative2.xml";
		
		
		saleAnalyserMain.runParser(path);
		assertEquals(true, true);
	}
}


