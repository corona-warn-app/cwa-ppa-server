package app.coronawarn.datadonation.services.ppac.android.controller;

import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteSaltService {

  @Autowired
  private SaltRepository saltRepository;

  Logger logger = LoggerFactory.getLogger(DeleteSaltService.class);

  /**
   * Deletes the salt provided.
   * @param salt String salt
   */
  public void deleteSalt(String salt) {

    try {
      saltRepository.deleteSalt(salt);
    } catch (SaltNotFoundException saltNotFoundException) {
      logger.error("Salt " + salt + " not found.");
    }
  }
}
