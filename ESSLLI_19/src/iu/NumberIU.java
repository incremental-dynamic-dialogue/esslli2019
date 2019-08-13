package iu;

import inpro.incremental.unit.IU;

public class NumberIU extends IU {
	
	String number;
	
	public NumberIU(String n) {
		super();
		number = n;
	}
	

	@Override
	public String toPayLoad() {
		// TODO Auto-generated method stub
		return number;
	}

}
