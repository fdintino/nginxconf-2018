# Using NGiNX for release automation at The Atlantic

This repository contains an example environment that implements the techniques described in the 2018 NGINXConf talk [Using NGINX for release automation at The Atlantic](https://www.youtube.com/watch?v=ax5qhChhNpE) ([slides](https://www.slideshare.net/fdintino/using-nginx-for-release-automation-at-the-atlantic)).

## Environment setup

1. Run `git clone --recursive git@github.com:theatlantic/nginxconf-2018.git`

2. Enable wildcard DNS for *.nginxdemo by adding the DNS server 127.0.0.1:19321,
   or add to your /etc/hosts file:
 
    ```
    127.0.0.1   jenkins.nginxdemo develop.djangobeta.nginxdemo plaid.djangobeta.nginxdemo
    ```

    If you have setup docker networking to bind to a different ip address than localhost,
    replace `127.0.0.1` with that ip address.

3. Run `docker-compose up`

4. Visit http://jenkins.nginxdemo:8001/ in a web browser

5. Login with the credentials user: admin, pass: admin

6. There should be an addons-server project. Navigate to it.

7. If the builds have failed (this can happen due to a race condition during startup),
   restart the builds.

8. After the branches have built, you can visit:

    - http://develop.djangobeta.nginxdemo:8001
    - http://plaid.djangobeta.nginxdemo:8001
