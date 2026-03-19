package deusto.sd.service;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import deusto.sd.entity.Greeting;

@Service
public class GreetingService {
    private final ArrayList<Greeting> listaGreetings = new ArrayList<>();

    public Greeting createGreeting(Greeting greeting){
        boolean valido=true;
        for(Greeting g1: listaGreetings){
            if(g1.getId()==greeting.getId()){
                valido=false;
            }
        }
        if(valido){
            listaGreetings.add(greeting);
            return greeting;
        }else{
            return null;
        }
        
    }

    public List<Greeting> getAllGreetings(){
        List<Greeting> lista = this.listaGreetings;
        return lista;
    }

    public Greeting getGreetingByID(Long id){
        for(Greeting g1 :listaGreetings){
            if(g1.getId()==id){
                return g1;
            }
        }
        return null;
    }

    public Greeting editGreeting(Long idGreeting, String content){
        for(Greeting g1 :listaGreetings){
            if(g1.getId()==idGreeting){
                g1.setContent("Hello, "+content);
                return g1;
            }
        }
        return null;
    }

    public boolean deleteGreetingByID(Long id){
        for(Greeting g1 :listaGreetings){
            if(g1.getId()==id){
                listaGreetings.remove(g1);
                return true;
            }
        }
        return false;
    }
}
