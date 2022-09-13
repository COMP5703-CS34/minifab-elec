package org.hyperledger.fabric.chaincode;

import java.util.List;

import com.google.protobuf.ByteString;
import io.netty.handler.ssl.OpenSsl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.nio.charset.StandardCharsets.UTF_8;

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
            Account account1 = Account(account1Key, account1ElecAmount, account1Balance);

            String account2Key = args.get(3);
            int account2ElecAmount = Integer.parseInt(args.get(4));
            int account2Balance = Integer.parseInt(args.get(5));
            Account account2 = Account(account2Key, account2ElecAmount, account2Balance);

            _logger.info(String.format("account %s, elec = %d, balance = %d; account %s, elec = %d, balance = %d", 
                                account1Key, account1ElecAmount, account1Balance, 
                                account2Key, account2ElecAmount, account2Balance));

            stub.putState(account1Key, (new ObjectMapper()).writeValueAsBytes(account1));
            stub.putState(account2Key, (new ObjectMapper()).writeValueAsBytes(account2));

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
            return newErrorResponse("Invalid invoke function name. Expecting one of: [\"invoke\", \"delete\", \"query\"]");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    // Make a Trasfer
    // Transfer format: {accountFrom, accountTo, transferredAmount, elecPrice}
    private Response transfer(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3) {
            return newErrorResponse("Incorrect number of arguments. Expecting 4");
        }
        // Parse parameters
        String fromAccountKey = args.get(0);
        String toAccountKey = args.get(1);
        int transferredAmount = Integer.parseInt(args.get(2);
        int elecPrice = Integer.parseInt(args.get(3));

        String fromAccountStr = stub.getStringState(fromAccountKey);
        if (fromAccountStr == null) {
            return newErrorResponse(String.format("Entity %s not found", accountFromKey));
        }

        String toAccountStr = stub.getStringState(toAccountKey);
        if (toAccountStr == null) {
            return newErrorResponse(String.format("Entity %s not found", accountToKey));
        }

        ObjectMapper mapper = new ObjectMapper();
        Account fromAccount = mapper.readValue(fromAccountStr, Account.class);
        Account toAccount = mapper.readValue(toAccountStr, Account.class);

        int fromAccountElecAmount = fromAccount.elecAmount;
        int fromAccountBalance = fromAccount.balance;
        int toAccountElecAmount = toAccount.elecAmount;
        int toAccountBalance = toAccount.balance;

        if (transferredAmount * elecPrice > fromAccountBalance) {
            return newErrorResponse(String.format("not enough money in account %s", accountFromKey));
        }

        fromAccountElecAmount -= transferredAmount;
        toAccountElecAmount += transferredAmount;
        fromAccountBalance += transferredAmount * elecPrice;
        toAccountBalance -= transferredAmount * elecPrice;

        _logger.info(String.format("new status of %s: %d %d", fromAccountKey, fromAccountElecAmount, fromAccountBalance));
        _logger.info(String.format("new status of %s: %d %d", toAccountKey, toAccountElecAmount, toAccountBalance));

        stub.putState(fromAccountKey, mapper.writeValueAsBytes(fromAccount));
        stub.putState(toAccountKey, mapper.writeValueAsBytes(toAccount));

        _logger.info("Transfer complete");

        return newSuccessResponse("transfer finished successfully", ByteString.copyFrom(accountFromKey + ": " + accountFromValue + " " + accountToKey + ": " + accountToValue, UTF_8).toByteArray());
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
        String accountStr	= stub.getStringState(key);

        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(accountStr, Account.class);
        
        if (val == null) {
            return newErrorResponse(String.format("Error: state for %s is null", key));
        }
        _logger.info(String.format("Query Response:\nName: %s, Amount: %d, Balance\n", key, account.elecAmount, acconut.balance));
        return newSuccessResponse();
    }

    public static void main(String[] args) {
        System.out.println("OpenSSL avaliable: " + OpenSsl.isAvailable());
        new AccountBasedChaincode().start(args);
    }

}