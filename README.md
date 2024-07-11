# IoT System API

This is the IoT System API, which manages IoT devices and their monitoring. The API allows registration, authentication, and management of devices, as well as device status monitoring.

## Technologies Used

- Java 17
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- Lombok
- JPA (Java Persistence API)
- MySQL
- Swagger/OpenAPI
- Gradle

## Project Structure

### Controllers

1. **AuthenticationController**
   - Manages user authentication.
2. **DeviceController**
   - Manages IoT devices.
3. **MonitoringController**
   - Manages the monitoring of IoT devices.

### Services

1. **AuthenticationService**
   - Service responsible for user authentication and registration.
2. **DeviceService**
   - Service responsible for device management.
3. **MonitoringService**
   - Service responsible for device monitoring.
4. **UserService**
   - Service responsible for user management.

## Endpoints

### AuthenticationController

- `POST /auth/register` - Register a new user
- `POST /auth/login` - Authenticate a user
- `POST /auth/logout` - Logout a user
- `GET /auth/user` - Get authenticated user information

### DeviceController

- `GET /devices` - Get devices
- `GET /devices/pageable` - Get devices with pagination and filtering
- `GET /devices/{deviceCode}` - Get a device by its code
- `POST /devices/command/{deviceCode}` - Send a command to a device
- `POST /devices` - Add a new device
- `PUT /devices/{deviceCode}` - Update an existing device
- `DELETE /devices/{id}` - Delete a device
- `GET /devices/{deviceCode}/monitorings` - Get paginated monitoring for a device

### MonitoringController

- `GET /monitoring` - Get monitorings
- `POST /monitoring` - Add a new monitoring

## Running the Application

### Prerequisites

- Java 17
- MySQL

### Initial Setup

1. Clone the repository:
   ```bash
   git clone <REPOSITORY_URL>
   ```
2. Navigate to the project directory:
   ```bash
   cd project-name
   ```
3. Configure the database in the `application.properties` file:
   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/database_name
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### Running the Application

1. Compile and run the application:
   ```bash
   ./gradlew bootRun
   ```
2. Access the API documentation on Swagger:
   ```
   http://localhost:8080/swagger-ui.html
   ```

## Consuming the API with Postman

### Setting up Postman

- Download and install [Postman](https://www.postman.com/).
- Create a new collection for the API endpoints.

### Request Examples

The requests can be made to the local address `http://localhost:8080` or to the production API `https://iot-system.rodolfo-silva-api.com`.

### Rquest to AuthenticationController

#### Register a New User

#### Description

Register a new user in the system with necessary credentials. If successful, returns a token.

- **Example of Register Request in Postman**
  ![Register Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/POST-register-a-new-user.png)

- Method: POST
- URL: `http://localhost:8080/auth/register` or `https://iot-system.rodolfo-silva-api.com/auth/register`

- CURL:

  ```bash
  curl --location 'https://iot-system.rodolfo-silva-api.com/auth/register' \\
  --header 'Content-Type: application/json' \\
  --data '{
    "name": "User da Silva",
    "email": "user.silva@gmail.com",
    "password": "Abc123",
    "username": "usersilva"
  }'

  ```

- Response (raw JSON):
  ```json
  {
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvcmciLCJleHAiOjE1OTAwODMwMjcsImlhdCI6MTU5MDA4MzAyN30.qpIToU8_gwK9PX8E2lXIN_QuXaE59VU6hvb17i9t9wc"
  }
  ```

#### Login a User

#### Description

Authenticate a user using their credentials. If successful, returns a token.

- **Example of Login Request in Postman**
  ![Login Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/POST-login-a-user.png)

- Method: POST
- URL: `http://localhost:8080/auth/login` or `https://iot-system.rodolfo-silva-api.com/auth/login`
- CURL:
  ```bash
  curl --location 'https://iot-system.rodolfo-silva-api.com/auth/login' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "username":"usersilva",
    "password": "Abc@123"
  }
  '
  ```
- Response (raw JSON):
  ```json
  {
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2aWNhciIsImV4cCI6MTU5MDA4MzAyNywiaWF0IjoxNTkwMDgzMDI3fQ.tgKAFKN8w6QPH64-Ed-VP6Hk36LDGYU-EUDBqlS6A"
  }
  ```

#### Logout a User

#### Description

Log out the authenticated user and invalidate the current session token.

- **Example of Logout Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/POST-logout-a-user.png)

- Method: POST
- URL: `http://localhost:8080/auth/logout` or `https://iot-system.rodolfo-silva-api.com/auth/logout`
- CURL:
  ```bash
  curl --location --request POST 'https://iot-system.rodolfo-silva-api.com/auth/logout' \
  --header 'Authorization: Bearer token_generated_in_login_request'
  ```
- Response (raw JSON):
  ```json
  {
    "status": 200,
    "message": "User was successfully logged out.",
    "timestamp": "2024-07-13T14:34:05.979847"
  }
  ```

### Request to DeviceController

#### Add a New Device

#### Description

Register a new device in the system with necessary details.

- **Example of Add Device Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/POST-add-a-new-device.png)

- Method: POST
- URL: `http://localhost:8080/devices` or `https://iot-system.rodolfo-silva-api.com/devices`
- CURL:
  ```bash
  curl --location 'https://iot-system.rodolfo-silva-api.com/devices' \
  --header 'Authorization: Bearer token_generated_in_login_request' \
  --header 'Content-Type: application/json' \
  --data '{
    "deviceName": "Climate Sensor",
    "description": "Temperature and humidity sensor for greenhouses",
    "industryType": "Agriculture",
    "manufacturer": "Greenhouse Solutions",
    "url": "http://localhost:8080/devices/command/DVC00001",
    "deviceStatus": "OFF",
    "commands": [
        {
            "operation": "Activate",
            "description": "Turn on the climate sensor",
            "result": "Climate sensor activated",
            "format": "JSON",
            "command": {
                "command": "Activate Climate Sensor",
                "parameters": [
                    {
                        "name": "sensor_id",
                        "description": "The unique identifier of the climate sensor"
                    }
                ]
            }
        },
        {
            "operation": "Deactivate",
            "description": "Turn off the climate sensor",
            "result": "Climate sensor deactivated",
            "format": "JSON",
            "command": {
                "command": "Deactivate Climate Sensor",
                "parameters": [
                    {
                        "name": "sensor_id",
                        "description": "The unique identifier of the climate sensor"
                    }
                ]
            }
        },
        {
            "operation": "Get",
            "description": "Retrieve climate data",
            "result": "Climate data retrieved",
            "format": "JSON",
            "command": {
                "command": "Deactivate Performance Monitor",
                "parameters": [
                    {
                        "name": "climate_sensor_id",
                        "description": "The unique identifier of the climate sensor"
                    }
                ]
            }
        },
        {
            "operation": "Adjust",
            "description": "Adjust climate settings",
            "result": "Climate settings adjusted",
            "format": "JSON",
            "command": {
                "command": "Activate Signal Booster",
                "parameters": [
                    {
                        "name": "settings",
                        "description": "Climate settings to be adjusted"
                    }
                ]
            }
        }
    ]
  }'
  ```
- Response (raw JSON):

  ```json
  {
    "id": 1,
    "deviceCode": "DVC00001",
    "deviceName": "Climate Sensor",
    "description": "Temperature and humidity sensor for greenhouses",
    "industryType": "Agriculture",
    "manufacturer": "Greenhouse Solutions",
    "url": "http://localhost:8080/devices/command/DVC00001",
    "deviceStatus": "OFF",
    "commands": [
      {
        "id": 1,
        "operation": "Activate",
        "description": "Turn on the climate sensor",
        "result": "Climate sensor activated",
        "format": "JSON",
        "command": {
          "id": 1,
          "command": "Activate Climate Sensor",
          "parameters": [
            {
              "id": 1,
              "name": "sensor_id",
              "description": "The unique identifier of the climate sensor"
            }
          ]
        }
      },
      {
        "id": 2,
        "operation": "Deactivate",
        "description": "Turn off the climate sensor",
        "result": "Climate sensor deactivated",
        "format": "JSON",
        "command": {
          "id": 2,
          "command": "Deactivate Climate Sensor",
          "parameters": [
            {
              "id": 2,
              "name": "sensor_id",
              "description": "The unique identifier of the climate sensor"
            }
          ]
        }
      },
      {
        "id": 3,
        "operation": "Get",
        "description": "Retrieve climate data",
        "result": "Climate data retrieved",
        "format": "JSON",
        "command": {
          "id": 3,
          "command": "Deactivate Performance Monitor",
          "parameters": [
            {
              "id": 3,
              "name": "climate_sensor_id",
              "description": "The unique identifier of the climate sensor"
            }
          ]
        }
      },
      {
        "id": 4,
        "operation": "Adjust",
        "description": "Adjust climate settings",
        "result": "Climate settings adjusted",
        "format": "JSON",
        "command": {
          "id": 4,
          "command": "Activate Signal Booster",
          "parameters": [
            {
              "id": 4,
              "name": "settings",
              "description": "Climate settings to be adjusted"
            }
          ]
        }
      }
    ],
    "user": {
      "id": 1,
      "name": "User da Silva",
      "email": "user.silva@gmail.com",
      "username": "usersilva",
      "role": "USER",
      "enabled": true,
      "authorities": [
        {
          "authority": "USER"
        }
      ],
      "accountNonExpired": true,
      "credentialsNonExpired": true,
      "accountNonLocked": true
    },
    "createdAt": "2024-07-13T14:43:01.715131"
  }
  ```

#### Get a Device by Device Code

#### Description

Retrieve details of a specific device by its unique device code.

- **Example of Get Device Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/GET-a-device-by-device-code.png)

- Method: GET
- URL: `http://localhost:8080/devices` or `https://iot-system.rodolfo-silva-api.com/devices`
- CURL:
  ```bash
  curl --location 'https://iot-system.rodolfo-silva-api.com//devices/DVC00001' \
  --header 'Authorization: Bearer token_generated_in_login_request'
  ```
- Response (raw JSON):

  ```json
  {
    "id": 1,
    "deviceCode": "DVC00001",
    "deviceName": "Climate Sensor",
    "description": "Temperature and humidity sensor for greenhouses",
    "industryType": "Agriculture",
    "manufacturer": "Greenhouse Solutions",
    "url": "http://localhost:8080/devices/command/DVC00001",
    "deviceStatus": "OFF",
    "commands": [
      {
        "id": 1,
        "operation": "Activate",
        "description": "Turn on the climate sensor",
        "result": "Climate sensor activated",
        "format": "JSON",
        "command": {
          "id": 1,
          "command": "Activate Climate Sensor",
          "parameters": [
            {
              "id": 1,
              "name": "sensor_id",
              "description": "The unique identifier of the climate sensor"
            }
          ]
        }
      },
      {
        "id": 2,
        "operation": "Deactivate",
        "description": "Turn off the climate sensor",
        "result": "Climate sensor deactivated",
        "format": "JSON",
        "command": {
          "id": 2,
          "command": "Deactivate Climate Sensor",
          "parameters": [
            {
              "id": 2,
              "name": "sensor_id",
              "description": "The unique identifier of the climate sensor"
            }
          ]
        }
      },
      {
        "id": 3,
        "operation": "Get",
        "description": "Retrieve climate data",
        "result": "Climate data retrieved",
        "format": "JSON",
        "command": {
          "id": 3,
          "command": "Deactivate Performance Monitor",
          "parameters": [
            {
              "id": 3,
              "name": "climate_sensor_id",
              "description": "The unique identifier of the climate sensor"
            }
          ]
        }
      },
      {
        "id": 4,
        "operation": "Adjust",
        "description": "Adjust climate settings",
        "result": "Climate settings adjusted",
        "format": "JSON",
        "command": {
          "id": 4,
          "command": "Activate Signal Booster",
          "parameters": [
            {
              "id": 4,
              "name": "settings",
              "description": "Climate settings to be adjusted"
            }
          ]
        }
      }
    ],
    "user": {
      "id": 1,
      "name": "User da Silva",
      "email": "user.silva@gmail.com",
      "username": "usersilva",
      "role": "USER",
      "enabled": true,
      "authorities": [
        {
          "authority": "USER"
        }
      ],
      "accountNonExpired": true,
      "credentialsNonExpired": true,
      "accountNonLocked": true
    },
    "createdAt": "2024-07-13T14:43:01.715131"
  }
  ```

#### Update an Existing Device

#### Description

Update the details of an existing device by its device code.

- **Example of Update Device Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/PUT-update-an-exixting-device.png)

- Method: PUT
- URL: `http://localhost:8080/devices/DVC00001` or `https://iot-system.rodolfo-silva-api.com/devices/DVC00001`
- CURL:
  ```bash
  curl --location --request PUT 'https://iot-system.rodolfo-silva-api.com/devices/DVC00001' \
  --header 'Authorization: Bearer token_generated_in_login_request' \
  --header 'Content-Type: application/json' \
  --data '{
    "deviceName": "Climate Sensor Update",
    "description": "Temperature and humidity sensor for greenhouses Update",
    "industryType": "Agriculture Update",
    "manufacturer": "Greenhouse Solutions Update",
    "url": "http://localhost:8080/devices/command/DVC00001",
    "deviceStatus": "ON",
    "commands": [
        {
            "operation": "Activate Update",
            "description": "Turn on the climate sensor Update",
            "result": "Climate sensor activated Update",
            "format": "JSON Update",
            "command": {
                "command": "Activate Climate Sensor Update",
                "parameters": [
                    {
                        "name": "sensor_id",
                        "description": "The unique identifier of the climate sensor Update"
                    }
                ]
            }
        }
    ]
  }'
  ```
- Response (raw JSON):

  ```json
  {
    "id": 1,
    "deviceCode": "DVC00001",
    "deviceName": "Climate Sensor Update",
    "description": "Temperature and humidity sensor for greenhouses Update",
    "industryType": "Agriculture Update",
    "manufacturer": "Greenhouse Solutions Update",
    "url": "http://localhost:8080/devices/command/DVC00001",
    "deviceStatus": "ON",
    "commands": [
      {
        "id": 111,
        "operation": "Activate Update",
        "description": "Turn on the climate sensor Update",
        "result": "Climate sensor activated Update",
        "format": "JSON Update",
        "command": {
          "id": 1,
          "command": "Activate Climate Sensor Update",
          "parameters": [
            {
              "id": 1,
              "name": "sensor_id",
              "description": "The unique identifier of the climate sensor Update"
            }
          ]
        }
      }
    ],
    "user": {
      "id": 1,
      "name": "User da Silva",
      "email": "user.silva@gmail.com",
      "username": "usersilva",
      "role": "USER",
      "enabled": true,
      "authorities": [
        {
          "authority": "USER"
        }
      ],
      "accountNonExpired": true,
      "credentialsNonExpired": true,
      "accountNonLocked": true
    },
    "createdAt": "2024-07-13T14:43:01.715131"
  }
  ```

#### Get Devices with Pagination and Filtering

#### Description

Retrieve a paginated and filtered list of devices. Supports various filters such as status, industry type, device name, and user name.

#### Parameters

- `pageNo` (optional): The page number to retrieve. Default is `0`.
- `pageSize` (optional): The number of records per page. Default is `10`.
- `sortBy` (optional): The field to sort by. Default is `deviceCode`.
- `sortDir` (optional): The direction of sorting. Default is `asc` (ascending). Use `desc` for descending order.
- `status` (optional): Filter devices by their status.
- `industryType` (optional): Filter devices by their industry type.
- `deviceName` (optional): Filter devices by their name.
- `userName` (optional): Filter devices by the associated user's name.
- `description` (optional): Filter devices by their description.
- `deviceCode` (optional): Filter devices by their device code.

- **Example of Get Devices with Pagination and Filtering Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/GET-all-pageable-devices.png)

- Method: GET
- URL: `http://localhost:8080/devices/pageable` or `https://iot-system.rodolfo-silva-api.com/devices/pageable`
- CURL:
  ```bash
  curl --location 'https://iot-system.rodolfo-silva-api.com/devices/pageable?pageNo=0&pageSize=10&sortBy=deviceCode&sortDir=desc&deviceCode=DVC00001&deviceStatus=OFF&deviceName=Climate Sensor&description=Temperature and humidity sensor for greenhouses&industryType=Agriculture&userName=User da Silva' \
  --header 'Authorization: Bearer token_generated_in_login_request' \
  --data ''
  ```
- Response (raw JSON):
  ```json
  {
    "content": [
      {
        "id": 1,
        "deviceCode": "DVC00001",
        "deviceName": "Climate Sensor",
        "description": "Temperature and humidity sensor for greenhouses",
        "industryType": "Agriculture",
        "manufacturer": "Greenhouse Solutions",
        "url": "http://localhost:8080/devices/command/DVC00001",
        "deviceStatus": "OFF",
        "commands": [
          {
            "id": 1,
            "operation": "Activate",
            "description": "Turn on the climate sensor",
            "result": "Climate sensor activated",
            "format": "JSON",
            "command": {
              "id": 1,
              "command": "Activate Climate Sensor",
              "parameters": [
                {
                  "id": 1,
                  "name": "sensor_id",
                  "description": "The unique identifier of the climate sensor"
                }
              ]
            }
          },
          {
            "id": 2,
            "operation": "Deactivate",
            "description": "Turn off the climate sensor",
            "result": "Climate sensor deactivated",
            "format": "JSON",
            "command": {
              "id": 2,
              "command": "Deactivate Climate Sensor",
              "parameters": [
                {
                  "id": 2,
                  "name": "sensor_id",
                  "description": "The unique identifier of the climate sensor"
                }
              ]
            }
          },
          {
            "id": 3,
            "operation": "Get",
            "description": "Retrieve climate data",
            "result": "Climate data retrieved",
            "format": "JSON",
            "command": {
              "id": 3,
              "command": "Deactivate Performance Monitor",
              "parameters": [
                {
                  "id": 3,
                  "name": "climate_sensor_id",
                  "description": "The unique identifier of the climate sensor"
                }
              ]
            }
          },
          {
            "id": 4,
            "operation": "Adjust",
            "description": "Adjust climate settings",
            "result": "Climate settings adjusted",
            "format": "JSON",
            "command": {
              "id": 4,
              "command": "Activate Signal Booster",
              "parameters": [
                {
                  "id": 4,
                  "name": "settings",
                  "description": "Climate settings to be adjusted"
                }
              ]
            }
          }
        ],
        "user": {
          "id": 1,
          "name": "User da Silva",
          "email": "user.silva@gmail.com",
          "username": "usersilva",
          "role": "USER",
          "enabled": true,
          "authorities": [
            {
              "authority": "USER"
            }
          ],
          "accountNonExpired": true,
          "credentialsNonExpired": true,
          "accountNonLocked": true
        },
        "createdAt": "2024-07-13T15:15:32.734521"
      }
    ],
    "pageNo": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1,
    "last": true
  }
  ```

#### Get All Devices

#### Description

Retrieve a list of all devices managed by the system.

- **Example of Get All Devices Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/GET-all-devices.png)

- Method: GET
- URL: `http://localhost:8080/devices` or `https://iot-system.rodolfo-silva-api.com/devices`
- CURL:
  ```bash
  curl --location 'https://iot-system.rodolfo-silva-api.com/devices' \
  --header 'Authorization: Bearer token_generated_in_login_request' \
  --data ''
  ```
- Response (raw JSON):
  ```json
  [
    {
      "id": 1,
      "deviceCode": "DVC00001",
      "deviceName": "String",
      "description": "String",
      "industryType": "String",
      "manufacturer": "String",
      "url": "https://iot-system.rodolfo-silva-api.com/devices/command/DVC00001",
      "deviceStatus": "ON",
      "commands": [
        {
          "id": 1,
          "operation": "String",
          "description": "String",
          "result": "String",
          "format": "String",
          "command": {
            "id": 1,
            "command": "String",
            "parameters": [
              {
                "id": 1,
                "name": "String",
                "description": "String"
              }
            ]
          }
        }
      ],
      "user": {
        "id": 1,
        "name": "User da Silva",
        "email": "user.silva@gmail.com",
        "username": "usersilva",
        "role": "USER",
        "enabled": true,
        "authorities": [
          {
            "authority": "USER"
          }
        ],
        "accountNonLocked": true,
        "credentialsNonExpired": true,
        "accountNonExpired": true
      },
      "createdAt": "2024-07-10T18:44:31"
    },
    {
      "id": 2,
      "deviceCode": "DVC00002",
      "deviceName": "String",
      "description": "String",
      "industryType": "Automotive",
      "manufacturer": "String",
      "url": "https://iot-system.rodolfo-silva-api.com/devices/command/DVC00002",
      "deviceStatus": "ON",
      "commands": [
        {
          "id": 2,
          "operation": "String",
          "description": "String",
          "result": "String",
          "format": "String",
          "command": {
            "id": 2,
            "command": "String",
            "parameters": [
              {
                "id": 2,
                "name": "String",
                "description": "String"
              }
            ]
          }
        }
      ],
      "user": {
        "id": 1,
        "name": "User da Silva",
        "email": "user.silva@gmail.com",
        "username": "usersilva",
        "role": "USER",
        "enabled": true,
        "authorities": [
          {
            "authority": "USER"
          }
        ],
        "accountNonLocked": true,
        "credentialsNonExpired": true,
        "accountNonExpired": true
      },
      "createdAt": "2024-07-12T08:35:34.797433"
    }
  ]
  ```

#### Delete a Device by Device Code

#### Description

Delete a specific device by its device code.

- **Example of Delete Device Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/DELETE-a-device.png)

- Method: GET
- URL: `http://localhost:8080/devices` or `https://iot-system.rodolfo-silva-api.com/devices`
- CURL:
  ```bash
  curl --location --request DELETE 'https://iot-system.rodolfo-silva-api.com/devices/DVC00001' \
  --header 'Authorization: Bearer token_generated_in_login_request'
  ```
- Response (raw JSON):
  ```json
  {
    "status": 200,
    "message": "Device was successfully deleted",
    "timestamp": "2024-07-12T10:09:22.4498815"
  }
  ```

#### Send a Command to a Device

#### Description

Send a specific command to a device by its device code.

- **Example of Send Command to Device Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/POST-send-device-command.png)

- Method: POST
- URL: `http://localhost:8080/devices/command/DVC00001` or `https://iot-system.rodolfo-silva-api.com/devices/command/DVC00001`
- CURL:
  ```bash
  curl --location --request POST  'https://iot-system.rodolfo-silva-api.com/devices/command/DVC00001' \
  --header 'Authorization: Bearer token_generated_in_login_request' \
  --header 'Content-Type: application/json' \
  --data '   {
    "commands":
        {
            "operation": "Deactivate",
            "description": "Turn off the moisture sensor",
            "result": "Sensor deactivated",
            "format": "JSON",
            "command": {
                "command": "Deactivate Moisture Sensor",
                "parameters": [
                    {
                        "name": "sensor_id",
                        "description": "The unique identifier of the sensor"
                    }
                ]
            }
        }
   }'
  ```
- Response (raw JSON):
  ```json
  {
    "id": 1,
    "deviceCode": "DVC00001",
    "deviceName": "Climate Sensor",
    "description": "Temperature and humidity sensor for greenhouses",
    "industryType": "Agriculture",
    "manufacturer": "Greenhouse Solutions",
    "url": "http://localhost:8080/devices/command/DVC00001",
    "deviceStatus": "OFF",
    "commands": [
      {
        "id": 1,
        "operation": "Activate",
        "description": "Turn on the climate sensor",
        "result": "Climate sensor activated",
        "format": "JSON",
        "command": {
          "id": 1,
          "command": "Activate Climate Sensor",
          "parameters": [
            {
              "id": 1,
              "name": "sensor_id",
              "description": "The unique identifier of the climate sensor"
            }
          ]
        }
      },
      {
        "id": 2,
        "operation": "Deactivate",
        "description": "Turn off the climate sensor",
        "result": "Climate sensor deactivated",
        "format": "JSON",
        "command": {
          "id": 2,
          "command": "Deactivate Climate Sensor",
          "parameters": [
            {
              "id": 2,
              "name": "sensor_id",
              "description": "The unique identifier of the climate sensor"
            }
          ]
        }
      }
    ],
    "user": {
      "id": 2,
      "name": "User 1",
      "email": "user1@example.com",
      "username": "user1",
      "role": "ADMIN",
      "enabled": true,
      "authorities": [
        {
          "authority": "ADMIN"
        }
      ],
      "accountNonExpired": true,
      "credentialsNonExpired": true,
      "accountNonLocked": true
    },
    "createdAt": "2024-07-10T18:44:31"
  }
  ```

### Request to MonitoringController

#### Add a New Monitoring

#### Description

Register a new monitoring record in the system.

- **Example of Add Monitoring Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/POST-add-a-new-monitoring.png)

- Method: POST
- URL: `http://localhost:8080/monitoring` or `https://iot-system.rodolfo-silva-api.com/monitoring`
- CURL:
  ```bash
  curl --location --request POST 'https://iot-system.rodolfo-silva-api.com/monitoring' \
  --header 'Authorization: Bearer token_generated_in_login_request' \
  --header 'Content-Type: application/json' \
  --data '[
        {
            "monitoringStatus": "ON",
            "deviceCode": "DVC00001",
            "description": "a monitoring descripition for DVC00001"
        },
         {
            "monitoringStatus": "ON",
            "deviceCode": "DVC00002",
            "description": "a monitoring descripition for DVC00002"
        }
  ]'
  ```
- Response (raw JSON):

  ```json
  [
    {
      "id": 1,
      "monitoringCode": "MON00001",
      "description": "a monitoring descripition for DVC00001",
      "user": {
        "id": 24,
        "name": "User da Silva",
        "email": "user.silva@gmail.com",
        "username": "usersilva",
        "role": "ADMIN",
        "enabled": true,
        "accountNonExpired": true,
        "credentialsNonExpired": true,
        "authorities": [
          {
            "authority": "ADMIN"
          }
        ],
        "accountNonLocked": true
      },
      "device": {
        "id": 2,
        "deviceCode": "DVC00001",
        "deviceName": "Climate Sensor",
        "description": "Temperature and humidity sensor for greenhouses",
        "industryType": "Agriculture",
        "manufacturer": "Greenhouse Solutions",
        "url": "http://localhost:8080/devices/command/DVC00001",
        "deviceStatus": "OFF",
        "commands": [
          {
            "id": 2,
            "operation": "Activate",
            "description": "Turn on the climate sensor",
            "result": "Climate sensor activated",
            "format": "JSON",
            "command": {
              "id": 3,
              "command": "Activate Climate Sensor",
              "parameters": [
                {
                  "id": 3,
                  "name": "sensor_id",
                  "description": "The unique identifier of the climate sensor"
                }
              ]
            }
          },
          {
            "id": 2,
            "operation": "Deactivate",
            "description": "Turn off the climate sensor",
            "result": "Climate sensor deactivated",
            "format": "JSON",
            "command": {
              "id": 4,
              "command": "Deactivate Climate Sensor",
              "parameters": [
                {
                  "id": 4,
                  "name": "sensor_id",
                  "description": "The unique identifier of the climate sensor"
                }
              ]
            }
          },
          {
            "id": 2,
            "operation": "Get",
            "description": "Retrieve climate data",
            "result": "Climate data retrieved",
            "format": "JSON",
            "command": {
              "id": 2,
              "command": "Deactivate Performance Monitor",
              "parameters": [
                {
                  "id": 2,
                  "name": "climate_sensor_id",
                  "description": "The unique identifier of the climate sensor"
                }
              ]
            }
          },
          {
            "id": 2,
            "operation": "Adjust",
            "description": "Adjust climate settings",
            "result": "Climate settings adjusted",
            "format": "JSON",
            "command": {
              "id": 2,
              "command": "Activate Signal Booster",
              "parameters": [
                {
                  "id": 2,
                  "name": "settings",
                  "description": "Climate settings to be adjusted"
                }
              ]
            }
          }
        ],
        "user": {
          "id": 2,
          "name": "User 1",
          "email": "user1@example.com",
          "username": "user1",
          "role": "ADMIN",
          "enabled": true,
          "accountNonExpired": true,
          "credentialsNonExpired": true,
          "authorities": [
            {
              "authority": "ADMIN"
            }
          ],
          "accountNonLocked": true
        },
        "createdAt": "2024-07-10T18:44:31"
      },
      "status": "ON",
      "createdAt": "2024-07-13T16:38:28.8386329",
      "updatedAt": "2024-07-13T16:38:28.8346344"
    },
    {
      "id": 2,
      "monitoringCode": "MON00003",
      "description": "a monitoring descripition for DVC00002",
      "user": {
        "id": 1,
        "name": "User da Silva",
        "email": "user.silva@gmail.com",
        "username": "usersilva",
        "role": "ADMIN",
        "enabled": true,
        "accountNonExpired": true,
        "credentialsNonExpired": true,
        "authorities": [
          {
            "authority": "ADMIN"
          }
        ],
        "accountNonLocked": true
      },
      "device": {
        "id": 2,
        "deviceCode": "DVC00002",
        "deviceName": "Tractor Controller",
        "description": "Automated tractor controller",
        "industryType": "Agriculture",
        "manufacturer": "TractorTech",
        "url": "http://localhost:8080/devices/command/DVC00002",
        "deviceStatus": "STANDBY",
        "commands": [
          {
            "id": 2,
            "operation": "Activate",
            "description": "Turn on the tractor controller",
            "result": "Tractor controller activated",
            "format": "JSON",
            "command": {
              "id": 5,
              "command": "Activate Tractor Controller",
              "parameters": [
                {
                  "id": 5,
                  "name": "controller_id",
                  "description": "The unique identifier of the tractor controller"
                }
              ]
            }
          },
          {
            "id": 2,
            "operation": "Deactivate",
            "description": "Turn off the tractor controller",
            "result": "Tractor controller deactivated",
            "format": "JSON",
            "command": {
              "id": 6,
              "command": "Deactivate Tractor Controller",
              "parameters": [
                {
                  "id": 6,
                  "name": "controller_id",
                  "description": "The unique identifier of the tractor controller"
                }
              ]
            }
          }
        ],
        "user": {
          "id": 3,
          "name": "User 2",
          "email": "user2@example.com",
          "username": "user2",
          "role": "USER",
          "enabled": true,
          "accountNonExpired": true,
          "credentialsNonExpired": true,
          "authorities": [
            {
              "authority": "USER"
            }
          ],
          "accountNonLocked": true
        },
        "createdAt": "2024-07-10T18:44:31"
      },
      "status": "ON",
      "createdAt": "2024-07-13T16:38:28.8406317",
      "updatedAt": "2024-07-13T16:38:28.8386329"
    }
  ]
  ```

#### Get a Monitoring by Monitoring Code

#### Description

Retrieve details of a specific monitoring by its monitoring code.

- **Example of Get Monitoring by Monitoring Code Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/GET-a-monitoring-by-monitoringcode.png)

- Method: GET
- URL: `http://localhost:8080/monitoring/MON00001` or `https://iot-system.rodolfo-silva-api.com/monitoring/MON00001`
- CURL:
  ```bash
  curl --location 'https://iot-system.rodolfo-silva-api.com/monitoring/MON00001' \
  --header 'Authorization: Bearer token_generated_in_login_request' \
  --data ''
  ```
- Response (raw JSON):

  ```json
  {
    "id": 1,
    "monitoringCode": "MON00001",
    "description": "a monitoring descripition for DVC00001",
    "user": {
      "id": 1,
      "name": "User da Silva",
      "email": "user.silva@gmail.com",
      "username": "usersilva",
      "role": "ADMIN",
      "enabled": true,
      "accountNonExpired": true,
      "credentialsNonExpired": true,
      "authorities": [
        {
          "authority": "ADMIN"
        }
      ],
      "accountNonLocked": true
    },
    "device": {
      "id": 1,
      "deviceCode": "DVC00001",
      "deviceName": "Climate Sensor",
      "description": "Temperature and humidity sensor for greenhouses",
      "industryType": "Agriculture",
      "manufacturer": "Greenhouse Solutions",
      "url": "http://localhost:8080/devices/command/DVC00001",
      "deviceStatus": "OFF",
      "commands": [
        {
          "id": 1,
          "operation": "Activate",
          "description": "Turn on the climate sensor",
          "result": "Climate sensor activated",
          "format": "JSON",
          "command": {
            "id": 1,
            "command": "Activate Climate Sensor",
            "parameters": [
              {
                "id": 1,
                "name": "sensor_id",
                "description": "The unique identifier of the climate sensor"
              }
            ]
          }
        },
        {
          "id": 2,
          "operation": "Deactivate",
          "description": "Turn off the climate sensor",
          "result": "Climate sensor deactivated",
          "format": "JSON",
          "command": {
            "id": 2,
            "command": "Deactivate Climate Sensor",
            "parameters": [
              {
                "id": 2,
                "name": "sensor_id",
                "description": "The unique identifier of the climate sensor"
              }
            ]
          }
        },
        {
          "id": 3,
          "operation": "Get",
          "description": "Retrieve climate data",
          "result": "Climate data retrieved",
          "format": "JSON",
          "command": {
            "id": 3,
            "command": "Deactivate Performance Monitor",
            "parameters": [
              {
                "id": 3,
                "name": "climate_sensor_id",
                "description": "The unique identifier of the climate sensor"
              }
            ]
          }
        },
        {
          "id": 4,
          "operation": "Adjust",
          "description": "Adjust climate settings",
          "result": "Climate settings adjusted",
          "format": "JSON",
          "command": {
            "id": 4,
            "command": "Activate Signal Booster",
            "parameters": [
              {
                "id": 4,
                "name": "settings",
                "description": "Climate settings to be adjusted"
              }
            ]
          }
        }
      ],
      "user": {
        "id": 1,
        "name": "User 1",
        "email": "user1@example.com",
        "username": "user1",
        "role": "ADMIN",
        "enabled": true,
        "accountNonExpired": true,
        "credentialsNonExpired": true,
        "authorities": [
          {
            "authority": "ADMIN"
          }
        ],
        "accountNonLocked": true
      },
      "createdAt": "2024-07-10T18:44:31"
    },
    "status": "ON",
    "createdAt": "2024-07-13T16:38:28.838633",
    "updatedAt": "2024-07-13T16:38:28.834634"
  }
  ```

#### Update an Existing Monitoring

#### Description

Update the details of a specific monitoring by its monitoring code.

- **Example of Update Monitoring Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/PUT-update-an-exixting-monitoring.png)

- Method: PUT
- URL: `http://localhost:8080/monitoring/MON00001` or `https://iot-system.rodolfo-silva-api.com/monitoring/MON00001`
- CURL:
  ```bash
  curl --location --request PUT 'https://iot-system.rodolfo-silva-api.com/monitoring/MON00001' \
  --header 'Authorization: Bearer token_generated_in_login_request' \
  --header 'Content-Type: application/json' \
  --data '{
    "monitoringStatus": "OFF",
    "deviceCode": "DVC00001",
    "description": "a monitoring descripition for DVC00001 updated"
  }'
  ```
- Response (raw JSON):

  ```json
  {
    "id": 1,
    "monitoringCode": "MON00001",
    "description": "a monitoring descripition for DVC00001 updated",
    "user": {
      "id": 1,
      "name": "User da Silva",
      "email": "user.silva@gmail.com",
      "username": "usersilva",
      "role": "ADMIN",
      "enabled": true,
      "authorities": [
        {
          "authority": "ADMIN"
        }
      ],
      "accountNonLocked": true,
      "accountNonExpired": true,
      "credentialsNonExpired": true
    },
    "device": {
      "id": 1,
      "deviceCode": "DVC00001",
      "deviceName": "Climate Sensor",
      "description": "Temperature and humidity sensor for greenhouses",
      "industryType": "Agriculture",
      "manufacturer": "Greenhouse Solutions",
      "url": "http://localhost:8080/devices/command/DVC00001",
      "deviceStatus": "OFF",
      "commands": [
        {
          "id": 1,
          "operation": "Activate",
          "description": "Turn on the climate sensor",
          "result": "Climate sensor activated",
          "format": "JSON",
          "command": {
            "id": 1,
            "command": "Activate Climate Sensor",
            "parameters": [
              {
                "id": 1,
                "name": "sensor_id",
                "description": "The unique identifier of the climate sensor"
              }
            ]
          }
        },
        {
          "id": 2,
          "operation": "Deactivate",
          "description": "Turn off the climate sensor",
          "result": "Climate sensor deactivated",
          "format": "JSON",
          "command": {
            "id": 2,
            "command": "Deactivate Climate Sensor",
            "parameters": [
              {
                "id": 2,
                "name": "sensor_id",
                "description": "The unique identifier of the climate sensor"
              }
            ]
          }
        },
        {
          "id": 3,
          "operation": "Get",
          "description": "Retrieve climate data",
          "result": "Climate data retrieved",
          "format": "JSON",
          "command": {
            "id": 3,
            "command": "Deactivate Performance Monitor",
            "parameters": [
              {
                "id": 3,
                "name": "climate_sensor_id",
                "description": "The unique identifier of the climate sensor"
              }
            ]
          }
        },
        {
          "id": 4,
          "operation": "Adjust",
          "description": "Adjust climate settings",
          "result": "Climate settings adjusted",
          "format": "JSON",
          "command": {
            "id": 4,
            "command": "Activate Signal Booster",
            "parameters": [
              {
                "id": 4,
                "name": "settings",
                "description": "Climate settings to be adjusted"
              }
            ]
          }
        }
      ],
      "user": {
        "id": 1,
        "name": "User 1",
        "email": "user1@example.com",
        "username": "user1",
        "role": "ADMIN",
        "enabled": true,
        "authorities": [
          {
            "authority": "ADMIN"
          }
        ],
        "accountNonLocked": true,
        "accountNonExpired": true,
        "credentialsNonExpired": true
      },
      "createdAt": "2024-07-10T18:44:31"
    },
    "monitoringStatus": "OFF",
    "createdAt": "2024-07-13T17:20:51.172235",
    "updatedAt": "2024-07-13T17:43:29.1875284"
  }
  ```

#### Get Monitorings with Pagination and Filtering

#### Description

Retrieve a paginated and filtered list of monitorings. Supports various filters such as status, device code, monitoring code, user name, and device name.

#### Parameters

- `pageNo` (optional): The page number to retrieve. Default is `0`.
- `pageSize` (optional): The number of records per page. Default is `10`.
- `sortBy` (optional): The field to sort by. Default is `monitoringCode`.
- `sortDir` (optional): The direction of sorting. Default is `asc` (ascending). Use `desc` for descending order.
- `monitoringStatus` (optional): Filter devices by their Monitoring Status.
- `deviceCode` (optional): Filter devices by their Device Code.
- `monitoringCode` (optional): Filter devices by their Monitoring Code.
- `userName` (optional): Filter devices by the associated user's name.
- `deviceName` (optional): Filter devices by their Device Name.

- **Example of Get Devices with Pagination and Filtering Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/GET-all-pageable-monitorings.png)

- Method: GET
- URL: `http://localhost:8080/monitoring/pageable` or `https://iot-system.rodolfo-silva-api.com/monitoring/pageable`
- CURL:
  ```bash
  curl --location 'https://iot-system.rodolfo-silva-api.com/monitoring/pageable?pageNo=0&pageSize=10&sortBy=id&sortDir=asc&monitoringCode=MON00001&deviceCode=DVC00001&monitoringStatus=ON&userName=User da Silva&deviceName=Climate Sensor' \
  --header 'Authorization: Bearer token_generated_in_login_request' \
  --data ''
  ```
- Response (raw JSON):
  ```json
  {
    "content": [
      {
        "id": 1,
        "monitoringCode": "MON00001",
        "description": "a monitoring descripition for DVC00001",
        "user": {
          "id": 1,
          "name": "User da Silva",
          "email": "user.silva@gmail.com",
          "username": "usersilva",
          "role": "ADMIN",
          "enabled": true,
          "authorities": [
            {
              "authority": "ADMIN"
            }
          ],
          "credentialsNonExpired": true,
          "accountNonExpired": true,
          "accountNonLocked": true
        },
        "device": {
          "id": 1,
          "deviceCode": "DVC00001",
          "deviceName": "Climate Sensor",
          "description": "Temperature and humidity sensor for greenhouses",
          "industryType": "Agriculture",
          "manufacturer": "Greenhouse Solutions",
          "url": "http://localhost:8080/devices/command/DVC00001",
          "deviceStatus": "OFF",
          "commands": [
            {
              "id": 1,
              "operation": "Activate",
              "description": "Turn on the climate sensor",
              "result": "Climate sensor activated",
              "format": "JSON",
              "command": {
                "id": 1,
                "command": "Activate Climate Sensor",
                "parameters": [
                  {
                    "id": 1,
                    "name": "sensor_id",
                    "description": "The unique identifier of the climate sensor"
                  }
                ]
              }
            },
            {
              "id": 2,
              "operation": "Deactivate",
              "description": "Turn off the climate sensor",
              "result": "Climate sensor deactivated",
              "format": "JSON",
              "command": {
                "id": 2,
                "command": "Deactivate Climate Sensor",
                "parameters": [
                  {
                    "id": 2,
                    "name": "sensor_id",
                    "description": "The unique identifier of the climate sensor"
                  }
                ]
              }
            },
            {
              "id": 3,
              "operation": "Get",
              "description": "Retrieve climate data",
              "result": "Climate data retrieved",
              "format": "JSON",
              "command": {
                "id": 3,
                "command": "Deactivate Performance Monitor",
                "parameters": [
                  {
                    "id": 3,
                    "name": "climate_sensor_id",
                    "description": "The unique identifier of the climate sensor"
                  }
                ]
              }
            },
            {
              "id": 4,
              "operation": "Adjust",
              "description": "Adjust climate settings",
              "result": "Climate settings adjusted",
              "format": "JSON",
              "command": {
                "id": 4,
                "command": "Activate Signal Booster",
                "parameters": [
                  {
                    "id": 4,
                    "name": "settings",
                    "description": "Climate settings to be adjusted"
                  }
                ]
              }
            }
          ],
          "user": {
            "id": 2,
            "name": "User 1",
            "email": "user1@example.com",
            "username": "user1",
            "role": "ADMIN",
            "enabled": true,
            "authorities": [
              {
                "authority": "ADMIN"
              }
            ],
            "credentialsNonExpired": true,
            "accountNonExpired": true,
            "accountNonLocked": true
          },
          "createdAt": "2024-07-10T18:44:31"
        },
        "monitoringStatus": "ON",
        "createdAt": "2024-07-13T17:20:51.172235",
        "updatedAt": "2024-07-13T17:20:51.165733"
      }
    ],
    "pageNo": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1,
    "last": true
  }
  ```

#### Get All Monitorings

#### Description

Retrieve a list of all monitorings registered in the system.

- **Example of Get All Devices Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/GET-all-monitorings.png)

- Method: GET
- URL: `http://localhost:8080/monitoring` or `https://iot-system.rodolfo-silva-api.com/monitoring`
- CURL:
  ```bash
  curl --location 'https://iot-system.rodolfo-silva-api.com/monitoring' \
  --header 'Authorization: Bearer token_generated_in_login_request' \
  --data ''
  ```
- Response (raw JSON):
  ```json
  [
    {
      "id": 1,
      "monitoringCode": "MON00001",
      "description": "a monitoring descripition for DVC00001",
      "user": {
        "id": 1,
        "name": "User da Silva",
        "email": "user.silva@gmail.com",
        "username": "usersilva",
        "role": "ADMIN",
        "enabled": true,
        "accountNonExpired": true,
        "credentialsNonExpired": true,
        "authorities": [
          {
            "authority": "ADMIN"
          }
        ],
        "accountNonLocked": true
      },
      "device": {
        "id": 1,
        "deviceCode": "DVC00001",
        "deviceName": "Climate Sensor",
        "description": "Temperature and humidity sensor for greenhouses",
        "industryType": "Agriculture",
        "manufacturer": "Greenhouse Solutions",
        "url": "http://localhost:8080/devices/command/DVC00001",
        "deviceStatus": "OFF",
        "commands": [
          {
            "id": 1,
            "operation": "Activate",
            "description": "Turn on the climate sensor",
            "result": "Climate sensor activated",
            "format": "JSON",
            "command": {
              "id": 1,
              "command": "Activate Climate Sensor",
              "parameters": [
                {
                  "id": 1,
                  "name": "sensor_id",
                  "description": "The unique identifier of the climate sensor"
                }
              ]
            }
          },
          {
            "id": 2,
            "operation": "Deactivate",
            "description": "Turn off the climate sensor",
            "result": "Climate sensor deactivated",
            "format": "JSON",
            "command": {
              "id": 2,
              "command": "Deactivate Climate Sensor",
              "parameters": [
                {
                  "id": 2,
                  "name": "sensor_id",
                  "description": "The unique identifier of the climate sensor"
                }
              ]
            }
          },
          {
            "id": 3,
            "operation": "Get",
            "description": "Retrieve climate data",
            "result": "Climate data retrieved",
            "format": "JSON",
            "command": {
              "id": 3,
              "command": "Deactivate Performance Monitor",
              "parameters": [
                {
                  "id": 3,
                  "name": "climate_sensor_id",
                  "description": "The unique identifier of the climate sensor"
                }
              ]
            }
          },
          {
            "id": 4,
            "operation": "Adjust",
            "description": "Adjust climate settings",
            "result": "Climate settings adjusted",
            "format": "JSON",
            "command": {
              "id": 4,
              "command": "Activate Signal Booster",
              "parameters": [
                {
                  "id": 4,
                  "name": "settings",
                  "description": "Climate settings to be adjusted"
                }
              ]
            }
          }
        ],
        "user": {
          "id": 1,
          "name": "User 1",
          "email": "user1@example.com",
          "username": "user1",
          "role": "ADMIN",
          "enabled": true,
          "accountNonExpired": true,
          "credentialsNonExpired": true,
          "authorities": [
            {
              "authority": "ADMIN"
            }
          ],
          "accountNonLocked": true
        },
        "createdAt": "2024-07-10T18:44:31"
      },
      "status": "ON",
      "createdAt": "2024-07-13T16:38:28.838633",
      "updatedAt": "2024-07-13T16:38:28.834634"
    },
    {
      "id": 3,
      "monitoringCode": "MON00003",
      "description": "a monitoring descripition for DVC00002",
      "user": {
        "id": 1,
        "name": "User da Silva",
        "email": "user.silva@gmail.com",
        "username": "usersilva",
        "role": "ADMIN",
        "enabled": true,
        "accountNonExpired": true,
        "credentialsNonExpired": true,
        "authorities": [
          {
            "authority": "ADMIN"
          }
        ],
        "accountNonLocked": true
      },
      "device": {
        "id": 2,
        "deviceCode": "DVC00002",
        "deviceName": "Tractor Controller",
        "description": "Automated tractor controller",
        "industryType": "Agriculture",
        "manufacturer": "TractorTech",
        "url": "http://localhost:8080/devices/command/DVC00003",
        "deviceStatus": "STANDBY",
        "commands": [
          {
            "id": 3,
            "operation": "Activate",
            "description": "Turn on the tractor controller",
            "result": "Tractor controller activated",
            "format": "JSON",
            "command": {
              "id": 5,
              "command": "Activate Tractor Controller",
              "parameters": [
                {
                  "id": 5,
                  "name": "controller_id",
                  "description": "The unique identifier of the tractor controller"
                }
              ]
            }
          },
          {
            "id": 3,
            "operation": "Deactivate",
            "description": "Turn off the tractor controller",
            "result": "Tractor controller deactivated",
            "format": "JSON",
            "command": {
              "id": 3,
              "command": "Deactivate Tractor Controller",
              "parameters": [
                {
                  "id": 3,
                  "name": "controller_id",
                  "description": "The unique identifier of the tractor controller"
                }
              ]
            }
          }
        ],
        "user": {
          "id": 2,
          "name": "User 2",
          "email": "user2@example.com",
          "username": "user2",
          "role": "USER",
          "enabled": true,
          "accountNonExpired": true,
          "credentialsNonExpired": true,
          "authorities": [
            {
              "authority": "USER"
            }
          ],
          "accountNonLocked": true
        },
        "createdAt": "2024-07-10T18:44:31"
      },
      "status": "ON",
      "createdAt": "2024-07-13T16:38:28.840632",
      "updatedAt": "2024-07-13T16:38:28.838633"
    }
  ]
  ```

#### Delete a Monitoring by Monitoring Code

#### Description

Delete a specific monitoring by its monitoring code.

- **Example of Delete Monitoring Request in Postman**
  ![Logout Request](https://my-portifolio-images.s3.us-east-2.amazonaws.com/imgs-readme/DELETE-a-monitoring.png)

- Method: GET
- URL: `http://localhost:8080/monitoring/MON00001` or `https://iot-system.rodolfo-silva-api.com//monitoring/MON00001`
- CURL:
  ```bash
  curl --location --request DELETE 'https://iot-system.rodolfo-silva-api.com//monitoring/MON00001' \
  --header 'Authorization: Bearer token_generated_in_login_request'
  ```
- Response (raw JSON):
  ```json
  {
    "status": 200,
    "message": "Monitoring was successfully deleted.",
    "timestamp": "2024-07-13T16:36:43.8811903"
  }
  ```

## Contributing

1. Fork the project
2. Create a new branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

## Contact and Support

For questions or support, contact us at: support@example.com

## License

This project is licensed under the MIT License. See the LICENSE file for more details.
