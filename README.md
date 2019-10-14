## **Natlex** -  Testcase for backend developer

- [x]  Add REST CRUD API for Sections and GeologicalClasses. Each Section has structure:
```json5 
{
    "name":"Section 1","geologicalClasses": [
        { "name": "Geo Class 1", "code": "GC1" },...
        ]
}
```
- [x] Add API GET /sections/by-code?code=... that returns a list of all Sections that have geologicalClasses
   with the specified code.

- [x] Add APIs for importing and exporting XLS files. Each XLS file contains list of sections and geological
classes. Example:

Section name | Class 1 name | Class 1 code | Class 2 name | Class 2 code
------------ | ------------ | ------------ | ------------ | ------------ 
Section 1    | Geo Class 1  | GC1          | Geo Class 2  | GC2
Section 2    | Geo Class 2  | GC2
Section 3    | Geo Class 5  | GCX7

Files should be parsed asynchronously, result should be stored id DB.
* API POST /import (file) returns ID of the Async Job and launches importing.
* API GET /import/{id} returns result of importing by Job ID ("DONE", "IN PROGRESS", "ERROR")
* API GET /export returns ID of the Async Job and launches exporting.
* API GET /export/{id} returns result of parsed file by Job ID ("DONE", "IN PROGRESS", "ERROR")
* API GET /export/{id}/file returns a file by Job ID (throw an exception if exporting is in process)

Requirements:

* Technology stack: Spring, Hibernate, Spring Data, Spring Boot, Gradle/Maven. (OK)
* All data (except files) should be in JSON format (OK)
* Basic Authorization should be supported (OK Created 2 users/passwords: "user"/"userpw", "admin"/"adminpw").

`Tema Grinevich :: nedvard@gmail.com`
