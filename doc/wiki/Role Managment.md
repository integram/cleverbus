<link rel="stylesheet" type="text/css" href="cleverbus-wiki.css" media="all">

# System Management Architecture - User/Role Managment

##**Preface**

CleverBus product doesn't provide standardized implementation for user/role 
relationship so that there is no possibility to define such a relations. 
This releations are seen as usefull when the access restriction for given resource o function 
must be define in CleverBus implementations.

This document is going to design such a implementation.

##**Reference**

| Ref. | Description|
|------|------------|
|[CLVBUS-198][1] | Related developmnet task|
|[RBAC][2]| Role Based Access Control wiki|
|[ACL][3]| Access Control List|
|[RBAC vs ACL Study][5]| NIST - Comparing Simple RBAC Models and ACL's (John Barkley)|
|[RBAC Models][6]|IEEE - RBAC Reference Models (Oct 26 1995)|
|[JSR-250][4]| Common Annotations for Java Platform v1.2 (Feb 15 2013)|

##**RBAC**

* **RBAC 0** - The base model, is at the bottom, indicating that it is the minimum 
requirement for any system that professes to support RBAC
* **RBAC 1** - Adds the concept of role hierarchies (situations where roles can 
inherit permissions from other roles).
* **RBAC 2** - Adds constraints (which impose restrictions on acceptable 
configurations of the different components of RBAC).
* **RBAC 3** - consolidated model, includes RBAC1 and RBAC2 and, by transitivity
RBAC 0

![RBAC models relations][8]

![ ][7]

###**Benefits**
- It provides mechanisms to secure the system againts fraud
- Tt provides access control to selected resource and functionality provided by 
the system
- It enables centralized user/role management
- It directly supports three well-known security principles: least privilege, 
separation of duties, and data abstraction

###**Processing phases**

>**Administration**
>
> The Administration phase consists of creating and maintaining user and 
object security attributes. Administration tools are usually privileged 
applications. Administration usually occurs the least often of the three phases. 

>**Session**
> The Session phase consists of establishing, changing the characteristics of, 
> and removing sessions. A session is a set of processes, called subjects,
which act on behalf of a user. Session establishment involves authenticating the 
user, creating one or more subjects, and associating user security attributes 
with each subject. The Session phase usually occurs more often than the 
Administration phase and less often than the Enforcement phase.
> 
>**Enforcement**
> The Enforcement phase consists of comparing the user security attributes 
> associated with the subject (i.e., the subject security attributes) to object 
> security attributes in order to grant or deny access. The Enforcement phase 
> occurs every time asubject attempts to access an object and is usually 
> the most frequently occurring phase of the three.




[1]: https://jira.cleverlance.com/jira/browse/CLVBUS-198
[2]: http://en.wikipedia.org/wiki/Role-based_access_control
[3]: http://en.wikipedia.org/wiki/Access_control_list
[4]: attachments/JSR/jsr-250-1.2-final.pdf
[5]: attachments/study/Barkley97a.pdf
[6]: attachments/IEEE/sandhu96.pdf
[7]: attachments/rbac_models.jpg
[8]: attachments/rbac_models_relations.jpg