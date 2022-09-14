# minifab-elec

This is for user-defined network on local machine.

##  Test Environment

VM: Vmware Fusion on MacOS

OS: Ubuntu 20.04 server

## Tutorials

1. Install prerequisites: docker and go env;

2. Bring up the network:  `sudo ./minifab netup -o stateA.elec.com -i 2.1`

   Note that there are compatibility problems on the newest fabric release. Use 2.1 instead.

3. Check it with `docker ps`. There should be multiple mirrors showing peers, channels, chaincodes.

4. Commit our chaincode to the network: `sudo ./minifab ccup -n elec-chaincode -l java -v 1.0 -p '"init","Plant","10000","500000","Home","10","100"'`

## Useful resources

1. Minifabric Document: https://github.com/hyperledger-labs/minifabric
2. Some videos on Minifabric: https://www.bilibili.com/video/BV1h54y1Y7Eo

3. A Contract Sample Code: https://cloud.tencent.com/developer/article/1402016
4. Chaincode API Document: https://help.aliyun.com/document_detail/141372.html
