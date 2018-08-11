[![Build Status](https://shardbytes.com:10099/buildStatus/icon?job=shardcore&.png)](https://shardbytes.com:10099/job/shardcore)

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

## enable build shields for readme in Jenkins ?
- https://wiki.jenkins.io/display/JENKINS/Role+Strategy+Plugin
- new role anonymous for job, add job name to pattern, set View-Status, assign to Anonymous user in Assign Roles
- then install and use build status plugin https://plugins.jenkins.io/embeddable-build-status


## Jenkins setup and stuff
- install https://wiki.jenkins.io/display/JENKINS/Installing+Jenkins+on+Ubuntu
- setup with 8080 (open port ufw)
- get access to jenkins linuxaccount (maybe same password as setup ?)
- setup SSL certificate (Java keystore .jks) -> edit `/etc/default/jenkins`   -> replace last line :
    `JENKINS_ARGS="--webroot=/var/cache/$NAME/war --httpPort=-1 --httpsPort=10099 --httpsKeyStore=/var/lib/jenkins/jenkins.jks --httpsKeyStorePassword=kysfaggot"`
- ( add jenkins.jks ssl certificate to `/var/lib/jenkins` directory)
- reboot or something, jenkins should be running with SSL on port 10099