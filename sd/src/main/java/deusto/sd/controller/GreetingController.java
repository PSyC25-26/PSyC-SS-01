package deusto.sd.controller;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import deusto.sd.entity.*;
import deusto.sd.service.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ej1")
public class GreetingController {

  private final AtomicLong counter = new AtomicLong();
  private final GreetingService greetingService;

  public GreetingController(GreetingService greetingService){
    this.greetingService=greetingService;
  }

  @GetMapping("/getDefaultGreeting")
  public Greeting greeting(@RequestParam(defaultValue = "World") 
    String name) {
    return new Greeting(counter.incrementAndGet(), name);
  }

  @GetMapping("/getGreetingByID/{id}")
  public ResponseEntity<Greeting> getGreetingByID(@PathVariable("id") Long id) {
    
    Greeting newGreeting = greetingService.getGreetingByID(id);
    if(newGreeting!=null){
      return new ResponseEntity<>(newGreeting, HttpStatus.OK);
    }else{
      return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
  }

  @GetMapping("/getAllGreetings")
  public ResponseEntity<List<Greeting>> getAllGreetings() {
    
    List<Greeting> newGreeting = greetingService.getAllGreetings();
    return new ResponseEntity<>(newGreeting, HttpStatus.OK);
  }

  @PostMapping("/postGreeting")
  public ResponseEntity<Greeting> createGreeting(@RequestBody Greeting greeting) {
    try{
        Greeting newGreeting =  greetingService.createGreeting(greeting);
        if(newGreeting!=null){
          return new ResponseEntity<>(newGreeting, HttpStatus.OK);
        }else{
          return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }catch (Exception e){
      e.printStackTrace();
      return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping("/putGreeting")
  public ResponseEntity<Greeting> editGreeting(@RequestParam("idGreeting") Long idGreeting, 
  @RequestParam("content") String content) {
    Greeting editedGreeting = greetingService.editGreeting(idGreeting, content);
    if(editedGreeting!=null){
      return new ResponseEntity<>(editedGreeting, HttpStatus.OK);
    }else{
      return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
  }

  @DeleteMapping("/deleteGreetingByID/{id}")
  public ResponseEntity<Greeting> deleteGreetingByID(@PathVariable("id") Long id) {
    
    boolean wasDeleted = greetingService.deleteGreetingByID(id);
    if(wasDeleted!=false){
      return new ResponseEntity<>(HttpStatus.OK);
    }else{
      return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
  }
}