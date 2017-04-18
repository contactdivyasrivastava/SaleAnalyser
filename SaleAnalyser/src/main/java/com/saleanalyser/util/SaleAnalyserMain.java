package com.saleanalyser.util;

/**
 * @author Divya
 *
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.saleanalyser.model.Adjustment;
import com.saleanalyser.model.Message;
import com.saleanalyser.model.ProductSale;
import com.saleanalyser.model.Sale;
import com.saleanalyser.exception.InvalidMessageException;


public class SaleAnalyserMain {
	
	final static Logger logger = Logger.getLogger(SaleAnalyserMain.class);
	
	
	private List<Message> myMsgList;
	

	private Document dom;
	private List<Adjustment> adjustmentList;
	private Map<String,ProductSale> productRecords = new HashMap<String,ProductSale>();

	public SaleAnalyserMain(){
		//create a list to hold the Message objects
		myMsgList = new ArrayList<Message>();
		adjustmentList = new ArrayList<Adjustment>();
	}

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args){
		
		logger.info("Entry main");
		BasicConfigurator.configure();
		logger.debug("Inside main : properties configured");
		
		SaleAnalyserMain sam = new SaleAnalyserMain();
		String path ;
		
		path = "./Message.xml";
		logger.debug("Inside main : FileName to input Message.xml");
		
		sam.runParser(path);
		logger.info("Exit main");
	}
	
	public void runParser(String path) {
		logger.info("Enter: runParser");
		parseXmlFile(path);
		parseMessages();
			
		logger.info("Exit: runParser");
	}
	
	
	private void parseXmlFile(String path){
		logger.info("Enter: parseXmlFile");
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		logger.debug("Inside parseXmlFile: DocumentBuilderFactory instance"+dbf);
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			logger.debug("Inside parseXmlFile: DocumentBuilder instance"+db);
			
			//parse using builder to get DOM representation of the XML file
			dom = db.parse(path);
			logger.debug("Inside parseXmlFile: Document instance"+dom);

		}catch(ParserConfigurationException pce) {
			logger.error(pce);
		}catch(SAXException se) {
			logger.error(se);
		}catch(IOException ioe) {
			logger.error(ioe);
		}
		logger.info("Exit: parseXmlFile");
	}

	
	private void parseMessages(){
		logger.info("Enter: parseMessages");
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of <Message> elements
		NodeList nl = docEle.getElementsByTagName("Message");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
			 try{
					//get the Message element
					Element el = (Element)nl.item(i);
					
					//get the Message object
					Message m = getMessage(el);
					
					//add it to list
					myMsgList.add(m);
					
					logger.debug("Inside parseMessages: "+(i+1)+" iteration Message instance"+m+" Message "
							+ "type: "+m.getType()+" Producttype: "+m.getSale().getProductType()+" SalesCount:"
									+ " "+m.getSale().getSalesCount()+" SalesValue: "+m.getSale().getSaleValue());
					
					//update productRecord List to keep track of product sales
					updateProductRecords(m);
					
					//After every 10th message read, log Total sales and Total Value 
					//corresponding to the Product
					if((i+1)%10==0) {
						logProductSalesAndValue();
					}
					
					//After reading 50 messages, report the Adjustments made
					if((i+1)%50 == 0) {
						logger.info("Inside parseMessages: 50th message encountered!");
						System.out.println(" Message Processor is now pausing and stop accepting new messages. \n "
								+ "Report of adjustments that have been made to each sale while running are below: \n");
						logAdjustments();
						
						//exit MessageProcessor to stop reading rest of the Messages in XML file
						return;//System.exit(0);
					}
			 }catch(InvalidMessageException e) {
				 logger.error(e);
			 }catch(NumberFormatException e) {
				 logger.error(e);
			 }catch(Exception e) {
				 logger.error(e);
			 }
			 
				
			}
		}
		logger.info("Exit: parseMessages");
	}


	/**
	 * Take a Message element and read the values in, create
	 * an Message object and return it
	 * @param empEl
	 * @return
	 */
	private Message getMessage(Element msgElement){
		logger.info("Enter: getMessage");
		
		String operation,type,productType;
		Message m = null;
		Float saleValue;
		int salesCount;
		
		try{
		type = msgElement.getAttribute("type");
		productType = getTextValue(msgElement,"productType").toUpperCase();
		saleValue = getFloatValue(msgElement,"saleValue");
		
		validateString(productType);
		validateMsgType(type);
		validateFloat(saleValue);
		
		
		Sale s  = new Sale();
		s.setProductType(productType);
		s.setSaleValue(saleValue);
		
		//populate Message on the basis of type 
		if (type.toUpperCase().equals("TYPE3")){
			operation = getTextValue(msgElement,"salesOperation");
			validateOperation(operation);
			m = new Message(type,s,operation);
		}
		else{
			salesCount = getIntValue(msgElement,"salesCount");
			validateInt(salesCount);
			s.setSalesCount(salesCount);
			m = new Message(type,s,null);
						
		}
		}catch(InvalidMessageException e){
			logger.error(e);
		}catch(NumberFormatException e){
			logger.error(e);
		}catch(Exception e){
			logger.error(e);
		}
		logger.debug(m);
		logger.info("Exit: getMessage");
		return m;
		
	}



	
/**
 * This will log total sales and total value for products after reading 10 messages
 */
private String logProductSalesAndValue(){
	logger.info("Enter: logProductSalesAndValue 10x message encountered");
	String logMessage="";
	for (Map.Entry<String,ProductSale> entry : productRecords.entrySet()) {
		logMessage = "Product: " + entry.getKey() + ", Total Sales: " + entry.getValue().getTotalCount()+" "
	    		+ ", Total Value: " + entry.getValue().getTotalValue();
		logger.debug("Inside logProductSalesAndValue: "+ logMessage);
	    System.out.println(logMessage);
	}
	System.out.println("\n");
	logger.info("Exit: logProductSalesAndValue");
	return logMessage;
}

/**update productRecord List to keep track of product sales
 *  or perform operation if message type is Type3
 * @param msg
 */
private void updateProductRecords(Message msg){
	logger.info("Entry: updateProductRecords");
	Sale sale = msg.getSale();
	String productType=sale.getProductType();
	int salesCnt = sale.getSalesCount();
	Float saleVal = sale.getSaleValue();
	String type = msg.getType().toUpperCase();
	
	if(type.equals("TYPE3"))
		performOperation(msg);
	else if( productRecords.containsKey(productType.toUpperCase())) {
		ProductSale ps = productRecords.get(productType);
		int totCount = ps.getTotalCount();
		Float totValue= ps.getTotalValue();
		ps.setTotalValue(totValue + (saleVal*salesCnt) );
		ps.setTotalCount(totCount + salesCnt);
		logger.debug("Inside updateProductRecords: Existing Product: "+productType+"  ExistingtotCount:"
				+ ""+totCount+" Existingtotvalue:"+totValue+" Current salesCnt:"+salesCnt+" "
						+ "Current saleVal"+saleVal+" New totcount:(totCount + salesCnt):"+(totCount + salesCnt)+" "
								+ "and totvalue(totValue + (saleVal*salesCnt)):"+(totValue + (saleVal*salesCnt)));
		productRecords.put(productType,ps);
		
	} else{
		logger.debug("Inside updateProductRecords: New Product"+productType+"  salesCnt:"+salesCnt+" and "
				+ "totvalue(saleVal*salesCnt):"+(saleVal*salesCnt));
		productRecords.put(productType,new ProductSale(salesCnt,(saleVal*salesCnt)));
		
	}
	
	logger.info("Exit: updateProductRecords");
}
	
	/**
	 * This will perform operation-add,subtract,multiply on all the registered 
	 * sales for a producttype.Also will track totalsalesvalue for a product
	 * before and after applying the operation
	 * @param msg
	 */
	private void performOperation(Message msg) {
		logger.info("Entry: performOperation");
		ProductSale ps =  null;
		int totCount=0;
		Float totValue=0f,updatedValue=0f;
		
		Sale sale = msg.getSale();
		String productType=sale.getProductType();
		Float saleVal = sale.getSaleValue();
		String operation = msg.getSalesOperation().toUpperCase();
		if( productRecords.containsKey(productType.toUpperCase())) {
			ps = productRecords.get(productType);
			if(operation.equals("ADD")) {
				totValue= ps.getTotalValue();
				totCount = ps.getTotalCount();
				updatedValue = totValue+(totCount*saleVal);
				ps.setTotalValue(updatedValue);
				logger.debug("Inside performOperation: Product: "+productType+" operation:"+operation+" totCount:"
						+ ""+totCount+" totvalue:"+totValue+" saleVal"+saleVal+" "
								+ "UpdatedValue(totValue+(totCount*saleVal))"+updatedValue);
				productRecords.put(productType,ps);
			}else if(operation.equals("SUBTRACT")) {
				totValue= ps.getTotalValue();
				totCount = ps.getTotalCount();
				updatedValue = totValue-(totCount*saleVal);
				ps.setTotalValue(updatedValue);
				logger.debug("Inside performOperation: Product: "+productType+" operation:"+operation+" totCount:"
						+ ""+totCount+" totvalue:"+totValue+" saleVal"+saleVal+" "
								+ "UpdatedValue(totValue-(totCount*saleVal))"+updatedValue);
				productRecords.put(productType,ps);
			}else if(operation.equals("MULTIPLY")) {
				totValue= ps.getTotalValue();
				totCount = ps.getTotalCount();
				updatedValue = totValue*saleVal;
				ps.setTotalValue(updatedValue);
				logger.debug("Inside performOperation: Product: "+productType+" operation:"+operation+" totCount:"
						+ ""+totCount+" totvalue:"+totValue+" saleVal"+saleVal+" "
								+ "UpdatedValue(totValue*saleVal)"+updatedValue);
				productRecords.put(productType,ps);
					
				
			}
			adjustmentList.add(new Adjustment(productType,operation,totValue,updatedValue));
		}
		logger.info("Exit: performOperation");
	}
	
	
	/**
	 * Log and report all the adjustments made to sales for a product type
	 * will display operation,producttype,sales before applying operation and 
	 * sales after applying operation
	 */
	private String logAdjustments() {
		logger.info("Enter: logAdjustments");
		Iterator<Adjustment> itr = adjustmentList.iterator();
		String logMessage="";
		while (itr.hasNext()) {
			Adjustment adj =(Adjustment) itr.next();
			logMessage= "Adjustment Operation: "+adj.getOperationApplied()+" ProductType: "+adj.getProductType()+""
					+ " Sales Before Adjustment: "+adj.getSalesBeforeOperation()+" Sales after adjustment: "+adj.getSalesAfterOperation();
			System.out.println(logMessage);
			logger.debug("Inside logAdjustments: "+logMessage);
		}
		logger.info("Exit: logAdjustments");
		return logMessage;
	}
	
	

	/**
	 * Take a xml element and the tag name, look for the tag and get
	 * the text content 
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private String getTextValue(Element ele, String tagName){
		String textVal = null;
		
			NodeList nl = ele.getElementsByTagName(tagName);
		
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}
		
		return textVal;
	}

	private void validateString(String str)  {
		try{
		if(str == null)
			throw new InvalidMessageException("Value entered is null");
		else if(!(str.matches("[a-zA-Z]+"))){
			throw new InvalidMessageException("Value entered "+str+" is not a valid Productype");
		}
		}catch(InvalidMessageException e) {
			logger.error(e);
		}
			
	}
	
	
	private void validateMsgType(String type) {
		try{
			if(type == null)
			throw new InvalidMessageException("Message Type is null");
		else if (!(type.equalsIgnoreCase("TYPE1")) && !(type.equalsIgnoreCase("TYPE2")) && !(type.equalsIgnoreCase("TYPE3")))
			throw new InvalidMessageException("Message Type is invalid");
	}catch(InvalidMessageException e) {
		logger.error(e);
	}
	}
	
	private void validateOperation(String op) {
	try{
		if(op == null)
			throw new InvalidMessageException("Operation entered is null");
		else if (!(op.equalsIgnoreCase("ADD")) && !(op.equalsIgnoreCase("SUBTRACT")) && !(op.equalsIgnoreCase("MULTIPLY")))
			throw new InvalidMessageException("Operation entered is invalid");
	}catch(InvalidMessageException e) {
		logger.error(e);
	}
	}
		
	private void validateFloat(Float var){
		String var1 = new Float(var).toString();
	try{	
		if(var == null)
			throw new InvalidMessageException("Value entered is null");
		else if(!(var1.matches("[+-]?([0-9]*[.])?[0-9]+"))){
			throw new InvalidMessageException("Value entered "+var1+" is not a valid Float");
		}
	}catch(InvalidMessageException e) {
		logger.error(e);
	}
	}
	
	private void validateInt(int var) {
		String var1 = new Integer(var).toString();
		try{
			if(!(var1.matches("[0-9]+"))){
			throw new InvalidMessageException("Value entered "+var1+" is not a valid integer");
		}
	}catch(InvalidMessageException e) {
		logger.error(e);
	}
	}
	
	
	
	/**
	 * Calls getIntValue and returns a int value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private int getIntValue(Element ele, String tagName) {
		int intValue=0;
		try{ 
			intValue = Integer.parseInt(getTextValue(ele,tagName));
		}catch(NumberFormatException e) {
			logger.error(e);
		}
			return intValue;
	}
	
	/**
	 * Calls getFloatValue and returns a Float value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private Float getFloatValue(Element ele, String tagName) {
		return Float.valueOf(getTextValue(ele,tagName));
	}
	/**
	 * @return the myMsgList
	 */
	public List<Message> getMyMsgList() {
		return myMsgList;
	}

	/**
	 * @return the adjustmentList
	 */
	public List<Adjustment> getAdjustmentList() {
		return adjustmentList;
	}

	/**
	 * @return the productRecords
	 */
	public Map<String, ProductSale> getProductRecords() {
		return productRecords;
	}
}
