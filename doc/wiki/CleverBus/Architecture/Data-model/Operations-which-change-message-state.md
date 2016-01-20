# Operations which change message state

The following table presents operations or actions which change message states (see [Data Model](../Data-model) with state workflow diagram):

| Source state      | Target state      | Operation/action |
|-------------------|-------------------|------------------|
| NEW               | PROCESSING        | New request/message starts processing (*org.cleverbus.core.common.asynch.AsynchInMessageRoute*) |
| PROCESSING        | POSTPONED         | Message is being processed but there is conflict with another message with same "[funnel](../CleverBus-components/msg-funnel)" value. |
| PROCESSING        | PARTLY\_FAILED    | Processing failed but there are next tries to finish it. <ul><li>error occured during message processing (*org.cleverbus.core.common.asynch.AsynchMessageRoute*)</li><li>time processing exceeds limit for processing (*org.cleverbus.core.common.asynch.repair.RepairProcessingMsgRoute*)</li></ul> |
| PROCESSING        | WAITING\_FOR\_RES | Message is being processed and waits for response from external system. |
| PROCESSING        | WAITING           | Message is being processed and waits for response from external system (valid for [parent message](../CleverBus-components/asynch-child) only). |
| PROCESSING        | OK                | Message is successfully processed. |
| PROCESSING        | FAILED            | Processing of the message failed - there is no next try for processing. |
| POSTPONED         | PROCESSING        | Previous processing was postponed and started next try. *org.cleverbus.core.common.asynch.queue.PartlyFailedMessagesPoolRoute* |
| POSTPONED         | CANCEL            | Admin canceled further processing in [Admin GUI](../../User-guide/Admin-GUI) |
| POSTPONED         | FAILED            | Message has been waiting for starting processing more then interval defined by *asynch.postponedIntervalWhenFailed. org.cleverbus.core.common.asynch.queue.MessagePollExecutor* |
| PARTLY\_FAILED    | PROCESSING        | Previous processing was postponed and started next try. *org.cleverbus.core.common.asynch.queue.PartlyFailedMessagesPoolRoute* |
| PARTLY\_FAILED    | CANCEL            | Admin canceled further processing in [Admin GUI](../../User-guide/Admin-GUI) |
| WAITING\_FOR\_RES | PROCESSING        | Message got response from external system and continues in processing. |
| WAITING\_FOR\_RES | CANCEL            | Admin canceled further processing in [Admin GUI](../../User-guide/Admin-GUI) |
| WAITING           | FAILED            | Parent message was waiting for processing of child message but at least one child message failed. |
| WAITING           | OK                | All child messages of parent message finished successfully. |
| FAILED            | PROCESSING        | Restart failed message from [admin GUI](../../User-guide/Admin-GUI). |

