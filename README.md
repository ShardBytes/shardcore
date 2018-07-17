# ShardCore is our main JVM server for our page

## -> [shardbytes.com](https://s)

Based on [Javalin](https://javalin.io/), [KMongo](https://litote.org/kmongo/) and similar stuff. Writen in Kotlin.

## **Notes**

**To run server one needs to specify configuration in config.json**
( or specify java argument with config file path in ./run )
( see configTemplate.json )

### Deploy ( ./deploy )
```
git pull
gradle build
./run
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