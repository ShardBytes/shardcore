## Welcome, my dude
# ShardCore = our main JVM server for our page

## pls visit -> [shardbytes.com](https://shardbytes.com) (if it's not down) ( it probably is )

Based on [Javalin](https://javalin.io/), [KMongo](https://litote.org/kmongo/), Thymeleaf, Angular and other weird things which apparently seem popular in web industry and I like them lmao. Writen with love, tears and Kotlin <3.

## **Notes**

- **To run server one needs to specify configuration in config.json**
	- ( or specify java argument with config file path in ./run )
	- ( see configTemplate.json )
    - run server by executing `run` command from anywhere

**Create "static" and "logs" directories because the server serves them statically (or Javalin will rage)**

## deploy ?
- use Jenkins faggt

## Service
- see `shardcore.service`, place in `/lib/systemd/system` and use ->
    - start/restart/stop -> `sudo systemctl start/restart/stop shardcore`
    - logs tail -> `sudo journalctl -f -u shardcore`
    - last 100 lines of logs ? -> `sudo journalctl -n 100 --no-pager -u shardcore`
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