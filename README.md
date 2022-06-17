<div align="center">
<h1>RatORM</h1>
<h4>Simple, Fast and easy to implement ORM for most popular databases</h4>
<img src="https://forthebadge.com/images/badges/made-with-java.svg" alt="Build with java">
<img src="https://forthebadge.com/images/badges/it-works-why.svg" alt="It works why?">
</div>

## Status:

| Branch  | Tests                                                                                               | Code Quality |
|--------|-----------------------------------------------------------------------------------------------------|--------------|
| master  | ![CircleCI](https://img.shields.io/circleci/build/github/Szczurowsky/RatORM/master?style=for-the-badge) | ![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/Szczurowsky/ratorm/master?style=for-the-badge) |

## Usefull links
Helpful links:
- [GitHub issues](https://github.com/Szczurowsky/RatORM/issues)
- [Docs (Beta)](https://docs.szczurowsky.pl/v/ratorm-wiki/)
- [Javadocs](https://szczurowsky.github.io/RatORM/)

## List of databases
- MongoDB
- MySQL (Soon)
- MariaDB (Soon)
- PostgreSQL (Soon)
- SQLite (Soon)

## MineCodes Repository (Maven or Gradle) ️
```xml
<repository>
  <id>minecodes-repository</id>
  <url>https://repository.minecodes.pl/releases</url>
</repository>
```
```groovy
maven { url "https://repository.minecodes.pl/releases" }
```

### Dependencies (Maven or Gradle)
Framework Core
```xml
<dependency>
    <groupId>pl.szczurowsky</groupId>
    <artifactId>rat-orm-core</artifactId>
    <version>2.0.0-alpha1</version>
</dependency>
```
```groovy
implementation 'pl.szczurowsky:rat-orm-core:2.0.0-alpha1'
```

Database

```xml
<dependency>
    <groupId>pl.szczurowsky</groupId>
    <artifactId>rat-orm-type</artifactId>
    <version>2.0.0-alpha1</version>
</dependency>
```
```groovy
implementation 'pl.szczurowsky:rat-orm-type:2.0.0-alpha1'
```

## Usage
More advance examples in docs
    
### Connect

<details>
<summary>By credentials</summary>

```java
public class Example {
    
    Database database;
    
    public void connect() {
        // Replace MongoDB() with your database type
        this.database = new MongoDB();
        Credentials credentials = new Credentials("Username", "Password", "Name of database", "Host", Port);
        this.database.connect(credentials);
    }
    
}

```

</details>

<details>
<summary>By URI</summary>

```java
public class Example {
    
    Database database;
    
    public void connect() {
        // Replace MongoDB() with your database type
        this.database = new MongoDB();
        this.database.connect("URI String");
    }
    
}

```

</details>

### Create model

<details>
<summary>Model class</summary>

```java
@DBModel(tableName="example-table")
public class ExampleModel extends BaseModel {
    @Field(isPrimaryKey = true)
    int id;
    @Field
    String username = "default value";
    // Custom table name
    @Field(name="test")
    String oneName;
}
```

</details>

<details>
<summary>Initialization of model</summary>

```java
public class Example {
    
    Database database;
    
    public void connect() {
        // Replace MongoDB() with your database type
        this.database = new MongoDB();
        this.database.connect("URI String");
        this.database.initModel(Collections.singleton(ExampleModel.class));
    }
    
}

```

</details>

### Read model(s)

<details>
<summary>Every model</summary>

```java
public class Example {
    
    Database database;
    
    public void connect() {
        // Replace MongoDB() with your database type
        this.database = new MongoDB();
        this.database.connect("URI String");
        this.database.initModel(Collections.singleton(ExampleModel.class));
        List<TestModel> fetched = this.database.fetchAll(ExampleModel.class);
    }
    
}

```

</details>

<details>
<summary>Exact model(s)</summary>

```java
public class Example {
    
    Database database;
    
    public void connect() {
        // Replace MongoDB() with your database type
        this.database = new MongoDB();
        this.database.connect("URI String");
        this.database.initModel(Collections.singleton(ExampleModel.class));
        List<TestModel> fetched = this.database.fetchMatching(ExampleModel.class, "Key", "Value");
    }
    
}

```

</details>

### Save model

<details>
<summary>Save one</summary>

```java
public class Example {
    
    Database database;
    
    public void connect() {
        // Replace MongoDB() with your database type
        this.database = new MongoDB();
        this.database.connect("URI String");
        this.database.initModel(Collections.singleton(ExampleModel.class));
        ExampleModel exampleModel = new ExampleModel();
        this.database.save(exampleModel);
    }
    
}

```

</details>