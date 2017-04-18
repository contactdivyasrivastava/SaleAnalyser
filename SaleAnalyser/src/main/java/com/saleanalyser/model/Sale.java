/**
 * 
 */
package com.saleanalyser.model;

/**
 * @author Divya
 *
 */
public class Sale {
	private String productType;
	private Float saleValue;
	private int salesCount;
	
	
	
	public Sale() {
		productType=null;
		saleValue = 0f;
		salesCount = 0;
	}
	
	public Sale(String productType,Float saleValue,int salesCount) {
		this.productType=productType;
		this.saleValue = saleValue;
		this.salesCount = salesCount;
		
	}
	
	
	
	/**
	 * @return the productType
	 */
	public String getProductType() {
		return productType;
	}
	/**
	 * @param productType the productType to set
	 */
	public void setProductType(String productType) {
		this.productType = productType;
	}
	/**
	 * @return the saleValue
	 */
	public Float getSaleValue() {
		return saleValue;
	}
	/**
	 * @param saleValue the saleValue to set
	 */
	public void setSaleValue(Float saleValue) {
		this.saleValue = saleValue;
	}
	/**
	 * @return the saleCount
	 */
	public int getSalesCount() {
		return salesCount;
	}
	/**
	 * @param saleCount the saleCount to set
	 */
	public void setSalesCount(int salesCount) {
		this.salesCount = salesCount;
	}
	
	
}
