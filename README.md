# GrenadeMapper

A client-server GrenadeMapper application, using Akka [`remote actors`](https://doc.akka.io/docs/akka/current/remoting.html) communicating over TCP.

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

The client is automated to send Person co-ordinates as well as the Grenade's co-ordinates and its radius, to the Server. The client follows `fire-and-forget` for its requests, but continuously listens for incoming response from Server.

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

The client events are modelled as below:
```scala
trait Event
case object Start extends Event
case object Tick extends Event
case class Message(msg: String) extends Event
```

## Server

The Server is responsible for maintaining the state of the Grid as well as updating the Grid as per client's input.

The Server runs autonomously, i.e. even if the Client has stopped sending requests to the Server, it will keep updating the GridState at a regular time-interval defined in the `application.conf` file (and print the visual reprentation of the Grid for each interval):

```properties
server{
  params{
    grid_width = 100
    grid_length = 100
    tick_interval_seconds = 5
  }
}
```

The Server works on the below rules & assumptions:

* If a Person dies on the same step of 2 Grenade's lifecycle, the Grenade which has lesser time left is given credit for killing the Person, and the Server will response after the lifecycle of that Grenade comes to an end.
* If the same Grenade kills 2 Persons in its lifecycle, then it will send 2 responses (for each dead Person) at the end of its lifecycle.
* The Person will automatically jump once any Grenade's lifecycle ends.
* If the client sends a Person coordinate and at-least 1 grenade is in explosion state, and there is an already alive Person, then this will move the Person to the input co-ordinates.


The client events are modelled as below:
```scala
trait Event
case object Start extends Event
case object Tick extends Event
case object PersonDead extends Event
case object InvalidCoordinates extends Event
case class Message(msg: String) extends Event
```

To run tests for the Server spec:
```console
cd Server
sbt test
```
