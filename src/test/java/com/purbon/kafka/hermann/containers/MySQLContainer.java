package com.purbon.kafka.hermann.containers;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/*

db:
    image: debezium/example-mysql:1.5
    container_name: db
    ports:
      - 3306:3306
    environment:
      MYSQL_ROOT_PASSWORD: confluent
      MYSQL_USER: mysqluser
      MYSQL_PASSWORD: mysqlpw
    volumes:
     - ./data/customers.sql:/docker-entrypoint-initdb.d/z99_dump.sql

 */
public class MySQLContainer extends GenericContainer<MySQLContainer> {

    private static final DockerImageName DEFAULT_IMAGE =
            DockerImageName.parse("debezium/example-mysql").withTag("1.5");

    public static final int MYSQL_PORT = 3306;

    private static MySQLContainer container;

    private MySQLContainer() {
        super(DEFAULT_IMAGE);

        withExposedPorts(MYSQL_PORT);

        withEnv("MYSQL_ROOT_PASSWORD", "confluent");
        withEnv("MYSQL_USER", "mysqluser");
        withEnv("MYSQL_PASSWORD", "mysqlpw");

        withFileSystemBind("./data/customers.sql",
                "/docker-entrypoint-initdb.d/z99_dump.sql",
                BindMode.READ_ONLY);
    }

    public String getUrl(String db) {
        return "jdbc:mysql://localhost:"+getMappedPort(MYSQL_PORT)+"/"+db;
    }


    public static MySQLContainer getInstance() {
        if (container == null) {
            container = new MySQLContainer();
        }
        return container;
    }
}
