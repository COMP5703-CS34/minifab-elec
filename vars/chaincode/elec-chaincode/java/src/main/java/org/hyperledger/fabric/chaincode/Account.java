package org.hyperledger.fabric.chaincode;

public class Account {
    private String accountId;
    private Double elecAmount;
    private Double balance;
    
    public Account(String accountId, Double elecAmount, Double balance) {
        this.accountId = accountId;
        this.elecAmount = elecAmount;
        this.balance = balance;
    }

    private Account() {}

    public String getAccountId() {
        return accountId;
    }

    public Double getElecAmount() {
        return elecAmount;
    }

    public Double getElecAmount() {
        return elecAmount;
    }

    public void setElecAmount(String elecAmount) {
        this.elecAmount = elecAmount;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}