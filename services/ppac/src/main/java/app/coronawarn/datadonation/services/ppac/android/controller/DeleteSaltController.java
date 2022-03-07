package app.coronawarn.datadonation.services.ppac.android.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteSaltController {

  @Autowired
  private DeleteSaltService saltService;

  @DeleteMapping(value = "/delete/salt/{salt}")
  public ResponseEntity<String> deleteEmployee(@PathVariable("salt") String salt) {
    saltService.deleteSalt(salt);
    return new ResponseEntity<>("Salt " + salt + " deleted", HttpStatus.OK);
  }


  @GetMapping(value = "/api/ping")
  public ResponseEntity<String> getPing() {
    return new ResponseEntity<>("pong", HttpStatus.OK);
  }

}
