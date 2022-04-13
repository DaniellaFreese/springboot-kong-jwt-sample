# Tutorial on Configuring and Testing JWT Plugin in Gateway 

Using this spring boot application with no security enabled as the backend api, this readme steps through how to install, configure, and test the JWT token.
This tutorial was pieced together following the [Kong JWT Plugin documentation](https://docs.konghq.com/hub/kong-inc/jwt). 

# Prequisites
1. Have the gateway running.
2. Install the deck cli tool. 

# What does the JWT Plugin do?
With the JWT plugin, the Gateway can verify requests with JWT tokens using HS256 or RS256 algorithm before proxying request to designated backend services. 

There are multiple possible configures available to the JWT Plugin including: configuring it globally on the gateway, on a service, or on a particular route. 

For this tutorial the plugin will be configured to run on routes. 

# Objective
Here we will use the JWT Plugin to secure the springboot routes only. High level the steps to do this are the following:

1. Configure the JWT plugin on the Route Resource
2. Create a Consumer Resource
3. Create a JWT secret on the Consumer Resource configured to use HS2566 algorithm and to auto-generate the secret and key.
4. Use the jwt.io debugger to create a jwt token to validate the backend springboot service is secured, and accessible with a properly generated token

# Configure the gateway via declarative yaml (deck)
Using the kong.yaml as a baseline, copy it to a new file (kong-jwt.yaml). 

Sync the configuration to the gateway `deck sync -s kong.yaml` and validate the spring-boot endpoint  can be called without any security

```shell
$ http example.com:8000/spring/hello 
HTTP/1.1 200 
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: keep-alive
Content-Length: 6
Content-Type: text/plain;charset=UTF-8
Date: Tue, 12 Apr 2022 19:33:14 GMT
Expires: 0
Pragma: no-cache
Via: kong/2.8.0
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-Kong-Proxy-Latency: 13
X-Kong-Upstream-Latency: 3
X-XSS-Protection: 1; mode=block

hello!
```

Once validated that the springboot application is accessible from the gateway (no security), lets configure the jwt plugin. To install the plugin to run on the springboot routes only update the deck file: 
```shell
    routes:
      - name: spring
        paths:
          - /spring
        methods:
          - GET
        hosts:
          - example.com
        strip_path: true
        plugins:
          - name: jwt
```
Secondly, in the deck file create a consumer, and configure the jwt_secret on the consumer 
```shell
consumers:
  - custom_id: consumer123
    username: dev
    jwt_secrets:
      - algorithm: HS256
        key: RHDNQFDePhIULbX4lmlTT5gS0wJ7xRNK
        secret: fS4X0FcM7rWPimRNreTzF0vL57t7b3Gl
```

In the end the kong-jwt.yaml should look like the following approximately.
```shell
...
    routes:
      - name: spring
        paths:
          - /spring
        methods:
          - GET
        hosts:
          - example.com
        strip_path: true
        plugins:
          - name: jwt
consumers:
  - custom_id: consumer123
    username: dev
    jwt_secrets:
      - algorithm: HS256
        key: RHDNQFDePhIULbX4lmlTT5gS0wJ7xRNK
        secret: fS4X0FcM7rWPimRNreTzF0vL57t7b3Gl
```
For the jwt_secrets, the key and secret fields are not required. If not provided the gateway will create those.
In that case you can use the admin api to retrieve the information `http :8001/consumers/dev/jwt` 


Once the deck file is ready sync it to the gateway: `deck sync -s kong-jwt.yaml`

# Validate the Springboot Routes are Secured 
Create a JWT token on [jwt.io](https://jwt.io). To the payload add the iss field with the key attribute from the jwt_secrets
and add the secret to the signature section. Take a look at the image below as a sample. Copy the encoded token to use to make the api call. 

![jwt.io token sample](images/jwt-sample.png)

To make the api call execute the following curl command with your jwt token in the authorization header: 
```shell
$ curl http://example.com:8000/spring/hello     -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJSSEROUUZEZVBoSVVMYlg0bG1sVFQ1Z1Mwd0o3eFJOSyIsImV4cCI6MTQ0MjQzMDA1NCwibmJmIjoxNDQyNDI2NDU0LCJpYXQiOjE0NDI0MjY0NTQsIm5hbWUiOiJkYW5ueSBkb2UifQ.Xd5JqRfe0l6j3OX_zUX3KO7KSJzivIxRiveEq1gU67A'
hello!
```

Also validate a failure when removing the header: 
```shell
$ curl http://example.com:8000/spring/hello
{"message":"Unauthorized"}
```




