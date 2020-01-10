# json-schema-registry
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![](https://img.shields.io/badge/Package-JAR-2396ad)
![](https://img.shields.io/badge/Repository-Maven%20Central-2396ad)  
![](https://img.shields.io/badge/Java-8%2B-d6a827)
![](https://github.com/wigforss/json-schema-registry/workflows/Test%20and%20Deploy/badge.svg) 
[![codecov](https://codecov.io/gh/wigforss/json-schema-registry/branch/master/graph/badge.svg)](https://codecov.io/gh/wigforss/json-schema-registry)


Auto discovery of java classes annotated with ```@JsonSchema```, which are placed into a JsonSchemaRegistry.

Validation of JSON is also included using the bean validation annotation ```@ValidJson```.

## Example
Java Backing bean of the actual data that should comply to a JSON schema
```
@JsonSchema(name = "person", version = "1.0", location = "/person-schema-v1.json")
    private static class Person {
        private String firstName;

        private String lastName;
    }
```
Or location from url
```
@JsonSchema(name = "person", version = "1.0", location = "http://mysite/schemas/person-schema-v1.json")
    private static class Person {
        private String firstName;

        private String lastName;
    }
```

The JSON schema itself which resides on the classpath at the location specified in the location attribute of the ```@JsonSchema``` annotation.
```
{
  "title": "Person",
  "type": "object",
  "properties": {
    "firstName": {
      "type": "string"
    },
    "lastName": {
      "type": "string"
    },
    "age": {
      "description": "Age in years",
      "type": "integer",
      "minimum": 0
    }
  },
  "required": ["firstName", "lastName"]
}
```
And to get the Registry
```
public JsonSchemaRegistry createRegistry(ObjectMapper objectMapper, String packageToScan) {
    return new JsonSchemaScanner(objectMapper).scan(packageToScan);
}
```
