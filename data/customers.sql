#GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'replicator' IDENTIFIED BY 'replpass';
#GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT  ON *.* TO 'debezium' IDENTIFIED BY 'dbz';

# Create the database that we'll use to populate data and watch the effect in the binlog
CREATE DATABASE hermann;
GRANT ALL PRIVILEGES ON hermann.* TO 'mysqluser'@'%';

use hermann;

create table orders (
                        id INT PRIMARY KEY,
                        product VARCHAR(256),
                        quantity INT,
                        price FLOAT
);