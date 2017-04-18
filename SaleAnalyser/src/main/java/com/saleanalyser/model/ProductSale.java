package com.saleanalyser.model;

/**
 * @author Divya
 *
 */
public class ProductSale {
	private int totalCount;
	private Float totalValue;
	
	public ProductSale(){
		this.totalCount = 0;
		this.totalValue= 0f;
	}
	
	public ProductSale(int count, Float Value){
		this.totalCount =count;
		this.totalValue= Value;
	}

	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * @return the totalValue
	 */
	public Float getTotalValue() {
		return totalValue;
	}

	/**
	 * @param totalValue the totalValue to set
	 */
	public void setTotalValue(Float totalValue) {
		this.totalValue = totalValue;
	}
	
	
}
