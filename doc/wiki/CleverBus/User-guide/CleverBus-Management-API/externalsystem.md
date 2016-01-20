# CleverBus Management API: externalsystem

## Operation: findAll (GET)

This method returns all external systems defined in the class implementing `ExternalSystemProvider` interface.

**URI:** `/v1/externalsystem/findAll/` (GET)

### Input params
N/A

### Output params
Collection of system names

```
[ 
	{"name":"ESB"},
	{"name":"CRM"},
	{"name":"BILLING"}
]
```

