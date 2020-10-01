package com.game.dto.wallet;

import java.util.Date;

public class WalletAudit {
	public WalletAudit() {
		super();
	}

	Integer walletAuditId;
	String action;
	Integer walletId;
	String walletSource;

	public Integer getWalletId() {
		return walletId;
	}

	public void setWalletId(Integer walletId) {
		this.walletId = walletId;
	}

	Double auditAmount;
	String orderId;
	private Date updatedTimeStamp;

	public Integer getWalletAuditId() {
		return walletAuditId;
	}

	public void setWalletAuditId(Integer walletAuditId) {
		this.walletAuditId = walletAuditId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Double getAuditAmount() {
		return auditAmount;
	}

	public void setAuditAmount(Double auditAmount) {
		this.auditAmount = auditAmount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Date getUpdatedTimeStamp() {
		return updatedTimeStamp;
	}

	public void setUpdatedTimeStamp(Date updatedTimeStamp) {
		this.updatedTimeStamp = updatedTimeStamp;
	}

	public String getWalletSource() {
		return walletSource;
	}

	public void setWalletSource(String walletSource) {
		this.walletSource = walletSource;
	}
}
