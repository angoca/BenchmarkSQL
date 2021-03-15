
Instructions for running BenchmarkSQL on PostgreSQL
---------------------------------------------------

# Requirements

Use of JDK8 is required.

## Create the BenchmarkSQL user and a database

[comment]: <> (TODO Include Windows)

As Unix user postgres use the psql shell to connect to the PostgreSQL database
and issue the CREATE USER and CREATE DATABASE commands.

    $ psql postgres
    psql (9.5.2)
    Type "help" for help.

    postgres=# CREATE USER benchmarksql WITH ENCRYPTED PASSWORD 'changeme';
    postgres=# CREATE DATABASE benchmarksql OWNER benchmarksql;
    postgres=# \q
    $

# Compile the BenchmarkSQL source code

[comment]: <> (TODO Change when Maven is included)

As your own UNIX user change into the toplevel directory of the BenchmarkSQL
git repository checkout or the directory that was created by unpacking the
release tarball/zipfile.
Use the ant command to compile the code.

    $ cd benchmarksql
    $ ant
    Buildfile: /home/user/benchmarksql.git/build.xml

    init:
        [mkdir] Created dir: /home/user/benchmarksql/build

    compile:
	[javac] Compiling 11 source files to /home/user/benchmarksql/build

    dist:
	[mkdir] Created dir: /home/user/benchmarksql/dist
	  [jar] Building jar: /home/user/benchmarksql/dist/BenchmarkSQL-6.devel.jar
    BUILD SUCCESSFUL
    Total time: 1 second
    $

# Create the BenchmarkSQL configuration file

Change to the run directory, copy the sample.postgresql.properties file and
edit the copy to match your system setup and desired scaling.

    $ cd run
    $ cp sample.postgresql.properties my_postgres.properties
    $ vi my_postgres.properties
    $

Note that the provided example configuration is meant to test the functionality
of your setup.
That benchmark can connect to the database and execute transactions.
That configuration is NOT a benchmark run.
To make it into one you need to have a configuration that matches your database
server size and workload.
Leave the sizing for now and perform a first functional test.

The BenchmarkSQL database has an initial size of approximately 100MB per
configured warehouse.
A typical setup would be a database of 2-5 times the physical RAM of the
server.

Likewise the number of concurrent database connections (config parameter
terminals) should be something about 2-6 times the number of CPU threads.

Last but not least benchmark runs are normally done for hours, if not days.
This is because on the database sizes above it will take that long to reach a
steady state and make sure that all performance relevant functionality of the
database, like checkpointing and vacuuming, is included in the measurement.

So you can see that with a modern server, that has 32-256 CPU threads and
64-512GBi, of RAM we are talking about thousands of warehouses and hundreds of
concurrent database connections.

# Build the schema and initial database load

Execute the runDatabaseBuild.sh script with your configuration file.

    $ ./runDatabaseBuild.sh my_postgres.properties
    # ------------------------------------------------------------
    # Loading SQL file ./sql.common/tableCreates.sql
    # ------------------------------------------------------------
    create table bmsql_config (
    cfg_name    varchar(30) primary key,
    cfg_value   varchar(50)
    );
    create table bmsql_warehouse (
    w_id        integer   not null,
    w_ytd       decimal(12,2),
    [...]
    Starting BenchmarkSQL LoadData

    driver=org.postgresql.Driver
    conn=jdbc:postgresql://localhost:5432/benchmarksql
    user=benchmarksql
    password=***********
    warehouses=30
    loadWorkers=10
    fileLocation (not defined)
    csvNullValue (not defined - using default 'NULL')

    Worker 000: Loading ITEM
    Worker 001: Loading Warehouse      1
    Worker 002: Loading Warehouse      2
    Worker 003: Loading Warehouse      3
    [...]
    Worker 000: Loading Warehouse     30 done
    Worker 008: Loading Warehouse     29 done
    # ------------------------------------------------------------
    # Loading SQL file ./sql.common/indexCreates.sql
    # ------------------------------------------------------------
    alter table bmsql_warehouse add constraint bmsql_warehouse_pkey
    primary key (w_id);
    alter table bmsql_district add constraint bmsql_district_pkey
    primary key (d_w_id, d_id);
    [...]
    vacuum analyze;
    $

# Run the configured benchmark

    $ ./runBenchmark.sh my_postgres.properties

The benchmark should run for the number of configured concurrent connections
(terminals) and the duration or number of transactions.

The end result of the benchmark will be reported like this:

    01:58:09,081 [Thread-1] INFO   jTPCC : Term-00,
    01:58:09,082 [Thread-1] INFO   jTPCC : Term-00, Measured tpmC (NewOrders) = 179.55
    01:58:09,082 [Thread-1] INFO   jTPCC : Term-00, Measured tpmTOTAL = 329.17
    01:58:09,082 [Thread-1] INFO   jTPCC : Term-00, Session Start     = 2016-05-25 01:58:07
    01:58:09,082 [Thread-1] INFO   jTPCC : Term-00, Session End       = 2016-05-25 01:58:09
    01:58:09,082 [Thread-1] INFO   jTPCC : Term-00, Transaction Count = 10

At this point you have a working setup.

# Scale the benchmark configuration

Change the my_postgres.properties file to the correct scaling
(number of warehouses and concurrent connections/terminals). Switch
from using a transaction count to time based:

        runTxnsPerTerminal=0
	runMins=180

    Rebuild the database (if needed) by running

    $ ./runDatabaseDestroy.sh my_postgres.properties
    $ ./runDatabaseBuild.sh my_postgres.properties

    Then run the benchmark again.

    Rinse and repeat.

# Result report

BenchmarkSQL collects detailed performance statistics and (if
configured) OS performance data. The example configuration file
defaults to a directory starting with my_result_.

Use the generateReport.sh DIRECTORY script to create an HTML file
with graphs. This requires R to be installed, which is beyond the
scope of this HOW-TO.

