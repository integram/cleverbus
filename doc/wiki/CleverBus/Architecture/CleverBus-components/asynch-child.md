# asynch-child

## Description

Component creates new asynchronous message.

New messages can be created from asynchronous and even from synchronous messages (binding type will be *SOFT* for this case).

Current message body will be body of new asynch. message.

### URI format

```
asynch-child:service:operation[?options]
```

where *service *is service name (e.g. *customer*) and *operation *is operation name (e.g. *createCustomer*).

## Options

| Parameter       | Default | Description                                                                       |
| --------------- | ------- | --------------------------------------------------------------------------------- |
| *correlationId* | null    | Unique message ID. If not defined then ID is generated with *UUID.randomUUID().*  |
| *sourceSystem*  | null    | Source system (e.g. CRM). If not defined then default value *IP* (internal integration platform) is used. |
| *bindingType*   | HARD    | Binding type defines how tightly child message does influence parent message:<ul><li>*HARD*: result of child message influences result of parent message (for example when child message ends in *FAILED* state then parent message will in *FAILED* state too)</li><li>*SOFT*: result of child message has no effect to parent message</li></ul> |
| *objectId*      | null    | Object ID which is impacted by this message. For example when we change customer object then this is customer ID. |
| *funnelValue*   | null    | This value is used in detection of concurrent messages which impact identical target object. |

## Example usage

```
asynch-child:customer:createCustomer
asynch-child:customer:createCustomer?bindingType=HARD&correlationId=566&sourceSystem=CRM
```
