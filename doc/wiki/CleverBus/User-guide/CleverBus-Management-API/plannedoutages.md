# CleverBus Management API: plannedoutages

## Retrieve an outage

Retrieves the speficic outage, identified by an unique `id`

**URI:** `/v1/plannedoutagesystem/{id}`  
**Method:** GET

### Input
`{id}`: (URL) numerical ID of the outage instance. 

### Output
Outage object:

`(long) id`: numerical ID of the outage instance  
`(string) externalSystemCode`: code of the system being affected by the outage
`(dateTime) startOutageTimestamp`: start of the outage
`(dateTime) endOutageTimestamp`: end of the outage
`(string) description`: Outage description

#### Example
```
{
	"id":2,
	"externalSystemCode":"CRM",
	"startOutageTimestamp":"2015-06-29T23:05:00.000Z",
	"endOutageTimestamp":"2015-06-30T19:45:00.000Z",
	"description": "HW upgrade"
}
```

## Delete an outage

Deletes the outage identified by an unique `id`

**URI:** `/v1/plannedoutagesystem/{id}`  
**Method:** DELETE

### Input
`{id}`: (URL) numerical ID of the outage instance. 

### Output
N/A

## Update an outage

Updates the parameters of a specific outage.

**URI:** `/v1/plannedoutagesystem/`  
**Method:** PUT

### Input
Outage object - see above for details

### Output
N/A

## Create an outage

Creates a new outage and returns its outage ID.

**URI:** `/v1/plannedoutagesystem/`  
**Method:** POST

### Input
Outage object - see above for details

### Output
numerical ID of the new outage instance.

## Find all outages

Retrieve the list of all defined outages.

**URI:** `/v1/plannedoutagesystem/findAll`  
**Method:** GET

### Input
N/A

### Output
Array of outage objects. See above for the description of outage object's fields.

```
[
	{
		"id":2,
		"externalSystemCode":"CRM",
		"startOutageTimestamp":"2015-06-29T23:05:00.000Z",
		"endOutageTimestamp":"2015-06-30T19:45:00.000Z",
		"description": "HW upgrade"
	},
	{
		"id":3,
		"externalSystemCode":"BILLING",
		"startOutageTimestamp":"2015-06-29T23:05:00.000Z",
		"endOutageTimestamp":"2015-06-30T19:45:00.000Z",
		"description": "HW upgrade"
	}
]
```

## Find conflicting outage

For a given outage it verifies whether it collides with some already defined outage for the same system. Used eg. in GUI as a validator during the definition of a new outage.

**URI:** `/v1/plannedoutagesystem/hasSystemOutage`  
**Method:** POST

### Input
Outage object - see above for details

### Output
TRUE: The 
