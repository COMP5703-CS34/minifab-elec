# minifab-elec

This is for user-defined network on local machine.

##  Test Environment

VM: Vmware Fusion on MacOS

OS: Ubuntu 20.04 server

## Tutorials

1. Install prerequisites: docker: 

   ```bash
   sudo apt install docker
   sudo apt install docker-compose
   ```

3. Commit our chaincode to the network: 

   ```Bash
   sudo ./minifab up -o stateA.elec.com -i 2.1 -n elec-chaincode -l java -v 1.0 -d true -p '"init","Plant","10000","500000","Home","10","100"'
   ```

## Useful resources

1. Minifabric Document: https://github.com/hyperledger-labs/minifabric
2. Some videos on Minifabric: https://www.bilibili.com/video/BV1h54y1Y7Eo

3. A Contract Sample Code: https://cloud.tencent.com/developer/article/1402016
4. Chaincode API Document: https://help.aliyun.com/document_detail/141372.html
