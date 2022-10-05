package org.hyperledger.fabric.chaincode;

import java.io.Serializable;

public class Account implements Serializable {
    private String accountId;
    private double elecAmount;
    private double balance;
    private String password;
    private String identity;

    public Account(String accountId, double elecAmount, double balance, String password, String identity) {
        this.accountId = accountId;
        this.elecAmount = elecAmount;
        this.balance = balance;
        this.password = password;
        this.identity = identity;
    }

    public String getAccountId() {
        return accountId;
    }

    public double getElecAmount() {
        return elecAmount;
    }

    public double getBalance() {
        return balance;
    }

    public String getPassword() {
        return password;
    }

    public String getIdentity() {
        return identity;
    }

    public void setElecAmount(double elecAmount) {
        this.elecAmount = elecAmount;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}