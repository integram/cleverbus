# CleverBus components

## Description

Implementations of camel components are in *components* Maven module.

## Components overview

| Component                                         | Description                                      |
| ------------------------------------------------- | ------------------------------------------------ |
| [asynch-child](asynch-child) | This component creates new asynchronous message. |
| [extcall](extcall)           | Component for wrapping external calls with checks for duplicate and outdated calls. |
| [msg-funnel](msg-funnel)     | Component for filtering concurrent asynch. messages which influence identical object. |
| [throttling](throttling)     | Component for [throttling](http://en.wikipedia.org/wiki/Throttling_process_(computing)) functionality. |
