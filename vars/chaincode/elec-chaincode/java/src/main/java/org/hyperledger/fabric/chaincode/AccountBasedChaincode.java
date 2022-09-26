package org.hyperledger.fabric.chaincode;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import io.netty.handler.ssl.OpenSsl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

public class AccountBasedChaincode extends ChaincodeBase {

    private static Log _logger = LogFactory.getLog(AccountBasedChaincode.class);

    @Override
    public Response init(ChaincodeStub stub) {
        try {
            _logger.info("Init elec-transaction java chaincode");
            String func = stub.getFunction();
            if (!func.equals("init")) {
                return newErrorResponse("function other than init is not supported");
            }
            List<String> args = stub.getParameters();
            if (args.size() != 6) {
                newErrorResponse("Incorrect number of arguments. Expecting 6");
            }
            // Initialize the chaincode
            String account1Key = args.get(0);
            int account1ElecAmount = Integer.parseInt(args.get(1));
            int account1Balance = Integer.parseInt(args.get(2));
            Account account1 = new Account(account1Key, account1ElecAmount, account1Balance);

            String account2Key = args.get(3);
            int account2ElecAmount = Integer.parseInt(args.get(4));
            int account2Balance = Integer.parseInt(args.get(5));
            Account account2 = new Account(account2Key, account2ElecAmount, account2Balance);

            _logger.info(String.format("account %s, elec = %d, balance = %d; account %s, elec = %d, balance = %d", 
                                account1Key, account1ElecAmount, account1Balance, 
                                account2Key, account2ElecAmount, account2Balance));


            stub.putState(account1Key, Utility.toByteArray(account1));
            stub.putState(account2Key, Utility.toByteArray(account2));

            return newSuccessResponse();
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            _logger.info("Invoke java simple chaincode");
            String func = stub.getFunction();
            List<String> params = stub.getParameters();
            if (func.equals("transfer")) {
                return transfer(stub, params);
            }
            if (func.equals("delete")) {
                return delete(stub, params);
            }
            if (func.equals("query")) {
                return query(stub, params);
            }
            if (func.equals("queryHistory")){
                return queryHistory(stub, params);
            }
            if (func.equals("queryAllAccount")){
                return queryAllAccount(stub);
            }
            return newErrorResponse("Invalid invoke function name. Expecting one of: [\"invoke\", \"delete\", \"query\"]");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    // Make a Trasfer
    // Transfer format: {accountFrom, accountTo, transferredAmount, elecPrice}
    // electricity flows from accountFrom to accountTo
    private Response transfer(ChaincodeStub stub, List<String> args) {
        if (args.size() != 4) {
            return newErrorResponse("Incorrect number of arguments. Expecting 4");
        }
        // Parse parameters
        String fromAccountKey = args.get(0);
        String toAccountKey = args.get(1);
        int transferredAmount = Integer.parseInt(args.get(2));
        int elecPrice = Integer.parseInt(args.get(3));

        byte[] fromAccountBytes = stub.getState(fromAccountKey);
        if (fromAccountBytes == null) {
            return newErrorResponse(String.format("Entity %s not found", fromAccountKey));
        }

        byte[] toAccountBytes = stub.getState(toAccountKey);
        if (toAccountBytes == null) {
            return newErrorResponse(String.format("Entity %s not found", toAccountKey));
        }

        Account fromAccount = (Account)Utility.toObject(fromAccountBytes);;
        Account toAccount = (Account)Utility.toObject(toAccountBytes);

        int fromAccountElecAmount = fromAccount.getElecAmount();
        int fromAccountBalance = fromAccount.getBalance();
        int toAccountElecAmount = toAccount.getElecAmount();
        int toAccountBalance = toAccount.getBalance();

        if (transferredAmount > fromAccountElecAmount) {
            return newErrorResponse(String.format("not enough money in account %s", fromAccountKey));
        }

        fromAccountElecAmount -= transferredAmount;
        toAccountElecAmount += transferredAmount;
        fromAccountBalance += transferredAmount * elecPrice;
        toAccountBalance -= transferredAmount * elecPrice;

        fromAccount.setElecAmount(fromAccountElecAmount);
        toAccount.setElecAmount(toAccountElecAmount);
        fromAccount.setBalance(fromAccountBalance);
        toAccount.setBalance(toAccountBalance);

        _logger.info(String.format("new status of %s: %d %d", fromAccountKey, fromAccountElecAmount, fromAccountBalance));
        _logger.info(String.format("new status of %s: %d %d", toAccountKey, toAccountElecAmount, toAccountBalance));

        stub.putState(fromAccountKey, Utility.toByteArray(fromAccount));
        stub.putState(toAccountKey, Utility.toByteArray(toAccount));

        _logger.info("Transfer complete");

        return newSuccessResponse("transfer finished successfully");
        //, ByteString.copyFrom(fromAccountKey + ": " + accountFromValue + " " + accountToKey + ": " + accountToValue, UTF_8).toByteArray());
    }

    // Deletes an entity from state
    // Delete format: {deleteAccount}
    private Response delete(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect number of arguments. Expecting 1");
        }
        String key = args.get(0);
        // Delete the key from the state in ledger
        stub.delState(key);
        return newSuccessResponse();
    }

    // Query callback representing the query of a chaincode
    // Query format: {queryID}
    private Response query(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect number of arguments. Expecting name of the person to query");
        }
        String key = args.get(0);

        byte[] accountBytes = stub.getState(key);
        if (accountBytes == null) {
            return newErrorResponse(String.format("Error: state for %s is null", key));
        }

        Account account = (Account)Utility.toObject(accountBytes);

        _logger.info(String.format("Query Response:\nName: %s, Amount: %d, Balance: %d\n", key, account.getElecAmount(), account.getBalance()));
        return newSuccessResponse("Query Success", accountBytes);
    }

    // Query All accounts in the chain
    //QeuryAll format: {}
    private Response queryAllAccount(ChaincodeStub stub) {
        QueryResultsIterator<KeyValue> queryResultsIterator = stub.getStateByRange("A", "Z");

        JSONArray jsonArray = new JSONArray();
        queryResultsIterator.forEach(keyValue ->  {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("key", keyValue.getKey());
            Account account = (Account) Utility.toObject(keyValue.getValue());
            map.put("accountId", account.getAccountId());
            map.put("elecAmount", account.getElecAmount());
            map.put("balance", account.getBalance());

            jsonArray.put(map);
        });

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("accounts", jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException("exception while generating json object");
        }

        _logger.info(jsonObject.toString());
        return newSuccessResponse(jsonObject.toString());
    }

    // Query the history by key
    // Query history format: {queryID}
    private Response queryHistory(ChaincodeStub stub, List<String> args){
        // Check the number of arguments
        if (args.size() != 1){
            return newErrorResponse("Incorrect number of arguments. Expecting name of the person to query");
        }
        String key = args.get(0);

        if (stub.getState(key) == null){
            return newErrorResponse(String.format("Error: state for %s is null", key));
        }

        // Get history
        QueryResultsIterator<KeyModification> queryResultsIterator = stub.getHistoryForKey(key);

        JSONArray jsonArray = new JSONArray();
        queryResultsIterator.forEach(keyModification -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("transactionId", keyModification.getTxId());
            map.put("timestamp", keyModification.getTimestamp().toString());
            Account account = (Account) Utility.toObject(keyModification.getValue());
            map.put("accountId", account.getAccountId());
            map.put("elecAmount", account.getElecAmount());
            map.put("balance", account.getBalance());
            map.put("isDeleted", keyModification.isDeleted());
            jsonArray.put(map);
        });

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("transactions", jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException("exception while generating json object");
        }

        _logger.info(jsonObject.toString());
        return newSuccessResponse(jsonObject.toString());
    }

    public static void main(String[] args) {
        System.out.println("OpenSSL avaliable: " + OpenSsl.isAvailable());
        new AccountBasedChaincode().start(args);
    }
}