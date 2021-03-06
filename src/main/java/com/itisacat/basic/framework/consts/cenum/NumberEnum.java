package com.itisacat.basic.framework.consts.cenum;

public enum NumberEnum {
	
	ZERO(0), ONE(1), TWO(2), THREE(3), 
	FOUR(4), FIVE(5), SIX(6), SEVEN(7), 
	EIGHT(8), NINE(9), TEN(10), ELEVEN(11), 
	TWELEVE(12), THIRTEEN(13), FOURTEEN(14), 
	FIFTEEN(15), SIXTEEN(16), SEVENTEEN(17), 
	EIGHTEEN(18), NINETEEN(19), TWENTY(20), TWENTYTHREE(23);
	
	private NumberEnum(int code) {
		this.code = code;
	}
	
	private int code;
	
	public int getCode() {
		return code;
	}
	
}
