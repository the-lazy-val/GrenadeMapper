# GrenadeMapper

A client-server GrenadeMapper application, using Akka `remote actors` communicating over TCP.

## Quickstart

* start Server
```console
cd Server
sbt run
```
* start Client in a seperate terminal
```console
cd Client
sbt run
```

## Client

The client is automated to send Person co-ordinates as well as the Grenade's co-ordinates and its radius, to the Server.

The client behaviour can be configured through editing the below section of the `application.conf` file:

```properties
client{
  params{
    grid_width = 100
    grid_length = 100
    max_num_bombs = 5
    max_bomb_radius = 10
    tick_interval_seconds = 10
    }
  }
```

* (grid_width, grid_length) -> defines the dimensions of the Grid over which the Person/Grenade can be spawned.
* max_num_bombs -> max number of grenades that the client can spawn simultaneously
* max_bomb_radius -> max radius for each bomb
* tick_interval_seconds -> interval in seconds after which the Client can randomly send/not send a message to the Server for spawning a Person/Grenade.
