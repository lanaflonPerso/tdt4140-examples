# DB-based persistence of domain data

DB-based persistence is provided by (an implementation of) the **IDbAccess** interface. It includes life-cycle methods corresponding to CRUD operations, so by using these methods, the corresponding DB operations may be performed to keep the DB in sync with the local domain model object structure.

The implementation is split in three:
* **AbstractDbAccessImpl**: Implements the DB-independent logic, i.e. life-cycle of collections of linked domain objects.
* **DbAccessImpl**: Inherits from **AbstractDbAccessImpl** and adds DB logic using SQL.
* **DbAccessHelper**: Helper class for the DB access.

Note that identifiers are not part of the domain model and are handled by the **IdMap** class, which provides a pair of maps from a String identifier to/from domain objects of some type.
 
A class diagram of the main classes related to db-based persistence is shown below.

<img src="db-persistence-classes.png" alt="DB-based persistence" style="width: 800px;"/>
