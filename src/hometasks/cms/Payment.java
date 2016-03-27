package hometasks.cms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class Payment {
	private int id;
	private int merchantId;
	private int customerId;
	private String goods;
	private double sumPayed;
	private double chargePayed;
	private LocalDate paymentDate;
	
	public Payment() {	
	}
	
	public Payment(int id, int merchantId, int customerId, String goods, double sumPayed, double chargePayed, LocalDate paymentDate) {
		this.id = id;
		this.merchantId = merchantId;
		this.customerId = customerId;
		this.goods = goods;
		this.sumPayed = sumPayed;
		this.chargePayed = chargePayed;
		this.paymentDate = paymentDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(int merchantId) {
		Connection con = CMS.getConnection();
		String sql = "select count(*) from merchant where id = " + merchantId;
		try (Statement stm = con.createStatement()) {
			ResultSet rs = stm.executeQuery(sql);
			if (rs.next() && (rs.getInt(1) == 1)) {
				this.merchantId = merchantId;
			} else {
				System.out.println("In DB there is no such merchant with merchantId passed as a parameter to setMerchantId.");
			}
			rs.close();
			con.close();
		} catch (SQLException e3) {
			e3.printStackTrace();
			e3.getSQLState();
			e3.getErrorCode();
			e3.getMessage();
			e3.getCause();
		}
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		Connection con = CMS.getConnection();
		String sql = "select count(*) from customer where id = " + customerId;
		try (Statement stm = con.createStatement()) {
			ResultSet rs = stm.executeQuery(sql);
			if (rs.next() && (rs.getInt(1) == 1)) {
				this.customerId = customerId;
			} else {
				System.out.println("In DB there is no such customer with customerId passed as a parameter to setCustomerId.");
			}
			rs.close();
			con.close();
		} catch (SQLException e3) {
			e3.printStackTrace();
			e3.getSQLState();
			e3.getErrorCode();
			e3.getMessage();
			e3.getCause();
		}
	}

	public String getGoods() {
		return goods;
	}

	public void setGoods(String goods) {
		this.goods = goods;
	}

	public double getSumPayed() {
		return sumPayed;
	}

	public void setSumPayed(double sumPayed) {
		this.sumPayed = sumPayed;
	}

	public double getChargePayed() {
		return chargePayed;
	}
	public void setChargePayed(double chargePayed) {
		this.chargePayed = chargePayed;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	public void addPaymentToDB() {
		Merchant merch = new Merchant();
		merch.loadFromDB(merchantId);
		
		Connection con = CMS.getConnection();
		String sql = "insert into payment (dt, merchantId, customerId, goods, sumPayed, chargePayed) " +
				 	 "values (CURRENT_TIMESTAMP, ?, ?, ?, ?, 0,01*?*?)";
		try (PreparedStatement stm = con.prepareStatement(sql)) {
			
			con.setAutoCommit(false); 
			stm.setInt(1, merchantId);
			stm.setInt(2, customerId);
			stm.setString(3, goods);
			stm.setDouble(4, sumPayed);
			stm.setDouble(5, sumPayed);
			stm.setDouble(6, merch.getCharge());
			stm.executeUpdate();
			sql = "update merchant set needToSend = needToSend + 0,01*?*? where id = ?";
			try (PreparedStatement stm1 = con.prepareStatement(sql)) {
				stm1.setDouble(1, sumPayed);
				stm1.setDouble(2, merch.getCharge());
				stm1.setInt(3, merchantId);
				stm1.executeUpdate();
			}
			con.commit();
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				e.getSQLState();
				e.getErrorCode();
				e.getMessage();
				e.getCause();
				con.rollback();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			e1.getSQLState();
			e1.getErrorCode();
			e1.getMessage();
			e1.getCause();
			try {
				con.rollback();
			} catch (SQLException e2) {
				e2.printStackTrace();
				e2.getSQLState();
				e2.getErrorCode();
				e2.getMessage();
				e2.getCause();
			}
		}
		
	}
	
}
