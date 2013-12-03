Copyright to :
Yue Xing(yuexing@andrew.cmu.edu)

Features :
1. Management : Maven, version control : SVN, log : log4j
2. Support UDP/TCP 
3. Support configure file updates(dropbox/FTP), a FTPServer Class has been designed, to use it need a little work :)

MainClass : 
msg.lab0.amixyue.MessagePasser.java
Command 1: sendto [name] [kind] [content]// send a message
Command 2: receive// get a message from queue

Test :
--------------------TCP ------------------------
1.
Configuration :
- Name : alice
  IP : 128.237.244.96
  Port : 12344
  Protocal : TCP
- Name : charlie
  IP : 128.237.244.96
  Port : 12998
  Protocal : TCP
Alice:
sendto charlie Ack test

Charlie:
receive
2. 
SendRules :
- Action : drop
  Src : alice
  Dest : charlie
  Kind : Ack
  ID : 0
- Action : delay
  Kind : Request
- Action : duplicate
  Dest : charlie
  ID : 3
3.
ReceiveRules :
- Action : drop
  Kind : Request
- Action : duplicate
  Dest : charlie
  ID : 2

--------------------UDP ------------------------
1.
Configuration :
- Name : alice
  IP : 128.237.244.96
  Port : 12344
  Protocal : UDP
- Name : charlie
  IP : 128.237.244.96
  Port : 12998
  Protocal : UDP

Alice:
sendto charlie Ack test

Charlie:
receive
2. 
SendRules :
- Action : drop
  Src : alice
  Dest : charlie
  Kind : Ack
  ID : 0
- Action : delay
  Kind : Request
- Action : duplicate
  Dest : charlie
  Nth: 3
3.
ReceiveRules :
- Action : drop
  Kind : Request
- Action : duplicate
  Dest : charlie
  ID : 2