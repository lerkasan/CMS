package hometasks.cms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoneyTransfer {
	private int id;
	private int merchantId;
	private String merchantName;
	private double sumSent;
	private LocalDate sentDate;
	private boolean status;
	
	public MoneyTransfer() {	
	}
	
	public MoneyTransfer(int id, int merchantId, String merchantName, double sumSent, LocalDate sentDate, boolean status) {
		super();
		this.id = id;
		this.merchantId = merchantId;
		this.merchantName = merchantName;
		this.sumSent = sumSent;
		this.sentDate = sentDate;
		this.status = status;
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
		this.merchantId = merchantId;
	}
	
	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public double getSumSent() {
		return sumSent;
	}

	public void setSumSent(double sumSent) {
		this.sumSent = sumSent;
	}

	public LocalDate getSentDate() {
		return sentDate;
	}

	public void setSentDate(LocalDate sentDate) {
		this.sentDate = sentDate;
	}

	public boolean isStatus() {
		return status;
	} 

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		Formatter aFormat = new Formatter();
		String result = aFormat.format("|   %1$3d   |   %2$25s   |   %3$10.2f   |   %4$8tD   |   %5$5b   |\n", 
				id, merchantName, sumSent, sentDate, status).toString();
		aFormat.close();
		return result;
	}
	
	public static void printMoneyTransferToBeSent(List<MoneyTransfer> moneyTransList) {
		if ( (moneyTransList != null) && (! moneyTransList.isEmpty()) ) {
			System.out.println("\nMoney transfer to be sent:");
			System.out.println("|    id   |         merchant name         |    sum sent    |   sent date  |   status  |");
			for (MoneyTransfer moneyTrans : moneyTransList) {
				System.out.print(moneyTrans);
			}
		}
	}
	
	public static void fillMoneyTransferTableinDB() {
		Connection con = CMS.getConnection();
		try (Statement stm = con.createStatement()) {
			con.setAutoCommit(false); 
			String sql = "insert into transMoney (merchantId, sumSent, sentDate, status) " +
						     "select m.id, m.needToSend, CURRENT_DATE, 0 from merchant m " +
						 	 "where (m.needToSend > m.minSum) and " +
						 	 "({fn timestampdiff(SQL_TSI_DAY, m.lastSent, CURRENT_DATE)} > " +
						 	     "(select p.days from periods p where m.period = p.id) )";
			int result = stm.executeUpdate(sql);
			if (result > 0) {
				sql = "update merchant set needToSend = 0 " + 
					  "where id in " +
					      "(select merchantId from transMoney " +
					      "where status = 0)";
				stm.executeUpdate(sql);
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
	
	public static List<MoneyTransfer> loadFromDB() {
		Connection con = CMS.getConnection();
		List<MoneyTransfer> moneyTransferList = new ArrayList<>();
		try (Statement stm = con.createStatement()) {
			String sql = "select t.id, t.merchantId, m.name, t.sumSent, t.sentDate, t.status " +
						 "from transMoney t left outer join merchant m on t.merchantId = m.id " +
						 "order by t.sentDate, t.sumSent";
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				MoneyTransfer moneyTrans = new MoneyTransfer();
				moneyTrans.setId(rs.getInt("id"));
				moneyTrans.setMerchantId(rs.getInt("merchantId"));
				moneyTrans.setMerchantName(rs.getString("name"));
				moneyTrans.setSumSent(rs.getDouble("sumSent"));
				moneyTrans.setSentDate(rs.getDate("sentDate").toLocalDate());
				moneyTrans.setStatus(rs.getBoolean("status"));
				moneyTransferList.add(moneyTrans);
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
		return moneyTransferList;
	}
	
	public static Set<Integer> sendMoneyTransfer(List<MoneyTransfer> moneyTransList, double availableMoney) {
		Set<Integer> merchantsToSend = new HashSet<>();
		double sum = 0.0;
		if ((moneyTransList != null) && (! moneyTransList.isEmpty())) {
			for (MoneyTransfer moneyTrans : moneyTransList) {
				sum += moneyTrans.getSumSent();
				if (sum <= availableMoney) {
					merchantsToSend.add(moneyTrans.getMerchantId());
				} else {
					break;
				}
			}
			Connection con = CMS.getConnection();
			try (Statement stm = con.createStatement()) {
				con.setAutoCommit(false); 
				String sql = "update transMoney set status = 1, sentDate = CURRENT_DATE " +
							 "where merchantId in " + merchantsToSend.toString().replace('[', '(').replace(']', ')');
				int result = stm.executeUpdate(sql);
				if (result > 0) {
					sql = "update merchant m set m.lastSent = CURRENT_DATE, m.sent = m.sent + " +
						      "( select t.sumSent from transMoney t " +
						      "where (t.merchantId = m.id) and (t.sentDate = CURRENT_DATE) ) " + 
						  "where id in " + merchantsToSend.toString().replace('[', '(').replace(']', ')');
					stm.executeUpdate(sql);
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
		return merchantsToSend;
	}
	
	

}
