# minifab-elec

This is for user-defined network on local machine.

##  Test Environment

VM: Vmware Fusion on MacOS

OS: Ubuntu 20.04 desktop

## Tutorials

1. Set up the network: See the documentation.

3. Useful Commands:

   ```Bash
   # Set up the network
   sudo ./minifab up -o stateA.elec.com -i 2.1 -n elec-chaincode -l java -v 1.0 -d true -e true -p '"init"'
   
   # Invoke Chaincode function
   sudo ./minifab invoke -p "'function','parameter1','parameter2',..."
   
   # Clean up the network
   sudo ./minifab cleanup
   ```

## Useful resources

1. Minifabric Document: https://github.com/hyperledger-labs/minifabric
2. Some videos on Minifabric: https://www.bilibili.com/video/BV1h54y1Y7Eo

3. A Contract Sample Code: https://cloud.tencent.com/developer/article/1402016
4. Chaincode API Document: https://help.aliyun.com/document_detail/141372.html
