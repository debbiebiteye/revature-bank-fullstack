package com.revature.bank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import com.revature.bank.dto.GreetingResponse;
import com.revature.bank.dto.GreetingRequest;
import com.revature.bank.service.GreetingService;

// - This annotation tells Spring to write your return value into the HTTP response body
// - Tells Spring this class handles web requests and writes method results into HTTP responses.
@RestController
public class HelloController {

    //A controller class is the entry point for HTTP requests into your application.
    //The controller handles receiving the request and returning the response. T
    // he service decides how to create the greeting.
    //A web controller receives HTTP input and calls application logic.
    //HelloController:
    //1. Matches a request to a method using annotations such as @GetMapping.
    //2. Receives input from the URL or JSON body.
    //3. Calls a service to perform the application’s work.
    //4. Returns a response to the client.

    private final GreetingService greetingService;

    //This is constructor injection: Spring supplies the GreetingService when it creates your controller.
    // You don’t need new GreetingService() or @Autowired when there is only one constructor.
    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }



        //**Controller methods handle how input arrives; the service handles the shared logic.

        //connects an HTTP GET request at that path to hello().
        //GET /api/hello
        //    → hello() creates a GreetingResponse
        //    → Spring converts the object to JSON
        //    → Client receives 200 OK and the JSON body
        @GetMapping("/api/hello")
        public GreetingResponse hello(
                //@RequestParam filters, sorts, or adjusts a request:
                @RequestParam(defaultValue = "Guest") String name) {

            return new GreetingResponse(
                    greetingService.createGreeting(name)
            );
        }

    @GetMapping("/api/hello/{name}")
    public GreetingResponse helloByName(
            //@PathVariable reads a value from the URL path
            //@PathVariable identifies a resource
            //The {name} placeholder captures user's name, and @PathVariable("name") passes it into the method.
            @PathVariable("name") String name) {

        return new GreetingResponse(
                greetingService.createGreeting(name)
        );

    }

    //@RequestBody usually goes with POST, such as submitting registration details.
    //@RequestBody tells Spring to convert the incoming JSON into a GreetingRequest object.
    //@RequestBody converts the JSON; @Valid checks the resulting object. If validation fails,
    // Spring returns 400 before entering your method.
    @PostMapping("/api/hello")
    public GreetingResponse greet(
            @Valid @RequestBody GreetingRequest request) {

        String message =
                greetingService.createGreeting(request.getName());

        return new GreetingResponse(message);
    }
}

//Annotation	    Reads input from	    Example
//@RequestParam	    Query string	    /api/hello?name=Debbie
//@PathVariable 	URL path	        /api/hello/Debbie
//@RequestBody	    Request body	    {"name":"Debbie"}

//For the banking API, we’ll generally use path variables for IDs and
// query parameters for filtering and pagination.