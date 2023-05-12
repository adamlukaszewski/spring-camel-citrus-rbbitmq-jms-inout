# spring-camel-rabbit-reply-timeout

Presenting the reply-timeout issue with `spring-rabbit` component. 

## Setup

Start rabbit in docker with management ui:

```bash
docker run -it -p 5672:5672 -p 15672:15672 --hostname my-rabbit --name some-rabbi rabbitmq:3-management
```
