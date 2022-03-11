package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.DELETE_SALT;

import app.coronawarn.datadonation.common.persistence.service.SaltService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("test-signature")
public class DeleteSaltController {

  @Autowired
  private SaltService saltService;

  @DeleteMapping(value = DELETE_SALT)
  public ResponseEntity<String> deleteSalt(@PathVariable("salt") String salt) {
    saltService.deleteSalt(salt);
    return new ResponseEntity<>(String.format("Salt: %s was deleted", salt), HttpStatus.OK);
  }
}
