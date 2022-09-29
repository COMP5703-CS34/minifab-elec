package org.hyperledger.fabric.chaincode;

import java.io.Serializable;

public class Account implements Serializable {
    private String accountId;
    private int elecAmount;
    private int balance;
    private String password;

    public Account(String accountId, int elecAmount, int balance, String password) {
        this.accountId = accountId;
        this.elecAmount = elecAmount;
        this.balance = balance;
        this.password = password;
    }

    private Account() {}

    public String getAccountId() {
        return accountId;
    }

    public int getElecAmount() {
        return elecAmount;
    }

    public int getBalance() {
        return balance;
    }

    public String getPassword() {
        return password;
    }

    public void setElecAmount(int elecAmount) {
        this.elecAmount = elecAmount;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}