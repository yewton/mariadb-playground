mariadb-playground
===

Toy project to play with MariaDB Galera Cluster.

Usage
---

### Bootstrap Galera Cluster

```
docker-compose run --rm db2 mysql_install_db --datadir=/var/lib/mysql
docker-compose run --rm db3 mysql_install_db --datadir=/var/lib/mysql
docker-compose up -d db1
docker-compose logs -f
# wait for `[Note] WSREP: Synchronized with group, ready for connections'
^C
docker-compose up -d
``` 

### Test

```
./gradlew flywayMigrate clean test -i 
```

```
mariadbplayground.MariaDbTest > loadbalance STANDARD_OUT
    jdbc:mariadb:loadbalance://127.0.0.1:13306,127.0.0.1:13307,127.0.0.1:13308/test
    0: db1
    1(ReadOnly): db1
    2: db1
    3(ReadOnly): db3
    4: db2
    5(ReadOnly): db2
    6: db1
    7(ReadOnly): db2
    8: db3
    9(ReadOnly): db1

mariadbplayground.MariaDbTest > failover STANDARD_OUT
    jdbc:mariadb:failover://127.0.0.1:13306,127.0.0.1:13307,127.0.0.1:13308/test
    0: db3
    1: db3
    2: db1
    3: db3
    4: db2
    5: db2
    6: db1
    7: db1
    8: db2
    9: db2

mariadbplayground.MariaDbTest > sequential STANDARD_OUT
    jdbc:mariadb:sequential://127.0.0.1:13306,127.0.0.1:13307,127.0.0.1:13308/test
    0: db1
    1: db1
    2: db1
    3: db1
    4: db1
    5: db1
    6: db1
    7: db1
    8: db1
    9: db1

mariadbplayground.MariaDbTest > replication STANDARD_OUT
    jdbc:mariadb:replication://127.0.0.1:13306,127.0.0.1:13307,127.0.0.1:13308/test
    0: db1
    1(ReadOnly): db2
    2: db1
    3(ReadOnly): db2
    4: db1
    5(ReadOnly): db2
    6: db1
    7(ReadOnly): db3
    8: db1
    9(ReadOnly): db3

Gradle Test Executor 16 finished executing tests.
```

Links
----

- [Connector/Jの設定確認 – variable\.jp \[データベース,パフォーマンス,運用\]](http://variable.jp/2018/08/18/connector-j%E3%81%AE%E8%A8%AD%E5%AE%9A%E7%A2%BA%E8%AA%8D/)
- [MariaDB Galera Clusterを動かしてみた \| ten\-snapon\.com](https://ten-snapon.com/archives/2123)
