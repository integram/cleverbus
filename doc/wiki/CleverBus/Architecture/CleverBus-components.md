# CleverBus components

## Description

Implementations of camel components are in *components* Maven module.

## Components overview

| Component                                         | Description                                      |
| ------------------------------------------------- | ------------------------------------------------ |
| [asynch-child](CleverBus-components/asynch-child) | This component creates new asynchronous message. |
| [extcall](CleverBus-components/extcall)           | Component for wrapping external calls with checks for duplicate and outdated calls. |
| [msg-funnel](CleverBus-components/msg-funnel)     | Component for filtering concurrent asynch. messages which influence identical object. |
| [throttling](CleverBus-components/throttling)     | Component for [throttling](http://en.wikipedia.org/wiki/Throttling_process_(computing)) functionality. |
