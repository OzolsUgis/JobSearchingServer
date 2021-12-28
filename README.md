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
          <li><a href="#registration-example">Registration example</a></li>
        </ul> 
        <ul>
          <li><a href="#login-example">Login example</a></li>
        </ul>
        <ul>
          <li><a href="#get-users-profile">Get users profile</a></li>
        </ul>
        <ul>
          <li><a href="#update-profile">Update profile</a></li>
        </ul>
    </li> 
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

<p align="right" dir="auto">(<a href="#top">back to top</a>)</p>

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
    
   <p align="right" dir="auto">(<a href="#top">back to top</a>)</p>
    
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
        
        
        <p align="right" dir="auto">(<a href="#top">back to top</a>)</p>
        
    ### Login example
    
    * This example will show how to login in users account:
    
        In this example you will need `AccountRequest` 
        ```kotlin
        data class AccountRequest(
            val email : String,
            val password : String
        )
        ```
        ```json
        {
          "email":"Test@test.com",
          "password":"ThisIsTestPassword"
        }
        ```
        
        When you will send this request you will recieve `MainApiResponse` where in `data` field is specified `AuthResponse`
        
        ```json
        {
          "successful": true,
          "data": {
                "userId": "618cf7fb61c5875f146e0bbb", 
                "email": "Test@test.com", 
                "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJtYWluIiwiaXNzIjoiaHR0cDovLzAuMC4wLjA6ODA4MC8iLCJleHAiOjE2NjgxNjU1NTcsInVzZXJJZCI6IjYxOGNmN2ZiNjFjNTg3NWYxNDZlMGJiYiIsImVtYWlsIjoiNjE4Y2Y3ZmI2MWM1ODc1ZjE0NmUwYmJiIn0.bCofF0EkYgZOwik3gezUPols6gUTVpopUj5jmYEsrng"
                // Users token for authorization
          }
        }
        ```
        
        
        
    ### Get users profile
    
      * Get users profile functionality seperates in 2 pieces (Get public profile, and Get private profile)

          ** Public profile is response what unauthorized user gets when pass in UsersId for query
          
          ** Private profile is when authorized user access hes own profile
          
          In this example, authorized user access hes own profile: 
          
      * You will need: QueryParameter which contains UserID, you can get that in successful login response 
          
          Assign users id to request in Postman you need to specify `BASE_URL` with `Route`. and as a request select GET request
      
          ![product-screenshot](https://live.staticflickr.com/65535/51672061843_f7af5b210e_c.jpg)
          
          To specify userID in params block write KEY `userId` and VALUE `618cf7fb61c5875f146e0bbb`, like you see in example above - This value is user id what you got from successful login request
          
      * To access your own profile you need to pass Authorization security for that we need addHeaders to our request, that contains our TOKEN


          ![product-screenshot](https://live.staticflickr.com/65535/51672691715_4eb4d043b3_c.jpg)
          
          In Headers block you need to specify KEY  `Authorization` and VALUE `Bearer //Input token here//`
          
          !!! Check if between Bearer and token is only one white space
          
          Response will be Empty User in `ProfileReponse` 
          ```kotlin 
          data class ProfileResponse(
              val id : String,
              val name : String,
              val lastName : String,
              val profession : String,
              val profileImageUrl : String?,
              val instagramUrl : String?,
              val linkedInUrl : String?,
              val githubUrl : String?,
              val bio : String?,
              val experience : Int?,
              val education : Education?,
              val skills : List<Skills> = listOf(),
              val currentJobState : CurrentJobState? = null,
              val profileUpdateDate : Long?,
              val keywords : List<Keywords> = listOf(),
              val category : Categories?
          )
          ``` 
          
     
     <p align="right" dir="auto">(<a href="#top">back to top</a>)</p>
     
     ### Update user
     
      * This will be multipart put request where you can send Form Data (TEXT & FILE) for updating user profile information and picture 

          Outdated user looks like this :
          
          ![profuct-screenshot](https://live.staticflickr.com/65535/51671936448_17c60d183d.jpg)
          
      To update user in Postman you must do following : 
      
      * Specify user id in Params block you want to update in Query parameter 
          
          As a KEY insert `userId` and VALUE insert userId 
          
          ![product-screenshot](https://live.staticflickr.com/65535/51672550083_65bf76be76_c.jpg)
          
      * Add authorization header in Headers block 
          
          As a KEY insert `Authorization` and in VALUE field insert `Bearer //insert token here//`
          
          ![product-screenshot](https://live.staticflickr.com/65535/51672309831_50d99ed13e_c.jpg)
          
      * Add Body in Body Form-data: 
          
          As a KEY you must select type TEXT and insert key `profile_update`. And in VALUE you need to pass JSON data string. Like following: 
          ```json
            {
                "id": "618689bd3f4886646510390b",
                "name": "Peter",
                "lastName": "Davis",
                "education":{"name": "Google Associate android developer "},
                "profession": "Backend Developer",
                "experience" : 3,
                "profileImageUrl": "http://192.168.8.103:8080/profile_pictures/default_profile_picture.png",
                "instagramUrl": "Test web page",
                "linkedInUrl": "Test web page",
                "githubUrl": "Test web page",
                "bio": "Want to start job in some great IT company, my strengths are Backend development in Ktor using Kotlin language.",
                "skills": [
                    {
                        "name": "Java"
                    },
                    {
                        "name": "Kotlin"
                    },
                    {
                        "name": "C#"
                     }
                 ],
                "keywords": [{"name":"Android"},{"name":"Java"}],
                "category": {
                    "name": "IT"
                }
            }
          ```
          ![product-screenshot](https://live.staticflickr.com/65535/51672550058_c50c48c3be_c.jpg)
          
          If you want to change your profile picture you must attach another KEY `picture_update`of type File and upload file. 
          
          ![product-screenshot](https://live.staticflickr.com/65535/51673179995_48f28a7f7f_c.jpg)
          
          After request you can check your data in MongoDB Compass and it should look like this 
          
          ![product-screenshot](https://live.staticflickr.com/65535/51672347936_d95b53f47c_z.jpg)
          
          
          <p align="right" dir="auto">(<a href="#top">back to top</a>)</p>
   ## Contacts

     Ugis Ozols - ozols.ugis@outlook.com , LinkedIn - www.linkedin.com/in/uģis-ozols-2192a8226


     Project Link - https://github.com/OzolsUgis/JobSearchingServer

      
          
       
          
          
          
          
