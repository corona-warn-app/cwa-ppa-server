package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.DELETE_SALT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteSaltController {

  @Autowired
  private DeleteSaltService saltService;

  @DeleteMapping(value = DELETE_SALT)
  public ResponseEntity<String> deleteSalt(@PathVariable("salt") String salt) {
    saltService.deleteSalt(salt);
    return new ResponseEntity<>("Salt " + salt + " deleted", HttpStatus.OK);
  }
}
