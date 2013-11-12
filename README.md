
 jmssql  - simple ms sqlserver client.

# About 

 Simple command-line client for sqlserver, open source of course.

 Each line is executed in a separate transaction.

# Usage 

### File-based sql

```bash
java -jar jmssql-0.0.6-uberjar.jar --sqlfile <sqlstatements.sql> --config <java-properties-file.conf>
```

### Interactive

```bash
java -jar jmssql-0.0.6-uberjar.jar --config <java-properties-file.conf>
jmssql> select * from customer
```

### Pipeline 

```bash
echo "select * from customer" | java -jar jmssql-0.0.6-uberjar.jar --config <java-properties-file.conf>
```

or with custom output

```bash
echo "select * from customer" | java -jar jmssql-0.0.6-uberjar.jar --config <java-properties-file.conf> --output json
```

Possible output formats include

* __json__ single json line
* __prettyjson__ json output with pretty printing turned on
* __csv__ comma separated and single-quote delimited values
* __headedcsv__ same as __csv__, attribute names added as first line
* __tab__ tab separated values, no delimiters
* __headedtab__ same as __tab__, attribute names added as first line 

# Dev notes

Complete build with maven

```bash
mvn clean javadoc:jar source:jar install package
```

Have fun
