![product-screenshot](https://live.staticflickr.com/65535/51668926040_e41f025445_o.jpg)
<h1 align="center">Ktor backend server for users database</h1>

<details open ="open">
  <summary>Contains</summary>
  <ol>
    <li>
      <a href='#about-the-project'>About Project</a>
        <ul>
          <li><a href="#built-with">Built With</a></li>
        </ul>
    </li>
    <li>
      <a href='#getting-started'>Getting Started</a>
        <ul>
          <li><a href="#prerequisites">Prerequisites</a></li>
        </ul> 
        <ul>
          <li><a href="#installation">Installation</a></li>
        </ul>
    </li> 
 <li>
      <a href='#usage'>Usage</a>
        <ul>
          <li><a href="#registrationexample">Registration example</a></li>
        </ul> 
        <ul>
          <li><a href="#installation">Installation</a></li>
        </ul>
    </li> 
    <li>
    <li>
      <a href='#contacts'>Contacts</a> 
    </li>
  </ol>
</details>


## About Project

This Ktor backend server is for job searching application "Found me". This application allows for employers to search for new employees in database. 
Where they created their profile. Employers can sort all users in categories, and recieve list of users in those specific categories.
Employers can sort list even further sorting users by keywords. When they open users profile they recieve information about that single user.

This server :
*  Provides secure way to save users password (Hash and Salt password encryption)
*  Provides precise Authentication and Authorization using JWT Auth
*  Allows you to upload pictures via multipart request
*  Is built using KOIN dependency injection

### Built With 

This application is built in  [IntelliJ IDEA 2021.1.2 (Community Edition )(Artic Fox)](https://www.jetbrains.com/idea/download/#section=windows) 
using :

* [Ktor](https://ktor.io/) - For backend creation 
* [MongoDB](https://www.mongodb.com/) - For database 
* [KMongo](https://litote.org/kmongo/) - Kotlin toolkit for MongoDB
* [Kotlin](https://kotlinlang.org/) - Main development language
* [Coroutines](https://kotlinlang.org/docs/coroutines-guide.html) - for making asynchronous tasks
* [JWT (Json Web Tokens)](https://ktor.io/docs/jwt.html) - For safe authorization
* [Koin](https://insert-koin.io/) - For dependency injection
* [Gson](https://github.com/google/gson) - for serialization with Json 
* [Commons Codec](https://commons.apache.org/proper/commons-codec/) - for security
* [Truth](https://truth.dev/) - For making fluent assortations in testing

For testing routes : 
* [Postman](https://www.postman.com/)

For observing database : 
* [MongoDB Compass](https://www.mongodb.com/products/compass)


## Getting Started
### Prerequisites 

1. You need to install IntelliJ IDEA version 2012.1.2 or newer
you can find installation in : [IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=windows) 

2. For testing routes you will need Postman you can download it here : [Postman](https://www.postman.com/downloads/)

3. For observing database data you will need MongoDB Compass, you can download it here : [MongoDB Compass](https://www.mongodb.com/try/download/compass)

### Installation 

1. Clone repository using : 

* IntelliJ IDE **file -> New -> Project from version control...** And enter this followin in URL
   ```sh
   https://github.com/OzolsUgis/JobSearchingServer
   ```

* Using terminal 
  ```sh
   git clone https://github.com/OzolsUgis/JobSearchingServer.git
   ```

2. For configuration you must create your ` application.conf ` file  : 

    In resources folder create new file and name it ` application.conf ` and in this folder add following : 
    
    ``` ktor {
    deployment {
        port = // Here you need to assign empty port default is 8080
        port = ${?PORT}
    }
    application {
        modules = [ com.ugisozols.ApplicationKt.module ]
    }
    }
    jwt {
        issuer = // Here you assign issuer 
        domain = // Here you assign domain
        audience = //Here you assign audience
        secret = // Here you assign secret 
        realm = // Here you assign realm
    }
    ``` 
    !!! NOTE : You should never share this file with your JWT secret in it. That's why we put this `application.conf` in `.gitignore`
    
    
    If you dont understand what jwt properties are you can look up for information in [Json Web Tokens](https://ktor.io/docs/jwt.html)
    
    ## Usage
    
    ### Registration example
    
    * This example will show how to register user:
    
        For this request you will need CreateAccountRequest attached to your endpoint `"api/user/create"`
        ```kotlin 
        data class CreateAccountRequest(
          val email : String,
          val password : String,
          val confirmedPassword : String
        )
        ```
        You can do it with Postman using JSON 
        
        For example
        ```json
        {
           "email":"Test@test.com",
           "password":"ThisIsTestPassword",
           "confirmedPassword":"ThisIsTestPassword"
        }
        ```
        ! Email string need to contain `@` and `.` chars, if not there will be error.
        
        ! Passord must be atleast 8 units long.
        
        When you send request you will recieve response like following or any other response if there is some error. You can see all errors in `ApiResponses` object  
        ```json
        {
            "successful": true,
            "message": "Account Created"
        }
        ```
       
        
        And this is how it looks like in DB, you can observe it in MongoDB compass
        ![profuct-screenshot](https://live.staticflickr.com/65535/51671936448_17c60d183d.jpg)
        
        
   
