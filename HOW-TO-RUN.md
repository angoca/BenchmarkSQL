
Instructions for running BenchmarkSQL
-------------------------------------

Depending on the DBMS, you need to modify a set of files to configure the
connection properties and the environment.

There is a file in the root directory for each DBMS that describes the details
to configure and execute BenchmarkSQL.

These are the global steps for any DBMS.

# Requirements.

* JDK8 is required.
* Driver to access the database.

## Compile the BenchmarkSQL source code.

This is a one-time step, to generate the jar file to execute BenchmarkSQL.

# Create a database and the credentials to access.

In this step the database is created in the DMBS and a set of credentials are
generated.

# Create the BenchmarkSQL configuration file.

Modify the corresponding file under /run.
Make sure you can connect to database with the credentials.

# Build the schema and initial database load.

Creates the tables and other objects in the database.
Also, generates the data for each warehouse, or loads this data from files.

# Run the configured BenchmarkSQL

Run your first benchmark, to make sure everything is correct.

# Scale the benchmark configuration

Tune your database for the workload.

