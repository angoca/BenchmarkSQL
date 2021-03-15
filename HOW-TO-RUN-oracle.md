
Instructions for running BenchmarkSQL on Oracle
------------------------------------------------

# Requirements

The following assumes a default installation of oracle-xe-11.2.0-1.0.

## Compile the BenchmarkSQL source code.

Copy the ojdbc<version>.jar to use with Oracle in /lib, or make
sure that the environment variable ORACLE_HOME is set properly
and the JDBC driver is found at $ORACLE_HOME/lib.

You can download the Oracle 12c JDBC driver (ojdbc7.jar) here:
http://www.oracle.com/technetwork/database/features/jdbc/jdbc-drivers-12c-download-1958347.html

## Create the BenchmarkSQL user and a database

Creating the benchmarksql user run the following commands in sqlplus
under the sysdba account:

```
<<_EOF_

CREATE USER benchmarksql
	IDENTIFIED BY "bmsql1"
	DEFAULT TABLESPACE users
	TEMPORARY TABLESPACE temp;

GRANT CONNECT TO benchmarksql;
GRANT CREATE PROCEDURE TO benchmarksql;
GRANT CREATE SEQUENCE TO benchmarksql;
GRANT CREATE SESSION TO benchmarksql;
GRANT CREATE TABLE TO benchmarksql;
GRANT CREATE TRIGGER TO benchmarksql;
GRANT CREATE TYPE TO benchmarksql;
GRANT UNLIMITED TABLESPACE TO benchmarksql;

_EOF_
```

# Create the BenchmarkSQL configuration file

Change to the run directory, copy the sample.oracle.properties  file and edit
the copy to match your system setup and desired scaling.

    $ cd run
    $ cp sample.oracle.properties my_oracle.properties
    $ vi my_oracle.properties
    $

Note that the provided example configuration is meant to test the functionality
of your setup.

# Build the schema and initial database load.

ToDo

# Run the configured BenchmarkSQL

ToDo

# Scale the benchmark configuration

ToDo


