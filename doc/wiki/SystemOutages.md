# <span id="title-text">CleverBus: System Outages</span>


Cleverbus contains the functionality allows to register expected outages of external systems. Asynchronous messages sent to the system during the outage window are not sent directly to the system, but are postponed until the registered outage ends.

All asynchronous routes to external systems can utilize the "postpone during outage" functionality directly; no special code treatment is necessary. To define the outage, the external system must be defined in the enum implementing `ExternalSystemProvider` interface. The list of all external systems can be retrieved by the REST API method [`GET:/v1/externalsystem/findAll`](externalsystem)

<font color='red'>**FIXME** The postponed messages waiting for the end of the outage can be identified by ...</font>

The external system outage can be registered either through GUI or via JSON REST API.

## Outages - REST API

Following methods can be used to manage the outages of external systems:
[`/v1/plannedoutagesystem/{id}` (GET)](plannedoutages): retrieve the information about the specific outage

[`/v1/plannedoutagesystem/{id}` (DELETE)](plannedoutages): delete the specific outage

[`/v1/plannedoutagesystem/` (PUT)](plannedoutages): edit the specific outage

[`/v1/plannedoutagesystem/` (POST)](plannedoutages): create a new outage, returns an outage ID

[`/v1/plannedoutagesystem/findAll` (GET)](plannedoutages): retrieve all defined outages

[`/v1/plannedoutagesystem/hasSystemOutage` (POST)](plannedoutages): for a given outage it verifies whether it collides with some already defined outage for the same system.