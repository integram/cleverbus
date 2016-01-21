# CleverBus extensions

## Descriptions

CleverBus extensions is standalone project that contains already implemented routes for possible use in CleverBus application.

CleverBus extensions are not open-sourced, it's proprietary code of Cleverlance company. If you would like to use them then contact CleverBus team.

If you want to write new extensions then see <a href='How-to-implement-new-extensions'>How to implement new extensions</a>

## Extensions overview

| Extension                            | Description |
| ------------------------------------ | ----------- |
| [ARES](ARES)                         | checking if specified company ID is in ARES system and verifies if the company isn't in the registry of debtors |
| [MVCR](MVCR-identity-card-validaton) | checking identity card state, if exists and if yes if it's not stolen |
| [File upload](File-upload)           | uploading files with use *FileRepository* implementation |


## Development info

*Extensions* are located in separated project and Maven modules:

``` xml
<groupId>com.cleverlance.cleverbus.extensions</groupId>
<artifactId>extensions</artifactId>
```

**Stable version**: 0.1

**Development version**: 0.3-SNAPSHOT

**Nightly builds**: <https://hudson2.clance.local/view/CleverBus/job/CleverBus%20extensions/> (Cleverlance proprietary)

See <a href='Development'>development info</a> for where to find CleverBus extensions.
