package hometasks.cms;

import static org.junit.Assert.*;

import org.junit.Test;

public class MerchantTest {

	@Test
	public void loadFromDBTest() {
		Merchant m1 = new Merchant();
		m1.loadFromDB(1);
		assertEquals("Jim Smith Ltd.", m1.getName());
	}
	
	@Test
	public void getTotalSumPayedTest() {
		Merchant m1 = new Merchant();
		m1.loadFromDB(2);
		assertEquals(1422.0, m1.getTotalSumPayed(), 0.00001);
	}

}
