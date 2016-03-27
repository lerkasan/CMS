package hometasks.cms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

enum Periods {WEEK, TENDAYS, MONTH};

public class Merchant {
	private int id;
	private String name;
	private double charge;
	private Periods period;
	private double minSum;
	private String bankName;
	private String swift;
	private String account;
	private double needToSend;
	private double sent;
	private LocalDate lastSent;
	
	public Merchant() {
		id = -1;
	}

	public Merchant(String name, double charge, Periods period, double minSum, String bankName, String swift,
			String account) {
		this.id = -1;
		this.name = name;
		this.charge = charge;
		this.period = period;
		this.minSum = minSum;
		this.bankName = bankName;
		this.swift = swift;
		this.account = account;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCharge() {
		return charge;
	}

	public void setCharge(double charge) {
		this.charge = charge;
	}

	public Periods getPeriod() {
		return period;
	}

	public void setPeriod(Periods period) {
		this.period = period;
	}

	public double getMinSum() {
		return minSum;
	}

	public void setMinSum(double minSum) {
		this.minSum = minSum;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getSwift() {
		return swift;
	}

	public void setSwift(String swift) {
		this.swift = swift;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public double getNeedToSend() {
		return needToSend;
	}

	public void setNeedToSend(double needToSend) {
		this.needToSend = needToSend;
	}

	public double getSent() {
		return sent;
	}

	public void setSent(double sent) {
		this.sent = sent;
	}

	public LocalDate getLastSent() {
		return lastSent;
	}

	public void setLastSent(LocalDate lastSent) {
		this.lastSent = lastSent;
	}

	@Override
	public String toString() {
		if (id == -1) {
			return "";
		}
		Formatter aFormat = new Formatter();
		String result = aFormat.format("|   %1$3d   |   %2$25s   |   %3$4.2f   |   %4$7s   |   %5$7.2f   |   %6$20s   |   %7$12s   |"+
				"   %8$12s   |   %9$12.2f   |   %10$20.2f   |   %11$8tD   |%n", 
				id, name, charge, period, minSum, bankName, swift, account, needToSend, sent, lastSent).toString();
		aFormat.close();
		return result;
	}
	
	public void loadFromDB(int id) {
		Connection con = CMS.getConnection();
		try (Statement stm = con.createStatement()) {
			String sql = "select id, name, charge, period, minSum, bankName, swift, account, needToSend, sent, lastSent "+
						 "from merchant where id = " + id;
			ResultSet rs = stm.executeQuery(sql);
			if (rs.next()) {
				this.id = rs.getInt("id");
				this.name = rs.getString("name");
				this.charge = rs.getDouble("charge");
				this.period = Periods.values()[rs.getInt("period")-1];
				this.minSum = rs.getDouble("minSum");
				this.bankName = rs.getString("bankName");
				this.swift = rs.getString("swift");
				this.account = rs.getString("account");
				this.needToSend = rs.getDouble("needToSend");
				this.sent = rs.getDouble("sent");
				this.lastSent = rs.getDate("lastSent").toLocalDate();
			} else {
				System.out.println("Merchant not found. There is no merchant with id "+id+" in database.");
			}
			rs.close();
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				e.getSQLState();
				e.getErrorCode();
				e.getMessage();
				e.getCause();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			e1.getSQLState();
			e1.getErrorCode();
			e1.getMessage();
			e1.getCause();
		}
	}
	
	public static List<Merchant> loadMerchantsFromDB() {
		Connection con = CMS.getConnection();
		List<Merchant> merchantList = new ArrayList<>();
		try (Statement stm = con.createStatement()) {
			String sql = "select id, name, charge, period, minSum, bankName, swift, account, needToSend, sent, lastSent from merchant";
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				Merchant merch = new Merchant();
				merch.setId(rs.getInt("id"));
				merch.setName(rs.getString("name"));
				merch.setCharge(rs.getDouble("charge"));
				merch.setPeriod(Periods.values()[rs.getInt("period")-1]);
				merch.setMinSum(rs.getDouble("minSum"));
				merch.setBankName(rs.getString("bankName"));
				merch.setSwift(rs.getString("swift"));
				merch.setAccount(rs.getString("account"));
				merch.setNeedToSend(rs.getDouble("needToSend"));
				merch.setSent(rs.getDouble("sent"));
				merch.setLastSent(rs.getDate("lastSent").toLocalDate());
				merchantList.add(merch);
			}
			rs.close();
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				e.getSQLState();
				e.getErrorCode();
				e.getMessage();
				e.getCause();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			e1.getSQLState();
			e1.getErrorCode();
			e1.getMessage();
			e1.getCause();
		}
		return merchantList;
	}
	
	public static void printMerchantList(List<Merchant> merchantList) {
		if ( (merchantList != null) && (! merchantList.isEmpty()) ) {
			System.out.println("\nMerchants list:");
			System.out.println("|    id   |              name             |  charge  |    period   |    minSum   |        bank name         |       swift      |"+
							   "      account     |   need to send   |           sent           |   last sent  |");
			for (Merchant merch : merchantList) {
				System.out.print(merch);
			}
		}
	}
	
	public double getTotalSumPayed() {
		if (id == -1) {
			System.out.println("Merchant not found. There is no merchant with id "+id+" in database.");
			return 0;
		}
		Connection con = CMS.getConnection();
		try (Statement stm = con.createStatement()) {
			String sql = "select sum(sumPayed) from payment " +
						 "where merchantId = " + id + 
						 " group by merchantId";
			ResultSet rs = stm.executeQuery(sql);
			if (rs.next()) {
				return rs.getDouble(1);
			} 
			rs.close();
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				e.getSQLState();
				e.getErrorCode();
				e.getMessage();
				e.getCause();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			e1.getSQLState();
			e1.getErrorCode();
			e1.getMessage();
			e1.getCause();
		}
		return 0;
	}
	
	/**
	 *returns income (through charge) got by CMS from a merchant
	 */
	public double getGivenIncome() { 
		if (id == -1) {
			System.out.println("Merchant not found. There is no merchant with id "+id+" in database.");
			return 0;
		}
		Connection con = CMS.getConnection();
		try (Statement stm = con.createStatement()) {
			String sql = "select sum(chargePayed) from payment " +
						 "where merchantId = " + id + 
						 " group by merchantId";
			ResultSet rs = stm.executeQuery(sql);
			if (rs.next()) {
				return rs.getDouble(1);
			}
			rs.close();
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				e.getSQLState();
				e.getErrorCode();
				e.getMessage();
				e.getCause();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			e1.getSQLState();
			e1.getErrorCode();
			e1.getMessage();
			e1.getCause();
		}
		return 0;
	}
	
	public static  void printGivenIncomeByMerchants() { 
		Connection con = CMS.getConnection();
		try (Statement stm = con.createStatement()) {
			String sql = "select p.merchantId, m.name, sum(p.chargePayed) as income " +
						 "from payment p left outer join merchant m on m.id = p.merchantId " +
						 "group by p.merchantId, m.name";
			ResultSet rs = stm.executeQuery(sql);
			System.out.println("\nIncome from merchants:");
			while (rs.next()) {
				Formatter aFormat = new Formatter();
				String result = aFormat.format("|   %1$3d   |   %2$25s   |   %3$8.2f   |", 
						rs.getInt("merchantId"), rs.getString("name"), rs.getDouble("income")).toString();
				System.out.println(result);
				aFormat.close();
			} 
			rs.close();
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				e.getSQLState();
				e.getErrorCode();
				e.getMessage();
				e.getCause();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			e1.getSQLState();
			e1.getErrorCode();
			e1.getMessage();
			e1.getCause();
		}
	}
}
