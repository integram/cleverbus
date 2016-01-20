# extcall

## Description

Apache Camel component for wrapping external calls with checks for duplicate and outdated calls.

### URI format

```
extcall:[keyType]:[targetURI]
```

where *keyType* can be one of:

-   **message** - to generate a key based on message source system and correlation ID, effectively providing duplicate call protection, but not obsolete call protection
-   **entity** - to generate a key based on message objectId property, providing both duplicate call protection and obsolete call protection
-   **custom** - to use a custom key provided in the *ExtCallComponentParams.EXTERNAL\_CALL\_KEY* exchange property

In the first two cases (message and entity), if the *ExtCallComponentParams.EXTERNAL\_CALL\_KEY* exchange property is provided, it will be appended to the generated key.

By default, the *targetURI* is used as the operation. This can be changed by providing an optional *ExtCallComponentParams.EXTERNAL\_CALL\_OPERATION* exchange property. The *targetURI* will still be the URI that is called, if the external call is not skipped, but the duplicate/obsolete protection logic will use the *ExtCallComponentParams.EXTERNAL\_CALL\_OPERATION* value for checking, if the call should be made or skipped.
