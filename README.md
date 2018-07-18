# ShardCore is our main JVM server for our page

## -> [shardbytes.com](https://shardbytes.com)

Based on [Javalin](https://javalin.io/), [KMongo](https://litote.org/kmongo/), Thymeleaf and similar stuff. Writen in Kotlin.

## **Notes**

**To run server one needs to specify configuration in config.json**
( or specify java argument with config file path in ./run )
( see configTemplate.json )

### How to deploy ?
- ./deploy -> it will git pull and build

### Custom CI ->
- start shardcore screen with ./corescreen ( stop all existing shardcore screens )
- run ./integrate inside shardcore directory and it will 1. interrupt server 2. deploy 3. run server
- therefore you can have crontab like this :
	```
	0 * * * * cd /home/faggot/shardcore && ./integrate |& tee logs/ci.txt
	```

### RECONFIGURE FIREWALL PORTFORWARDING
- https://linuxacademy.com/howtoguides/posts/show/topic/11630-internal-port-forwarding-on-linux-using-the-firewall
- so we dont run java with sudo

- ufw rules ->  /etc/ufw/before.rules ( !! to top of file )

    ```
    *nat
    :PREROUTING ACCEPT [0:0]
    -A PREROUTING -p tcp --dport 443 -j REDIRECT --to-port 10443
    -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 10080
    COMMIT
    ```