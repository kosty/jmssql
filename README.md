
 jmssql  - simple ms sqlserver client.

# About 

 Simple command-line client for sqlserver, open source of course.

 Each line is executed in a separate transaction.

# Usage 

### File-based sql

```bash
java -jar jmssql-0.0.2-uberjar.jar --sqlfile <sqlstatements.sql> --config <java-properties-file.conf>
```

### Interactive

```bash
java -jar jmssql-0.0.2-uberjar.jar --config <java-properties-file.conf>
```

### Pipeline 

```bash
echo "select * from customer" | java -jar jmssql-0.0.2-uberjar.jar --config <java-properties-file.conf>
```

Have fun
