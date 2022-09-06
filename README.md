# minifab-elec

This is for user-defined network on local machine.

##  Test Environment

Vmware Fusion on MacOS

Ubuntu 20.04 server

## Tutorials

1. Install prerequisites: docker and go env;

2. Bring up the network:  `./minifab up -o stateA.elec.com`

   Wait for the minifab to bring up the network.

3. Check it with `docker ps`. There should be multiple mirrors showing peers, channels, chaincodes.

## Useful resources

1. Minifabric Document: https://github.com/hyperledger-labs/minifabric
2. Some videos on Minifabric: https://www.bilibili.com/video/BV1h54y1Y7Eo
