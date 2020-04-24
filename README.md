# iota-jammer
IOTA Tangle spammer written in Java

## Run Jammer

1: Download
```
sudo wget https://github.com/arne-fuchs/iota-jammer/releases/download/v1.2/iota-jammer-1.2.tar.gz
```
2: Extract Files 
```
tar -xvzf iota-jammer-1.2.tar.gz && cd iota-jammer-1.2
```
3: Run with
```
java -jar iota-jammer-1.2.jar
```
## Agruments for the program:

java -jar iota-jammerXXXX.jar "address XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" EnableNodeList "threads 5"

##  EnableNodeList : 
Uses the Nodes which are listed in the node list. For every given node there will be created a new thread, unless specified with the argument threads. If the option is deactivated the standard node http://node01.iotatoken.nl:14265/ will be used.

#### Usage:
EnableNodeList

## EnableLocalPOW : 
Enables local proof of work for validating transactions. It is more possible to get a valid transaction if local proof of work is enabled.

#### Usage:
EnableLocalPOW

## Seed : 
Specifys which seed should be used to validate the transactions. Can be relevant if you want to send transactions with values. The seed won't be stored anywhere. Be careful saving the seed in script as clear test, because it is then readable for everyone. If no seed is given through argument, a random seed will be created without any funds.

#### Usage:
"seed 81CHARACTERS9SEED9999999999999999999999999999999999999999999999999999999999999999"

## address : 
Specifys where the transaction should be send to. If not specified the transaction will be send to:
9FNJWLMBECSQDKHQAGDHDPXBMZFMQIMAFAUIQTDECJVGKJBKHLEBVU9TWCTPRJGYORFDSYENIQKBVSYKW9NSLGS9UW

#### Usage:
"address 9FNJWLMBECSQDKHQAGDHDPXBMZFMQIMAFAUIQTDECJVGKJBKHLEBVU9TWCTPRJGYORFDSYENIQKBVSYKW9NSLGS9UW"

## tag : 
Specifiys which tag should be used for the transactions. Default tag is IOTAJAMMER. Chars A-Z and the number 9 are allowed.

#### Usage:
"tag YOUR9TAG"

## threads : 
Specifiys how many threads should be initialized for each node. Pc is able to lag if there are many threads with local proof of work enabled.

#### Usage:
"threads 5"

## reconnect : 
Specifiys after how many transactions the spammer should reconnect to the node. It can improve performance for long term use. Default is 0

#### Usage:
"reconnect 100"

## mwm : 
Specifiys which minimum weight magnitued should be used for the transactions. Default is 14.

#### Usage:
"mwm 16"

# depth : 
Specifiys which depth should be used for the transactions. Default is 4.

#### Usage:
"depth 4"


## Donations
even very small ones, are always welcome and makes me happy:
DPGDUNCNMPLKIHR9HFOOKEZUXTVWSCESZNDNXOBYXWOF9FETDQLULIRRWICOQSJMYXXRNWWO9MQPQJGEWMIJVCMNYC
